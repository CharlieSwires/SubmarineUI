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

public class DepthKeeping {
	public enum INSTRUCTION {
		SURFACE, CRASH_DIVE, EMERGENCY_SURFACE, FREEZE, ALTER_DEPTH, DIVE
	}
	private static boolean isDiveAngleSet = false;
	private static boolean isAlterDepthAngleSet = false;
	private static boolean success =true;
	private static Integer actualAngle = 0;
	private static Integer requiredAngle = 0;
	private static String error;
	private static final String COMMS_LOST = "COMMUNICATION LOST";
	private static final String COMMS_OK = "COMMUNICATION OK";
	// Creating the JSlider
	private static JSlider diveDepth = new JSlider(JSlider.VERTICAL, -5000, 0, 0); // Arguments: orientation, min, max, initial value
	// Creating the JSlider
	private static JSlider diveAngle = new JSlider(JSlider.VERTICAL, -45, 45, 0); // Arguments: orientation, min, max, initial value
	private static JButton emergencySurface = new JButton("Emergency Surface");
	private static JButton crashDive = new JButton("Crash Dive");
	private static JButton surface = new JButton("Surface");
	private static JButton freeze = new JButton("Freeze");
	private static JButton dive = new JButton("Dive");
	private static JButton alterDepth = new JButton("Alter Depth");
	private static Color original = crashDive.getBackground();
	private static double controlOutput = 0.0;
	private static PIDControllerAngle pidController = new PIDControllerAngle(0.1, 0.01, 0.05);
	private static Integer previousControlOutput = null;
	private static Integer depth = 0;
	private static Integer fillOk;
	private static Integer rudderAngle;
	private static Integer requestedAngle;

	
	private static JPanel diveAngleGauge = new JPanel() {

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			int angle = (int)DepthKeeping.actualAngle;			
			g.setColor(Color.BLACK);
			g.drawArc(10, 120, 200, 200, 0, 360);
			if (angle == Constant.ERROR) {
				g.setColor(Color.RED);
				angle = -90;
			}
			else {
				g.setColor(Color.BLACK);
			}
			g.drawLine(10+100, 120+100, 
					10+100+(int)(100*Math.sin((-angle+90)/(180.0/Math.PI))), 
					120+100-(int)(100*Math.cos((-angle+90)/(180.0/Math.PI))));
			g.setColor(Color.BLACK);
			g.drawChars("+90".toCharArray(), 0, 3, 10+100+(int)(100*Math.sin((0)/(180.0/Math.PI))), 
					120+100-(int)(100*Math.cos((0)/(180.0/Math.PI))));
			g.drawChars("0".toCharArray(), 0, 1, 10+100+(int)(100*Math.sin((0+90)/(180.0/Math.PI))), 
					120+100-(int)(100*Math.cos((+90)/(180.0/Math.PI))));
			g.drawChars("-90".toCharArray(), 0, 3, 10+100+(int)(100*Math.sin((+180)/(180.0/Math.PI))), 
					120+100+10-(int)(100*Math.cos((+180)/(180.0/Math.PI))));
			depth = getDepth();
			if (depth == Constant.ERROR) {
				g.setColor(Color.RED);
				depth = 0;
			}
			else {
				g.setColor(Color.BLACK);
			}
			g.drawChars(("Actual Depth:"+depth+"mm    ").toCharArray(), 0, 20, -60+100+(int)(100*Math.sin((+180)/(180.0/Math.PI))), 
					120+100+30-(int)(100*Math.cos((+180)/(180.0/Math.PI))));
			if (!isDiveAngleSet && !isAlterDepthAngleSet && !success) {
				g.setColor(Color.RED);
				g.drawChars(("ERROR"+"     ").toCharArray(), 0, 10, -60+100+(int)(100*Math.sin((+180)/(180.0/Math.PI))), 
						140+100+30-(int)(100*Math.cos((+180)/(180.0/Math.PI))));

			} else {
				g.setColor(Color.GREEN);
				g.drawChars(("SUCCESS"+"     ").toCharArray(), 0, 10, -60+100+(int)(100*Math.sin((+180)/(180.0/Math.PI))), 
						140+100+30-(int)(100*Math.cos((+180)/(180.0/Math.PI))));
			}
			if (error.equals(COMMS_LOST)) {
				g.setColor(Color.RED);
				g.drawChars((error+"     ").toCharArray(), 0, 20, -60+100+(int)(100*Math.sin((+180)/(180.0/Math.PI))), 
						160+100+30-(int)(100*Math.cos((+180)/(180.0/Math.PI))));

			} else {
				g.setColor(Color.GREEN);
				g.drawChars((error+"        ").toCharArray(), 0, 20, -60+100+(int)(100*Math.sin((+180)/(180.0/Math.PI))), 
						160+100+30-(int)(100*Math.cos((+180)/(180.0/Math.PI))));
			}
			if (requestedAngle == Constant.ERROR)
				g.setColor(Color.RED);
			else 
				g.setColor(Color.BLACK);
			g.drawChars(("Plane Angle: "+ Math.round(controlOutput)+"degrees       ").toCharArray(), 0, 25, -60+100+(int)(100*Math.sin((+180)/(180.0/Math.PI))), 
					180+100+30-(int)(100*Math.cos((+180)/(180.0/Math.PI))));

			if ((isDiveAngleSet || isAlterDepthAngleSet) && success) {
				g.setColor(Color.GREEN);
			} else {
				g.setColor(Color.RED);
			}

			g.drawLine(10+100, 120+100, 
					10+100+(int)(100*Math.sin((-requiredAngle+90)/(180.0/Math.PI))), 
					120+100-(int)(100*Math.cos((-requiredAngle+90)/(180.0/Math.PI))));

			if (requestedAngle == Constant.ERROR)
				diveAngle.setForeground(Color.RED);
			else
				diveAngle.setForeground(Color.BLACK);


		}
	};

	private static void reference(int value) {
		requiredAngle = value;		
	}

	private static Boolean quickControls(DepthKeeping.INSTRUCTION action, JSlider diveAngle, JSlider diveDepth, Integer depth) {
		String url;
		Boolean success = true;
		switch (action) {
		case SURFACE:
			if (diveDepth.getValue() > depth && diveAngle.getValue() > 0) {
				url = new String("/dive/fill-tank/false");
				try {
					DepthKeeping.fillOk = GenericGet.getGeneric(url);
					error = COMMS_OK;
				} catch (RuntimeException e) { //need something other end, if COMMS_LOST this won't work.
					error = COMMS_LOST;
					e.printStackTrace();
				}
				newPid(diveAngle.getValue());
				isAlterDepthAngleSet = true;
				allFull();
				rudderZero();
			} else {
				isAlterDepthAngleSet = false;
				success =false;
			}
			break;
		case CRASH_DIVE:
			diveAngle.setValue(-45);
			diveDepth.setValue(-4000); //4m
			url = new String("/dive/fill-tank/true");
			try {
				DepthKeeping.fillOk = GenericGet.getGeneric(url);
				error = COMMS_OK;
			} catch (RuntimeException e) { //need something other end, if COMMS_LOST this won't work.
				error = COMMS_LOST;
				e.printStackTrace();
			}
			newPid(diveAngle.getValue());
			isAlterDepthAngleSet = true;
			allFull();
			rudderZero();
			break; 
		case EMERGENCY_SURFACE:
			diveAngle.setValue(45);
			diveDepth.setValue(0);
			url = new String("/dive/fill-tank/false");
			try {
				DepthKeeping.fillOk = GenericGet.getGeneric(url);
				error = COMMS_OK;
			} catch (RuntimeException e) { //need something other end, if COMMS_LOST this won't work.
				error = COMMS_LOST;
				e.printStackTrace();
			}
			newPid(diveAngle.getValue());
			isAlterDepthAngleSet = true;
			allFull();
			rudderZero();
			break; 
		case FREEZE:
			diveAngle.setValue(0);
			diveDepth.setValue(-depth); //current depth
			newPid(diveAngle.getValue());
			isAlterDepthAngleSet = true;
			allStop();
			rudderZero();
			break; 
		case ALTER_DEPTH:
			if ((diveDepth.getValue() < depth && diveAngle.getValue() < 0) ||
					(diveDepth.getValue() > depth && diveAngle.getValue() > 0)) {
				rudderZero();
				newPid(diveAngle.getValue());
				isAlterDepthAngleSet = true;
			} else {
				isAlterDepthAngleSet = false;
				success =false;
			}
			break; 
		case DIVE:
			if (-diveDepth.getValue() > -depth && diveAngle.getValue() < 0) {
				url = new String("/dive/fill-tank/true");
				try {
					DepthKeeping.fillOk = GenericGet.getGeneric(url);
					error = COMMS_OK;
				} catch (RuntimeException e) { //need something other end, if COMMS_LOST this won't work.
					error = COMMS_LOST;
					e.printStackTrace();
				}
				rudderZero();
				newPid(diveAngle.getValue());
				isDiveAngleSet = true;

			} else {
				isDiveAngleSet = false;
				success = false;
			}
			break; 
		default:
			throw new IllegalArgumentException("action out of range!!");
		}
		return success;
	}
	private static void newPid(int diveAngle) {
		reference(diveAngle);
		pidController = new PIDControllerAngle(0.1, 0.01, 0.05);
		previousControlOutput = null;
	}
	private static void allFull() {
		String url;
		url = new String("/engine/right/" + 100);
		try {
			GenericGet.getGeneric(url);
			error = COMMS_OK;
		} catch (RuntimeException e) { //need something other end, if COMMS_LOST this won't work.
			error = COMMS_LOST;
			e.printStackTrace();
		}
		url = new String("/engine/left/" + 100);
		try {
			GenericGet.getGeneric(url);
			error = COMMS_OK;
		} catch (RuntimeException e) { //need something other end, if COMMS_LOST this won't work.
			error = COMMS_LOST;
			e.printStackTrace();
		}
	}

	private static void rudderZero() {
		String url;
		url = new String("/navigation/rudder/"+0);
		try {
			DepthKeeping.rudderAngle = GenericGet.getGeneric(url);
			error = COMMS_OK;
		} catch (RuntimeException e) { //need something other end, if COMMS_LOST this won't work.
			error = COMMS_LOST;
			e.printStackTrace();
		}

	}

	private static void allStop() {
		String url;
		url = new String("/engine/right/" + 0);
		try {
			GenericGet.getGeneric(url);
			error = COMMS_OK;
		} catch (RuntimeException e) { //need something other end, if COMMS_LOST this won't work.
			error = COMMS_LOST;
			e.printStackTrace();
		}
		url = new String("/engine/left/" + 0);
		try {
			GenericGet.getGeneric(url);
			error = COMMS_OK;
		} catch (RuntimeException e) { //need something other end, if COMMS_LOST this won't work.
			error = COMMS_LOST;
			e.printStackTrace();
		}
	}

	private static int getDepth() {
		String url;
		Integer depth = 0;
		try {
			url = new String("/dive/depth");
			depth = GenericGet.getGeneric(url);
			error = COMMS_OK;
		} catch (RuntimeException e) { //need something other end, if COMMS_LOST this won't work.
			error = COMMS_LOST;
			resetButtons(original, crashDive, surface, emergencySurface,freeze,dive,alterDepth);
			success = quickControls(INSTRUCTION.EMERGENCY_SURFACE, diveAngle, diveDepth, depth);
			emergencySurface.setBackground(Color.GREEN);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
			}
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
			while (true) {
				String url;
				depth = getDepth();
				url = new String("/dive/dive-angle");
				try {
					actualAngle = GenericGet.getGeneric(url);
					error = COMMS_OK;
				} catch (RuntimeException e) {
					e.printStackTrace();
					error = COMMS_LOST;
					continue;
				}
				if ((isDiveAngleSet || isAlterDepthAngleSet) && success) {
					if (requiredAngle < 0 && -depth >= -diveDepth.getValue()) {
						requiredAngle = 0;
					} else if (requiredAngle > 0 && depth >= diveDepth.getValue()) {
						requiredAngle = 0;
					}
					pidController.setSetpoint(requiredAngle); // Set desired setpoint

					double tempControlO = pidController.compute(actualAngle);
					tempControlO = tempControlO > 45.0 ? 45.0 : tempControlO;
					tempControlO = tempControlO < -45.0 ? -45.0 : tempControlO;
					controlOutput = tempControlO;
					if (previousControlOutput != null && Math.round(controlOutput) != (Integer)previousControlOutput) {
						url = new String("/dive/front/"+((int)Math.round(controlOutput)));
						DepthKeeping.requestedAngle = GenericGet.getGeneric(url);
						url = new String("/dive/back/"+((int)Math.round(-controlOutput)));
						DepthKeeping.requestedAngle = GenericGet.getGeneric(url);
					}
					previousControlOutput = (int)Math.round(controlOutput);
				}
				diveAngleGauge.repaint();

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
		JFrame frame = new JFrame("Depth Keeping");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 550);
		diveAngleGauge.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Add some padding
		diveAngleGauge.setSize(300, 300); // Setting size here won't have any effect in this layout

		frame.add(diveAngleGauge, BorderLayout.CENTER);

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
		JLabel leftTitle = new JLabel("REQUIRED ANGLE", SwingConstants.LEFT);
		JLabel middleTitle = new JLabel("DEPTH", SwingConstants.CENTER);
		JButton offsetButton = new JButton("Zero");
		JPanel toppanel = new JPanel();
		toppanel.add(crashDive);
		toppanel.add(leftTitle);
		toppanel.add(middleTitle);
		toppanel.add(offsetButton);
		toppanel.add(surface);

		// Adding the panel to the frame
		frame.add(toppanel, BorderLayout.NORTH);
		JPanel bottompanel = new JPanel();
		bottompanel.add(dive);
		bottompanel.add(alterDepth);
		bottompanel.add(emergencySurface);
		bottompanel.add(freeze);
		// Adding the panel to the frame
		frame.add(bottompanel, BorderLayout.SOUTH);
		// Adding a change listener to the slider to update the label when the slider value changes
		diveAngle.addChangeListener(e -> {
			resetButtons(original, crashDive, surface, emergencySurface,freeze,dive,alterDepth);
			label.setText("" + ((JSlider) e.getSource()).getValue()+"degrees");
			reference( ((JSlider) e.getSource()).getValue());
			diveAngleGauge.repaint();});
		// Adding a change listener to the slider to update the label when the slider value changes
		diveDepth.addChangeListener(e -> {
			resetButtons(original, crashDive, surface, emergencySurface,freeze,dive,alterDepth);
			rightlabel.setText("" + (-((JSlider) e.getSource()).getValue())+"mm");
			diveAngleGauge.repaint();});
		crashDive.addActionListener(e -> {
			resetButtons(original, crashDive, surface, emergencySurface,freeze,dive,alterDepth);
			success = quickControls(INSTRUCTION.CRASH_DIVE, diveAngle, diveDepth, depth);
			crashDive.setBackground(Color.GREEN);});
		surface.addActionListener(e -> {
			resetButtons(original, crashDive, surface, emergencySurface,freeze,dive,alterDepth);
			success = quickControls(INSTRUCTION.SURFACE, diveAngle, diveDepth, depth);
			surface.setBackground(Color.GREEN);});
		emergencySurface.addActionListener(e -> {
			resetButtons(original, crashDive, surface, emergencySurface,freeze,dive,alterDepth);
			success = quickControls(INSTRUCTION.EMERGENCY_SURFACE, diveAngle, diveDepth, depth);
			emergencySurface.setBackground(Color.GREEN);});
		freeze.addActionListener(e -> {
			resetButtons(original, crashDive, surface, emergencySurface,freeze,dive,alterDepth);
			success = quickControls(INSTRUCTION.FREEZE, diveAngle, diveDepth, depth);
			freeze.setBackground(Color.GREEN);});
		dive.addActionListener(e -> {
			isDiveAngleSet = !isDiveAngleSet;
			if (!isDiveAngleSet) {
				dive.setBackground(original);
				success = true;
			} else {
				resetButtons(original, crashDive, surface, emergencySurface,freeze,dive,alterDepth);
				success = quickControls(INSTRUCTION.DIVE, diveAngle, diveDepth, depth);
				dive.setBackground(Color.GREEN);
			}
			diveAngleGauge.repaint();
		});
		offsetButton.addActionListener(e -> {
			String url  = ("/dive/zero");
			try {
				GenericGet.getGeneric(url);
				error = COMMS_OK;
			} catch (RuntimeException e1) { //need something other end, if COMMS_LOST this won't work.
				error = COMMS_LOST;
				e1.printStackTrace();
			}
		});

		alterDepth.addActionListener(e -> {
			isAlterDepthAngleSet = !isAlterDepthAngleSet;
			if (!isAlterDepthAngleSet) {
				alterDepth.setBackground(original);
				success = true;
			} else {
				resetButtons(original, crashDive, surface, emergencySurface,freeze,dive,alterDepth);
				success = quickControls(INSTRUCTION.ALTER_DEPTH, diveAngle, diveDepth, depth);
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
