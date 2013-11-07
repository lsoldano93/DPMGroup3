/*
 * File: Odometer.java
 * 
 * TEAM-03
 */

import lejos.util.Timer;
import lejos.util.TimerListener;
import lejos.nxt.NXTRegulatedMotor;

/**
 * The Odometer class keeps track of the robot's current position in cartesian coordinates. 
 * This class implements a TimerListener.
 */

public class Odometer implements TimerListener {

	private Timer timer;
	private NXTRegulatedMotor leftMotor, rightMotor;
	private final int DEFAULT_TIMEOUT_PERIOD = 20;
	private double leftRadius, rightRadius, width;
	private double x, y, theta;
	private double[] oldDH, dDH;
	
	/**
	 * Constructor for the Odometer class
	 * @param leftMotor The left motor of the robot
	 * @param rightMotor The right motor of the robot
	 * @param INTERVAL The time interval for the timer until it times out
	 * @param autostart Decision to start the timer instantly
	 */
	public Odometer (NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, int interval, boolean autostart) {
		
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		
		
		this.rightRadius = 2.054;
		this.leftRadius = 2.054;
		this.width = 17.43; 
		
		this.x = 0.0;
		this.y = 0.0;
		this.theta = 0.0;
		this.oldDH = new double[2];
		this.dDH = new double[2];

		if (autostart) {
			// if the timeout interval is given as <= 0, default to 20ms timeout 
			this.timer = new Timer((interval <= 0) ? interval : DEFAULT_TIMEOUT_PERIOD, this);
			this.timer.start();
		} else
			this.timer = null;
	}
	
	/**
	 * Stops the timer
	 */
	public void stop() {
		if (this.timer != null)
			this.timer.stop();
	}
	/**
	 * Starts the timer
	 */
	public void start() {
		if (this.timer != null)
			this.timer.start();
	}
	
	/**
	 * Calculates the robot's displacement and heading
	 * @param data Contains the robot's displacement and heading
	 */
	private void getDisplacementAndHeading(double[] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();

		data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) * Math.PI / 360.0;
		data[1] = (leftTacho * leftRadius - rightTacho * rightRadius) / width;
	}

	/**
	 * Recomputes the robot's position and heading angle using the change in displacement and heading
	 */
	public void timedOut() {
		this.getDisplacementAndHeading(dDH);
		dDH[0] -= oldDH[0];
		dDH[1] -= oldDH[1];

		// update the position in a critical region
		synchronized (this) {
			theta += dDH[1];
			theta = fixDegAngle(theta);

			x += dDH[0] * Math.sin(Math.toRadians(theta));
			y += dDH[0] * Math.cos(Math.toRadians(theta));
		}

		oldDH[0] += dDH[0];
		oldDH[1] += dDH[1];
	}

	/**
	 * Returns the x-position of the robot as calculated by the odometer
	 * @return The current x-position of the robot
	 */
	public double getX() {
		synchronized (this) {
			return x;
		}
	}
	
	/**
	 * Returns the y-position of the robot as calculated by the odometer
	 * @return The current y-position of the robot
	 */
	public double getY() {
		synchronized (this) {
			return y;
		}
	}

	/**
	 * Returns the heading angle of the robot as calculated by the odometer
	 * @return The current heaidng angle of the robot
	 */
	public double getTheta() {
		synchronized (this) {
			return theta;
		}
	}

	
	/**
	 * Sets the position and heading angle of the robot
	 * @param position The robot's position
	 * @param update Decides if the position or heading angle will be updated
	 */
	public void setPosition(double[] position, boolean[] update) {
		synchronized (this) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	/**
	 * Saves the robot's position and heading angle
	 * @param position Holds the robot's position and angle
	 */
	public void getPosition(double[] position) {
		synchronized (this) {
			position[0] = x;
			position[1] = y;
			position[2] = theta;
		}
	}

	/**
	 * Returns the robot's position and heading angle
	 * @return Returns the robot's position and heading angle
	 */
	public double[] getPosition() {
		synchronized (this) {
			return new double[] { x, y, theta };
		}
	}
	
	/**
	 * Calculates the complimentary angle if the angle is larger than 360 or negative.
	 * This prevents having negative angles.
	 * @param angle The robot's heading angle
	 * @return The robot's new calculated heading angle 
	 */
	public static double fixDegAngle(double angle) {
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);

		return angle % 360.0;
	}

	/**
	 * Determining the smallest angle between two points
	 * @param a First point
	 * @param b Second point
	 * @return The smallest angle between two points
	 */
	public static double minimumAngleFromTo(double a, double b) {
		double d = fixDegAngle(b - a);

		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}
		
}
