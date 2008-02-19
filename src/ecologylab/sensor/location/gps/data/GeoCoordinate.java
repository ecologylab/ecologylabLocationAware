/**
 * 
 */
package ecologylab.sensor.location.gps.data;

import ecologylab.sensor.location.Location;
import ecologylab.xml.xml_inherit;

/**
 * An object for representing a set of 3d coordinates on the earth's surface:
 * latitude, longitude, and altitude.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 */
@xml_inherit public class GeoCoordinate extends Location
{
	/** The latitude, expressed in degrees in double-precision degrees. */
	@xml_attribute double	lat;

	/** The longitude, expressed in degrees in double-precision degrees. */
	@xml_attribute double	lon;

	/** The altitude, expressed in meters. */
	@xml_attribute double			alt;

	/**
	 * 
	 */
	public GeoCoordinate()
	{
	}

	public GeoCoordinate(double lat, double lon, double alt)
	{
		this.lat = lat;
		this.lon = lon;
		this.alt = alt;
	}

	public double getLat()
	{
		return lat;
	}

	public void setLat(double lat)
	{
		this.lat = lat;

		kMLCommaDelimited = null;
	}

	public double getLon()
	{
		return lon;
	}

	public void setLon(double lon)
	{
		this.lon = lon;

		kMLCommaDelimited = null;
	}

	public double getAlt()
	{
		return alt;
	}

	public void setAlt(double alt)
	{
		this.alt = alt;

		kMLCommaDelimited = null;
	}

	/**
	 * A String reprsenting the current data for KML; cached for re-use and
	 * computed only when needed.
	 */
	String	kMLCommaDelimited	= null;

	/**
	 * Get the set of coordinates, serialized for use in KML / Google Earth.
	 * 
	 * @return
	 */
	public String getKMLCommaDelimitedString()
	{
		if (kMLCommaDelimited == null)
		{
			kMLCommaDelimited = this.lon + "," + this.lat
					+ "," + this.alt;
		}

		return kMLCommaDelimited;
	}
}
