/**
 * 
 */
package ecologylab.sensor.network.wireless;

import java.util.LinkedList;
import java.util.List;

import stec.jenie.NativeException;
import ecologylab.generic.Generic;
import ecologylab.generic.StartAndStoppable;
import ecologylab.sensor.network.wireless.listener.WiFiStringDataListener;

/**
 * An abstraction of the WiFi adapter interface that is configured to
 * automatically update a set of listeners at a specified interval.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 */
public class RunnableWiFiAdapter extends WiFiAdapter implements
		StartAndStoppable
{
	int												updateInterval;

	private Thread									t										= null;

	protected List<WiFiStringDataListener>	listeners							= new LinkedList<WiFiStringDataListener>();

	private Object									timerInstantiationSemaphore	= new Object();

	private boolean								running								= false;

	/**
	 * @param updateRate
	 *           the number of milliseconds to wait in between updating the
	 *           listeners on the WiFi adapter's status.
	 * @throws NativeException
	 */
	public RunnableWiFiAdapter(int updateRate)
	{
		super();

		this.updateInterval = updateRate;
	}

	public void addListener(WiFiStringDataListener newListener)
	{
		this.listeners.add(newListener);
	}

	public void removeListener(WiFiStringDataListener oldListener)
	{
		this.listeners.remove(oldListener);
	}

	/**
	 * Activates the backend DLL for WiFi adapter communication, and calls
	 * start() in order to start transmitting WiFi status data to the listeners.
	 * 
	 * @see ecologylab.sensor.network.wireless.WiFiAdapter#connect()
	 */
	@Override public void connect() throws NativeException
	{
		super.connect();

		this.start();
	}

	/**
	 * Deactivates the backend DLL for WiFi adapter communication and calls
	 * stop() to cancel transmitting data to the listeners.
	 * 
	 * @see ecologylab.sensor.network.wireless.WiFiAdapter#disconnect()
	 */
	@Override public void disconnect()
	{
		super.disconnect();

		this.stop();
	}

	/**
	 * Starts sending status data at the specified interval. This method should
	 * not be invoked directly, but should only be called through the connect()
	 * method.
	 * 
	 * @see ecologylab.generic.StartAndStoppable#start()
	 */
	public void start()
	{
		if (!this.connected())
		{
			try
			{
				this.connect();
			}
			catch (NativeException e)
			{
				e.printStackTrace();
			}
			return;
		}

		this.running = true;

		if (t == null)
		{
			synchronized (timerInstantiationSemaphore)
			{
				if (t == null)
				{
					debug("runnable wifi adapter creating timer.");
					t = new Thread(this);

					// we always only start t here
					debug("starting timer.");
					t.start();
				}
			}
		}
	}

	/**
	 * Stops sending status data at the specified interval. This method should
	 * never be invoked directly, but only by calling disconnect().
	 * 
	 * @see ecologylab.generic.StartAndStoppable#stop()
	 */
	public void stop()
	{
		if (this.connected())
		{
			this.disconnect();
			return;
		}

		this.running = false;

		if (t != null)
		{
			synchronized (t)
			{
				t = null;
			}
		}
	}

	/**
	 * Causes this to check the WiFi adapter on this computer, and to report its
	 * status to all of the listeners at the specified interval.
	 */
	public void run()
	{
		long startExecute;
		while (running)
		{
			startExecute = System.currentTimeMillis();

			try
			{
				// XXX this should be taken out when the bug in the DLL is fixed
				super.connect();
				
				String currentMac = this.getAssociatedMac();
				String newAPData = this.getAPData();
				
				// XXX this should be taken out when the bug in the DLL is fixed
				super.disconnect();

				for (WiFiStringDataListener l : this.listeners)
				{
					l.apListUpdate(newAPData);
					l.macAddressUpdate(currentMac);
				}

			}
			catch (NativeException e1)
			{
				e1.printStackTrace();
			}

			// sleep for the interval time minus however long this last invocation
			// took
			long now = System.currentTimeMillis();
			int sleepTime = (int) (this.updateInterval - (now - startExecute));

			if (sleepTime > 0)
			{
				Generic.sleep(sleepTime);
			}
			else
			{
				error("sleep time was negative: " + sleepTime + " = "
						+ this.updateInterval + " - (" + now + " - " + startExecute
						+ ")");
			}
		}
	}

	public void setUpdateInterval(int updateInterval)
	{
		this.updateInterval = updateInterval;
	}
}
