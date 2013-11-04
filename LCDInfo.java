/* 
 * 
 * 
 * File: LCDInfo.java
 * 
 * TEAM-03
 */
import lejos.nxt.LCD;

import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * The LCDInfo class displays the robot's position in cartesian coordinates and heading angle on the NXT brick's screen
 * @author Team-03
 *
 */
public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Odometer odo;
	private Timer lcdTimer;
	
	// arrays for displaying data
	private double [] pos;
	
	/**
	 * The LCDInfo constructor that builds the object. It takes an odometer as a parameter
	 * @param odo The odometer that will provide the screen with information to display
	 */
	public LCDInfo(Odometer odo) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		
		// initialise the arrays for displaying data
		pos = new double [3];
		
		// start the timer
		lcdTimer.start();
	}
	
	/**
	 * Inherited abstract method from TimerListener. When timedOut is called, it will display to the screen
	 * the robot's position in cartesian coordinates and its heaidng angle.
	 */
	public void timedOut() { 
		odo.getPosition(pos);
		LCD.clear();
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawInt((int)(pos[0]), 3, 0);
		LCD.drawInt((int)(pos[1]), 3, 1);
		LCD.drawInt((int)pos[2], 3, 2);
	
	}
}
