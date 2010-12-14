/**
 * 
 */
package ecologylab.services.distributed.server.varieties;

import ecologylab.collections.Scope;
import ecologylab.net.NetTools;
import ecologylab.oodss.distributed.server.clientsessionmanager.HTTPGetClientSessionManager;
import ecologylab.oodss.distributed.server.varieties.HttpGetServer;
import ecologylab.oodss.messages.DefaultServicesTranslations;
import ecologylab.sensor.location.compass.CompassDatum;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.TranslationScope;
import ecologylab.serialization.library.kml.KMLTranslations;
import ecologylab.serialization.library.kml.Kml;
import ecologylab.services.distributed.server.contextmanager.EarthGPSSimCSManager;
import ecologylab.services.messages.KmlRequest;
import ecologylab.services.messages.KmlResponse;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.nio.channels.SelectionKey;

/**
 * Acts as a simulated source of location data; acquires data from an instance of Google Earth.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 */
public class EarthGPSSimulatorServer extends HttpGetServer
{
	public static final Class		KML_MESSAGE_CLASSES[]	=
																										{ KmlRequest.class, KmlResponse.class };

	public static String				someKml								= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
																												+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">\r\n"
																												+ "<Document>\r\n"
																												+ "  <name>BalloonStyle.kml</name>\r\n"
																												+ "  <open>1</open>\r\n"
																												+ "  <Placemark>\r\n"
																												+ "    <name>BalloonStyle</name>\r\n"
																												+ "    <description>An example of BalloonStyle</description>\r\n"
																												+ "    <Point>\r\n"
																												+ "      <coordinates>-122.370533,37.823842,0</coordinates>\r\n"
																												+ "    </Point>\r\n"
																												+ "  </Placemark>\r\n"
																												+ "</Document>\r\n" + "</kml>";

	/**
	 * Application object scope object of type GPSDatum. Incoming Google Earth GET requests will
	 * modify the GPSDatum through the ReportEarthLookLocationRequest message type.
	 */
	public static final String	GPS_DATUM							= "GPS_DATUM";

	public static final String	COMPASS_DATUM					= "COMPASS_DATUM";

	/**
	 * @param portNumber
	 * @param inetAddresses
	 * @param requestTranslationSpace
	 * @param objectRegistry
	 * @param idleConnectionTimeout
	 * @param maxPacketSize
	 * @param gpsDatum
	 *          A GPSDatum object that this should modify, based on the requests from the Google Earth
	 *          application.
	 * @throws IOException
	 * @throws BindException
	 */
	public EarthGPSSimulatorServer(int portNumber, InetAddress[] inetAddresses,
			TranslationScope requestTranslationSpace, Scope objectRegistry, int idleConnectionTimeout,
			int maxPacketSize, GPSDatum gpsDatum, CompassDatum compassDatum) throws IOException,
			BindException
	{
		super(portNumber, inetAddresses, TranslationScope.get(
				connectionTscopeName(inetAddresses, portNumber), DefaultServicesTranslations.get(),
				requestTranslationSpace, KML_MESSAGE_CLASSES), objectRegistry, idleConnectionTimeout,
				maxPacketSize);

		this.applicationObjectScope.put(GPS_DATUM, gpsDatum);
		this.applicationObjectScope.put(COMPASS_DATUM, compassDatum);

		// this.applicationObjectScope.put(KmlRequest.KML_DATA, kmlData);
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws BindException
	 * @throws SIMPLTranslationException
	 */
	public static void main(String[] args) throws BindException, IOException,
			SIMPLTranslationException
	{
		TranslationScope serverTranslations = DefaultServicesTranslations.get();

		Kml kmlData = (Kml) KMLTranslations.get().deserializeCharSequence(someKml);

		EarthGPSSimulatorServer s = new EarthGPSSimulatorServer(8080,
				NetTools.getAllInetAddressesForLocalhost(), serverTranslations, new Scope(), 1000000,
				1000000, new GPSDatum(), new CompassDatum());

		s.start();
	}

	@Override
	protected HTTPGetClientSessionManager generateContextManager(String token, SelectionKey sk,
			TranslationScope translationScopeIn, Scope registryIn)
	{
		return new EarthGPSSimCSManager(token, this.maxMessageSize, this.getBackend(), this, sk,
				translationScopeIn, registryIn);
	}
}
