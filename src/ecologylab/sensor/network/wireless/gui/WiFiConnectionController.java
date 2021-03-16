/**
 * 
 */
package ecologylab.sensor.network.wireless.gui;

import stec.jenie.NativeException;
import ecologylab.sensor.network.wireless.RunnableWiFiAdapter;

/**
 * @author Z O. Toups (zach@ecologylab.net)
 */
public interface WiFiConnectionController
{
	public boolean connectWiFi() throws NativeException;

	public void disconnectWiFi();

	/**
	 * @return the current WiFi adapter object
	 */
	public RunnableWiFiAdapter getWiFiAdapter();
}