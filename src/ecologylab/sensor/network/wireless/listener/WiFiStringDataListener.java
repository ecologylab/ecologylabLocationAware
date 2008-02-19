/**
 * 
 */
package ecologylab.sensor.network.wireless.listener;

/**
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 */
public interface WiFiStringDataListener
{
	public void apListUpdate(String newData);
	public void macAddressUpdate(String newData);
}
