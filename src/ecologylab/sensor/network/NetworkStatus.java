/**
 * 
 */
package ecologylab.sensor.network;

import ecologylab.xml.ElementState;

/**
 * Represents a data network, most likely wireless. 
 * 
 * TODO Support for current data rate, etc.??
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 */
public abstract class NetworkStatus extends ElementState
{
	/**
	 * The identifier for the network, for example, SSID for a wifi network. Note
	 * that id is not necessarily unique.
	 */
	@xml_attribute String	id;

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
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}
}
