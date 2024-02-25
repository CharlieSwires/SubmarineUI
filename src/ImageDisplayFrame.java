
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import javax.imageio.ImageIO;
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
	private boolean disable = false;
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
		JButton fullScreenButton = new JButton("SD");
		JButton photoButton = new JButton("Photo");
		Color original = fullScreenButton.getBackground();
		JPanel top = new JPanel();
		top.add(fullScreenButton);
		top.add(photoButton);
		this.add(top,BorderLayout.NORTH);
		fullScreenButton.addActionListener(e -> {
			full = !full;
			if (!full) {
				fullScreenButton.setBackground(original);
				fullScreenButton.setText("SD");
				idf.setSize(500, 500); // Set the initial frame size

			} else {
				fullScreenButton.setBackground(Color.GREEN);
				fullScreenButton.setText("HD");
				idf.setSize(1920,1080); // Set the initial frame size
			}
			repaint(); // Tell the panel to repaint itself

		});          
		photoButton.addActionListener(e -> {
			disable = true;
			photoButton.setBackground(Color.GREEN);
			photoButton.setText("Taking");

			// Use a separate thread for long-running operations
			new Thread(() -> {
				Image image = GenericGet.getImage("/image/capture/photo"); // Assuming "/capture" is the suffix used in the getImage method
				File photo = new File("photo"+LocalDateTime.now().toString().replaceAll("\\.","_").replaceAll("\\:","_")+".jpg");
				try {
					ImageIO.write((RenderedImage) image, "JPEG", photo);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				// Update the button back to its original state on the EDT
				SwingUtilities.invokeLater(() -> {
					photoButton.setBackground(original);
					photoButton.setText("Photo");
					disable = false;
				});
			}).start();
		});

		this.setVisible(true);
	}

	private void startImageUpdateTask() {
		// Define the task to fetch and display the image
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(!disable ) {
					Image image = GenericGet.getImage("/image/capture/"+full); // Assuming "/capture" is the suffix used in the getImage method
					if (image != null) {
						imagePanel.setImage(image);
					}
				}
			}
		};
		// Schedule the task to run every 50ms (20 times per second)
		if(!disable ) {
			timer = new Timer(4000, taskPerformer);
			timer.start();
		}
	}


	class ImagePanel extends JPanel {
		private Image image;

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
