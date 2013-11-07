/*
 * Navigation.java
 */

import lejos.nxt.*;
import lejos.robotics.Color;


/**
 * The Navigation class is responsible for navigating the robot around the course. 
 * It has methods that will make the robot travel from one point to another. The robot
 * will always take the shortest path when traveling from point A to B.
 * 
 * @author Team-03
 *
 */
public class Navigation {
	// declare member variables inherited from Lab3.java
	// private NXTRegulatedMotor leftMotor;
	// private NXTRegulatedMotor rightMotor;
	private Dashboard dashboard;
	private Odometer odometer;
	private UltrasonicSensorFilter usSensor;

	// speed and error constants, and position of the walls for avoidance
	final private static int FORWARD_SPEED = 200;
	final private static int ROTATE_SPEED = 200;
	final private double acceptableErrorX_Y = 1;
	final private double acceptableErrorTheta = 2;
	final private double width = 16.85;
	final private double radius = 2.15;
	final private int left_wall_x_position = -30;
	final private int right_wall_x_position = 90;
	final private int top_wall_y_position = 210;
	final private int bottom_wall_y_position = -30;

	// default constructor
	/**
	 * The contructor for building a Navigation Object
	 * @param db A dashboard that will contorl the robots' motors
	 * @param odo An odometer that will keep track of the robot's position and heading angle
	 * @param ultras An ultrasonic sensor to detect obstacles in a path 
	 */
	public Navigation(Dashboard db, Odometer odo, UltrasonicSensorFilter ultra) {

		this.dashboard = db;
		this.odometer = odo;
		this.usSensor = ultra;
	}

	/**
	 * Method that takes distance and angle measure by ultrasonic sensor and
	 * adds it to the odometer to see if that reading was of a wall or not.
	 * Returns true for a wall position, false otherwise.
	 *
	 * @param distanceDetectedfromUs Distance reading from the ultrasonic sensor
	 * @param theta The heading angle of the robot
	 * @return True if a robot has reached a wall. Otherwise, false
	 */
	public boolean wallDetected(int distanceDetectedfromUS, double theta) {

		double x = odometer.getX();

		if (((x + distanceDetectedfromUS * Math.sin(theta * Math.PI / 180)) < left_wall_x_position + 10)
				|| ((x + distanceDetectedfromUS	* Math.sin(theta * Math.PI / 180)) > right_wall_x_position - 10))
			return true;

		double y = odometer.getY();

		if (((y + distanceDetectedfromUS * Math.cos(theta * Math.PI / 180)) > top_wall_y_position - 10)
				|| ((y + distanceDetectedfromUS	* Math.cos(theta * Math.PI / 180)) < bottom_wall_y_position + 10))
			return true;

		return false;
	}

	// Method to find x and y positions of objects with a specific distance away
	// at a specific theta, returns an array of x and y position
	/**
	 * Method to find x and y positions of objects with a specific distance away
	 * @param distance The distance an object is detected at
	 * @param theta The angle an object is detected at
	 * @return An array of the position in cartesian coordinates (x and y positions)
	 */
	public double[] findPositionWithDistanceAndAngle(double distance,
			double theta) {

		double xposition = odometer.getX() + distance
				* Math.sin(theta * Math.PI / 180);
		double yposition = odometer.getY() + distance
				* Math.cos(theta * Math.PI / 180);

		double[] Position = { xposition, yposition };

		return Position;

	}

	// Method to find the desired Angle that takes change in x and change in y
	// as parameters, returns the angle
	/**
	 * Method to find the angle between to positions
	 * @param deltaX The change in x-coordinates between two positions
	 * @param deltaY The change in y-coordinates between two positions
	 * @return The angle between two positions
	 */
	public double findAngleBetweenTwoPositions(double deltaX, double deltaY) {
		double desiredAngle = Math.atan(deltaX / deltaY) * 180 / Math.PI;

		// account of arc-tangent calculation (right is +ve x and up is +ve y)
		if ((deltaX > 0) && (deltaY < 0))
			desiredAngle += 180;
		if ((deltaX < 0) && (deltaY < 0))
			desiredAngle -= 180;

		double deltaAngle = this.odometer.minimumAngleFromTo(this.odometer.getTheta(), desiredAngle);

		return deltaAngle;
	}

	// Method that travels to certain x and y coordinate
	/**
	 * Method that travels to a certain point given its x and y coordinates
	 * @param x The x-coordinate of the point
	 * @param y The y-coordinate of the point
	 */
	public void travelTo(double x, double y) {
		// finding the error to the desired destination
		double deltaX = x - this.odometer.getX();
		double deltaY = y - this.odometer.getY();
		int goingForward = 0;

		// constantly check if the robot is at the correct coordinates within
		// reasonable error, if not then check the following
		while (!((Math.abs(deltaX) < acceptableErrorX_Y) && (Math.abs(deltaY) < acceptableErrorX_Y))) {

			// find the angle between wanted to get to the destination
			double deltaAngle = findAngleBetweenTwoPositions(deltaX, deltaY);

			// verify if the current heading is the desired one
			// if at the right angle, go straight.
			if (Math.abs(deltaAngle) <= acceptableErrorTheta && goingForward == 0) {

				dashboard.goForward(FORWARD_SPEED);

				goingForward = 1;
			}
			if (Math.abs(deltaAngle) > acceptableErrorTheta) { // if not, change the heading

				turn(deltaAngle);
				goingForward = 0;
			}

			// update odometer values
			deltaX = x - this.odometer.getX();
			deltaY = y - this.odometer.getY();
		}

		// stop when you are at the right spot

		dashboard.stop();
	}

	/**
	 * The method turns the robot to a specified angle
	 * @param theta The angle, which the robot turns to.
	 */
	public void turnTo(double theta) {

		// retrieve theta and calculate angle change between desired and current

		double theta_change = this.odometer.minimumAngleFromTo(this.odometer.getTheta(), theta);

		// calculate tachocount needed to turn to desired angle (divide by two
		// since each wheel will do half the needed tachocount)
		double target_tacho = (width * theta_change) / (2 * radius);
		int target_tacho_int = (int) target_tacho;

		// rotate each wheel by the calculated number

		dashboard.rotateMotor(target_tacho_int, -target_tacho_int, ROTATE_SPEED);
	}

	// Method that makes robot turn slowly left or right indefinitely
	/**
	 * The method makes the robot turn slowly left or right indefintely 
	 * @param direction The direction the robot turns (1 turns to the right, 2 turns to the left)
	 */
	public void turnSlow(int direction) {
		if (direction == 1) {
			// goes right
			dashboard.turnRight(40);

		}
		if (direction == 2) {
			// goes left
			dashboard.turnLeft(40);
		}
	}

	// turn by angle theta (clockwise is positive)
	/**
	 * The robot turns clockwise by a specified angle
	 * @param theta The angle, which the robot turns clockwise by
	 */
	public void turn(double theta) {

		double target_tacho = (width * theta) / (2 * radius);
		int target_tacho_int = (int) target_tacho;
		dashboard.rotateMotor(target_tacho_int, -target_tacho_int, ROTATE_SPEED);
	}

}
