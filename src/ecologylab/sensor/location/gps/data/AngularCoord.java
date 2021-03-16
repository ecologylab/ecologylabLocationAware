/**
 * 
 */
package ecologylab.sensor.location.gps.data;

import ecologylab.serialization.ElementState;

/**
 * An object for representing a world coordinate (either latitude, or longitude), stored as a floating-point degree. Can
 * convert from deg/min/sec.
 * 
 * Also provides static methods for manipulating coordinates.
 * 
 * @author Z O. Toups (zach@ecologylab.net)
 * 
 */
public abstract class AngularCoord extends ElementState
{
	public static double fromDegMinSec(double deg, double min, double sec)
	{
		double retCoord = Math.abs(deg) + (Math.abs(min) + Math.abs(sec / 60.0)) / 60.0;

		if (deg < 0)
		{
			retCoord *= -1;
		}
		
		return retCoord;
	}

	public static double fromDegMinSecHemi(double deg, double min, double sec, char hemisphere)
	{
		double retCoord = fromDegMinSec(deg, min, sec);

		return signForHemisphere(hemisphere, retCoord);
	}

	public static double signForHemisphere(char h, double origCoord)
	{
		switch (h)
		{
		case ('S'):
		case ('s'):
		case ('W'):
		case ('w'):
			return Math.abs(origCoord) * -1.0;
		default:
			return Math.abs(origCoord);
		}
	}
	
	public static char characterForSignLat(double coord)
	{
		if(coord < 0)
			return 'S';
		else
			return 'N';
	}
	
	public static char characterForSignLon(double coord)
	{
		if(coord < 0)
			return 'W';
		else
			return 'E';
	}
}
