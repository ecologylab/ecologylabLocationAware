/**
 * 
 */
package ecologylab.sensor.network.wireless.data;

import stec.jenie.NativeException;
import ecologylab.generic.Generic;
import ecologylab.sensor.network.wireless.RunnableWiFiAdapter;
import ecologylab.sensor.network.wireless.WiFiAdapter;
import ecologylab.sensor.network.wireless.listener.WiFiStringDataListener;
import ecologylab.serialization.ElementState;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.SimplTypesScope;
import ecologylab.serialization.annotations.simpl_composite;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_scalar;
import ecologylab.serialization.formatenums.StringFormat;

/**
 * Represents the current status of a WiFi adapter, including a list of
 * WiFiConnections and the current MAC address of the connected router (if any).
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 */
@simpl_inherit public class WiFiAdapterStatus extends ElementState implements
		WiFiStringDataListener
{
	@simpl_scalar String		currentMacAddr = WiFiAdapter.NOT_ASSOCIATED;

	@simpl_composite WiFiSourceList	availableConnections	= new WiFiSourceList();

	public WiFiAdapterStatus()
	{
	}

	public String getCurrentMacAddr()
	{
		return currentMacAddr;
	}

	public WiFiSourceList getAvailableConnections()
	{
		return availableConnections;
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
				SimplTypesScope.serialize(status, System.out, StringFormat.XML);
				System.out.println();
			}
			catch (SIMPLTranslationException e1)
			{
				e1.printStackTrace();
			}
			
			Generic.sleep(250);
		}
	}
}
