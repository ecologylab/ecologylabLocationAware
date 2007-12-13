/**
 * 
 */
package ecologylab.sensor.gps.listener;

import ecologylab.sensor.gps.data.GPSDatum;

/**
 * For classes that need to listen for changes in a GPSDatum.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 *
 */
public interface GPSDataUpdatedListener
{
	public void gpsDatumUpdated(GPSDatum datum);
}
