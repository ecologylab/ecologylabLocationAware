package ecologylab.sensor.location.compass;

import java.util.Calendar;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.sensor.location.gps.listener.GPSDataUpdatedListener;
import ecologylab.serialization.ElementState;
import ecologylab.serialization.annotations.simpl_scalar;

/**
 * 
 * @author William A. Hamilton (bill@ecologylab.net)
 * @author Z O. Toups (ztoups@nmsu.edu)
 */
public class CompassDatum extends ElementState implements Cloneable, GPSDataUpdatedListener
{
	/**
	 * The current heading in degrees. If calibrated correctly, 0 is North, and degrees increase
	 * clockwise.
	 */
	@simpl_scalar
	private float			heading;

	/** The current pitch in degrees. */
	@simpl_scalar
	private float			pitch;

	/** The current roll in degrees. */
	@simpl_scalar
	private float			roll;

	/** The current temperature of the sensor. */
	@simpl_scalar
	private float			temp;

	/** The total acceleration, measured in G's */
	@simpl_scalar
	private float			totAcc;

	/** The current acceleration in the X direction, measured in G's */
	@simpl_scalar
	private float			accX;

	/** The current acceleration in the Y direction, measured in G's */
	@simpl_scalar
	private float			accY;

	/** The current acceleration in the Z direction, measured in G's */
	@simpl_scalar
	private float			accZ;

	/** Optional field; cannot come from compass sensor; generally has to come from GPS. */
	@simpl_scalar
	private Calendar	utcTime;

	@Override
	public CompassDatum clone()
	{
		CompassDatum clone = new CompassDatum(this.heading, this.pitch, this.roll, this.temp);

		clone.totAcc = this.totAcc;
		clone.accX = this.accX;
		clone.accY = this.accY;
		clone.accZ = this.accZ;
		if (this.utcTime != null)
			clone.utcTime = (Calendar) this.utcTime.clone();

		return clone;
	}

	private List<CompassDataListener>	compassDataUpdatedListeners;

	private EnumSet<GPSUpdateInterest>	interestSet;

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

	@Override
	public EnumSet<GPSUpdateInterest> getInterestSet()
	{
		if (interestSet == null)
			interestSet = EnumSet.of(GPSUpdateInterest.OTHERS);

		return interestSet;
	}

	@Override
	public void gpsDatumUpdated(GPSDatum datum)
	{
		this.utcTime = datum.getUtcTime();
	}

	public long getTimeInMillis()
	{
		if (utcTime == null)
			return -1;
		return utcTime.getTimeInMillis();
	}

	public Calendar getTime()
	{
		return this.utcTime;
	}
}
