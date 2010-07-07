/**
 * 
 */
package ecologylab.sensor.network.wireless.data;

import ecologylab.sensor.network.NetworkStatus;
import ecologylab.serialization.simpl_inherit;

/**
 * Extends Network to provide signal strength information.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 */
@simpl_inherit public abstract class WirelessNetwork extends NetworkStatus
{
	/** Signal strength to the specified wireless access point in terms of dBm. */
	@simpl_scalar @xml_tag("ss") protected int				signalStrength				= Integer.MIN_VALUE;

	/** Signal strength as percent specified by WAP manufacturer. */
	@simpl_scalar @xml_tag("ss_percent") protected int	signalStrengthPercent	= 0;

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

	public <NS extends WirelessNetwork> void conformTo(NS that)
	{
		super.conformTo(that);
		
		this.signalStrength = that.signalStrength;
		this.signalStrengthPercent = that.signalStrengthPercent;
	}

	public int getSignalStrengthPercent()
	{
		return signalStrengthPercent;
	}
}
