/* UltrasonicSensorFilter.java
 * 
 * TEAM-03
 */
import lejos.nxt.*;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * This class is a filter for the ultrasonic sensor mounted on the robot
 * 
 * @author Team-03
 * 
 */
public class UltrasonicSensorFilter implements TimerListener {

	private UltrasonicSensor us;
	private int filteredDistance;
	private Timer timer;
	private final static int DEFAULT_TIMEOUT_PERIOD = 60;

	/**
	 * The constructor for the UltrasonicSensorFilter. It requires an ultrasonic
	 * sensor to function.
	 * 
	 * @param ultrasonic The ultrasonic sensor being used by the robot
	 */
	public UltrasonicSensorFilter(UltrasonicSensor ultrasonic, int interval, boolean autostart) {

		us = ultrasonic;

		if (autostart) {
			this.timer = new Timer((interval <= 0) ? interval : DEFAULT_TIMEOUT_PERIOD, this);
			this.timer.start();
		} else {
			this.timer = null;
		}

	}

	/**
	 * Stops the timer
	 */
	public void stop() {

		if (this.timer != null) {
			this.timer.stop();
		}

	}

	/**
	 * Starts the timer
	 */
	public void start() {

		if (this.timer != null) {
			this.timer.start();
		}

	}

	/**
	 * The inherited method. Updates the filtered distance of the ultrasonic sensor.
	 */
	public void timedOut() {
		synchronized (this) {
			filteredDistance = getFilteredDistance();
		}

	}

	/**
	 * Filters the ultrasonic sensor's readings of distances
	 * 
	 * @return The filtered distance from the ultrasonic sensor readings
	 */
	public int getFilteredDistance() {

		int filtered = us.getDistance();
//		if (us.getDistance() > 60) { // Find correct value. 60 isn't a final
//										// value. Needs to be determined
//			filtered = 60;
//		}

		return filtered;

	}

}
