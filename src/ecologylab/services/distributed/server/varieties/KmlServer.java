/**
 * 
 */
package ecologylab.services.distributed.server.varieties;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.nio.channels.SelectionKey;

import ecologylab.appframework.ObjectRegistry;
import ecologylab.net.NetTools;
import ecologylab.services.distributed.server.clientmanager.AbstractClientManager;
import ecologylab.services.distributed.server.contextmanager.KMLGetClientManager;
import ecologylab.services.messages.DefaultServicesTranslations;
import ecologylab.services.messages.KmlRequest;
import ecologylab.services.messages.KmlResponse;
import ecologylab.standalone.TestKML;
import ecologylab.xml.ElementState;
import ecologylab.xml.TranslationSpace;
import ecologylab.xml.XMLTranslationException;
import ecologylab.xml.library.kml.KMLTranslations;
import ecologylab.xml.library.kml.Kml;

/**
 * @author Zach
 * 
 */
public class KmlServer extends HttpGetServer
{
	public static final Class	KML_MESSAGE_CLASSES[]	=
																		{ KmlRequest.class,
			KmlResponse.class									};

	/**
	 * @param portNumber
	 * @param inetAddresses
	 * @param requestTranslationSpace
	 * @param objectRegistry
	 * @param idleConnectionTimeout
	 * @param maxPacketSize
	 * @param kmlData
	 *           the singleton Kml object that will be sent when Google Earth
	 *           requests KML. This object can be modified by other parts of the
	 *           application to change what information is displayed in Google
	 *           Earth.
	 * @throws IOException
	 * @throws BindException
	 */
	public KmlServer(int portNumber, InetAddress[] inetAddresses,
			TranslationSpace requestTranslationSpace,
			ObjectRegistry objectRegistry, int idleConnectionTimeout,
			int maxPacketSize, Kml kmlData) throws IOException, BindException
	{
		super(portNumber, inetAddresses, TranslationSpace.get(
				"double_threaded_logging " + inetAddresses[0].toString() + ":"
						+ portNumber, KML_MESSAGE_CLASSES, requestTranslationSpace,
				KMLTranslations.get()), objectRegistry, idleConnectionTimeout,
				maxPacketSize);

		this.registry.registerObject(KmlRequest.KML_DATA, kmlData);
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
			TranslationSpace requestTranslationSpace,
			ObjectRegistry objectRegistry, int idleConnectionTimeout,
			int maxPacketSize, Kml kmlData) throws IOException, BindException
	{
		this(portNumber, addressToAddresses(inetAddress),
				requestTranslationSpace, objectRegistry, idleConnectionTimeout,
				maxPacketSize, kmlData);
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws BindException
	 * @throws XMLTranslationException 
	 */
	public static void main(String[] args) throws BindException, IOException, XMLTranslationException
	{
		TranslationSpace serverTranslations = DefaultServicesTranslations.get();

		Kml kmlData = (Kml) ElementState.translateFromXMLCharSequence(TestKML.someKml, KMLTranslations.get());
		
		KmlServer s = new KmlServer(8080, NetTools
				.getAllInetAddressesForLocalhost(), serverTranslations,
				new ObjectRegistry(), 1000000, 1000000, kmlData);

		s.start();
	}

	@Override protected AbstractClientManager generateContextManager(
			Object token, SelectionKey sk, TranslationSpace translationSpaceIn,
			ObjectRegistry registryIn)
	{
		return new KMLGetClientManager(token, maxPacketSize, this.getBackend(),
				this, sk, translationSpaceIn, registryIn);
	}
}