/**
 * 
 */
package ecologylab.sensor.location.gps.data.dataset;

import ecologylab.sensor.location.gps.data.GPSDatum;

/**
 * @author Z O. Toups (zach@ecologylab.net)
 * 
 */
public interface GPSDataFieldBase
{
	/**
	 * Updates dst's internal data by parsing src according to the interpretation
	 * of the current mode.
	 * 
	 * @param src
	 * @param dst
	 */
	void update(String src, GPSDatum dst);
	
}