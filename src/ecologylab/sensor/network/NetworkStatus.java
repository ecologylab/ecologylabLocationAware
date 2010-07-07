/**
 * 
 */
package ecologylab.sensor.network;

import ecologylab.serialization.ElementState;
import ecologylab.serialization.types.element.Mappable;

/**
 * Represents a data network, most likely wireless.
 * 
 * TODO Support for current data rate, etc.??
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 */
public abstract class NetworkStatus extends ElementState implements
		Mappable<String>
{
	/**
	 * The identifier for the network, for example, SSID for a wifi network. Note
	 * that id is not necessarily unique.
	 */
	@simpl_scalar protected String						id;

	/** The MAC address of the network. */
	@simpl_scalar @xml_tag("mac") protected String	macAddr;

	/**
	 * 
	 */
	public NetworkStatus()
	{
	}

	public NetworkStatus(String id)
	{
		this.id = id;
	}

	/**
	 * Updates this to match the contents of that (any nested objects are
	 * clone()'d).
	 * 
	 * Subclasses should make sure to call super.conformTo().
	 * 
	 * @param that
	 */
	public <NS extends NetworkStatus> void conformTo(NS that)
	{
		this.id = that.id;
		this.macAddr = that.macAddr;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id
	 *           the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the macAddr
	 */
	public String getMacAddr()
	{
		return macAddr;
	}

	/**
	 * @param macAddr
	 *           the macAddr to set
	 */
	public void setMacAddr(String macAddr)
	{
		this.macAddr = macAddr;
	}

	public String key()
	{
		return getMacAddr();
	}
}
