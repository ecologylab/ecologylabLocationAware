/**
 * 
 */
package ecologylab.sensor.network.wireless;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.types.element.Mappable;

/**
 * Represents a moment of wifi status data.
 * 
 * Inherited field, id, contains the ssid for the network in question.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 */
@xml_inherit public class WiFiStatus extends WirelessNetwork implements Mappable<String>
{
	@xml_attribute @xml_tag("mac") protected String	macAddr;

	public WiFiStatus()
	{
	}
	
	public WiFiStatus(String id)
	{
		super(id);
	}
	
	public WiFiStatus(String id, String macAddr)
	{
		this(id);
		
		this.macAddr = macAddr;
	}

	public String key()
	{
		return getMacAddr();
	}

	/**
	 * @return the macAddr
	 */
	public String getMacAddr()
	{
		return macAddr;
	}

	/**
	 * @param macAddr the macAddr to set
	 */
	public void setMacAddr(String macAddr)
	{
		this.macAddr = macAddr;
	}
}
