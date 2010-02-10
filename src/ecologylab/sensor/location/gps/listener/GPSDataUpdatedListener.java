/**
 * 
 */
package ecologylab.sensor.location.gps.listener;

import java.util.EnumSet;

import ecologylab.sensor.location.gps.data.GPSDatum;

/**
 * For classes that need to listen for changes in a GPSDatum. The
 * getRegisteredChanges() method indicates what kinds of changes the listener is
 * interested in. Note that in order to ensure high performance, the caller may
 * not check this every time an event is generated, and so changing what data
 * the listener is interested in requires removing and re-adding the listener.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 * 
 */
public interface GPSDataUpdatedListener
{
	// TODO add others in more detail
	public enum GPSUpdateInterest
	{
		LAT_LON, ALT, SPEED, OTHERS
	}
	
	/**
	 * Indicates what changes in GPS datum should trigger a gpsDatumUpdated call
	 * for this.
	 * 
	 * @return
	 */
	public EnumSet<GPSUpdateInterest> getInterestSet();

	public void gpsDatumUpdated(GPSDatum datum);
}
