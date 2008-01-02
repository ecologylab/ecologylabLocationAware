/*
 * Created on Dec 28, 2007
 */
package ecologylab.sensor.gps.data;

import ecologylab.xml.ElementState;
import ecologylab.xml.types.element.Mappable;

/**
 * Stores information about a GPS satellite (space vehicle, or SV), as reported
 * by a GPS receiver.
 * 
 * @author Zach Toups (toupsz@gmail.com)
 */
public class SVData extends ElementState implements Mappable<Integer>
{
	/** The key for hashing this object; key is the same as id. */
	private Integer				key;

	/** The identification number of the satellite. */
	@xml_attribute private int	id;

	/**
	 * The elevation from the horizon in integer degrees (0-90); -1 (null) if not
	 * tracking.
	 */
	@xml_attribute private int	elevation;

	/**
	 * The azimuth of the satellite, measured in integer degrees from true north
	 * (0-360); -1 (null) if not tracking.
	 */
	@xml_attribute private int	azimuth;

	/**
	 * The signal-to-noise ratio for the satellite's signal, in db-Hz (1-99); -1
	 * (null) if not tracking.
	 */
	@xml_attribute private int	s2nRatio;

	/**
	 * No-argument constructor for automatic XML translation.
	 */
	public SVData()
	{
		super();
	}

	/**
	 * @param id
	 * @param elevation
	 * @param azimuth
	 * @param ratio
	 */
	public SVData(int id, int elevation, int azimuth, int s2nRatio)
	{
		super();
		this.id = id;
		this.elevation = elevation;
		this.azimuth = azimuth;
		this.s2nRatio = s2nRatio;
	}
	
	public SVData(int id)
	{
		this(id, -1, -1, -1);
	}

	public Integer key()
	{
		if (key == null)
		{
			key = new Integer(this.id);
		}

		return key;
	}

	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @return the elevation
	 */
	public int getElevation()
	{
		return elevation;
	}

	/**
	 * @return the azimuth
	 */
	public int getAzimuth()
	{
		return azimuth;
	}

	/**
	 * @return the s2nRatio
	 */
	public int getS2nRatio()
	{
		return s2nRatio;
	}
}
