package ecologylab.standalone;

import ecologylab.collections.Scope;
import ecologylab.composite.kml.sensor.WiFiColoredIcon;
import ecologylab.composite.kml.sensor.WiFiDataComment;
import ecologylab.net.NetTools;
import ecologylab.oodss.messages.DefaultServicesTranslations;
import ecologylab.sensor.location.gps.data.GeoCoordinate;
import ecologylab.sensor.network.wireless.RunnableWiFiAdapter;
import ecologylab.sensor.network.wireless.listener.WiFiStringDataListener;
import ecologylab.serialization.TranslationScope;
import ecologylab.serialization.library.kml.Kml;
import ecologylab.serialization.library.kml.feature.Placemark;
import ecologylab.serialization.library.kml.feature.container.Document;
import ecologylab.serialization.library.kml.geometry.Point;
import ecologylab.serialization.library.kml.style.LineStyle;
import ecologylab.serialization.library.kml.style.PolyStyle;
import ecologylab.serialization.library.kml.style.Style;
import ecologylab.services.distributed.server.varieties.KmlServer;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.awt.Color;
import java.io.IOException;
import java.util.TooManyListenersException;

import stec.jenie.NativeException;

public class GoogleEarthServerWithWiFiStatus
{

	/**
	 * @param args
	 * @throws IOException
	 * @throws TooManyListenersException
	 * @throws UnsupportedCommOperationException
	 * @throws PortInUseException
	 * @throws NoSuchPortException
	 * @throws NativeException
	 */
	public static void main(String[] args) throws IOException,
			PortInUseException, UnsupportedCommOperationException,
			TooManyListenersException, NoSuchPortException, NativeException
	{
		WiFiColoredIcon wifiVis = WiFiColoredIcon.GREEN_RED_STANDARD;

		WiFiDataComment comments = new WiFiDataComment();

		RunnableWiFiAdapter wiFi = new RunnableWiFiAdapter(5000);

		wiFi.addListener(wifiVis);
		wiFi.addListener(comments);

		
		  wiFi.addListener(new WiFiStringDataListener() { public void
		  apListUpdate(String newData) { System.out.println("APs:");
		  System.out.println(newData); }
		  
		  public void macAddressUpdate(String newData) {
		  System.out.println("--------------- current AP: "+newData); }
		  
		  });
		 

		wiFi.connect();

		// build KML
		Kml kmlData = new Kml();

		Style lineStyle = new Style("yellowLineGreenPoly",

		new LineStyle(new Color(Integer.parseInt("ff", 16), Integer.parseInt(
				"ff", 16), Integer.parseInt("00", 16), Integer.parseInt("7f", 16)),
				"normal", 4),

		new PolyStyle(new Color(Integer.parseInt("00", 16), Integer.parseInt(
				"ff", 16), Integer.parseInt("00", 16), Integer.parseInt("7f", 16)),
				"normal", false, false),

		// use the icon from the WiFiColoredIcon object, so it will be
		// automatically updated to reflect the status of wiFi
				wifiVis.getIcon());

		Document doc = new Document("To the grocery store",
				"This was my trip to the grocery store one day.", lineStyle);

		Placemark head = new Placemark("point",
				"", lineStyle.getId());
		head.setPoint(new Point(new GeoCoordinate(29.960144444,-95.6711667,0)));
		
		// register with the comment creator, so the comment matches the current wifi status
		comments.registerKmlFeature(head);

		doc.addPlacemark(head);

		kmlData.setDocument(doc);

		// setup server
		TranslationScope serverTranslations = DefaultServicesTranslations.get();

		KmlServer s = new KmlServer(8080, NetTools
				.getAllInetAddressesForLocalhost(), serverTranslations,
				new Scope(), 1000000, 1000000, kmlData);

		s.start();
	}
}
