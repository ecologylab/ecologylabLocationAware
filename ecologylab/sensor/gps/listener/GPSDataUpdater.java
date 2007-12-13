/**
 * 
 */
package ecologylab.sensor.gps.listener;

import ecologylab.generic.Debug;
import ecologylab.sensor.gps.data.GPSDatum;

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
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see ecologylab.sensor.gps.listener.GPSDataListener#readGPSData(java.lang.String)
	 */
	public void readGPSData(String gpsDataString)
	{
		datum.integrateGPSData(gpsDataString);
		System.out.println(datum.toString());
	}

}
