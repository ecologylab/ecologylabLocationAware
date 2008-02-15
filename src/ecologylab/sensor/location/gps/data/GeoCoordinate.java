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
	@xml_attribute AngularCoord	lat;

	/** The longitude, expressed in degrees in double-precision degrees. */
	@xml_attribute AngularCoord	lon;

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
		this.lat = new AngularCoord(lat);
		this.lon = new AngularCoord(lon);
		this.alt = alt;
	}

	public AngularCoord getLat()
	{
		if (this.lat == null)
		{
			lat = new AngularCoord();
		}

		return lat;
	}

	public void setLat(AngularCoord lat)
	{
		this.lat = lat;

		kMLCommaDelimited = null;
	}

	public AngularCoord getLon()
	{
		if (this.lon == null)
		{
			lon = new AngularCoord();
		}

		return lon;
	}

	public void setLon(AngularCoord lon)
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

	public void setLon(double lon)
	{
		this.getLon().set(lon);
	}

	public void setLat(double lat)
	{
		this.getLat().set(lat);
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
			kMLCommaDelimited = this.lon.getCoord() + "," + this.lat.getCoord()
					+ "," + this.alt;
		}

		return kMLCommaDelimited;
	}
}
