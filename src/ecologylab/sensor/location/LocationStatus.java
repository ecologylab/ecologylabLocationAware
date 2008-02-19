/**
 * 
 */
package ecologylab.sensor.location;

import ecologylab.sensor.location.gps.data.GeoCoordinate;
import ecologylab.xml.ElementState;

/**
 * Represents the location data from a location sensor of some sort.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 */
public class LocationStatus extends ElementState
{
	/** The current location from the sensor. */
	@xml_nested @xml_tag("loc") protected GeoCoordinate	currentLocation;

	/** TODO make this an actual date. */
	@xml_attribute protected String				utcTime;

	@xml_attribute protected String				utcDate;

	/**
	 * 
	 */
	public LocationStatus()
	{
	}

	public GeoCoordinate getCurrentLocation()
	{
		return currentLocation;
	}

	public String getUtcTime()
	{
		return utcTime;
	}

	public String getUtcDate()
	{
		return utcDate;
	}
}
