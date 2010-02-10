/**
 * 
 */
package ecologylab.sensor.network.wireless.listener;

/**
 * @author Zachary O. Toups (zach@ecologylab.net)
 */
public interface WiFiStringDataListener
{
	public void apListUpdate(String newData);
	public void macAddressUpdate(String newData);
}
