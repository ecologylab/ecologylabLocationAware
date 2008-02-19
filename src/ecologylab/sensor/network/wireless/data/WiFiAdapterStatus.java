/**
 * 
 */
package ecologylab.sensor.network.wireless.data;

import stec.jenie.NativeException;
import ecologylab.generic.Generic;
import ecologylab.sensor.network.wireless.RunnableWiFiAdapter;
import ecologylab.sensor.network.wireless.WiFiAdapter;
import ecologylab.sensor.network.wireless.listener.WiFiStringDataListener;
import ecologylab.xml.ElementState;
import ecologylab.xml.XMLTranslationException;
import ecologylab.xml.xml_inherit;

/**
 * Represents the current status of a WiFi adapter, including a list of
 * WiFiConnections and the current MAC address of the connected router (if any).
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 */
@xml_inherit public class WiFiAdapterStatus extends ElementState implements
		WiFiStringDataListener
{
	@xml_attribute String		currentMacAddr;

	@xml_nested WiFiSourceList	availableConnections	= new WiFiSourceList();

	public WiFiAdapterStatus()
	{
	}

	public void apListUpdate(String newData)
	{
		this.availableConnections.apListUpdate(newData);
	}

	public void macAddressUpdate(String newData)
	{
		this.currentMacAddr = newData;
	}

	/**
	 * Indicates whether or not the WiFiAdapterStatus is connected to a wireless
	 * network.
	 * 
	 * @return
	 */
	public boolean connected()
	{
		return !WiFiAdapter.NOT_ASSOCIATED.equals(this.currentMacAddr);
	}

	public static void main(String[] args) throws NativeException
	{
		final WiFiAdapterStatus status = new WiFiAdapterStatus();
		RunnableWiFiAdapter adapter = new RunnableWiFiAdapter(500);

		adapter.addListener(status);

		System.out.println("starting listening to wifi");
		adapter.connect();
		
		for (int i = 0; i < 100; i++)
		{
			try
			{
				status.translateToXML(System.out);
				System.out.println();
			}
			catch (XMLTranslationException e1)
			{
				e1.printStackTrace();
			}
			
			Generic.sleep(250);
		}
	}
}
