/**
 * 
 */
package ecologylab.sensor.location.gps.listener;

import ecologylab.generic.Debug;
import ecologylab.sensor.location.gps.data.GPSDatum;

/**
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 *
 */
public class GPSDataUpdater extends Debug implements GPSDataListener
{
	GPSDatum datum = new GPSDatum();

	/**
	 * 
	 */
	public GPSDataUpdater()
	{
	}

	/**
	 * @see ecologylab.sensor.location.gps.listener.GPSDataListener#readGPSData(java.lang.String)
	 */
	public void readGPSData(String gpsDataString)
	{
		datum.integrateGPSData(gpsDataString);
	}

	/**
	 * Convience method to add a listener to the datum contained in this object.
	 * @param l
	 */
	public void addDataUpdatedListener(GPSDataUpdatedListener l)
	{
		this.datum.addGPSDataUpdatedListener(l);
	}

	/**
	 * @return the datum
	 */
	public GPSDatum getDatum()
	{
		return datum;
	}
}
