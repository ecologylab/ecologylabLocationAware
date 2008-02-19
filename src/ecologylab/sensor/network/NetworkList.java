/**
 * 
 */
package ecologylab.sensor.network;

import java.util.Collection;

import ecologylab.generic.ResourcePool;
import ecologylab.xml.xml_inherit;
import ecologylab.xml.types.element.HashMapState;

/**
 * A moment of data on wifi status objects, which are hashed according to MAC
 * address.
 * 
 * WiFiList can only be updated by providing a list of WiFiStatus objects, which
 * will be checked against the existing list by MAC address. The existing list
 * will then be updated to contain ONLY those access points provided. In this
 * way, the WiFiList should only contain the most current data, and
 * currently-undetected WiFi networks will not be in the list.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 */
@xml_inherit public abstract class NetworkList<NET extends NetworkStatus>
		extends HashMapState<String, NET>
{
	/**
	 * 
	 */
	public NetworkList()
	{
		super();
	}

	/**
	 * Interprets a String of data into data for a network list.
	 * 
	 * @param newData
	 */
	public abstract void updateFromDataString(String newData);

	/**
	 * Clears the ap list and adds all the currentWiFiStatuses. Order(n+m) time,
	 * where n = currentWiFiStatuses.size() and m = aps.size().
	 * 
	 * The ap list contains clones of the data in the Collection, so it is safe
	 * to modify currentWiFiStatuses after this method call.
	 * 
	 * @param currentWiFiStatuses
	 */
	public void update(Collection<NET> currentWiFiStatuses)
	{
		for (String macAddr : keySet())
		{
			this.pool().release(remove(macAddr));
		}

		for (NetworkStatus w : currentWiFiStatuses)
		{
			// we need to add it, gotta get a fresh one from the pool and make
			// its data match
			NET newW = pool().acquire();

			newW.conformTo(w);

			add(newW);
		}
	}

	/**
	 * The proper way to access the pool object in this. Use of this method, only
	 * when necessary, will ensure that pool will only be instaniated if
	 * necessary.
	 * 
	 * This allows a WiFiList that is, for example, sent over the network, to
	 * only have pool instantiated if it updates the list of access points.
	 * 
	 * @return
	 */
	protected abstract ResourcePool<NET> pool();
}