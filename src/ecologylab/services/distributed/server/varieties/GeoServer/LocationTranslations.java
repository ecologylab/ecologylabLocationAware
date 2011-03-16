package ecologylab.services.distributed.server.varieties.GeoServer;

import ecologylab.oodss.messages.DefaultServicesTranslations;
import ecologylab.sensor.location.compass.CompassDatum;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.sensor.location.gps.data.SVData;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.TranslationScope;
import ecologylab.services.messages.LocationDataRequest;
import ecologylab.services.messages.LocationDataResponse;

public class LocationTranslations
{
	public static Class[]	LOCATION_MESSAGE_CLASSES	=
																									{ LocationDataRequest.class,
			LocationDataResponse.class, CompassDatum.class, GPSDatum.class, SVData.class };

	public static TranslationScope get()
	{
		TranslationScope trans = TranslationScope.get("Location_Scope", DefaultServicesTranslations
				.get(), LOCATION_MESSAGE_CLASSES);
		return trans;

	}
}
