/*
	Lab 4 - Group 7 Code
	Benjamin Brown 
	Mohamed El Gindi
	
	USLocalizer.java
*/

import lejos.nxt.UltrasonicSensor;

public class USLocalizer 
{
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	
	// constructor objects
	private Odometer odo;
	private UltrasonicSensor us;
	private LocalizationType locType;
	private Navigation navigator;
	
	// constants
	private double angleAdjustment = -47;
	private double forwardDistance = 15;
	private double threshold = 45;
	private double quickAdjustment = 30;
	
	// error adjustments from calculation
	private double errorAdjustmentFalling = 2.1;
	private double errorAdjustmentRising = 2.35;
	
	public USLocalizer(Odometer odo, UltrasonicSensor ultras, LocalizationType locType, Navigation navi) 
	{
		this.odo = odo;
		this.us = ultras;
		this.locType = locType;
		this.navigator = navi;
		
		// switch off the ultrasonic sensor
		//us.off();
	}
	
	public void doLocalization() 
	{
		// declare array variables used for position update
		double [] pos = new double [3];
		boolean [] update = new boolean [3];
		
		// falling edge localization
		if (locType == LocalizationType.FALLING_EDGE) 
		{
			//NOTE: THE FIRST WHILE LOOP IS TO MOVE OUR ROBOT SO THAT IT IS NOT FACING THE WALL, THE OTHER TWO WHILE LOOPS PERFORM THE LOCALIZATION. 
			
			// get current distance from wall and current angle read by odometer
			double theta = this.odo.getTheta();
			int distance = getFilteredData();
			
			// rotate the robot until you no longer see a wall
			navigator.turnSlow(1);
			
			while (distance < (threshold + 4))
			{
				distance = getFilteredData();
			}
			
			// rotate the robot until it sees a wall
			navigator.turnSlow(2);
			
			while (!(distance < threshold))
			{
				theta = this.odo.getTheta();
				distance = getFilteredData();
			}
			
			// record and angle at which you saw the wall
			double theta1 = theta;
			
			// do a quick adjustment
			navigator.turn(quickAdjustment);
			
			// get current distance from wall and current angle read by odometer
			theta = this.odo.getTheta();
			distance = getFilteredData();
			
			// rotate the robot until it sees a wall (opposite direction)
			navigator.turnSlow(1);
			
			while (!(distance < threshold))
			{
				theta = this.odo.getTheta();
				distance = getFilteredData();
			}
			
			// record angle at which you saw the wall
			double theta2 = theta;
			
			if(theta1 > theta2)
			theta1 -= 360;
			// average these two angles
			double angleWanted = (theta1+theta2)/2;
			
			// turn to that angle
			navigator.turnTo(angleWanted); 
			
			// now that we are oriented correctly, go forward to the origin point (hypotenuse of a triangle with sides 15 cm and 15 cm)
			//navigator.goForward(forwardDistance); 
			
			// turn to face forward
			navigator.turn(angleAdjustment + errorAdjustmentFalling); 
		}
		
		// rising edge localization
		else 
		{
			// NOTE: THE FIRST WHILE LOOP IS TO MOVE OUR ROBOT SO THAT IT IS FACING THE WALL, THE OTHER TWO WHILE LOOPS PERFORM THE LOCALIZATION
			
			// get current distance from wall and current angle read by odometer
			double theta = this.odo.getTheta();
			int distance = getFilteredData();
			
			// rotate the robot until you see a wall
			navigator.turnSlow(2);
			
			while (!(distance < (threshold - 4)))
			{
				theta = this.odo.getTheta();
				distance = getFilteredData();
			}
			
			// rotate the robot until it does not see a wall
			navigator.turnSlow(1);
			
			while (distance < threshold)
			{
				
				theta = this.odo.getTheta();
				distance = getFilteredData();
				
			}
			
			// record angle at which you stopped seeing the wall
			double theta1 = theta;
			
			// do a quick adjustment
			navigator.turn(-quickAdjustment);
			
			// get current distance from wall and current angle read by odometer
			theta = this.odo.getTheta();
			distance = getFilteredData();
			
			// rotate the robot until it does not see a wall (opposite direction)
			navigator.turnSlow(2);
			
			while (distance < threshold)
			{
				theta = this.odo.getTheta();
				distance = getFilteredData();
			}
			
			// record angle at which you stopped seeing the wall
			double theta2 = theta;
			
			if(theta1 > theta2)
			theta1 -= 360;
			// average these two angles
			double angleWanted = (theta1+theta2)/2;
			
			// turn to that angle
			this.navigator.turnTo(angleWanted); 
			
			// now that we are oriented correctly, go forward to the origin point (hypotenuse of a triangle with sides 15 cm and 15 cm)
			//this.navigator.goForward(forwardDistance); 
			
			// turn to face forward
			this.navigator.turn(angleAdjustment + errorAdjustmentRising); //
		}
		
		// set position
		pos[0] = 0; 
		pos[1] = 0;
		pos[2] = 0;
		update[0] = true; 
		update[1] = true; 
		update[2] = true;

		this.odo.setPosition(pos,update);
	}
	
	private int getFilteredData() 
	{
		int distance;
		
		// do a ping
		//this.us.ping();
		
		// wait for the ping to complete
		try 
		{ 
			Thread.sleep(50); 
		} 
		catch (InterruptedException e) 
		{
			// thread interrupt will never occur, so no need for exception handling
		}
		
		
		// there will be a delay here
		distance = this.us.getDistance();
				
		// basic filter		
		if (distance > 60)
			distance = 60;
			
		return distance;
	}

}
