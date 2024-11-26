import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import Const.Constant;
import GenericGet.GenericGet;
import pid.PIDControllerAngle;

public class Navigation {
	private static boolean isCourseSet = false;
	private static Integer relative = 0;
	private static Integer bearing = -20;
	private static Integer coursebearing = 0;
	private static PIDControllerAngle pidController = new PIDControllerAngle(0.1, 0.01, 0.05);
	private static Integer previousControlOutput = null;
	private static Integer rudderAngle;

	// Creating the JSlider
	private static JSlider rudder = new JSlider(JSlider.HORIZONTAL, -45, 45, 0); // Arguments: orientation, min, max, initial value
	private static JPanel compass = new JPanel() {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			int bearing = (int)Navigation.bearing;
			int relative = (int)Navigation.relative;
			g.drawArc(10, 120, 200, 200, 0, 360);
			g.drawLine(10+100, 120+100, 
					10+100+(int)(100*Math.sin((relative+bearing)/(180.0/Math.PI))), 
					120+100-(int)(100*Math.cos((relative+bearing)/(180.0/Math.PI))));
			g.drawChars("N".toCharArray(), 0, 1, 10+100+(int)(110*Math.sin((relative)/(180.0/Math.PI))), 
					120+100-(int)(110*Math.cos((relative)/(180.0/Math.PI))));
			g.drawChars("E".toCharArray(), 0, 1, 10+100+(int)(110*Math.sin((relative+90)/(180.0/Math.PI))), 
					120+100-(int)(110*Math.cos((relative+90)/(180.0/Math.PI))));
			g.drawChars("S".toCharArray(), 0, 1, 10+100+(int)(110*Math.sin((relative+180)/(180.0/Math.PI))), 
					120+100-(int)(110*Math.cos((relative+180)/(180.0/Math.PI))));
			g.drawChars("W".toCharArray(), 0, 1, 10+100+(int)(110*Math.sin((relative+270)/(180.0/Math.PI))), 
					120+100-(int)(110*Math.cos((relative+270)/(180.0/Math.PI))));
			if (!isCourseSet) {
				g.setColor(Color.RED);
			} else {
				g.setColor(Color.GREEN);
			}
			g.drawLine(10+100, 120+100, 
					10+100+(int)(100*Math.sin((relative+coursebearing)/(180.0/Math.PI))), 
					120+100-(int)(100*Math.cos((relative+coursebearing)/(180.0/Math.PI))));
			if (bearing == Constant.ERROR) {
				g.setColor(Color.RED);
				bearing = 0;
			} else {
				g.setColor(Color.BLACK);
			}
			g.drawChars(("Bearing: "+(bearing < 0 ? 360 + bearing : bearing)+"degrees         ").toCharArray(), 0, 25, -60+100+(int)(100*Math.sin((+180)/(180.0/Math.PI))), 
					120+100+30-(int)(100*Math.cos((+180)/(180.0/Math.PI))));
			g.setColor(Color.BLACK);

			if (rudderAngle == Constant.ERROR)
				rudder.setForeground(Color.RED);
			else 
				rudder.setForeground(Color.BLACK);
		}
	};
	private static void course(int value) {
		Navigation.coursebearing = value;

	}

	private static void reference(int value) {
		Navigation.relative = value;
	}

	private static void resetButtons(Color original, JButton setCourse) {
		setCourse.setBackground(original);
	}
	private class MyThread extends Thread {

		@Override
		public void run() {
			while (true) {
				String url;
				url = new String("/navigation/bearing");
				try {
					Navigation.bearing = GenericGet.getGeneric(url);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
				if (isCourseSet) {
					double controlOutput = pidController.compute(Navigation.bearing);
					controlOutput = controlOutput > 45.0 ? 45.0 : controlOutput;
					controlOutput = controlOutput < -45.0 ? -45.0 : controlOutput;
					if (previousControlOutput != null && Math.round(controlOutput) != (Integer)previousControlOutput) {
						rudder.setValue((int)Math.round(controlOutput));
						url = new String("/navigation/rudder/"+((int)controlOutput));
						try {
							rudderAngle = GenericGet.getGeneric(url);
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
					}
					previousControlOutput = (int)Math.round(controlOutput);
				} else {
					url = new String("/navigation/rudder/"+((int)rudder.getValue()));
					try {
						rudderAngle = GenericGet.getGeneric(url);
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}
				compass.repaint();
				try {
					MyThread.sleep(Constant.tick_ms);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}
	public static void main(String[] args) {
		// Creating the JFrame for the application
		JFrame frame = new JFrame("Navigation");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 550);


		compass.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Add some padding
		compass.setSize(250, 300); // Setting size here won't have any effect in this layout

		frame.add(compass, BorderLayout.CENTER);

		// Creating the JSlider
		JSlider relativeSlider = new JSlider(JSlider.VERTICAL, -180, 180, 0); // Arguments: orientation, min, max, initial value
		// Get the current preferred size, double it, and set it back
		Dimension preferredSize = relativeSlider.getPreferredSize();
		preferredSize.width *= 4; // Double the width
		preferredSize.height *= 2; // Double the height
		relativeSlider.setPreferredSize(preferredSize); // Set the new preferred size
		relativeSlider.setMajorTickSpacing(20); // Set major tick spacing
		relativeSlider.setMinorTickSpacing(5); // Set minor tick spacing
		relativeSlider.setPaintTicks(true); // Display tick marks
		relativeSlider.setPaintLabels(true); // Display labels

		// Creating a JLabel to display the value of the JSlider
		JLabel label = new JLabel("0deg", SwingConstants.CENTER);


		// Creating a JPanel and adding the slider and label to it
		JPanel panel = new JPanel();
		panel.add(relativeSlider);
		panel.add(label);

		// Adding the panel to the frame
		frame.add(panel, BorderLayout.WEST);
		// Get the current preferred size, double it, and set it back
		Dimension commonpreferredSize = rudder.getPreferredSize();
		commonpreferredSize.width *= 2; // Double the width
		commonpreferredSize.height *= 3; // Double the height
		rudder.setPreferredSize(commonpreferredSize); // Set the new preferred size
		rudder.setMajorTickSpacing(5); // Set major tick spacing
		rudder.setMinorTickSpacing(1); // Set minor tick spacing
		rudder.setPaintTicks(true); // Display tick marks
		rudder.setPaintLabels(true); // Display labels

		// Creating a JLabel to display the value of the JSlider
		JLabel commonlabel = new JLabel("0deg", SwingConstants.CENTER);

		// Creating a JPanel and adding the slider and label to it
		JPanel commonpanel = new JPanel();
		commonpanel.add(rudder);
		commonpanel.add(commonlabel);
		frame.add(commonpanel, BorderLayout.SOUTH);
		// Creating the JSlider
		JSlider absoluteHeading = new JSlider(JSlider.VERTICAL, -180, 180, 0); // Arguments: orientation, min, max, initial value
		// Get the current preferred size, double it, and set it back
		Dimension rightpreferredSize = absoluteHeading.getPreferredSize();
		rightpreferredSize.width *= 4; // Double the width
		rightpreferredSize.height *= 2; // Double the height
		absoluteHeading.setPreferredSize(rightpreferredSize); // Set the new preferred size
		absoluteHeading.setMajorTickSpacing(20); // Set major tick spacing
		absoluteHeading.setMinorTickSpacing(5); // Set minor tick spacing
		absoluteHeading.setPaintTicks(true); // Display tick marks
		absoluteHeading.setPaintLabels(true); // Display labels

		// Creating a JLabel to display the value of the JSlider
		JLabel rightlabel = new JLabel("0deg", SwingConstants.CENTER);


		// Creating a JPanel and adding the slider and label to it
		JPanel rightpanel = new JPanel();
		rightpanel.add(absoluteHeading);
		rightpanel.add(rightlabel);

		// Adding the panel to the frame
		frame.add(rightpanel, BorderLayout.EAST);
		JButton setCourse = new JButton("Set Course");
		JLabel leftTitle = new JLabel("RELATIVE", SwingConstants.LEFT);
		JLabel middleTitle = new JLabel("RUDDER", SwingConstants.CENTER);
		JLabel rightTitle = new JLabel("ABSOLUTE", SwingConstants.RIGHT);
		JPanel toppanel = new JPanel();
		toppanel.add(setCourse);
		toppanel.add(leftTitle);
		toppanel.add(middleTitle);
		toppanel.add(rightTitle);

		// Adding the panel to the frame
		frame.add(toppanel, BorderLayout.NORTH);
		Color original = setCourse.getBackground();
		// Adding a change listener to the slider to update the label when the slider value changes
		relativeSlider.addChangeListener(e -> {
			resetButtons(original, setCourse);
			label.setText("" + ((JSlider) e.getSource()).getValue()+"deg");
			reference( ((JSlider) e.getSource()).getValue());
			Navigation.isCourseSet = false;
			compass.repaint();});
		// Adding a change listener to the slider to update the label when the slider value changes
		absoluteHeading.addChangeListener(e -> {
			resetButtons(original, setCourse);
			rightlabel.setText("" + ((JSlider) e.getSource()).getValue()+"deg");
			course( ((JSlider) e.getSource()).getValue());
			Navigation.isCourseSet = false;
			compass.repaint();
		});

		// Adding a change listener to the slider to update the label when the slider value changes
		rudder.addChangeListener(e -> {
			//resetButtons(original, setCourse);
			commonlabel.setText("" + ((JSlider) e.getSource()).getValue()+"deg");
			//Navigation.isCourseSet = false;
			compass.repaint();
		});
		setCourse.addActionListener(e -> {
			Navigation.isCourseSet = !Navigation.isCourseSet;
			if (!isCourseSet)
				resetButtons(original, setCourse);
			else {
				setCourse.setBackground(Color.GREEN);
				course( absoluteHeading.getValue());
				pidController = new PIDControllerAngle(0.1, 0.01, 0.05);
				pidController.setSetpoint(coursebearing); // Set desired setpoint
				previousControlOutput = null;
		}
			compass.repaint();
		});

		// Making the frame visible
		frame.setVisible(true);
		Navigation n = new Navigation();
		MyThread t = n.new MyThread();
		t.start();

	}


}
