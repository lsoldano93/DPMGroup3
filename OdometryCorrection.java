import lejos.nxt.*;
// Dummy class for now. It is made just so it could test the robot's threading capabilities.
public class OdometryCorrection extends Thread {
	
	private ColorSensor leftSensor;
	private ColorSensor rightSensor;
	private int leftColor = 0;
	private int rightColor = 0;
	private Odometer odometer;
	
	public OdometryCorrection(ColorSensor left, ColorSensor right, Odometer odo){
		
		leftSensor = left;
		rightSensor = right;
		leftSensor.setFloodlight(true);
		rightSensor.setFloodlight(true);
		odometer = odo;
	}
	
	public void run(){
		leftColor = leftSensor.getNormalizedLightValue();
		rightColor = rightSensor.getNormalizedLightValue();
		odometer.getX();
		odometer.getY();
		odometer.getTheta();
		int counter = 0;
		counter++;
	}

}
