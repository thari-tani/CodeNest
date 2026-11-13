import com.*;
import javax.swing.SwingUtilities;

public class ClientOrderApp {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            ProcessOrderFrame frame =
                    new ProcessOrderFrame();

            frame.setVisible(true);

        });

    }

}