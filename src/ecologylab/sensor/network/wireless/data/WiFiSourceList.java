/**
 * 
 */
package ecologylab.sensor.network.wireless.data;

import ecologylab.generic.ResourcePool;
import ecologylab.sensor.network.NetworkList;
import ecologylab.sensor.network.wireless.listener.WiFiStringDataListener;
import ecologylab.xml.xml_inherit;

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
@xml_inherit public class WiFiSourceList extends NetworkList<WiFiSource>
		implements WiFiStringDataListener
{
	/**
	 * 
	 */
	public WiFiSourceList()
	{
		super();
	}

	@Override public void updateFromDataString(String newData)
	{
		String[] aps = newData.split("\\*\n");

		for (String s : aps)
		{
			if (s.length() > 0)
			{
				String[] apData = s.split("\n");

				WiFiSource wfs = this.get(apData[1]);

				if (wfs == null)
				{
					wfs = new WiFiSource();
					this.put(apData[1], wfs);
				}

				wfs.updateData(s);
			}
		}
	}

	public void apListUpdate(String newData)
	{
		this.updateFromDataString(newData);
	}

	public void macAddressUpdate(String newData)
	{
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
	@Override protected WiFiStatusPool pool()
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
	class WiFiStatusPool extends ResourcePool<WiFiSource>
	{
		public WiFiStatusPool(int initialPoolSize, int minimumPoolSize)
		{
			super(initialPoolSize, minimumPoolSize);
		}

		@Override protected void clean(WiFiSource objectToClean)
		{
			objectToClean.setId(null);
			objectToClean.setMacAddr(null);
			objectToClean.setSignalStrength(-1);
		}

		@Override protected WiFiSource generateNewResource()
		{
			return new WiFiSource();
		}
	}
}
