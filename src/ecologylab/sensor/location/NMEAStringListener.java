/**
 * 
 */
package ecologylab.sensor.location;


/**
 * @author Zachary O. Toups (zach@ecologylab.net)
 * 
 */
public interface NMEAStringListener
{
	public void processIncomingNMEAString(String gpsDataString);
}
