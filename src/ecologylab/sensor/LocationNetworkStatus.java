/**
 * 
 */
package ecologylab.sensor;

import ecologylab.sensor.location.LocationStatus;
import ecologylab.sensor.network.NetworkStatus;
import ecologylab.services.logging.MixedInitiativeOp;

/**
 * Represents a moment of data from both a location sensor and a network sensor
 * with a time stamp.
 * 
 * These may be logged to create a set of data about locations, times, and
 * network status.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 */
public class LocationNetworkStatus<LOC_STATUS extends LocationStatus<?>, NET_STATUS extends NetworkStatus>
		extends MixedInitiativeOp
{
	/**
	 * The status from the location system at the moment this was recorded.
	 * Becomes frozen when the event is recorded.
	 */
	@xml_nested protected LOC_STATUS	locationStatus;

	/**
	 * The status from the network system at the moment this was recorded.
	 * Becomes frozen when the event is recorded.
	 */
	@xml_nested protected NET_STATUS	netStatus;

	@xml_attribute protected String	utcTime;

	/**
	 * 
	 */
	public LocationNetworkStatus()
	{
	}

	public LocationNetworkStatus(LOC_STATUS locationStatus,
			NET_STATUS netStatus, String utcTime)
	{
		this.reconfigure(locationStatus, netStatus, utcTime);
	}

	public void reconfigure(LOC_STATUS locationStatus, NET_STATUS netStatus,
			String utcTime)
	{
		this.locationStatus = locationStatus;
		this.netStatus = netStatus;
		this.utcTime = utcTime;
	}

	/**
	 * @see ecologylab.services.logging.MixedInitiativeOp#performAction(boolean)
	 */
	@Override public void performAction(boolean invert)
	{
	}
}
