package blockBuilder;

import blockBuilder.USLocalizer.LocalizationType;
import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;
import blockBuilder.USLocalizer;

/* Team 13's final DPM project.
 * Software Developpers: Patrick, Ilyas, Ahmet
 * Tester: Steve
 */

public class Main {
	// wheel motors should be public and static so they can be accessed by every
	// other class that needs them
	// (access the same instance of each motor each time to avoid conflicsts,
	// ex: Main.leftMotor)
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static final EV3LargeRegulatedMotor clawMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	public static final EV3LargeRegulatedMotor liftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	// public static final EV3LargeRegulatedMotor frontUsMotor = new
	// EV3LargeRegulatedMotor(LocalEV3.get().getPort("E"));
	public static Navigator nav;
	private static Navigation nav2;
	private static Odometer odo;
	private static UltrasonicPoller frontUs;
	private static UltrasonicPoller leftUs;
	private static UltrasonicPoller rightUs;
	private static float[] frontColorData;
	private static SensorModes frontColorSensor;
	public static SideUSController frontUsControl;
	public static SideUSController leftUsControl;
	public static SideUSController rightUsControl;
	private static WifiTest2 wifiTest;

	/*
	 * Sensors: - 1 Ultrasonic in the front - 3 Color sensors: 1 on the front,
	 * and 2 for the wheels ( 1 for each )
	 */
	private static final Port frontUsPort = LocalEV3.get().getPort("S3");
	private static final Port rightUsPort = LocalEV3.get().getPort("S2");
	private static final Port leftUsPort = LocalEV3.get().getPort("S1");
	private static final Port frontColorPort = LocalEV3.get().getPort("S4");
	// private static final Port rightColorPort = LocalEV3.get().getPort("S4");

	public static void main(String[] args) {
		// test();
		// From the lab instructions: Setup ultrasonic sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize
		// operating mode
		// 4. Create a buffer for the sensor data
		// @SuppressWarnings("resource")
		SensorModes frontUsSensor = new EV3UltrasonicSensor(frontUsPort);
		SampleProvider frontUsValue = frontUsSensor.getMode("Distance");
		float[] frontUsData = new float[frontUsValue.sampleSize()];

		SensorModes rightUsSensor = new EV3UltrasonicSensor(rightUsPort);
		SampleProvider rightUsValue = rightUsSensor.getMode("Distance");
		float[] rightUsData = new float[rightUsValue.sampleSize()];

		SensorModes leftUsSensor = new EV3UltrasonicSensor(leftUsPort);
		SampleProvider leftUsValue = leftUsSensor.getMode("Distance");
		float[] leftUsData = new float[leftUsValue.sampleSize()];

		frontColorSensor = new EV3ColorSensor(frontColorPort);
		SampleProvider frontColorValue = frontColorSensor.getMode("ColorID");
		frontColorData = new float[frontColorValue.sampleSize()];

		// Color sensor setup works just like US, but there are 3 of them
		// colorValue provides samples from this instance
		// colorData is the buffer in which data are returned

		// SensorModes leftColorSensor = new EV3ColorSensor(leftColorPort);
		// SensorModes rightColorSensor = new EV3ColorSensor(rightColorPort);

		// SampleProvider leftColorvalue = leftColorSensor.getMode("RGB");
		// SampleProvider rightColorvalue = rightColorSensor.getMode("RGB");

		// float[] leftColorData = new float[leftColorvalue.sampleSize()];
		// float[] rightColorData = new float[rightColorvalue.sampleSize()];

		leftMotor.setAcceleration(Constants.WHEEL_ACCELERATION);
		rightMotor.setAcceleration(Constants.WHEEL_ACCELERATION);

		rightUsControl = new SideUSController(6, "right");
		rightUs = new UltrasonicPoller(rightUsValue, rightUsData, rightUsControl);
		leftUsControl = new SideUSController(5, "left");
		leftUs = new UltrasonicPoller(leftUsValue, leftUsData, leftUsControl);


		frontUsControl = new SideUSController(4, "front");
		frontUs = new UltrasonicPoller(frontUsValue, frontUsData, frontUsControl);

		// rightUs.start();
		// leftUs.start();
		frontUs.start();
		leftUs.start();
		rightUs.start();

		odo = new Odometer(leftMotor, rightMotor, Constants.ODOMETER_INTERVAL, true);
		//comment out the setPostiion if localization is to be done
		//odo.setPosition(new double[]{0,0,0}, new boolean[]{true, true, true});
		odo.start();

		nav2 = new Navigation(odo);	







		// public USLocalizer(Navigator nav, Odometer odo, SampleProvider
		// usSensor, float[] usData, LocalizationType locType) {

		//		 BasicNavigator basicNav = new BasicNavigator(odo);
		// ahmetlocalizer ahm = new ahmetlocalizer(odo, basicNav, frontUsValue,
		// frontUsData, ahmetlocalizer.LocalizationType.FALLING_EDGE,
		// rightMotor, leftMotor );
		// ahm.doLocalization();]
		
		//USLocalizer localizer = new USLocalizer(nav2, odo, frontUsValue, frontUsData, USLocalizer.LocalizationType.FALLING_EDGE);
		//localizer.doLocalization();
		wifiTest = new WifiTest2();
		USLocalizer.isComplete = true;
		//		leftMotor.stop(true);
		//		rightMotor.stop(false);


		//	Button.waitForAnyPress();

		completeCourse();

		// use threads for both sensors to have them poll continuosly

	}

	private static void completeCourse() {
		// set points to go to
		while(!USLocalizer.isComplete){
			//wait for localization to complete
		}
		
		if (wifiTest.getBSC()!=-1){
//			boolean blockBuilder = true;
			int [][] greenZoneWayPoints = new int[2][2];
			if (wifiTest.getBSC()==1){
				greenZoneWayPoints[0][0] = wifiTest.getLGZx();
				greenZoneWayPoints[0][1] = wifiTest.getLGZy();
				greenZoneWayPoints[1][0] = wifiTest.getUGZx();
				greenZoneWayPoints[1][1] = wifiTest.getUGZy();
			} else if (wifiTest.getBSC()==2){
				//switch x and y axis
				//subtract 10 from y axis 
				greenZoneWayPoints[0][0] = wifiTest.getLGZy(); 
				greenZoneWayPoints[0][1] = 10 - wifiTest.getLGZx();
				greenZoneWayPoints[1][0] = wifiTest.getUGZy();
				greenZoneWayPoints[1][1] = 10 - wifiTest.getUGZx();
			} else if (wifiTest.getBSC()==3){
				greenZoneWayPoints[0][0] = 10 - wifiTest.getUGZx();
				greenZoneWayPoints[0][1] = 10 - wifiTest.getUGZy();
				greenZoneWayPoints[1][0] = 10 - wifiTest.getLGZx();
				greenZoneWayPoints[1][1] = 10 - wifiTest.getLGZy();
			} else if (wifiTest.getBSC()==4){
				greenZoneWayPoints[0][0] = 10 - wifiTest.getUGZy();
				greenZoneWayPoints[0][1] = wifiTest.getLGZx();
				greenZoneWayPoints[1][0] = 10 - wifiTest.getLGZy();
				greenZoneWayPoints[1][1] = wifiTest.getUGZx();
			}
			
		} else if (wifiTest.getCSC()!=-1){
//			boolean garbageCollector = true;
			int [][] redZoneWayPoints = new int[2][2];
			if (wifiTest.getCSC()==1){
				redZoneWayPoints[0][0] = wifiTest.getLRZx();
				redZoneWayPoints[0][1] = wifiTest.getLRZy();
				redZoneWayPoints[1][0] = wifiTest.getURZx();
				redZoneWayPoints[1][1] = wifiTest.getURZy();
			} else if (wifiTest.getCSC()==2){
				//switch x and y axis
				//subtract 10 from y axis 
				redZoneWayPoints[0][0] = wifiTest.getLRZy(); 
				redZoneWayPoints[0][1] = 10 - wifiTest.getLRZx();
				redZoneWayPoints[1][0] = wifiTest.getURZy();
				redZoneWayPoints[1][1] = 10 - wifiTest.getURZx();
			} else if (wifiTest.getCSC()==3){
				redZoneWayPoints[0][0] = 10 - wifiTest.getURZx();
				redZoneWayPoints[0][1] = 10 - wifiTest.getURZy();
				redZoneWayPoints[1][0] = 10 - wifiTest.getLRZx();
				redZoneWayPoints[1][1] = 10 - wifiTest.getLRZy();
			} else if (wifiTest.getCSC()==4){
				redZoneWayPoints[0][0] = 10 - wifiTest.getURZy();
				redZoneWayPoints[0][1] = wifiTest.getLRZx();
				redZoneWayPoints[1][0] = 10 - wifiTest.getLRZy();
				redZoneWayPoints[1][1] = wifiTest.getURZx();
			}
		}
		
		
		
		
		
		LCD.clear(7);
		LCD.drawString("COMPLETING COURSE", 0, 7);
		//initialize the second navigator to use for moving around the field after the localization is complete
		nav = new Navigator(odo, frontUs, frontColorSensor, frontColorData);
		nav.start();
		int[][] waypoints = {/*{ 60, 30 }, { 60, 60 }, { 30, 60 }, { 0, 60 },*/ { 0, 30 },
				{ 30, 30 }, { 60, 30 }, { 60, 0 }, { 30, 0 }, { 0, 0 },/* { 0, 30 }, { 0, 60 }, { 30, 60 } */};

		for (int[] point : waypoints) {
			nav.travelTo(point[0], point[1], true);
			while (nav.isTravelling()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void test() {
		int buttonChoice;

		final TextLCD t = LocalEV3.get().getTextLCD();

		do {
			// clear the display
			t.clear();

			// ask the user whether the motors should drive in a square or float
			t.drawString("< Left | Right >", 0, 0);
			t.drawString("       |        ", 0, 1);
			t.drawString("  Move | Drive  ", 0, 2);
			t.drawString("  Arm  | Stra   ", 0, 3);
			t.drawString("       | ight   ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT && buttonChoice != Button.ID_DOWN);

		if (buttonChoice == Button.ID_LEFT) { // start all the threads

			int angle = 360 * 6;
			int claw = 370;
			liftMotor.setSpeed(400);
			// each rotation => 1.5cm
			liftMotor.rotate(-angle, false);
			clawMotor.rotate(claw);
			liftMotor.rotate(angle, false);
			liftMotor.rotate(-angle, false);
			clawMotor.rotate(-claw);
			liftMotor.rotate(angle, false);
			liftMotor.flt();

			// Sound.beepSequence();

		}

		else {

			// nav.travelTo(0,60);
			// nav.travelTo(60, 60);
			// nav.travelTo(60, 0);
			// nav.travelTo(0, 0);
			// nav.turnBy(90);
			clawMotor.rotate(-30);
		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;
		System.exit(0);
	}

}
