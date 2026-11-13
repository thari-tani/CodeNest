package com.econest;

import java.sql.*;
import java.util.List;

public class OrderDAO {

    public int saveOrder(int clientId,
                         List<OrderItem> items)
            throws SQLException {

        String orderSQL =
                "INSERT INTO orders "
                + "(client_id, total_amount) "
                + "VALUES (?, ?)";

        String itemSQL =
                "INSERT INTO order_items "
                + "(order_id, product_id, quantity, "
                + "unit_price, subtotal) "
                + "VALUES (?, ?, ?, ?, ?)";

        String stockSQL =
                "UPDATE products "
                + "SET quantity = quantity - ? "
                + "WHERE product_id = ? "
                + "AND quantity >= ?";

        try (Connection con =
                     DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                double total = 0;

                for (OrderItem item : items) {

                    total += item.getSubtotal();

                }

                int orderId;

                try (PreparedStatement ps =
                             con.prepareStatement(
                                     orderSQL,
                                     Statement.RETURN_GENERATED_KEYS
                             )) {

                    ps.setInt(1, clientId);

                    ps.setDouble(2, total);

                    ps.executeUpdate();

                    try (ResultSet rs =
                                 ps.getGeneratedKeys()) {

                        if (rs.next()) {

                            orderId = rs.getInt(1);

                        } else {

                            throw new SQLException(
                                    "Order ID generation failed."
                            );

                        }

                    }

                }

                for (OrderItem item : items) {

                    try (PreparedStatement ps =
                                 con.prepareStatement(stockSQL)) {

                        ps.setInt(1, item.getQuantity());

                        ps.setInt(2, item.getProductId());

                        ps.setInt(3, item.getQuantity());

                        int updated = ps.executeUpdate();

                        if (updated == 0) {

                            throw new SQLException(
                                    "Insufficient stock for "
                                    + item.getProductName()
                            );

                        }

                    }

                    try (PreparedStatement ps =
                                 con.prepareStatement(itemSQL)) {

                        ps.setInt(1, orderId);

                        ps.setInt(2, item.getProductId());

                        ps.setInt(3, item.getQuantity());

                        ps.setDouble(4, item.getUnitPrice());

                        ps.setDouble(
                                5,
                                item.getSubtotal()
                        );

                        ps.executeUpdate();

                    }

                }

                con.commit();

                return orderId;

            } catch (SQLException e) {

                con.rollback();

                throw e;

            } finally {

                con.setAutoCommit(true);

            }

        }

    }

}