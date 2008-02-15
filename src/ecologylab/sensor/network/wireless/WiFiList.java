/**
 * 
 */
package ecologylab.sensor.network.wireless;

import java.util.Collection;

import ecologylab.generic.ResourcePool;
import ecologylab.xml.ElementState;
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
public class WiFiList extends ElementState
{
	@xml_nested protected HashMapState<String, WiFiStatus>	aps	= new HashMapState<String, WiFiStatus>();

	/**
	 * 
	 */
	public WiFiList()
	{
	}

	/**
	 * Clears the ap list and adds all the currentWiFiStatuses. Order(n+m) time,
	 * where n = currentWiFiStatuses.size() and m = aps.size().
	 * 
	 * The ap list contains clones of the data in the Collection, so it is safe
	 * to modify currentWiFiStatuses after this method call.
	 * 
	 * @param currentWiFiStatuses
	 */
	public void update(Collection<WiFiStatus> currentWiFiStatuses)
	{
		for (String macAddr : aps.keySet())
		{
			this.pool().release(aps.remove(macAddr));
		}

		for (WiFiStatus w : currentWiFiStatuses)
		{
			// we need to add it, gotta get a fresh one from the pool and make
			// its data match
			WiFiStatus newW = pool().acquire();

			newW.setId(w.getId());
			newW.setMacAddr(w.getMacAddr());
			newW.setSignalStrength(w.getSignalStrength());

			aps.add(newW);
		}
	}

	/**
	 * The source for all WiFiStatus objects stored in the map. This object
	 * should be accessed through the given method, so that it can be lazilly
	 * instantiated.
	 */
	private WiFiStatusPool	pool				= null;

	/**
	 * Used to ensure that lazy instantiation of the WiFiStatusPool takes place
	 * in a thread-safe way. This lightweight object must be synchronized in
	 * order to instantiate the pool.
	 */
	private Object				poolSemaphore	= new Object();

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
	private WiFiStatusPool pool()
	{
		if (pool == null)
		{
			synchronized (poolSemaphore)
			{
				if (pool == null)
				{ // someone else might have instanitated it before here
					pool = new WiFiStatusPool(5, 2);
				}
			}
		}

		return pool;
	}

	/**
	 * Resource pool for WiFiStatus objects. Used to track the actual WiFiStatus
	 * objects used by the WiFiList. Prevents unncessary instantiation if
	 * possible.
	 * 
	 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
	 */
	class WiFiStatusPool extends ResourcePool<WiFiStatus>
	{
		public WiFiStatusPool(int initialPoolSize, int minimumPoolSize)
		{
			super(initialPoolSize, minimumPoolSize);
		}

		@Override protected void clean(WiFiStatus objectToClean)
		{
			objectToClean.setId(null);
			objectToClean.setMacAddr(null);
			objectToClean.setSignalStrength(-1);
		}

		@Override protected WiFiStatus generateNewResource()
		{
			return new WiFiStatus();
		}
	}
}
