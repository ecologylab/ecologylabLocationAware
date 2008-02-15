/**
 * 
 */
package ecologylab.sensor.location.gps.data;

import ecologylab.xml.ElementState;

/**
 * An object for representing a world coordinate (either latitude, or longitude), stored as a floating-point degree. Can
 * convert from deg/min/sec.
 * 
 * Also provides static methods for manipulating coordinates.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public class AngularCoord extends ElementState implements Comparable<AngularCoord>
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

	public static void main(String args[])
	{
		AngularCoord w1 = new AngularCoord(10.123278);

		AngularCoord w2 = new AngularCoord(10, 7.38, 1);

		System.out.println(w1.getCoord() + " deg.");
		System.out.println(w2.getCoord() + " deg.");
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

	protected @xml_attribute double	coord;

	/**
	 * 
	 */
	public AngularCoord()
	{
	}

	/**
	 * Constructs a new WorldCoord with the specified coordinate value in factional degrees.
	 * 
	 * @param coord
	 */
	public AngularCoord(double coord)
	{
		this.coord = coord;
	}

	/**
	 * Specifies a new WorldCoord object in terms of degrees, minutes, and seconds. To specify hemisphere, degrees MAY be
	 * negative, minutes and seconds may not be. deg, min, and/or sec may be floating numbers, in which case they are
	 * added (so 1.5 deg, 30 min will result in 2.0 deg).
	 * 
	 * @param deg
	 * @param min
	 */
	public AngularCoord(double deg, double min, double sec)
	{
		this.coord = fromDegMinSec(deg, min, sec);
	}

	/**
	 * As WorldCoord(double, double, double), but ensures that the final sign is set based on hemisphere.
	 * 
	 * @param deg
	 * @param min
	 * @param sec
	 * @param hemisphere -
	 *           a character indicating the hemisphere for the coordinate, North and East are positive, South and West
	 *           are negative.
	 */
	public AngularCoord(double deg, double min, double sec, char hemisphere)
	{
		this(deg, min, sec);

		this.setHemisphere(hemisphere);
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(AngularCoord that)
	{
		return (int) (this.getCoord() - that.getCoord());
	}

	public double getCoord()
	{
		return coord;
	}

	/**
	 * Uses the specified hemisphere to ensure that the sign of deg is set correctly for this object.
	 * 
	 * The sign will be set to negative for S, s, W, or w, otherwise, it will be positive.
	 */
	public void setHemisphere(char h)
	{
		this.coord = signForHemisphere(h, this.coord);
	}
}
