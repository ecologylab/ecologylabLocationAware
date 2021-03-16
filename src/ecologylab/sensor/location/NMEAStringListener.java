/**
 * 
 */
package ecologylab.sensor.location;


/**
 * @author Z O. Toups (zach@ecologylab.net)
 * 
 */
public interface NMEAStringListener
{
	public void processIncomingNMEAString(String gpsDataString);
}
