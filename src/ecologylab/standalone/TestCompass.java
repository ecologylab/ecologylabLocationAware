package ecologylab.standalone;

import java.io.IOException;
import java.util.TooManyListenersException;

import ecologylab.sensor.location.NMEAReader;
import ecologylab.sensor.location.compass.CompassDataListener;
import ecologylab.sensor.location.compass.CompassDataUpdater;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

public class TestCompass implements CompassDataListener
{

	public void compassDataUpdated(float heading, float pitch, float roll,
			float temp)
	{
		System.out.println("Heading: " + heading + "\tPitch: " + pitch + "\tRoll: " + roll + "\tTemp: " + temp);
	}
	
	public static void main(String[] args) throws IOException
	{
		NMEAReader compass = null;
		try
		{
			compass = new NMEAReader("COM6", 9600);
		}
		catch (NoSuchPortException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CompassDataUpdater compassUpdater = new CompassDataUpdater();
		
		compass.addGPSDataListener(compassUpdater);
		
		compassUpdater.addCompassDataListener(new TestCompass());
		
		try
		{
			compass.connect();
		}
		catch (PortInUseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnsupportedCommOperationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TooManyListenersException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
