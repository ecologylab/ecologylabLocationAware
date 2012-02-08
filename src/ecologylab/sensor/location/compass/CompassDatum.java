package ecologylab.sensor.location.compass;

import java.util.LinkedList;
import java.util.List;

import ecologylab.serialization.ElementState;
import ecologylab.serialization.annotations.simpl_scalar;

/**
 * 
 * @author William A. Hamilton (bill@ecologylab.net)
 * @author Zachary O. Dugas Toups (zach@ecologylab.net)
 */
public class CompassDatum extends ElementState
{
	/** The current heading in degrees. */
	@simpl_scalar
	private float											heading;

	/** The current pitch in degrees. */
	@simpl_scalar
	private float											pitch;

	/** The current roll in degrees. */
	@simpl_scalar
	private float											roll;

	/** The current temperature of the sensor. */
	@simpl_scalar
	private float											temp;

	/** The total acceleration, measured in G's */
	@simpl_scalar
	private float											totAcc;

	/** The current acceleration in the X direction, measured in G's */
	@simpl_scalar
	private float											accX;

	/** The current acceleration in the Y direction, measured in G's */
	@simpl_scalar
	private float											accY;

	/** The current acceleration in the Z direction, measured in G's */
	@simpl_scalar
	private float											accZ;

	private List<CompassDataListener>	compassDataUpdatedListeners;

	public CompassDatum()
	{
		this(0, 0, 0, 0);
	}

	public CompassDatum(float heading, float pitch, float roll, float temp)
	{
		this.heading = heading;
		this.pitch = pitch;
		this.roll = roll;
		this.temp = temp;
	}

	public float getHeading()
	{
		return heading;
	}

	public void setHeading(float heading)
	{
		this.heading = heading;
		fireCompassDataUpdatedEvent();
	}

	public float getPitch()
	{
		return pitch;
	}

	public void setPitch(float pitch)
	{
		this.pitch = pitch;
		fireCompassDataUpdatedEvent();
	}

	public float getRoll()
	{
		return roll;
	}

	public void setRoll(float roll)
	{
		this.roll = roll;
		fireCompassDataUpdatedEvent();
	}

	public float getTemp()
	{
		return temp;
	}

	public void setTemp(float temp)
	{
		this.temp = temp;
		fireCompassDataUpdatedEvent();
	}

	/**
	 * @return the totAcc
	 */
	public float getTotAcc()
	{
		return totAcc;
	}

	/**
	 * @param totAcc
	 *          the totAcc to set
	 */
	public void setTotAcc(float totAcc)
	{
		this.totAcc = totAcc;
	}

	/**
	 * @return the accX
	 */
	public float getAccX()
	{
		return accX;
	}

	/**
	 * @param accX
	 *          the accX to set
	 */
	public void setAccX(float accX)
	{
		this.accX = accX;
		fireCompassDataUpdatedEvent();
	}

	/**
	 * @return the accY
	 */
	public float getAccY()
	{
		return accY;
	}

	/**
	 * @param accY
	 *          the accY to set
	 */
	public void setAccY(float accY)
	{
		this.accY = accY;
		fireCompassDataUpdatedEvent();
	}

	/**
	 * @return the accZ
	 */
	public float getAccZ()
	{
		return accZ;
	}

	/**
	 * @param accZ
	 *          the accZ to set
	 */
	public void setAccZ(float accZ)
	{
		this.accZ = accZ;
	}

	public void conformTo(CompassDatum data)
	{
		this.heading = data.heading;
		this.pitch = data.pitch;
		this.roll = data.roll;
		this.temp = data.temp;

		this.accX = data.accX;
		this.accY = data.accY;
		this.accZ = data.accZ;

		fireCompassDataUpdatedEvent();
	}

	public void addCompassDataListener(CompassDataListener l)
	{
		if (this.compassDataUpdatedListeners == null)
		{
			synchronized (this)
			{
				if (this.compassDataUpdatedListeners == null)
				{
					this.compassDataUpdatedListeners = new LinkedList<CompassDataListener>();
				}
			}
		}

		synchronized (this.compassDataUpdatedListeners)
		{
			this.compassDataUpdatedListeners.add(l);
		}
	}

	public void fireCompassDataUpdatedEvent()
	{
		if (this.compassDataUpdatedListeners != null)
			synchronized (this.compassDataUpdatedListeners)
			{
				for (CompassDataListener l : this.compassDataUpdatedListeners)
				{
					l.compassDataUpdated(this);
				}
			}
	}
}
