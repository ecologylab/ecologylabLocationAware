package ecologylab.sensor;

import ecologylab.sensor.location.Location;
import ecologylab.sensor.location.LocationStatus;
import ecologylab.sensor.location.gps.GPSDeviceProfile;
import ecologylab.sensor.location.gps.data.AngularCoord;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.sensor.location.gps.data.GeoCoordinate;
import ecologylab.sensor.location.gps.data.SVData;
import ecologylab.sensor.network.NetworkList;
import ecologylab.sensor.network.NetworkStatus;
import ecologylab.sensor.network.wireless.data.WiFiAdapterStatus;
import ecologylab.sensor.network.wireless.data.WiFiSourceList;
import ecologylab.sensor.network.wireless.data.WirelessNetwork;
import ecologylab.serialization.SimplTypesScope;
import ecologylab.serialization.types.element.ElementTypeTranslationsProvider;
import ecologylab.services.logging.WiFiGPSStatusOp;

/**
 * Provide XML translation mappings for use with sensors.
 * 
 * @author Zachary O. Toups (toupsz@gmail.com)
 */
public class BaseSensorTranslations
{
	public static final String	PACKAGE_NAME	= "ecologylab.serialization.library.kml";

	public static final Class	TRANSLATIONS[]	=
															{ 
		WiFiGPSStatusOp.class,

															NetworkList.class, WiFiSourceList.class,

															WiFiAdapterStatus.class,
			WirelessNetwork.class, NetworkStatus.class,

			GPSDeviceProfile.class,
			
			Location.class, LocationStatus.class, SVData.class, GPSDatum.class,
			GeoCoordinate.class, AngularCoord.class };

	/**
	 * This accessor will work from anywhere, in any order, and stay efficient.
	 * 
	 * @return TranslationSpace for basic ecologylab.oodss
	 */
	public static SimplTypesScope get()
	{
		return SimplTypesScope.get(PACKAGE_NAME, ElementTypeTranslationsProvider.get(),
				TRANSLATIONS);
	}
}
