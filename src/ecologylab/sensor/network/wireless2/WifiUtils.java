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
	
	private static int wlanInterfaceState 						= WLAN_INTERFACE_STATE_DISCONNECTED;
	
	private static String ssid 								= "";				//connected networks ssid
	
	private static int wlanSignalQuality					= 0;				//value from 0:100 specifying signal quality
	
	private static int rssi 									= 0;
	
	private static String bssid								= "00:00:00:00:00:00";	//mac address of wifi connection
	
	private static int updateInterval 						= 2000; 			//ms between updates;
	
	private static Timer t;
	
	private static boolean initialized						= false;
	
	private static ArrayList<WifiListener>	listeners	= new ArrayList<WifiListener>();
	
	private static String ipAddrString						= "";
	
	private static int interfaceState 						= INTF_OPER_STATUS_DOWN;
	
	
	static {
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
	} 
	
	native private static boolean initialize();
	
	native private static void updateStats();
	
	native public static boolean scan();
	
	private static void connectCallBack()
	{
		t.start();
		for(WifiListener listener : listeners)
		{
			listener.onConnect();
		}
	}
	
	private static void disconnectCallBack()
	{
		t.stop();
		wlanInterfaceState = WLAN_INTERFACE_STATE_DISCONNECTED;
		ssid = "";
		wlanSignalQuality = 0;
		rssi = -110;
		
		for(WifiListener listener : listeners)
		{
			listener.onDisconnect();
		}
	}
	
	public static void addListener(WifiListener listener)
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
		InetAddress addr = getAddress();
		return WifiUtils.interfaceState == INTF_OPER_STATUS_UP && addr != null && !addr.isLoopbackAddress();
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
		return (int) (((rssi + 85) / 75.0) * 100);
	}
	
	public static int getQuality()
	{
		return wlanSignalQuality;
	}
	
	public static InetAddress getAddress()
	{
		if(WifiUtils.ipAddrString == null || WifiUtils.ipAddrString.equals(""))
		{
			return null;
		} else {
			InetAddress tmp = null;
			try
			{
				tmp = InetAddress.getByName(ipAddrString);
			}
			catch (UnknownHostException e)
			{
				tmp = null;
				e.printStackTrace();
			}
			return tmp;
		}
	}
	
	public static String getStatusString()
	{
		String status = "";
		if((isConnected()))
		{
			status += "Connected to ssid: " + ssid + " with bssid: " + bssid + "\n";
			
			status += "IP-Address: " + getAddress();
			
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
