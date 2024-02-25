
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import GenericGet.GenericGet;

public class ImageDisplayFrame extends JFrame {

    private ImagePanel imagePanel;
    private Timer timer;
    private boolean full = false;
    private static JFrame idf;
    public ImageDisplayFrame() {
    	super("Periscope");
        initializeUI();
        startImageUpdateTask();
    }

    private void initializeUI() {
        imagePanel = new ImagePanel();
        this.add(imagePanel);
        this.setSize(500,500); // Set the initial frame size
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void startImageUpdateTask() {
        // Define the task to fetch and display the image
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Image image = GenericGet.getImage("/image/capture/"+full); // Assuming "/capture" is the suffix used in the getImage method
                if (image != null) {
                    imagePanel.setImage(image);
                }
            }
        };
        // Schedule the task to run every 50ms (20 times per second)
        timer = new Timer(4000, taskPerformer);
        timer.start();
    }


    class ImagePanel extends JPanel {
        private Image image;

        public ImagePanel() {
            JButton fullScreenButton = new JButton("Not Full");
            Color original = fullScreenButton.getBackground();
            this.add(fullScreenButton,BorderLayout.SOUTH);
            fullScreenButton.addActionListener(e -> {
            	full = !full;
    			if (!full) {
    				fullScreenButton.setBackground(original);
    				fullScreenButton.setText("Not Full");
    		        idf.setSize(500, 500); // Set the initial frame size

    			} else {
    				fullScreenButton.setBackground(Color.GREEN);
    				fullScreenButton.setText("Full");
    		        idf.setSize(1080, 960); // Set the initial frame size
    			}
                repaint(); // Tell the panel to repaint itself

    		});        	
        }
        public void setImage(Image image) {
            this.image = image;
            repaint(); // Tell the panel to repaint itself
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                // Scale the image to fit the panel size while maintaining the aspect ratio
                Dimension scaledDimension = getScaledDimension(new Dimension(image.getWidth(null), image.getHeight(null)), getSize());
                Image scaledImage = image.getScaledInstance(scaledDimension.width, scaledDimension.height, Image.SCALE_SMOOTH);
                int x = (getWidth() - scaledDimension.width) / 2; // Center the image horizontally
                int y = (getHeight() - scaledDimension.height) / 2; // Center the image vertically
                g.drawImage(scaledImage, x, y, this);
            }
        }

        private Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
            int originalWidth = imgSize.width;
            int originalHeight = imgSize.height;
            int boundWidth = boundary.width;
            int boundHeight = boundary.height;
            int newWidth = originalWidth;
            int newHeight = originalHeight;

            // Check if the image needs to be scaled down
            if (originalWidth > boundWidth) {
                newWidth = boundWidth;
                // Scale height to maintain aspect ratio
                newHeight = (newWidth * originalHeight) / originalWidth;
            }

            // Then check if we need to scale even with the new height
            if (newHeight > boundHeight) {
                newHeight = boundHeight;
                // Scale width to maintain aspect ratio
                newWidth = (newHeight * originalWidth) / originalHeight;
            }

            return new Dimension(newWidth, newHeight);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                idf = new ImageDisplayFrame(); // Create and display the JFrame
            }
        });
    }
}
