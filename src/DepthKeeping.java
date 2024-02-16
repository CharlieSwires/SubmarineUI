import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.net.MalformedURLException;
import java.net.URL;

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

public class DepthKeeping {
	public enum EMERGENCY {
		SCUTTLE, CRASH_DIVE, EMERGENCY_SURFACE, FREEZE, ALTER_DEPTH, DIVE
	}
	private static boolean isDiveAngleSet = false;
	private static boolean isAlterDepthAngleSet = false;
	private static boolean success =true;
	private static Integer actualAngle = 0;
	private static Integer requiredAngle = 0;
	// Creating the JSlider
	private static JSlider diveDepth = new JSlider(JSlider.VERTICAL, -8000, 0, 0); // Arguments: orientation, min, max, initial value
	private static JPanel diveAngleGauge = new JPanel() {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			int angle = DepthKeeping.actualAngle;
			g.drawArc(10, 120, 200, 200, 0, 360);
			g.drawLine(10+100, 120+100, 
					10+100+(int)(100*Math.sin((-angle+90)/(180.0/Math.PI))), 
					120+100-(int)(100*Math.cos((-angle+90)/(180.0/Math.PI))));
			g.drawChars("-90".toCharArray(), 0, 3, 10+100+(int)(100*Math.sin((0)/(180.0/Math.PI))), 
					120+100-(int)(100*Math.cos((0)/(180.0/Math.PI))));
			g.drawChars("0".toCharArray(), 0, 1, 10+100+(int)(100*Math.sin((0+90)/(180.0/Math.PI))), 
					120+100-(int)(100*Math.cos((+90)/(180.0/Math.PI))));
			g.drawChars("+90".toCharArray(), 0, 3, 10+100+(int)(100*Math.sin((+180)/(180.0/Math.PI))), 
					120+100+10-(int)(100*Math.cos((+180)/(180.0/Math.PI))));
			g.drawChars(("Actual Depth:"+getDepth()+"mm    ").toCharArray(), 0, 20, -60+100+(int)(100*Math.sin((+180)/(180.0/Math.PI))), 
					120+100+30-(int)(100*Math.cos((+180)/(180.0/Math.PI))));
			if (success == false) {
				g.setColor(Color.RED);
				g.drawChars(("ERROR"+"     ").toCharArray(), 0, 10, -60+100+(int)(100*Math.sin((+180)/(180.0/Math.PI))), 
						140+100+30-(int)(100*Math.cos((+180)/(180.0/Math.PI))));
				
			} else {
				g.setColor(Color.GREEN);
				g.drawChars(("SUCCESS"+"     ").toCharArray(), 0, 10, -60+100+(int)(100*Math.sin((+180)/(180.0/Math.PI))), 
						140+100+30-(int)(100*Math.cos((+180)/(180.0/Math.PI))));
			}
			if (!isDiveAngleSet) {
				g.setColor(Color.RED);
			} else {
				g.setColor(Color.GREEN);
			}

			g.drawLine(10+100, 120+100, 
					10+100+(int)(100*Math.sin((-requiredAngle+90)/(180.0/Math.PI))), 
					120+100-(int)(100*Math.cos((-requiredAngle+90)/(180.0/Math.PI))));

		}
	};

	private static void reference(int value) {
		requiredAngle = value;		
	}

	private static Boolean quickControls(DepthKeeping.EMERGENCY action, JSlider diveAngle, JSlider diveDepth) {
		URL url;
		Boolean success = true;
		switch (action) {
		//SCUTTLE, CRASH_DIVE, EMERGENCY_SURFACE
		case SCUTTLE:
			diveAngle.setValue(-45);
			diveDepth.setValue(-100000); //100m
			try {
				url = new URL(Constant.PI_HOME + Constant.PORT + "/fill-tank/true");
				GenericGet.getGeneric(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			allFull();
			rudderZero();
			break;
		case CRASH_DIVE:
			diveAngle.setValue(-45);
			diveDepth.setValue(-8000); //8m
			try {
				url = new URL(Constant.PI_HOME + Constant.PORT + "/fill-tank/true");
				GenericGet.getGeneric(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			allFull();
			rudderZero();
			break; 
		case EMERGENCY_SURFACE:
			diveAngle.setValue(45);
			diveDepth.setValue(0);
			try {
				url = new URL(Constant.PI_HOME + Constant.PORT + "/fill-tank/false");
				GenericGet.getGeneric(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			allFull();
			rudderZero();
			break; 
		case FREEZE:
			diveAngle.setValue(0);
			diveDepth.setValue(-getDepth()); //current depth
			allStop();
			rudderZero();
			break; 
		case ALTER_DEPTH:
			if ((diveDepth.getValue() < -getDepth() && diveAngle.getValue() > 0) ||
					(diveDepth.getValue() > -getDepth() && diveAngle.getValue() < 0)) {
				rudderZero();				
			} else {
				success = false;
			}
			break; 
		case DIVE:
			if (diveDepth.getValue() > -getDepth() && diveAngle.getValue() < 0) {
				try {
					url = new URL(Constant.PI_HOME + Constant.PORT + "/fill-tank/true");
					GenericGet.getGeneric(url);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				rudderZero();
			} else {
				success = false;
			}
			break; 
		default:
			throw new IllegalArgumentException("action out of range!!");
		}
		return success;
	}

	private static void allFull() {
		URL url;
		try {
			url = new URL(Constant.PI_HOME + Constant.PORT + "/engine/right/" + 100);
			GenericGet.getGeneric(url);
			url = new URL(Constant.PI_HOME + Constant.PORT + "/engine/left/" + 100);
			GenericGet.getGeneric(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private static void rudderZero() {
		URL url;
		try {
			url = new URL(Constant.PI_HOME + Constant.PORT + "/navigation/rudder/"+0);
			GenericGet.getGeneric(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void allStop() {
		URL url;
		try {
			url = new URL(Constant.PI_HOME + Constant.PORT + "/engine/right/" + 0);
			GenericGet.getGeneric(url);
			url = new URL(Constant.PI_HOME + Constant.PORT + "/engine/left/" + 0);
			GenericGet.getGeneric(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private static int getDepth() {
		URL url;
		Integer depth = 0;
		try {
			url = new URL(Constant.PI_HOME + Constant.PORT + "/depth");
			depth = GenericGet.getGeneric(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return depth;
	}

	private static void resetButtons(Color original, JButton emergencyLeft, JButton emergencyRight,
			JButton emergencyReverse, JButton allStop, JButton dive, JButton alterDepth) {
		emergencyLeft.setBackground(original);
		emergencyRight.setBackground(original);
		emergencyReverse.setBackground(original);
		allStop.setBackground(original);
		dive.setBackground(original);
		alterDepth.setBackground(original);
		isDiveAngleSet = false;
		isAlterDepthAngleSet = false;

	}
	private class MyThread extends Thread {

		@Override
		public void run() {
			PIDControllerAngle pidController = new PIDControllerAngle(0.1, 0.01, 0.05);
			pidController.setSetpoint(requiredAngle); // Set desired setpoint
			Integer previousControlOutput = null;
			while (true) {
				URL url;
				try {
					url = new URL(Constant.PI_HOME + Constant.PORT + "/depth");
					Integer depth = GenericGet.getGeneric(url);
					if (requiredAngle < 0 && depth >= -diveDepth.getValue()) {
						requiredAngle = 0;
						pidController.setSetpoint(requiredAngle); // Set desired setpoint
					} else if (requiredAngle > 0 && depth <= -diveDepth.getValue()) {
						requiredAngle = 0;
						pidController.setSetpoint(requiredAngle); // Set desired setpoint	
					}
					url = new URL(Constant.PI_HOME + Constant.PORT + "/dive/dive-angle");
					actualAngle = GenericGet.getGeneric(url);
					double controlOutput = pidController.compute(actualAngle);
					controlOutput = controlOutput > 45.0 ? 45.0 : controlOutput;
					controlOutput = controlOutput < -45.0 ? -45.0 : controlOutput;
					if (previousControlOutput != null && Math.round(controlOutput) != (Integer)previousControlOutput) {
						url = new URL(Constant.PI_HOME + Constant.PORT + "/dive/front/"+((int)Math.round(controlOutput)));
						url = new URL(Constant.PI_HOME + Constant.PORT + "/dive/back/"+((int)Math.round(-controlOutput)));
						Integer result = GenericGet.getGeneric(url);
					}
					previousControlOutput = (int)Math.round(controlOutput);
					diveAngleGauge.repaint();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				try {
					MyThread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	public static void main(String[] args) {
		// Creating the JFrame for the application
		JFrame frame = new JFrame("Depth Keeping");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 550);
		diveAngleGauge.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Add some padding
		diveAngleGauge.setSize(300, 300); // Setting size here won't have any effect in this layout

		frame.add(diveAngleGauge, BorderLayout.CENTER);

		// Creating the JSlider
		JSlider diveAngle = new JSlider(JSlider.VERTICAL, -45, 45, 0); // Arguments: orientation, min, max, initial value
		// Get the current preferred size, double it, and set it back
		Dimension preferredSize = diveAngle.getPreferredSize();
		preferredSize.width *= 4; // Double the width
		preferredSize.height *= 2; // Double the height
		diveAngle.setPreferredSize(preferredSize); // Set the new preferred size
		diveAngle.setMajorTickSpacing(5); // Set major tick spacing
		diveAngle.setMinorTickSpacing(1); // Set minor tick spacing
		diveAngle.setPaintTicks(true); // Display tick marks
		diveAngle.setPaintLabels(true); // Display labels

		// Creating a JLabel to display the value of the JSlider
		JLabel label = new JLabel("0degrees", SwingConstants.CENTER);


		// Creating a JPanel and adding the slider and label to it
		JPanel panel = new JPanel();
		panel.add(diveAngle);
		panel.add(label);

		// Adding the panel to the frame
		frame.add(panel, BorderLayout.WEST);
		// Get the current preferred size, double it, and set it back
		Dimension rightpreferredSize = diveDepth.getPreferredSize();
		rightpreferredSize.width *= 4; // Double the width
		rightpreferredSize.height *= 2; // Double the height
		diveDepth.setPreferredSize(rightpreferredSize); // Set the new preferred size
		diveDepth.setMajorTickSpacing(1000); // Set major tick spacing
		diveDepth.setMinorTickSpacing(100); // Set minor tick spacing
		diveDepth.setPaintTicks(true); // Display tick marks
		diveDepth.setPaintLabels(true); // Display labels

		// Creating a JLabel to display the value of the JSlider
		JLabel rightlabel = new JLabel("0mm", SwingConstants.CENTER);


		// Creating a JPanel and adding the slider and label to it
		JPanel rightpanel = new JPanel();
		rightpanel.add(diveDepth);
		rightpanel.add(rightlabel);

		// Adding the panel to the frame
		frame.add(rightpanel, BorderLayout.EAST);
		JButton crashDive = new JButton("Crash Dive");
		JLabel leftTitle = new JLabel("REQUIRED ANGLE", SwingConstants.LEFT);
		JLabel middleTitle = new JLabel("ACTUAL ANGLE", SwingConstants.CENTER);
		JLabel rightTitle = new JLabel("DEPTH", SwingConstants.RIGHT);
		JButton scuttle = new JButton("Scuttle");
		JPanel toppanel = new JPanel();
		toppanel.add(crashDive);
		toppanel.add(leftTitle);
		toppanel.add(middleTitle);
		toppanel.add(rightTitle);
		toppanel.add(scuttle);

		// Adding the panel to the frame
		frame.add(toppanel, BorderLayout.NORTH);
		JButton emergencySurface = new JButton("Emergency Surface");
		JButton freeze = new JButton("Freeze");
		JButton dive = new JButton("Dive");
		JButton alterDepth = new JButton("Alter Depth");
		JPanel bottompanel = new JPanel();
		bottompanel.add(dive);
		bottompanel.add(alterDepth);
		bottompanel.add(emergencySurface);
		bottompanel.add(freeze);
		// Adding the panel to the frame
		frame.add(bottompanel, BorderLayout.SOUTH);
		Color original = crashDive.getBackground();
		// Adding a change listener to the slider to update the label when the slider value changes
		diveAngle.addChangeListener(e -> {
			resetButtons(original, crashDive, scuttle, emergencySurface,freeze,dive,alterDepth);
			label.setText("" + ((JSlider) e.getSource()).getValue()+"degrees");
			reference( ((JSlider) e.getSource()).getValue());
			diveAngleGauge.repaint();});
		// Adding a change listener to the slider to update the label when the slider value changes
		diveDepth.addChangeListener(e -> {
			resetButtons(original, crashDive, scuttle, emergencySurface,freeze,dive,alterDepth);
			rightlabel.setText("" + (-((JSlider) e.getSource()).getValue())+"mm");
			diveAngleGauge.repaint();});
		crashDive.addActionListener(e -> {
			resetButtons(original, crashDive, scuttle, emergencySurface,freeze,dive,alterDepth);
			success = quickControls(EMERGENCY.CRASH_DIVE, diveAngle, diveDepth);
			crashDive.setBackground(Color.GREEN);});
		scuttle.addActionListener(e -> {
			resetButtons(original, crashDive, scuttle, emergencySurface,freeze,dive,alterDepth);
			success = quickControls(EMERGENCY.SCUTTLE, diveAngle, diveDepth);
			scuttle.setBackground(Color.GREEN);});
		emergencySurface.addActionListener(e -> {
			resetButtons(original, crashDive, scuttle, emergencySurface,freeze,dive,alterDepth);
			success = quickControls(EMERGENCY.EMERGENCY_SURFACE, diveAngle, diveDepth);
			emergencySurface.setBackground(Color.GREEN);});
		freeze.addActionListener(e -> {
			resetButtons(original, crashDive, scuttle, emergencySurface,freeze,dive,alterDepth);
			success = quickControls(EMERGENCY.FREEZE, diveAngle, diveDepth);
			freeze.setBackground(Color.GREEN);});
		dive.addActionListener(e -> {
			isDiveAngleSet = !isDiveAngleSet;
			if (!isDiveAngleSet) {
				dive.setBackground(original);
				success = true;
			} else {
				success = quickControls(EMERGENCY.DIVE, diveAngle, diveDepth);
				dive.setBackground(Color.GREEN);
			}
			diveAngleGauge.repaint();
		});
		alterDepth.addActionListener(e -> {
			isAlterDepthAngleSet = !isAlterDepthAngleSet;
			if (!isAlterDepthAngleSet) {
				alterDepth.setBackground(original);
				success = true;
			} else {
				success = quickControls(EMERGENCY.ALTER_DEPTH, diveAngle, diveDepth);
				alterDepth.setBackground(Color.GREEN);
			}
			diveAngleGauge.repaint();
		});

		// Making the frame visible
		frame.setVisible(true);
		DepthKeeping dk = new DepthKeeping();
		MyThread t = dk.new MyThread();
		t.start();

	}

}
