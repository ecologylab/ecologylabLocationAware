package ecologylab.standalone;

import ecologylab.collections.Scope;
import ecologylab.composite.kml.sensor.GPSToKMLTrail;
import ecologylab.net.NetTools;
import ecologylab.oodss.messages.DefaultServicesTranslations;
import ecologylab.sensor.location.NMEAReader;
import ecologylab.sensor.location.gps.SimGPS;
import ecologylab.sensor.location.gps.SimGPS.PlayMode;
import ecologylab.sensor.location.gps.data.GeoCoordinate;
import ecologylab.serialization.SimplTypesScope;
import ecologylab.serialization.library.kml.Kml;
import ecologylab.serialization.library.kml.feature.Placemark;
import ecologylab.serialization.library.kml.feature.container.Document;
import ecologylab.serialization.library.kml.geometry.LineString;
import ecologylab.serialization.library.kml.geometry.Point;
import ecologylab.serialization.library.kml.style.IconStyle;
import ecologylab.serialization.library.kml.style.LineStyle;
import ecologylab.serialization.library.kml.style.PolyStyle;
import ecologylab.serialization.library.kml.style.Style;
import ecologylab.services.distributed.server.varieties.KmlServer;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.TooManyListenersException;

public class GoogleEarthGPSTrail
{

	/**
	 * @param args
	 * @throws IOException
	 * @throws TooManyListenersException
	 * @throws UnsupportedCommOperationException
	 * @throws PortInUseException
	 * @throws NoSuchPortException
	 */
	public static void main(String[] args) throws IOException, PortInUseException,
			UnsupportedCommOperationException, TooManyListenersException, NoSuchPortException
	{
		NMEAReader gps = new SimGPS(new File("sampleLogs/billTest.nmea"), PlayMode.FORWARD_BACKWARD);

		// GPS gps = new GPS("COM19", 115200);

		Kml kmlData = new Kml();

		Style lineStyle = new Style("yellowLineGreenPoly",

		new LineStyle(new Color(Integer.parseInt("ff", 16), Integer.parseInt("ff", 16),
				Integer.parseInt("00", 16), Integer.parseInt("7f", 16)), "normal", 4),

		new PolyStyle(new Color(Integer.parseInt("00", 16), Integer.parseInt("ff", 16),
				Integer.parseInt("00", 16), Integer.parseInt("7f", 16)), "normal", false, false),

		new IconStyle(null, "http://maps.google.com/mapfiles/kml/pal3/icon21.png", 1.1f, 0f, null,
				new Color(0x00, 0xff, 0x00, 0xff), "random"));

		Document doc = new Document("To the grocery store",
				"This was my trip to the grocery store one day.", lineStyle);

		Placemark trail = new Placemark("To the grocery store",
				"This was my trip to the grocery store one day.", lineStyle.getId());

		LineString line = new LineString();

		line.setExtrude(true);
		line.setTessellate(true);
		line.setAltitudeMode("clampToGround");

		trail.setLineString(line);

		Placemark head = new Placemark("starting point", "this is where the trip starts",
				lineStyle.getId());
		head.setPoint(new Point(new GeoCoordinate(29.960144444, -95.6711667, 0)));

		doc.addPlacemark(trail);
		doc.addPlacemark(head);

		kmlData.setDocument(doc);

		// setup server
		SimplTypesScope serverTranslations = DefaultServicesTranslations.get();

		KmlServer s = new KmlServer(8080, NetTools.getAllInetAddressesForLocalhost(),
				serverTranslations, new Scope(), 1000000, 1000000, kmlData);

		s.start();

		gps.addGPSDataListener(new GPSToKMLTrail(line, 2000, 1));

		// System.out.println("Attempting to load the entire simulation file...");

		for (int i = 0; i < 33000; i++)
		{
			// ((SimGPS) gps).sendSentence();
		}

		System.out.println("...done.");

		// System.out.println("Starting simulator.");

		gps.connect();
	}
}
