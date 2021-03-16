/**
 * 
 */
package ecologylab.sensor.location;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ecologylab.sensor.location.gps.data.GeoCoordinate;
import ecologylab.serialization.annotations.simpl_classes;
import ecologylab.serialization.annotations.simpl_format;
import ecologylab.serialization.annotations.simpl_scalar;

/**
 * Represents the location data from a location sensor of some sort.
 * 
 * @author Z O. Toups (zach@ecologylab.net)
 */
public class LocationStatus extends GeoCoordinate
{
	/**
	 * Combines the date and time from GPS RMC and GGA data in Zulu time. The formatter specifies the
	 * RMC date format (but with 4-digit year), followed by a space, followed by the time in GGA/RMC
	 * format, and appends time zone.
	 */
	@simpl_scalar
	@simpl_format("yyyyMMdd HHmmss.SSS zzzzz")
	@simpl_classes(
	{ Calendar.class, GregorianCalendar.class })
	protected Calendar	utcTime	= null;

	/**
	 * 
	 */
	public LocationStatus()
	{
	}

	public GeoCoordinate getCurrentLocation()
	{
		return this;
	}

	public Calendar getUtcTime()
	{
		return utcTime;
	}
}
