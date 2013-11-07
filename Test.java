import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

/**
 * This is a test class. It tests the ultrasonic sensors and color sensors. It
 * will return the results through the console viewer provided by NXJ.
 * (Connection is through USB)
 * 
 * @author Team-03
 * 
 */
public class Test {
	
	private Dashboard dashboard;
	private ColorSensor leftColor;
	private ColorSensor rightColor;
	private Odometer odometer;
	private OdometryCorrection correction;
	private UltrasonicSensor usHigh;
	private UltrasonicSensor usLow;
	private UltrasonicSensorFilter usFilterHigh;
	private UltrasonicSensorFilter usFilterLow;
	private Navigation nav;
	private USLocalizer localizer;

	/**
	 * Constructor for this test class. It will constrcut all necessary objects
	 */
	public Test() {
		// Dashboard instantiation
		NXTRegulatedMotor leftMotor = Motor.A;
		NXTRegulatedMotor rightMotor = Motor.B;
		dashboard = new Dashboard(leftMotor, rightMotor);

		// Odometer & Odoemetry Correction instantiation
		leftColor = new ColorSensor(SensorPort.S3);
		rightColor = new ColorSensor(SensorPort.S4);
		odometer = new Odometer(leftMotor, rightMotor, 20, true);
		correction = new OdometryCorrection(leftColor,	rightColor, odometer);
		correction.start();

		// Ultrasonic Filter instantiation
		usHigh = new UltrasonicSensor(SensorPort.S1);
		usLow = new UltrasonicSensor(SensorPort.S2);
		usFilterHigh = new UltrasonicSensorFilter(usHigh, 20, true);
		usFilterLow = new UltrasonicSensorFilter(usLow,	20, true);

		// Navigation instantiation
		nav = new Navigation(dashboard, odometer, usFilterHigh);

		// Localition instantiation (but not actually perforing localization
		// yet)
		localizer = new USLocalizer(odometer, usHigh, USLocalizer.LocalizationType.FALLING_EDGE, nav);

	}

	// This test has the robot drive straight to a block with the ultrasonic
	// sensor facing it.
	// It will stop when the ultrasonic reading reads 5cm.
	/**
	 * This is the ultrasonic sensor test. This test has the robot drive
	 * straight to a block with the ultrasonic sensor facing it.
	 */
	public void usTest() {
		Button.waitForAnyPress();
		RConsole.open();
		RConsole.println("Connected");
		Button.waitForAnyPress();
		int distance = usFilterHigh.getFilteredDistance(); // The ultrasonic sensor
													// pings every 60ms
		while (distance > 5) {
			distance = usFilterHigh.getFilteredDistance();
			RConsole.println("" + distance);
		}

		RConsole.println("DONE");

	}

	// This test has the robot drive straight for 150 cm while having the color
	// sensor facing
	// directly downwards. Light readings were taken every 80ms.
	/**
	 * Performs a test on the color sensor. This test has the robot drive
	 * straight for 150 cm while having the color sensor facing downwards.
	 */
	public void lightTest() {

		leftColor.setFloodlight(true);
		RConsole.open();
		RConsole.println("Connected");
		Button.waitForAnyPress();
		dashboard.goForward(150, true);
		while (dashboard.isMotorRotating()) {
			// The color sensor will give a reading every 80ms
			try {
				Thread.sleep(80);
			} catch (Exception e) {
			}
			int color = leftColor.getNormalizedLightValue();
			RConsole.println("" + color);

		}

		RConsole.println("DONE");
	}

}
