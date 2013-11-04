import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

/*
 * This is a test class. It tests the ultrasonic sensors and color sensors. It will return
 * the results through the console viewer provided by NXJ. (Connection is through USB)
 */
public class Test {

	public static void main(String[] args) {
		Button.waitForAnyPress();
		lightTest();
		

	}

	// This test has the robot drive straight to a block with the ultrasonic sensor facing it.
	// It will stop when the ultrasonic reading reads 5cm. 
	private static void usTest() {

		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
		UltrasonicSensorFilter ultra = new UltrasonicSensorFilter(us, 60, true);
		RConsole.open();
		RConsole.println("Connected");
		Button.waitForAnyPress();
		int distance = ultra.getFilteredDistance(); // The ultrasonic sensor pings every 60ms
		while (distance > 5) { 
			distance = ultra.getFilteredDistance();
			RConsole.println("" + distance);
		}

		RConsole.println("DONE");

	}

	// This test has the robot drive straight for 150 cm while having the color sensor facing
	// directly downwards. Light readings were taken every 80ms.
	private static void lightTest() {

		ColorSensor cs = new ColorSensor(SensorPort.S2);
		cs.setFloodlight(true);
		RConsole.open();
		RConsole.println("Connected");
		Button.waitForAnyPress();
		Dashboard dashboard = new Dashboard(Motor.A, Motor.B);
		dashboard.goForward(150, true);
		while(dashboard.isMotorRotating()) {
			// The color sensor will give a reading every 80ms
			try{ 
				Thread.sleep(80);
			}
			catch(Exception e){}
			int color = cs.getNormalizedLightValue();
			RConsole.println("" + color);

		}

		RConsole.println("DONE");
	}

}