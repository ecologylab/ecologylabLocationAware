package ecologylab.standalone;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.TooManyListenersException;

import ecologylab.appframework.ObjectRegistry;
import ecologylab.net.NetTools;
import ecologylab.sensor.gps.GPS;
import ecologylab.sensor.gps.SimGPS;
import ecologylab.sensor.gps.SimGPS.PlayMode;
import ecologylab.sensor.gps.listener.GPSToKMLTrail;
import ecologylab.services.distributed.server.varieties.KmlServer;
import ecologylab.services.messages.DefaultServicesTranslations;
import ecologylab.xml.TranslationSpace;
import ecologylab.xml.library.kml.Kml;
import ecologylab.xml.library.kml.feature.Placemark;
import ecologylab.xml.library.kml.feature.container.Document;
import ecologylab.xml.library.kml.geometry.LineString;
import ecologylab.xml.library.kml.style.LineStyle;
import ecologylab.xml.library.kml.style.PolyStyle;
import ecologylab.xml.library.kml.style.Style;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

public class GoogleEarthGPSTrail
{

	/**
	 * @param args
	 * @throws IOException
	 * @throws TooManyListenersException
	 * @throws UnsupportedCommOperationException
	 * @throws PortInUseException
	 */
	public static void main(String[] args) throws IOException,
			PortInUseException, UnsupportedCommOperationException,
			TooManyListenersException
	{
		GPS gps = new SimGPS(new File("sampleLogs/zachToGrocery.txt"),
				PlayMode.FORWARD_BACKWARD);

		Kml kmlData = new Kml();

		Style lineStyle = new Style("yellowLineGreenPoly",

		new LineStyle(new Color(Integer.parseInt("ff", 16), Integer.parseInt(
				"ff", 16), Integer.parseInt("00", 16), Integer.parseInt("7f", 16)),
				"normal", 4),

		new PolyStyle(new Color(Integer.parseInt("00", 16), Integer.parseInt(
				"ff", 16), Integer.parseInt("00", 16), Integer.parseInt("7f", 16)),
				"normal", false, false));

		Document doc = new Document("To the grocery store",
				"This was my trip to the grocery store one day.", lineStyle);

		Placemark trail = new Placemark("To the grocery store",
				"This was my trip to the grocery store one day.", lineStyle.getId());

		LineString line = new LineString();

		line.setExtrude(true);
		line.setTessellate(true);
		line.setAltitudeMode("clampToGround");

		trail.setLineString(line);

		doc.addPlacemark(trail);

		kmlData.setDocument(doc);

		// setup server
		TranslationSpace serverTranslations = DefaultServicesTranslations.get();

		KmlServer s = new KmlServer(8080, NetTools
				.getAllInetAddressesForLocalhost(), serverTranslations,
				new ObjectRegistry(), 1000000, 1000000, kmlData);

		s.start();

		gps.addGPSDataListener(new GPSToKMLTrail(line, 2000, 100));

		System.out.println("Attempting to load the entire simulation file...");

		for (int i = 0; i < 33000; i++)
		{
			((SimGPS) gps).sendSentence();
		}

		System.out.println("...done.");

		// System.out.println("Starting simulator.");

		// gps.connect();
	}
}
