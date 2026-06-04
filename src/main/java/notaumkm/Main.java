package notaumkm;

import notaumkm.db.DBConnection;
import notaumkm.ui.LoginFrame;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("[Main] Gagal set L&F: " + e.getMessage());
        }

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DBConnection.closeConnection();
            System.out.println("[Main] Aplikasi ditutup.");
        }));
    }
}
