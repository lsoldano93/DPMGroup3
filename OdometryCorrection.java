/* OdometryCorrection.java
 * TEAM-03
 */
import lejos.nxt.*;
/**
 * The OdometryCorrection class is responsible for updating and correcting the robot's odometer each time it passes
 * a black solid grid line. Small errors that accumulate over time will make the odometer very inaccurate therefore
 * this OdometryCorrection class will correct the build up of errors.
 * @author Team-03
 *
 */
public class OdometryCorrection extends Thread {
	// Declaring class variables
	private Odometer odo;
	private ColorSensor leftSensor;
	private ColorSensor rightSensor;
	private int newValueLeft;
	private int newValueRight;
	private int oldValueLeft;
	private int oldValueRight;
	private int lineMultiplier;
	private double psi;
	private double leftCross;
	private double rightCross;
	private double Ang_Correction;
	private double Pos_Correction;
	private double crossDifference;
	private double lastLeftChange;
	private double lastRightChange;
	private double currentTime;
	private final double csDistance = 24; // needs to be adjusted
	private final int lineThresholdDifference = 35; // not yet determined
	private final double csDifference = 2.0; // needs to be adjusted
	private final int angleCorrection_Error = 10;
	private boolean On = false;

	/**
	 * The constructor for the OdometryCorrection class. It takes as parameters two color sensors, one on each wheel
	 * and an odometer.
	 * @param left The color sensor on the left wheel
	 * @param right The color sensor on the right wheel
	 * @param odo The odometer that this class will correct
	 */
	public OdometryCorrection(ColorSensor left, ColorSensor right, Odometer odo) {

		leftSensor = left;
		rightSensor = right;
		leftSensor.setFloodlight(true);
		rightSensor.setFloodlight(true);
		this.odo = odo;
		On = true;
	}

	/**
	 * Inherited method from the Thread class. This method is responsible for updating and correcting the odometer by
	 * calling the odoUpdater method. It updates every time it crosses a black grid line.
	 */
	public void run() {

		while (On) {

			// Obtains new color sensor value from both sensors
			newValueLeft = leftSensor.getNormalizedLightValue();
			newValueRight = rightSensor.getNormalizedLightValue();

			// If left or right sensor has crossed over a line, call the
			// odoUpdater method
			
			currentTime = System.nanoTime()/Math.pow(10, 9);
			
			if ((oldValueLeft - newValueLeft) > lineThresholdDifference && (currentTime - lastLeftChange) > 0.150)
				odoUpdater(true);
				lastLeftChange = System.nanoTime()/Math.pow(10, 9);
			if ((oldValueRight - newValueRight) > lineThresholdDifference && (currentTime - lastRightChange) > 0.150)
				odoUpdater(false);
				lastRightChange = System.nanoTime()/Math.pow(10, 9);

			// Sets new values as old values
			oldValueLeft = newValueLeft;
			oldValueRight = newValueRight;
			
			try{
			Thread.sleep(78);
			}
			catch(Exception e){}

		}
	}

	/**
	 * This method is responsible for updating the odometer. It determines in what direction the robot is heading and
	 * based on that, it corrects the x and y positions as well as the heading angle.
	 * @param left True if the left wheel has crossed the black grid line before the right wheel. Otherwise, false.
	 */
	public void odoUpdater(boolean left) {

		// Figures out the direction of travel:
		// North = 1 From 330 to 0 to 30 Degrees
		// East = 2 From 60 to 120 Degrees
		// South = 3 From 150 to 210 Degrees
		// West = 4 From 240 to 300 Degrees

		int direction = 0;

		if ((odo.getTheta() >= 330 && odo.getTheta() <= 360)
				|| (odo.getTheta() >= 0 && odo.getTheta() <= 30))
			direction = 1;

		else if (odo.getTheta() >= 60 && odo.getTheta() <= 120)
			direction = 2;

		else if (odo.getTheta() >= 150 && odo.getTheta() <= 210)
			direction = 3;

		else if (odo.getTheta() >= 240 && odo.getTheta() <= 300)
			direction = 4;

		// Sets the x,y variable for the proper sensor depending on the
		// direction of travel.
		if (direction == 1) {
			if (left)
				leftCross = odo.getY();
			else
				rightCross = odo.getY();
		} else if (direction == 2) {
			if (left)
				leftCross = odo.getX();
			else
				rightCross = odo.getX();
		} else if (direction == 3) {
			if (left)
				leftCross = odo.getY();
			else
				rightCross = odo.getY();
		} else if (direction == 4) {
			if (left)
				leftCross = odo.getX();
			else
				rightCross = odo.getX();
		}

		// Correction returns to initial while loop unless both cross values are not zero
		// and are between the minimum and maximum allowable error
		if (leftCross != 0 && rightCross != 0) {

			// Angle of correction
			Ang_Correction = Math.atan((rightCross - leftCross) / csDistance);

			// Position correction math
			crossDifference = Math.abs(rightCross - leftCross);
			psi = Math.asin(crossDifference / csDistance);
			Pos_Correction = Math.sin(psi) * (csDistance / 2);

			// Make sure angle isn't ridiculously off
			if (Ang_Correction < angleCorrection_Error && Ang_Correction > -angleCorrection_Error) {
				// Correction based off of direction
				if (direction == 1) {
					lineMultiplier = (int) ((csDifference + odo.getY()) / 30);
					Pos_Correction = csDifference + Pos_Correction + (lineMultiplier * 30);
					double[] send1 = { 0.0, Pos_Correction, Ang_Correction };
					boolean[] send2 = { false, true, true };
					odo.setPosition(send1, send2);
					
				} else if (direction == 2) {
					lineMultiplier = (int) ((csDifference + odo.getX()) / 30);
					Pos_Correction = csDifference + Pos_Correction + (lineMultiplier * 30);
					double[] send1 = { Pos_Correction, 0.0, 90 + Ang_Correction };
					boolean[] send2 = { true, false, true };
					odo.setPosition(send1, send2);
					
				}
				if (direction == 3) {
					lineMultiplier = (int) ((csDifference + odo.getY()) / 30) + 1;
					Pos_Correction = -Pos_Correction - csDifference + (lineMultiplier * 30);
					double[] send1 = { 0.0, Pos_Correction, 180 - Ang_Correction };
					boolean[] send2 = { false, true, true };
					odo.setPosition(send1, send2);
					
				}
				if (direction == 4) {
					lineMultiplier = (int) ((csDifference + odo.getX()) / 30) + 1;
					Pos_Correction = -Pos_Correction -csDifference + (lineMultiplier * 30);
					double[] send1 = { Pos_Correction, 0.0,	270 - Ang_Correction };
					boolean[] send2 = { true, false, true };
					odo.setPosition(send1, send2);
					
				}

			}
			leftCross = 0;
			rightCross = 0;

		}

	}

}
