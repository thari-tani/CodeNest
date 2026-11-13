/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;
import java.sql.*;
/*
 * @author P. U. Liyanage
 * @sanumber SA26610056
 * @group EcoNest
 */
public class DBManager {
    private String url, username, password;
    private Connection connection;

    public DBManager(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    
    //Connect to a database
    public void connect(){
        try{
            //Try connecting using the given connnection URL, username annd password
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e){
            System.err.println("Could not connect to database:\n\n" + e);
        }
    }
    
    
    public void connect(String url, String username, String password) {
        //Assign the fields the required values
        this.url = url;
        this.username = username;
        this.password = password;
       
        //Call the unparameterized version of the function
        this.connect();
    }
    
    public void close(){
       
        //Close the connection to the database
        try{connection.close();}
        catch(SQLException e){
            System.err.println("Could not close connection to database:\n\n" + e);
        }
    }
    
    public ResultSet searchQuery(String query) throws SQLException {
       
        try{
           
            //Prepare the query for execution
           PreparedStatement statement = connection.prepareStatement(query);
           ResultSet result = statement.executeQuery();
           statement.close();
           return result;
            
        } catch (SQLException e){
            System.err.println("Unable to execute query: \n\n" + e);
            throw e;
            
        }
    }
    
     public void updateQuery(String query, long rowCount) throws SQLException{
       
       try(PreparedStatement statement = connection.prepareStatement(query);){
                     
           //Check if the number of rows created exceeds the largest storable Integer in Java
           if(rowCount > Integer.MAX_VALUE){
               //Execute a Large Update
               statement.executeLargeUpdate(query);
           } else {
               //Execute a normal update
               statement.executeUpdate(query);
           }
                      
        } catch (SQLException e){
            System.err.println("Unable to execute query: \n\n" + e);
            throw e;
        } finally {
           
           return;
       }
        
    }
    
    //Used if the number of rows that will be created is not known
    public void updateQuery(String query) throws SQLException{
       
       try(PreparedStatement statement = connection.prepareStatement(query);){
                     
           statement.executeUpdate(query);
                      
        } catch (SQLException e){
            System.err.println("Unable to execute query: \n\n" + e);
            throw e;
        } finally {
           
           return;
       }
        
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
}
