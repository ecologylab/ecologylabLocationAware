/**
 * 
 */
package ecologylab.sensor.network.wireless;

import ecologylab.sensor.network.Network;
import ecologylab.xml.xml_inherit;

/**
 * Extends Network to provide signal strength information.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 */
@xml_inherit public abstract class WirelessNetwork extends Network
{
	/** Signal strength to the specified wireless access point in terms of dBm. */
	@xml_attribute @xml_tag("ss") protected int	signalStrength	= Integer.MIN_VALUE;

	/**
	 * 
	 */
	public WirelessNetwork()
	{
	}

	/**
	 * @param id
	 */
	public WirelessNetwork(String id)
	{
		super(id);
	}

	/** @return the signalStrength */
	public int getSignalStrength()
	{
		return signalStrength;
	}

	/**
	 * @param signalStrength
	 *           the signalStrength to set
	 */
	public void setSignalStrength(int signalStrength)
	{
		this.signalStrength = signalStrength;
	}
}
