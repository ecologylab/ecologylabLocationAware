/**
 * 
 */
package ecologylab.sensor.network.wireless.gui;

import stec.jenie.NativeException;
import ecologylab.sensor.network.wireless.RunnableWiFiAdapter;

/**
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
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