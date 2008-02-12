/**
 * 
 */
package ecologylab.sensor.gps.data.dataset;

import ecologylab.sensor.gps.data.GPSDatum;

/**
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 *
 */
public interface GPSDataFieldBase
{
	/**
	 * Updates dst's internal data by parsing src according to the interpretation of the current mode.
	 * 
	 * @param data
	 * @param decLoc
	 * @param dst
	 */
	void update(String src, GPSDatum dst);
}