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
	void update(String src, GPSDatum dst);
}