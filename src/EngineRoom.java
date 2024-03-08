import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import GenericGet.GenericGet;

public class EngineRoom {
	public enum EMERGENCY {
		ALL_STOP, FULL_LEFT, FULL_RIGHT, ALL_FULL, ALL_BACK
	}
	// Creating the JFrame for the application
	private static JFrame frame = new JFrame("Engine Room");
	// Creating the JSlider
	private static JSlider slider = new JSlider(JSlider.VERTICAL, -100, 100, 0); // Arguments: orientation, min, max, initial value
	// Creating the JSlider
	private static JSlider rightslider = new JSlider(JSlider.VERTICAL, -100, 100, 0); // Arguments: orientation, min, max, initial value
	private static JLabel leftTitle = new JLabel("LEFT", SwingConstants.LEFT);
	private static JLabel middleTitle = new JLabel("COMMON", SwingConstants.CENTER);
	private static JLabel rightTitle = new JLabel("RIGHT", SwingConstants.RIGHT);
	private static Color originalColour = rightTitle.getForeground();

	private static void quickControls(EngineRoom.EMERGENCY action, JSlider slider, JSlider rightslider) {
		switch (action) {
		case ALL_STOP:
			slider.setValue(0);
			rightslider.setValue(0);
			break;
		case FULL_LEFT:
			slider.setValue(-100);
			rightslider.setValue(100);
			break; 
		case FULL_RIGHT:
			slider.setValue(100);
			rightslider.setValue(-100);
			break; 
		case ALL_FULL:
			slider.setValue(100);
			rightslider.setValue(100);
			break; 
		case ALL_BACK:
			slider.setValue(-100);
			rightslider.setValue(-100);
			break;
		default:
			throw new IllegalArgumentException("action out of range!!");
		}
	}

	private static void resetButtons(Color original, JButton emergencyLeft, JButton emergencyRight,
			JButton emergencyReverse, JButton allStop, JButton allFull) {
		emergencyLeft.setBackground(original);
		emergencyRight.setBackground(original);
		emergencyReverse.setBackground(original);
		allStop.setBackground(original);
		allFull.setBackground(original);
	}
	private class MyThreadTemperature extends Thread {
		@Override
		public void run() {
			while (true) {
				String url;
				url = new String("/engine/cpu-temp");
				try {
					Integer result = GenericGet.getGeneric(url);
					frame.setTitle("Engine Room temp=" + (result/10.0) + "Celcius");
					leftTitle.setForeground(originalColour);
					middleTitle.setForeground(originalColour);
					rightTitle.setForeground(originalColour);
					leftTitle.setText("LEFT");
					middleTitle.setText("COMMON");
					rightTitle.setText("RIGHT");
				} catch (RuntimeException e) {
					leftTitle.setForeground(Color.RED);
					middleTitle.setForeground(Color.RED);
					rightTitle.setForeground(Color.RED);
					leftTitle.setText("NO");
					middleTitle.setText("COMMS");
					rightTitle.setText("FOUND");
					System.out.println(e);
				}
				// 1Hz
				try {
					MyThread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}
	private class MyThread extends Thread {
		Integer newSlider = slider.getValue();
		Integer previousSlider = null;
		Integer newRightSlider = rightslider.getValue();
		Integer previousRightSlider = null;
		@Override
		public void run() {
			while (true) {
				//only when changed
				if (previousSlider != null && !previousSlider.equals(newSlider)) {
					String url;
					url = new String("/engine/left/" + newSlider);
					try {
						Integer result = GenericGet.getGeneric(url);
						leftTitle.setForeground(originalColour);
						middleTitle.setForeground(originalColour);
						rightTitle.setForeground(originalColour);
						leftTitle.setText("LEFT");
						middleTitle.setText("COMMON");
						rightTitle.setText("RIGHT");
					} catch (RuntimeException e) {
						leftTitle.setForeground(Color.RED);
						middleTitle.setForeground(Color.RED);
						rightTitle.setForeground(Color.RED);
						leftTitle.setText("NO");
						middleTitle.setText("COMMS");
						rightTitle.setText("FOUND");
						System.out.println(e);
					}
				}
				previousSlider = newSlider;

				newSlider = slider.getValue();
				if (previousRightSlider != null && !previousRightSlider.equals(newRightSlider)) {
					String url;
					url = new String("/engine/right/" + newRightSlider);
					try {
						Integer result = GenericGet.getGeneric(url);
						leftTitle.setForeground(originalColour);
						middleTitle.setForeground(originalColour);
						rightTitle.setForeground(originalColour);
						leftTitle.setText("LEFT");
						middleTitle.setText("COMMON");
						rightTitle.setText("RIGHT");
					} catch (RuntimeException e) {
						leftTitle.setForeground(Color.RED);
						middleTitle.setForeground(Color.RED);
						rightTitle.setForeground(Color.RED);
						leftTitle.setText("NO");
						middleTitle.setText("COMMS");
						rightTitle.setText("FOUND");
						System.out.println(e);
					}
				}
				previousRightSlider = newRightSlider;

				newRightSlider = rightslider.getValue();
				//10Hz
				try {
					MyThread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	public static void main(String[] args) {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 550);

		// Get the current preferred size, double it, and set it back
		Dimension preferredSize = slider.getPreferredSize();
		preferredSize.width *= 4; // Double the width
		preferredSize.height *= 2; // Double the height
		slider.setPreferredSize(preferredSize); // Set the new preferred size
		slider.setMajorTickSpacing(25); // Set major tick spacing
		slider.setMinorTickSpacing(1); // Set minor tick spacing
		slider.setPaintTicks(true); // Display tick marks
		slider.setPaintLabels(true); // Display labels

		// Creating a JLabel to display the value of the JSlider
		JLabel label = new JLabel("0%", SwingConstants.CENTER);


		// Creating a JPanel and adding the slider and label to it
		JPanel panel = new JPanel();
		panel.add(slider);
		panel.add(label);

		// Adding the panel to the frame
		frame.add(panel, BorderLayout.WEST);
		// Creating the JSlider
		JSlider commonslider = new JSlider(JSlider.VERTICAL, -100, 100, 0); // Arguments: orientation, min, max, initial value
		// Get the current preferred size, double it, and set it back
		Dimension commonpreferredSize = commonslider.getPreferredSize();
		commonpreferredSize.width *= 4; // Double the width
		commonpreferredSize.height *= 2; // Double the height
		commonslider.setPreferredSize(commonpreferredSize); // Set the new preferred size
		commonslider.setMajorTickSpacing(25); // Set major tick spacing
		commonslider.setMinorTickSpacing(1); // Set minor tick spacing
		commonslider.setPaintTicks(true); // Display tick marks
		commonslider.setPaintLabels(true); // Display labels

		// Creating a JLabel to display the value of the JSlider
		JLabel commonlabel = new JLabel("0%", SwingConstants.CENTER);

		// Creating a JPanel and adding the slider and label to it
		JPanel commonpanel = new JPanel();
		commonpanel.add(commonslider);
		commonpanel.add(commonlabel);
		frame.add(commonpanel, BorderLayout.CENTER);
		// Get the current preferred size, double it, and set it back
		Dimension rightpreferredSize = rightslider.getPreferredSize();
		rightpreferredSize.width *= 4; // Double the width
		rightpreferredSize.height *= 2; // Double the height
		rightslider.setPreferredSize(rightpreferredSize); // Set the new preferred size
		rightslider.setMajorTickSpacing(25); // Set major tick spacing
		rightslider.setMinorTickSpacing(1); // Set minor tick spacing
		rightslider.setPaintTicks(true); // Display tick marks
		rightslider.setPaintLabels(true); // Display labels

		// Creating a JLabel to display the value of the JSlider
		JLabel rightlabel = new JLabel("0%", SwingConstants.CENTER);


		// Creating a JPanel and adding the slider and label to it
		JPanel rightpanel = new JPanel();
		rightpanel.add(rightslider);
		rightpanel.add(rightlabel);

		// Adding the panel to the frame
		frame.add(rightpanel, BorderLayout.EAST);
		JButton emergencyLeft = new JButton("Emergency Left");
		JButton emergencyRight = new JButton("Emergency Right");
		JPanel toppanel = new JPanel();
		toppanel.add(emergencyLeft);
		toppanel.add(leftTitle);
		toppanel.add(middleTitle);
		toppanel.add(rightTitle);
		toppanel.add(emergencyRight);

		// Adding the panel to the frame
		frame.add(toppanel, BorderLayout.NORTH);
		JButton emergencyReverse = new JButton("Emergency Reverse");
		JButton allFull = new JButton("All Full");
		JButton allStop = new JButton("All Stop");
		JPanel bottompanel = new JPanel();
		bottompanel.add(emergencyReverse);
		bottompanel.add(allStop);
		bottompanel.add(allFull);
		// Adding the panel to the frame
		frame.add(bottompanel, BorderLayout.SOUTH);
		Color original = emergencyLeft.getBackground();
		// Adding a change listener to the slider to update the label when the slider value changes
		slider.addChangeListener(e -> {
			resetButtons(original, emergencyLeft, emergencyRight, emergencyReverse,allStop,allFull);
			label.setText("" + ((JSlider) e.getSource()).getValue()+"%");});
		// Adding a change listener to the slider to update the label when the slider value changes
		rightslider.addChangeListener(e -> {
			resetButtons(original, emergencyLeft, emergencyRight, emergencyReverse,allStop,allFull);
			rightlabel.setText("" + ((JSlider) e.getSource()).getValue()+"%");});
		// Adding a change listener to the slider to update the label when the slider value changes
		commonslider.addChangeListener(e -> {
			resetButtons(original, emergencyLeft, emergencyRight, emergencyReverse,allStop,allFull);
			commonlabel.setText("" + ((JSlider) e.getSource()).getValue()+"%");
			slider.setValue((int)((JSlider) e.getSource()).getValue());
			rightslider.setValue((int)((JSlider) e.getSource()).getValue());});
		emergencyLeft.addActionListener(e -> {
			resetButtons(original, emergencyLeft, emergencyRight, emergencyReverse,allStop,allFull);
			quickControls(EMERGENCY.FULL_LEFT, slider, rightslider);
			emergencyLeft.setBackground(Color.GREEN);});
		emergencyRight.addActionListener(e -> {
			resetButtons(original, emergencyLeft, emergencyRight, emergencyReverse,allStop,allFull);
			quickControls(EMERGENCY.FULL_RIGHT, slider, rightslider);
			emergencyRight.setBackground(Color.GREEN);});
		emergencyReverse.addActionListener(e -> {
			resetButtons(original, emergencyLeft, emergencyRight, emergencyReverse,allStop,allFull);
			quickControls(EMERGENCY.ALL_BACK, slider, rightslider);
			emergencyReverse.setBackground(Color.GREEN);});
		allStop.addActionListener(e -> {
			resetButtons(original, emergencyLeft, emergencyRight, emergencyReverse,allStop,allFull);
			quickControls(EMERGENCY.ALL_STOP, slider, rightslider);
			allStop.setBackground(Color.GREEN);});
		allFull.addActionListener(e -> {
			resetButtons(original, emergencyLeft, emergencyRight, emergencyReverse,allStop,allFull);
			quickControls(EMERGENCY.ALL_FULL, slider, rightslider);
			allFull.setBackground(Color.GREEN);});

		// Making the frame visible
		frame.setVisible(true);
		EngineRoom er = new EngineRoom();
		MyThread t = er.new MyThread();
		MyThreadTemperature t2 = er.new MyThreadTemperature();
		t.start();
		t2.start();
	}

}
