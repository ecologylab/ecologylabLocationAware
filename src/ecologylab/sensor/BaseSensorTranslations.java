package ecologylab.sensor;

import ecologylab.sensor.location.Location;
import ecologylab.sensor.location.LocationStatus;
import ecologylab.sensor.location.gps.data.AngularCoord;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.sensor.location.gps.data.GeoCoordinate;
import ecologylab.sensor.location.gps.data.SVData;
import ecologylab.sensor.network.NetworkList;
import ecologylab.sensor.network.NetworkStatus;
import ecologylab.sensor.network.wireless.data.WiFiAdapterStatus;
import ecologylab.sensor.network.wireless.data.WiFiSourceList;
import ecologylab.sensor.network.wireless.data.WirelessNetwork;
import ecologylab.services.logging.WiFiGPSStatusOp;
import ecologylab.xml.TranslationSpace;
import ecologylab.xml.types.element.ElementTypeTranslations;

/**
 * Provide XML translation mappings for use with sensors.
 * 
 * @author Zachary O. Toups (toupsz@gmail.com)
 */
public class BaseSensorTranslations
{
	public static final String	PACKAGE_NAME	= "ecologylab.xml.library.kml";

	public static final Class	TRANSLATIONS[]	=
															{ WiFiGPSStatusOp.class,

															NetworkList.class, WiFiSourceList.class,

															WiFiAdapterStatus.class,
			WirelessNetwork.class, NetworkStatus.class,

			Location.class, LocationStatus.class, SVData.class, GPSDatum.class,
			GeoCoordinate.class, AngularCoord.class };

	/**
	 * This accessor will work from anywhere, in any order, and stay efficient.
	 * 
	 * @return TranslationSpace for basic ecologylab.services
	 */
	public static TranslationSpace get()
	{
		return TranslationSpace.get(PACKAGE_NAME, TRANSLATIONS,
				ElementTypeTranslations.get());
	}
}