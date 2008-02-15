/**
 * 
 */
package ecologylab.sensor;

import ecologylab.services.logging.MixedInitiativeOp;

/**
 * Represents a moment of data from both a location sensor and a network sensor
 * with a time stamp.
 * 
 * These may be logged to create a set of data about locations, times, and
 * network status.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public class LocationNetworkStatus extends MixedInitiativeOp
{

	/**
	 * 
	 */
	public LocationNetworkStatus()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see ecologylab.services.logging.MixedInitiativeOp#performAction(boolean)
	 */
	@Override public void performAction(boolean invert)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(Object o)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, Object element)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public Object set(int index, Object element)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
