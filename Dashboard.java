/* Dashboard.java
 * TEAM-03
 */

import lejos.nxt.NXTRegulatedMotor;

/**
 * The Dashboard class is responsible for controlling all the motors
 * It takes care of motor rotations so it will control speeds when the
 * robot is moving forwards or backwards. It needs access to two
 * motors to control the left and right wheels.
 * 
 * @author Team-03
 */

public class Dashboard {
	
	private final NXTRegulatedMotor leftMotor; 
	private final NXTRegulatedMotor rightMotor;

	
	/** Constructor for the Dashboard class that takes a left and right motor as parameters.
	 * 
	 * @param left The left motor of the robot.
	 * @param right The right motor of the robot.
	 */
	public Dashboard(NXTRegulatedMotor left, NXTRegulatedMotor right){
		
		leftMotor = left;
		rightMotor = right;
				
	}
	
	/** Turns the motor forwards to make the robot move forward at a constant speed.
	 * 
	 * @param speed The speed at which both left and right motors will rotate forward at.
	 */
	public void goForward(int speed){
		
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
		leftMotor.forward();
		rightMotor.forward();
		
	}
	
	public void goForward(double distance, boolean continue_code)
	{
			// goes forward by given distance
			
			leftMotor.setSpeed(150);
			rightMotor.setSpeed(150);
			
			double target_tacho = distance*180/(Math.PI*2.15);
			int  target_tacho_int = (int) target_tacho;
			
			if(continue_code)
			{
				leftMotor.rotate(target_tacho_int, true);
				rightMotor.rotate(target_tacho_int, true);
			}
			else
			{
				leftMotor.rotate(target_tacho_int, true);
				rightMotor.rotate(target_tacho_int, false);
			}
	}
	
	/** Turns the motor backwards to make the robot move backwards at a constant speed
	 * 
	 * @param speed The speed at which left and right motors will rotate backwards at.
	 */
	public void goBackward(int speed){
		
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
		leftMotor.backward();
		rightMotor.backward();
		
	}
	
	/** Rotates the motors a specified angle
	 * 
	 * @param leftAngle The angle the left motor rotates 
	 * @param rightAngle The angle the right motor rotates
	 */
	public void rotateMotor(int leftAngle, int rightAngle, int rotationSpeed){
		
		leftMotor.setSpeed(rotationSpeed);
		rightMotor.setSpeed(rotationSpeed);
		leftMotor.rotate(leftAngle, true);
		rightMotor.rotate(rightAngle, false);
		
	}
	
	/** Rotates motors such that the robot is moving directly to the right. The left motor will rotate backwards
	 *  and the right motor will rotate forwards to make the robot turn to the right
	 *  
	 * @param speed The speed at which both motors rotate.
	 */
	public void turnLeft(int speed){
		
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
		leftMotor.backward();
		rightMotor.forward();
		
	}
	/** Rotate motors such that the robot is moving directly to the left. The left motor will rotate forwards 
	 *  and the right motor will rotate backwards to make the robot turn to the left
	 * 
	 * @param speed The speed at which both motors rotate.
	 */
	public void turnRight(int speed){
		
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
		leftMotor.forward();
		rightMotor.backward();
		
	}
	
	/** 
	 * Stops rotating both motors to make the robot stop all motion
	 */
	public void stop(){
		
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
		
	}
	
	/**
	 * Determines if any of the motors are currently in motion
	 * @return The state of the motors. True if one of the motors are in motion. Otherwise, false.
	 */
	public boolean isMotorRotating(){
		
		return ((leftMotor.isMoving()) || (rightMotor.isMoving()));
		
	}

}
