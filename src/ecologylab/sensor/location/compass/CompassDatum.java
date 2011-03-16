package ecologylab.sensor.location.compass;

import ecologylab.serialization.ElementState;

/**
 * 
 * @author William A. Hamilton (bill@ecologylab.net)
 * @author Zachary O. Dugas Toups (zach@ecologylab.net)
 */
public class CompassDatum extends ElementState
{
	/** The current heading in degrees. */
	@simpl_scalar
	private float	heading;

	/** The current pitch in degrees. */
	@simpl_scalar
	private float	pitch;

	/** The current roll in degrees. */
	@simpl_scalar
	private float	roll;

	/** The current temperature of the sensor. */
	@simpl_scalar
	private float	temp;

	/** The total acceleration, measured in G's */
	@simpl_scalar
	private float	totAcc;
	
	/** The current acceleration in the X direction, measured in G's */
	@simpl_scalar
	private float	accX;

	/** The current acceleration in the Y direction, measured in G's */
	@simpl_scalar
	private float	accY;

	/** The current acceleration in the Z direction, measured in G's */
	@simpl_scalar
	private float	accZ;
	
	

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
	}

	public float getPitch()
	{
		return pitch;
	}

	public void setPitch(float pitch)
	{
		this.pitch = pitch;
	}

	public float getRoll()
	{
		return roll;
	}

	public void setRoll(float roll)
	{
		this.roll = roll;
	}

	public float getTemp()
	{
		return temp;
	}

	public void setTemp(float temp)
	{
		this.temp = temp;
	}

	/**
	 * @return the totAcc
	 */
	public float getTotAcc()
	{
		return totAcc;
	}

	/**
	 * @param totAcc the totAcc to set
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
	}
}
