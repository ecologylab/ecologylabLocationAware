/**
 * 
 */
package ecologylab.sensor.network.wireless.data;

import ecologylab.xml.XMLTranslationException;
import ecologylab.xml.xml_inherit;
import ecologylab.xml.types.element.Mappable;

/**
 * Represents a moment of wifi status data for a potential WiFi connection.
 * 
 * Inherited field, id, contains the ssid for the network in question.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 */
@xml_inherit public class WiFiSource extends WirelessNetwork implements
		Mappable<String>
{
	public static final String			AD_HOC			= "AD_HOC";

	public static final String			ACCESS_POINT	= "ACCESS_POINT";

	public static final String			SECURED			= "SECURED";

	public static final String			UNSECURED		= "UNSECURED";
	
	/** The operating channel for the network. */
	@xml_attribute protected int		channel;

	/** The type of the network; either AD_HOC or ACCESS_POINT. */
	@xml_attribute protected String	networkType;

	/** The security for the network; either SECURED or UNSECURED. */
	@xml_attribute protected String	security;

	public WiFiSource()
	{
	}

	public WiFiSource(String id)
	{
		super(id);
	}

	public WiFiSource(String id, String macAddr)
	{
		this(id);

		this.macAddr = macAddr;
	}

	public <NS extends WiFiSource> void conformTo(NS that)
	{
		super.conformTo(that);

		this.channel = that.channel;
		this.networkType = that.networkType;
		this.security = that.security;
	}

	/**
	 * A set of new data for the wifi status in the form of:
	 * 
	 * SSID
	 * 
	 * MAC address of SSID
	 * 
	 * Signal strength in dBm
	 * 
	 * Signal strength % based on manufacturer specification of the wireless card
	 * 
	 * Broadcasting channel
	 * 
	 * ACCESS_POINT or AD_HOC (access point or ad hoc network source)
	 * 
	 * SECURED / UNSECURED
	 * 
	 * @param newData
	 */
	public void updateData(String newData)
	{
		String[] data = newData.split("\n");

		this.id = data[0];
		this.macAddr = data[1];
		this.signalStrength = Integer.parseInt(data[2]);
		this.signalStrengthPercent = Integer.parseInt(data[3]);
		this.channel = Integer.parseInt(data[4]);
		this.networkType = data[5];
		this.security = data[6];
	}

	public static void main(String[] args) throws XMLTranslationException
	{
		WiFiSource w = new WiFiSource();

		w.updateData("Snakes on a Plane\r\n" + "00:18:39:d0:4d:43\r\n"
				+ "-40\r\n" + "94\r\n" + "8\r\n" + "ACCESS_POINT\r\n" + "SECURED");

		System.out.println(w.translateToXML());
	}

	public int getChannel()
	{
		return channel;
	}

	public String getNetworkType()
	{
		return networkType;
	}

	public String getSecurity()
	{
		return security;
	}
}
