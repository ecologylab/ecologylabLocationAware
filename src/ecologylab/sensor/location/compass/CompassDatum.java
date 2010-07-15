package ecologylab.sensor.location.compass;

import ecologylab.serialization.ElementState;

public class CompassDatum extends ElementState
{
	@simpl_scalar
	/**
	 * The current heading in degrees
	 */
	private float heading;
	
	@simpl_scalar
	/**
	 * The current pitch in degrees
	 */
	private float pitch;
	
	@simpl_scalar
	/**
	 * The current roll n degrees
	 */
	private float roll;
	
	@simpl_scalar
	/**
	 * The current temperature of the 
	 */
	private float temp;
	
	public CompassDatum()
	{
		this(0,0,0,0);
	}
	
	public CompassDatum (float heading, float pitch, float roll, float temp)
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
	
	public void conformTo(CompassDatum data)
	{
		this.heading = data.heading;
		this.pitch = data.pitch;
		this.roll = data.roll;
		this.temp = data.temp;
	}
}
