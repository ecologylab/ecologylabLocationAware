/**
 * 
 */
package ecologylab.sensor.location.gps.listener;


/**
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public interface NMEAStringListener
{
	public void processIncomingNMEAString(String gpsDataString);
}
