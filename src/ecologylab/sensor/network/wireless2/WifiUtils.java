package ecologylab.sensor.network.wireless2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import java.net.*;

import javax.swing.Timer;


public class WifiUtils implements WifiConstants
{
	private static class Updater implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			updateStats();
			updateCallBack();
		}
	}
	
	private static int interfaceState 						= WLAN_INTERFACE_STATE_DISCONNECTED;
	
	private static String ssid 								= "";				//connected networks ssid
	
	private static int wlanSignalQuality					= 0;				//value from 0:100 specifying signal quality
	
	private static int rssi 									= 0;
	
	private static String bssid								= "00:00:00:00:00:00";	//mac address of wifi connection
	
	private static NetworkInterface wirelessInterface	= null;
	
	private static int updateInterval 						= 2000; 			//ms between updates;
	
	private static Timer t;
	
	private static boolean initialized						= false;
	
	private static ArrayList<WifiListener>	listeners	= new ArrayList<WifiListener>();
	
	
	static {
		updateNetworkInterface();
		if(wirelessInterface != null)
		{
			System.loadLibrary("WifiUtils");
			if(!(initialized = initialize()))
			{
				System.err.println("Failed to initialize WifiUtils!");
			} else {
				t = new Timer(updateInterval, new Updater());
				t.setInitialDelay(0);
				t.setRepeats(true);
				t.start();
				
			}
		} else {
			System.err.println("Failed to initialize WifiUtils!");
		}
	}
	
	native private static boolean initialize();
	
	native private static void updateStats();
	
	private static void updateNetworkInterface()
	{
		Enumeration<NetworkInterface> interfaces = null;
		try
		{
			interfaces = NetworkInterface.getNetworkInterfaces();
		}
		catch (SocketException e)
		{
			System.err.println("Unable to enumerate network interfaces.");
		}
		
		if(interfaces != null)
		{
			//find first wireless interface
			while(interfaces.hasMoreElements())
			{
				NetworkInterface inf = interfaces.nextElement();
				if(inf.getDisplayName().toLowerCase().contains("wireless") ||
					inf.getDisplayName().contains("802.11"))
				{
					wirelessInterface = inf;
					break;
				}
			}
		}
	}
	
	private static void connectCallBack()
	{
		t.start();
		updateNetworkInterface();
		for(WifiListener listener : listeners)
		{
			listener.onConnect();
		}
	}
	
	private static void disconnectCallBack()
	{
		t.stop();
		updateNetworkInterface();
		interfaceState = WLAN_INTERFACE_STATE_DISCONNECTED;
		ssid = "";
		wlanSignalQuality = 0;
		rssi = -110;
		
		for(WifiListener listener : listeners)
		{
			listener.onDisconnect();
		}
	}
	
	protected static void addListener(WifiListener listener)
	{
		listeners.add(listener);
	}

	private static void updateCallBack()
	{		
		for(WifiListener listener : listeners)
		{
			listener.onUpdate();
		}
	}
	
	public static boolean isConnected()
	{
		return interfaceState == WLAN_INTERFACE_STATE_CONNECTED;
	}
	
	public static String getSSID()
	{
		return ssid;
	}
	
	public static String getBSSID()
	{
		return bssid;
	}
	
	public static int getRSSI()
	{
		return rssi;
	}
	
	public static int getRSSIPercentage()
	{
		return (int) ((rssi + 85) / 75.0);
	}
	
	public static int getQuality()
	{
		return wlanSignalQuality;
	}
	
	public static Enumeration<InetAddress> getAddresses()
	{
		return wirelessInterface.getInetAddresses();
	}
	
	public static InetAddress getAddress()
	{
		Enumeration<InetAddress> addresses = getAddresses();
		if(addresses.hasMoreElements())
			return addresses.nextElement();
		else
			return null;
	}
	
	public static String getStatusString()
	{
		String status = "";
		if((isConnected()))
		{
			status += "Connected to ssid: " + ssid + " with bssid: " + bssid + "\n";
			
			status += "IP-Addresses:\n";
			Enumeration<InetAddress> addresses = getAddresses();
			while(addresses.hasMoreElements())
			{
				status+= "\t" + addresses.nextElement() + "\n";
			}
			
			status += "\nConnection Quality: " + wlanSignalQuality + " RSSI: " + rssi;
		} else {
			status +=  "Disconnected";
		}
		return status;
	}
	
	public static boolean initialized()
	{
		return initialized;
	}
}
