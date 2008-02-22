/**
 * 
 */
package ecologylab.sensor.location.gps;

import ecologylab.sensor.BaseSensorTranslations;
import ecologylab.xml.ElementState;
import ecologylab.xml.XMLTranslationException;
import ecologylab.xml.types.element.ArrayListState;

/**
 * Ecapsulates information about a GPS device, including its specifications.
 * 
 * This information is not generally needed for working with a GPS, but may be
 * helpful in some data analysis and display.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public class GPSDeviceProfile extends ElementState
{
	@xml_attribute private String	make;

	@xml_attribute private String	model;

	/** Maximum baud for communicating with the device. */
	@xml_attribute private int		maxBaud;

	/**
	 * Expected GPS accuracy in meters when using differential GPS.
	 * 
	 * Example: <b>&lt;2 meter accuracy</b>, 95% typical
	 */
	@xml_attribute private float	dgpsAccuracy;

	/**
	 * (hope I got this right!) Expected likelihood that the accuracy is correct
	 * using differential GPS.
	 * 
	 * Example: &lt;2 meter accuracy, <b>95% typical</b>
	 */
	@xml_attribute private float	dgpsPrecision;

	/**
	 * Expected GPS accuracy in meters when using non-differential GPS.
	 * 
	 * Example: <b>&lt;2 meter accuracy</b>, 95% typical
	 */
	@xml_attribute private float	gpsAccuracy;

	/**
	 * (hope I got this right!) Expected likelihood that the accuracy is correct
	 * using non-differential GPS.
	 * 
	 * Example: &lt;2 meter accuracy, <b>95% typical</b>
	 */
	@xml_attribute private float	gpsPrecision;

	@xml_attribute private int		channels;

	/**
	 * 
	 */
	public GPSDeviceProfile()
	{
	}

	public GPSDeviceProfile(String make, String model, int maxBaud,
			float dgpsAccuracy, float dgpsPrecision, float gpsAccuracy,
			float gpsPrecision, int channels)
	{
		this.make = make;
		this.model = model;
		this.maxBaud = maxBaud;
		this.dgpsAccuracy = dgpsAccuracy;
		this.dgpsPrecision = dgpsPrecision;
		this.gpsAccuracy = gpsAccuracy;
		this.gpsPrecision = gpsPrecision;
		this.channels = channels;
	}

	@SuppressWarnings("unchecked") public static void main(String[] args) throws XMLTranslationException
	{
		ArrayListState<GPSDeviceProfile> profiles = (ArrayListState<GPSDeviceProfile>) ElementState
				.translateFromXML("config/gpsProfiles.xml", BaseSensorTranslations
						.get());
		
		profiles.translateToXML(System.out);
	}

	/**
	 * @return the make
	 */
	public String getMake()
	{
		return make;
	}

	/**
	 * @return the model
	 */
	public String getModel()
	{
		return model;
	}

	/**
	 * @return the maxBaud
	 */
	public int getMaxBaud()
	{
		return maxBaud;
	}

	/**
	 * @return the dgpsAccuracy
	 */
	public float getDgpsAccuracy()
	{
		return dgpsAccuracy;
	}

	/**
	 * @return the dgpsPrecision
	 */
	public float getDgpsPrecision()
	{
		return dgpsPrecision;
	}

	/**
	 * @return the gpsAccuracy
	 */
	public float getGpsAccuracy()
	{
		return gpsAccuracy;
	}

	/**
	 * @return the gpsPrecision
	 */
	public float getGpsPrecision()
	{
		return gpsPrecision;
	}

	/**
	 * @return the channels
	 */
	public int getChannels()
	{
		return channels;
	}
}
