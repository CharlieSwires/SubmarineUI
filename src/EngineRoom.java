import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import Const.Constant;

public class EngineRoom {
	public enum EMERGENCY {
		ALL_STOP, FULL_LEFT, FULL_RIGHT, ALL_FULL, ALL_BACK, CALIBRATION, CALIBRATION_END
	}
	private enum COMMS_STATUS {
		CPU_GOOD, CPU_DOWN, CPU_COMMS_DOWN, LEFT_GOOD, LEFT_DOWN, LEFT_COMMS_DOWN,
		RIGHT_GOOD, RIGHT_DOWN, RIGHT_COMMS_DOWN, POWER_COMMS_DOWN, POWER_GOOD, POWER_DOWN, PUMPS_DOWN, PUMPS_UP, PUMPS_COMMS_DOWN
	}
	public static EMERGENCY action = EMERGENCY.CALIBRATION;
	// Creating the JFrame for the application
	public static JFrame frame = new JFrame("Engine Room");
	// Creating the JSlider
	private static JSlider slider = new JSlider(JSlider.VERTICAL, -100, 100, 0); // Arguments: orientation, min, max, initial value
	// Creating the JSlider
	private static JSlider rightslider = new JSlider(JSlider.VERTICAL, -100, 100, 0); // Arguments: orientation, min, max, initial value
	private static JSlider commonslider = new JSlider(JSlider.VERTICAL, -100, 100, 0); // Arguments: orientation, min, max, initial value	private static JLabel leftTitle = new JLabel("LEFT", SwingConstants.LEFT);
	private static JLabel leftTitle = new JLabel("LEFT", SwingConstants.LEFT);
	private static JLabel middleTitle = new JLabel("COMMON", SwingConstants.CENTER);
	private static JLabel rightTitle = new JLabel("RIGHT", SwingConstants.RIGHT);
	private static Color originalColour = rightTitle.getForeground();
	private static JButton powerButton = new JButton("Power OFF");
	private static int engineLeft;
	private static int engineRight;
	private static int cpuTemp;
	private static int power = 0;
	private static Long calibrationStart = null;
	private static Integer pumps;

	private static void updateStatus(COMMS_STATUS stat) {
		switch(stat) {
		case CPU_DOWN:
			frame.setForeground(Color.RED);
			leftTitle.setForeground(Color.RED);
			middleTitle.setForeground(Color.RED);
			rightTitle.setForeground(Color.RED);
			leftTitle.setText("ERROR");
			middleTitle.setText("READING");
			rightTitle.setText("VALUE");
			break;
		case CPU_GOOD:
			frame.setForeground(Color.BLACK);
			leftTitle.setForeground(originalColour);
			middleTitle.setForeground(originalColour);
			rightTitle.setForeground(originalColour);
			leftTitle.setText("LEFT");
			middleTitle.setText("COMMON");
			rightTitle.setText("RIGHT");
			break;
		case CPU_COMMS_DOWN:
			leftTitle.setForeground(Color.RED);
			middleTitle.setForeground(Color.RED);
			rightTitle.setForeground(Color.RED);
			leftTitle.setText("NO");
			middleTitle.setText("COMMS");
			rightTitle.setText("FOUND");
			break;
		case RIGHT_DOWN:
			leftTitle.setForeground(Color.RED);
			middleTitle.setForeground(Color.RED);
			rightTitle.setForeground(Color.RED);
			leftTitle.setText("ERROR");
			middleTitle.setText("IN");
			rightTitle.setText("SENSOR");
			break;
		case RIGHT_GOOD:
			leftTitle.setForeground(originalColour);
			middleTitle.setForeground(originalColour);
			rightTitle.setForeground(originalColour);
			leftTitle.setText("LEFT");
			middleTitle.setText("COMMON");
			rightTitle.setText("RIGHT");
			break;
		case RIGHT_COMMS_DOWN:
			leftTitle.setForeground(Color.RED);
			middleTitle.setForeground(Color.RED);
			rightTitle.setForeground(Color.RED);
			leftTitle.setText("NO");
			middleTitle.setText("COMMS");
			rightTitle.setText("FOUND");
			break;
		case LEFT_DOWN:
			leftTitle.setForeground(Color.RED);
			middleTitle.setForeground(Color.RED);
			rightTitle.setForeground(Color.RED);
			leftTitle.setText("ERROR");
			middleTitle.setText("IN");
			rightTitle.setText("SENSOR");
			break;
		case LEFT_GOOD:
			leftTitle.setForeground(originalColour);
			middleTitle.setForeground(originalColour);
			rightTitle.setForeground(originalColour);
			leftTitle.setText("LEFT");
			middleTitle.setText("COMMON");
			rightTitle.setText("RIGHT");
			break;
		case LEFT_COMMS_DOWN:
			leftTitle.setForeground(Color.RED);
			middleTitle.setForeground(Color.RED);
			rightTitle.setForeground(Color.RED);
			leftTitle.setText("NO");
			middleTitle.setText("COMMS");
			rightTitle.setText("FOUND");
			break;
		case POWER_COMMS_DOWN:
			leftTitle.setForeground(Color.RED);
			middleTitle.setForeground(Color.RED);
			rightTitle.setForeground(Color.RED);
			leftTitle.setText("NO");
			middleTitle.setText("COMMS");
			rightTitle.setText("FOUND");
			break;
		case POWER_DOWN:
			leftTitle.setForeground(Color.RED);
			middleTitle.setForeground(Color.RED);
			rightTitle.setForeground(Color.RED);
			leftTitle.setText("ERROR");
			middleTitle.setText("IN");
			rightTitle.setText("SENSOR");
			break;
		case POWER_GOOD:
			leftTitle.setForeground(originalColour);
			middleTitle.setForeground(originalColour);
			rightTitle.setForeground(originalColour);
			leftTitle.setText("LEFT");
			middleTitle.setText("COMMON");
			rightTitle.setText("RIGHT");
			break;
		case PUMPS_DOWN:
			leftTitle.setForeground(Color.RED);
			middleTitle.setForeground(Color.RED);
			rightTitle.setForeground(Color.RED);
			leftTitle.setText("ERROR");
			middleTitle.setText("IN");
			rightTitle.setText("SENSOR");
			break;
		case PUMPS_UP:
			leftTitle.setForeground(originalColour);
			rightTitle.setForeground(originalColour);
			leftTitle.setText("LEFT");
			if (EngineRoom.action == EMERGENCY.CALIBRATION) {
				middleTitle.setForeground(Color.RED);
				middleTitle.setText("CALIBRATION");
			} else {
				middleTitle.setForeground(originalColour);
				middleTitle.setText("COMMOM");
			}
			rightTitle.setText("RIGHT");
			break;
		case PUMPS_COMMS_DOWN:
			leftTitle.setForeground(Color.RED);
			middleTitle.setForeground(Color.RED);
			rightTitle.setForeground(Color.RED);
			leftTitle.setText("NO");
			middleTitle.setText("COMMS");
			rightTitle.setText("FOUND");
			break;
		default:
			throw new RuntimeException("Not a valid stateus");
		}
	}

	private static void quickControls(EngineRoom.EMERGENCY action, JSlider slider, JSlider rightslider) {
		EngineRoom.action = action;
		switch (action) {
		case ALL_STOP:
			slider.setValue(0);
			rightslider.setValue(0);
			commonslider.setValue(0);
			break;
		case FULL_LEFT:
			commonslider.setValue(0);
			slider.setValue(-100);
			rightslider.setValue(100);
			break; 
		case FULL_RIGHT:
			commonslider.setValue(0);
			slider.setValue(100);
			rightslider.setValue(-100);
			break; 
		case ALL_FULL:
			slider.setValue(100);
			rightslider.setValue(100);
			commonslider.setValue(100);
			break; 
		case ALL_BACK:
			slider.setValue(-100);
			rightslider.setValue(-100);
			commonslider.setValue(-100);
			break;
		case CALIBRATION:
			if (calibrationStart == null) {
				calibrationStart = System.currentTimeMillis();
			}
			break;
		case CALIBRATION_END:
			calibrationStart = null;
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
				getCPUTemp();
				frame.setTitle("Engine Room temp=" + (cpuTemp/10.0) + "Celcius");
				try {
					Thread.sleep(Constant.sensor_read_ms);
				} catch (InterruptedException e) {
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
					setEngineLeft(newSlider);
				}
				previousSlider = newSlider;

				newSlider = slider.getValue();
				if (previousRightSlider != null && !previousRightSlider.equals(newRightSlider)) {
					engineRight(newRightSlider);
				}

				previousRightSlider = newRightSlider;

				newRightSlider = rightslider.getValue();
				if (action == EMERGENCY.CALIBRATION && calibrationStart != null && (System.currentTimeMillis() - calibrationStart) < 5000) {
					commonslider.setValue(0);
					slider.setValue(0);
					rightslider.setValue(0);
					setEngineLeft(slider.getValue());
					engineRight(rightslider.getValue());
					EngineRoom.pumps(0);				
				} else if (action == EMERGENCY.CALIBRATION && calibrationStart != null && (System.currentTimeMillis() - calibrationStart) >= 5000) {
					quickControls(EMERGENCY.CALIBRATION_END, slider, rightslider);
				}
				//10Hz
				try {
					MyThread.sleep(Constant.tick_ms);
				} catch (InterruptedException e) {
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
		bottompanel.add(powerButton);
		powerButton.setBackground(Color.GREEN);
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
		powerButton.addActionListener(e -> {
			if (EngineRoom.power == 0) {
				setPower(true);
			} else {
				setPower(false);
			}
		});

		// Making the frame visible
		frame.setVisible(true);
		EngineRoom er = new EngineRoom();
		MyThread t = er.new MyThread();
		MyThreadTemperature t2 = er.new MyThreadTemperature();
		t.start();
		t2.start();
		setPower(false);

	}

	public static void pumps(int i) {
		Constant.gg.getGenericAsync(
				"/dive/fill-tank/" + i,

				result -> {
					pumps = result;
					SwingUtilities.invokeLater(() -> {
						// update whatever visual indication you want here
						if (pumps == Constant.ERROR) {
							updateStatus(COMMS_STATUS.PUMPS_DOWN);
						} else {
							updateStatus(COMMS_STATUS.PUMPS_UP);
						}
						frame.revalidate();
						frame.repaint();
					});
				},

				errorMessage -> {
					SwingUtilities.invokeLater(() -> {
						updateStatus(COMMS_STATUS.PUMPS_COMMS_DOWN);
						frame.repaint();
					});
				}
				);
	}
	public void engineRight(Integer newRightSlider) {
		Constant.gg.getGenericAsync(
				"/engine/right/"+newRightSlider,
				result -> {
					SwingUtilities.invokeLater(() -> {
						engineRight = result;
						if (engineRight == Constant.ERROR) {
							updateStatus(COMMS_STATUS.RIGHT_DOWN);
						} else {
							updateStatus(COMMS_STATUS.RIGHT_GOOD);
						}
					});

				},
				errorMessage -> {
					SwingUtilities.invokeLater(() -> {
						updateStatus(COMMS_STATUS.RIGHT_COMMS_DOWN);
					});
				}
				);
	}

	public void setEngineLeft(Integer newSlider) {
		Constant.gg.getGenericAsync(
				"/engine/left/"+newSlider,
				result -> {
					SwingUtilities.invokeLater(() -> {
						engineLeft = result;
						if (engineLeft == Constant.ERROR) {
							updateStatus(COMMS_STATUS.LEFT_DOWN);
						} else {
							updateStatus(COMMS_STATUS.LEFT_GOOD);

						}
					});

				},
				errorMessage -> {
					SwingUtilities.invokeLater(() -> {
						updateStatus(COMMS_STATUS.LEFT_COMMS_DOWN);
					});

				}
				);
	}
	public static void setPower(boolean enable) {
		Constant.gg.getGenericAsync(
				"/dive/power/" + (enable ? 1 : 0),

				result -> {
					power = result;

					SwingUtilities.invokeLater(() -> {
						if (power == Constant.ERROR) {
							updateStatus(COMMS_STATUS.POWER_DOWN);
							return;
						}

						updateStatus(COMMS_STATUS.POWER_GOOD);

						if (power == 1) {
							powerButton.setBackground(Color.RED);
							powerButton.setText("Power ON");
						} else {
							powerButton.setBackground(Color.GREEN);
							powerButton.setText("Power OFF");
						}
					});
				},

				errorMessage -> {
					SwingUtilities.invokeLater(() ->
					updateStatus(COMMS_STATUS.POWER_COMMS_DOWN)
							);
				}
				);
	}
	public static void getPower() {
		Constant.gg.getGenericAsync(
				"/dive/power",
				result -> {
					power = result;

					SwingUtilities.invokeLater(() -> {
						if (power == 1) {
							powerButton.setBackground(Color.RED);
							powerButton.setText("Power ON");
						} else {
							powerButton.setBackground(Color.GREEN);
							powerButton.setText("Power OFF");
						}
					});
				},
				errorMessage -> {
					SwingUtilities.invokeLater(() ->
					updateStatus(COMMS_STATUS.POWER_COMMS_DOWN)
							);
				}
				);
	}

	public void getCPUTemp() {
		Constant.gg.getGenericAsync(
				"/engine/cpu-temp",
				result -> {
					SwingUtilities.invokeLater(() -> {
						cpuTemp = result;
						if (cpuTemp == Constant.ERROR) {
							updateStatus(COMMS_STATUS.CPU_DOWN);
						} else {
							updateStatus(COMMS_STATUS.CPU_GOOD);
						}
					});
				},
				errorMessage -> {
					SwingUtilities.invokeLater(() -> {

						updateStatus(COMMS_STATUS.CPU_COMMS_DOWN);
					});

				}
				);
	}

}
