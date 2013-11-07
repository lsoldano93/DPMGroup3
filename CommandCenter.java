import lejos.nxt.*;

public class CommandCenter {

	public static void main(String[] args){
		// Dashboard instantiation
		NXTRegulatedMotor leftMotor = Motor.A;
		NXTRegulatedMotor rightMotor = Motor.B;
		Dashboard dashboard = new Dashboard(leftMotor, rightMotor);
		
		// Odometer & Odoemetry Correction instantiation
		ColorSensor leftColor = new ColorSensor(SensorPort.S3);
		ColorSensor rightColor = new ColorSensor(SensorPort.S4);
		Odometer odometer = new Odometer(leftMotor, rightMotor, 20, true);
		OdometryCorrection correction = new OdometryCorrection(leftColor, rightColor, odometer);
		correction.start();
		
		// Ultrasonic Filter instantiation
		UltrasonicSensor usHigh = new UltrasonicSensor(SensorPort.S1);
		UltrasonicSensor usLow = new UltrasonicSensor(SensorPort.S2);
		UltrasonicSensorFilter usFilterHigh = new UltrasonicSensorFilter(usHigh, 20, true);
		UltrasonicSensorFilter usFilterLow = new UltrasonicSensorFilter(usLow, 20, true);
		
		// Navigation instantiation
		// NEED TO FIX NAVIGATION CONSTRUCTOR. NEW DESIGN!!! ************
		Navigation nav = new Navigation(dashboard, odometer, usHigh, leftColor);
		
		// Localition instantiation (but not actually perforing localization yet)
		USLocalizer localizer = new USLocalizer(odometer, usHigh, USLocalizer.LocalizationType.FALLING_EDGE, nav);
		
		
	}
	
	private void test1(Odometer odometer, UltrasonicSensor ultra){ // Testing ultrasonic sensor
	
		
		
	}
	
	private void test2(ColorSensor color){ // Testing color sensor
		
		
	}
	
	private void test3(){ // Testing Navigation & Odometer
		
	}
	
}