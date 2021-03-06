/**
 * 
 */
package ecologylab.services.distributed.server.varieties;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.nio.channels.SelectionKey;

import ecologylab.collections.Scope;
import ecologylab.net.NetTools;
import ecologylab.oodss.distributed.server.clientsessionmanager.HTTPGetClientSessionManager;
import ecologylab.oodss.distributed.server.varieties.HttpGetServer;
import ecologylab.oodss.messages.DefaultServicesTranslations;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.SimplTypesScope;
import ecologylab.serialization.formatenums.StringFormat;
import ecologylab.serialization.library.kml.KMLTranslations;
import ecologylab.serialization.library.kml.Kml;
import ecologylab.services.distributed.server.contextmanager.KMLGetClientSessionManager;
import ecologylab.services.messages.KmlRequest;
import ecologylab.services.messages.KmlResponse;

/**
 * @author Z O. Toups (zach@ecologylab.net)
 * 
 */
public class KmlServer extends HttpGetServer
{
	public static final Class	KML_MESSAGE_CLASSES[]	=
																									{ KmlRequest.class, KmlResponse.class };

	public static String			someKml								= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
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
																											+ "</Document>\r\n"
																											+ "</kml>";

	/**
	 * @param portNumber
	 * @param inetAddresses
	 * @param requestTranslationSpace
	 * @param objectRegistry
	 * @param idleConnectionTimeout
	 * @param maxPacketSize
	 * @param kmlData
	 *          the singleton Kml object that will be sent when Google Earth requests KML. This object
	 *          can be modified by other parts of the application to change what information is
	 *          displayed in Google Earth.
	 * @throws IOException
	 * @throws BindException
	 */
	public KmlServer(int portNumber, InetAddress[] inetAddresses,
			SimplTypesScope requestTranslationSpace, Scope objectRegistry, int idleConnectionTimeout,
			int maxPacketSize, Kml kmlData) throws IOException, BindException
	{
		super(portNumber, inetAddresses, SimplTypesScope.get(connectionTscopeName(inetAddresses,
				portNumber), DefaultServicesTranslations.get(), requestTranslationSpace,
				KML_MESSAGE_CLASSES), objectRegistry, idleConnectionTimeout, maxPacketSize);

		this.applicationObjectScope.put(KmlRequest.KML_DATA, kmlData);
	}

	/**
	 * @param portNumber
	 * @param inetAddress
	 * @param requestTranslationSpace
	 * @param objectRegistry
	 * @param idleConnectionTimeout
	 * @param maxPacketSize
	 * @throws IOException
	 * @throws BindException
	 */
	public KmlServer(int portNumber, InetAddress inetAddress,
			SimplTypesScope requestTranslationSpace, Scope objectRegistry, int idleConnectionTimeout,
			int maxPacketSize, Kml kmlData) throws IOException, BindException
	{
		this(portNumber, addressToAddresses(inetAddress), requestTranslationSpace, objectRegistry,
				idleConnectionTimeout, maxPacketSize, kmlData);
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws BindException
	 * @throws SIMPLTranslationException
	 */
	public static void main(String[] args) throws BindException, IOException, SIMPLTranslationException
	{
		SimplTypesScope serverTranslations = DefaultServicesTranslations.get();

		Kml kmlData = (Kml) KMLTranslations.get().deserialize(someKml, StringFormat.XML);

		KmlServer s = new KmlServer(8080, NetTools.getAllInetAddressesForLocalhost(),
				serverTranslations, new Scope(), 1000000, 1000000, kmlData);

		s.start();
	}

	@Override
	protected HTTPGetClientSessionManager generateContextManager(String token, SelectionKey sk,
			SimplTypesScope translationScopeIn, Scope registryIn)
	{
		return new KMLGetClientSessionManager(token, this.maxMessageSize, this.getBackend(), this, sk,
				translationScopeIn, registryIn);
	}
}
