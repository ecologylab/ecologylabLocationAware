/**
 * 
 */
package ecologylab.services.messages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ecologylab.collections.Scope;
import ecologylab.oodss.messages.RequestMessage;
import ecologylab.oodss.messages.ResponseMessage;
import ecologylab.sensor.location.compass.CompassDatum;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.serialization.ClassDescriptor;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.StringFormat;
import ecologylab.serialization.library.kml.Kml;
import ecologylab.serialization.library.kml.feature.Placemark;
import ecologylab.serialization.library.kml.geometry.Point;
import ecologylab.services.distributed.server.varieties.EarthGPSSimulatorServer;

/**
 * ReportEarthLookLocationRequest objects are generated automatically by an EarthGPSSimCSManager,
 * based on the GET parameters of a Google Earth HTTP request to the server. The GET parameters are
 * passed into the constructor, which parses them and uses them to modify a GPSDatum object in the
 * object registry.
 * 
 * It produces a KmlResponse message that is sent back to the Google Earth application, to show the
 * user where the point is located.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 */
public class ReportEarthLookLocationRequest extends RequestMessage
{
	float									lat;

	float									lon;

	float									heading;

	float									tilt;

	final static Pattern	EARTH_GET_PATTERN	= Pattern
																							.compile("GET /\\?CAMERA=([-\\.\\p{Digit}]*),([-\\.\\p{Digit}]*),([-\\.\\p{Digit}]*),([-\\.\\p{Digit}]*) HTTP/1\\.1");

	long									timePoint;

	/**
	 * 
	 */
	public ReportEarthLookLocationRequest(String getRequestFromEarth)
	{
		timePoint = System.currentTimeMillis();

		// debug(getRequestFromEarth);
		Matcher m = EARTH_GET_PATTERN.matcher(getRequestFromEarth);
		m.find();

		// debug("lat: " + m.group(1));
		lat = Float.parseFloat(m.group(1));
		// debug("lon: " + m.group(2));
		lon = Float.parseFloat(m.group(2));
		debug("heading: " + m.group(3));
		heading = Float.parseFloat(m.group(3));

		if (heading < 0)
			heading += 360.0; // correct for misinformation at
												// http://code.google.com/apis/kml/documentation/kmlreference.html#headingdiagram

		tilt = Float.parseFloat(m.group(4));
	}

	/**
	 * @see ecologylab.oodss.messages.RequestMessage#performService(ecologylab.collections.Scope)
	 */
	@Override
	public ResponseMessage performService(Scope objectRegistry)
	{
		GPSDatum gpsDatum = (GPSDatum) objectRegistry.get(EarthGPSSimulatorServer.GPS_DATUM);

		if (gpsDatum == null)
			gpsDatum = new GPSDatum();

		long lastTimePoint = (Long) objectRegistry.get(EarthGPSSimulatorServer.LAST_TIME_POINT);

		if (lastTimePoint != 0)
			gpsDatum.setGrndSpd(gpsDatum.distanceTo(lat, lon) / ((timePoint - lastTimePoint) / 1000.0));

		objectRegistry.put(EarthGPSSimulatorServer.LAST_TIME_POINT, timePoint);
		
		gpsDatum.setLat(lat);
		gpsDatum.setLon(lon);

		CompassDatum compassDatum = (CompassDatum) objectRegistry
				.get(EarthGPSSimulatorServer.COMPASS_DATUM);

		if (compassDatum == null)
			compassDatum = new CompassDatum();

		compassDatum.setHeading(heading);

		Kml currentLookPointKML = new Kml();
		Placemark currentLookPoint = new Placemark("location", "Current simulated GPS location.", null);
		currentLookPoint.setPoint(new Point(gpsDatum));

		try
		{
			debug(ClassDescriptor.serialize(currentLookPoint, StringFormat.XML));
		}
		catch (SIMPLTranslationException e)
		{
			e.printStackTrace();
		}

		currentLookPointKML.setPlacemark(currentLookPoint);

		KmlResponse resp = new KmlResponse(currentLookPointKML);

		debug("Ground speed: " + gpsDatum.getSpeed());

		return resp;
	}
}
