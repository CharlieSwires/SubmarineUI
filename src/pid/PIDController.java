package pid;

public class PIDController {
    private double kp; // Proportional gain
    private double ki; // Integral gain
    private double kd; // Derivative gain
    private double setpoint; // Desired value
    private double integral = 0; // Integral accumulator
    private double prevError = 0; // Previous error

    public PIDController(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

    public void setPID(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;
    }

    public double compute(double processVariable) {
        double error = setpoint - processVariable;
        integral += error;
        double derivative = error - prevError;

        // PID control output calculation
        double output = kp * error + ki * integral + kd * derivative;

        // Update previous error for next iteration
        prevError = error;

        return output;
    }

    public static void main(String[] args) {
        PIDController pidController = new PIDController(0.1, 0.01, 0.05);
        pidController.setSetpoint(50); // Set desired setpoint

        // Simulate the process loop
        for (int i = 0; i < 100; i++) {
            // Simulate the process variable (e.g., sensor reading)
            double processVariable = Math.random() * 100;

            // Compute control output
            double controlOutput = pidController.compute(processVariable);

            // Apply control output to the process (e.g., actuator)
            // Here we just print it for demonstration
            System.out.println("Iteration " + i + ": Process Variable = " + processVariable
                    + ", Control Output = " + controlOutput);
        }
    }
}
