package ecologylab.standalone;

import ecologylab.collections.Scope;
import ecologylab.composite.kml.sensor.WiFiColoredIcon;
import ecologylab.net.NetTools;
import ecologylab.sensor.location.gps.GPS;
import ecologylab.sensor.network.wireless.RunnableWiFiAdapter;
import ecologylab.sensor.network.wireless.listener.WiFiStringDataListener;
import ecologylab.services.distributed.server.varieties.KmlServer;
import ecologylab.services.messages.DefaultServicesTranslations;
import ecologylab.xml.TranslationSpace;
import ecologylab.xml.library.kml.Kml;
import ecologylab.xml.library.kml.feature.Placemark;
import ecologylab.xml.library.kml.feature.container.Document;
import ecologylab.xml.library.kml.geometry.Coordinates;
import ecologylab.xml.library.kml.geometry.Point;
import ecologylab.xml.library.kml.style.IconStyle;
import ecologylab.xml.library.kml.style.LineStyle;
import ecologylab.xml.library.kml.style.PolyStyle;
import ecologylab.xml.library.kml.style.Style;
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
		
		RunnableWiFiAdapter wiFi = new RunnableWiFiAdapter(1000);
		
		wiFi.addListener(wifiVis);
		
/*		wiFi.addListener(new WiFiStringDataListener() 
		{
			public void apListUpdate(String newData)
			{
				System.out.println("APs:");
				System.out.println(newData);
			}

			public void macAddressUpdate(String newData)
			{
				System.out.println("--------------- current AP: "+newData);
			}
			
		});*/
		
		wiFi.connect();
		
		Kml kmlData = new Kml();

		Style lineStyle = new Style("yellowLineGreenPoly",

		new LineStyle(new Color(Integer.parseInt("ff", 16), Integer.parseInt(
				"ff", 16), Integer.parseInt("00", 16), Integer.parseInt("7f", 16)),
				"normal", 4),

		new PolyStyle(new Color(Integer.parseInt("00", 16), Integer.parseInt(
				"ff", 16), Integer.parseInt("00", 16), Integer.parseInt("7f", 16)),
				"normal", false, false),

				wifiVis.getIcon());

		Document doc = new Document("To the grocery store",
				"This was my trip to the grocery store one day.", lineStyle);

		Placemark head = new Placemark("starting point", "this is where the trip starts", lineStyle.getId());
		head.setPoint(new Point(new Coordinates("-95.6711667,29.960144444,0")));
		
		doc.addPlacemark(head);

		kmlData.setDocument(doc);

		// setup server
		TranslationSpace serverTranslations = DefaultServicesTranslations.get();

		KmlServer s = new KmlServer(8080, NetTools
				.getAllInetAddressesForLocalhost(), serverTranslations,
				new Scope(), 1000000, 1000000, kmlData);

		s.start();
	}
}
