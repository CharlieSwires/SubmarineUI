import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import GenericGet.GenericGet;

public class ImageDisplayFrame extends JFrame {

    private JLabel imageLabel;
    private Timer timer;

    public ImageDisplayFrame() {
        initializeUI();
        startImageUpdateTask();
    }

    private void initializeUI() {
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        this.add(imageLabel);
        this.setSize(640, 480); // Set the initial frame size
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void startImageUpdateTask() {
        // Define the task to fetch and display the image
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Image image = GenericGet.getImage("/image/capture"); // Assuming "/capture" is the suffix used in the getImage method
                if (image != null) {
                    ImageIcon icon = new ImageIcon(image);
                    imageLabel.setIcon(icon);
                    pack(); // Adjust the frame size to fit the image
                }
            }
        };
        // Schedule the task to run every 50ms (20 times per second)
        timer = new Timer(2000, taskPerformer);
        timer.start();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ImageDisplayFrame(); // Create and display the JFrame
            }
        });
    }
}
