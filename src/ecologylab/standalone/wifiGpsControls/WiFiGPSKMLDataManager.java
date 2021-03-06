/**
 * 
 */
package ecologylab.standalone.wifiGpsControls;

import ecologylab.composite.kml.sensor.GPSToKMLPoint;
import ecologylab.composite.kml.sensor.GPSToKMLTrail;
import ecologylab.composite.kml.sensor.WiFiColoredIcon;
import ecologylab.composite.kml.sensor.WiFiDataComment;
import ecologylab.sensor.location.NMEAReader;
import ecologylab.sensor.network.wireless.RunnableWiFiAdapter;
import ecologylab.serialization.library.kml.Kml;
import ecologylab.serialization.library.kml.feature.Placemark;
import ecologylab.serialization.library.kml.feature.container.Document;
import ecologylab.serialization.library.kml.geometry.LineString;
import ecologylab.serialization.library.kml.geometry.Point;
import ecologylab.serialization.library.kml.style.LineStyle;
import ecologylab.serialization.library.kml.style.PolyStyle;
import ecologylab.serialization.library.kml.style.Style;

import java.awt.Color;

/**
 * @author Administrator
 * 
 */
public class WiFiGPSKMLDataManager
{
	static Kml							data;

	static WiFiColoredIcon	wifiVis;

	static WiFiDataComment	comments;

	static LineString				line;

	static Point						point;

	/**
	 * 
	 */
	public static Kml configureKml(RunnableWiFiAdapter wiFi, NMEAReader gps)
	{
		setupWiFiVis(wiFi);
		setupGPSVis(gps);

		data = new Kml();

		// configure style, using a reference to the wifi visualizer
		Style lineStyle = new Style("yellowLineGreenPoly", new LineStyle(new Color(Integer.parseInt(
				"ff", 16), Integer.parseInt("ff", 16), Integer.parseInt("00", 16), Integer.parseInt("7f",
				16)), "normal", 4),
				new PolyStyle(new Color(Integer.parseInt("00", 16), Integer.parseInt("ff", 16), Integer
						.parseInt("00", 16), Integer.parseInt("7f", 16)), "normal", false, false), wifiVis
						.getIcon());

		Document doc = new Document("Outdoor Testing - GPS & WiFi", "", lineStyle);

		// trail for previous points
		Placemark trail = new Placemark("Outdoor Testing - GPS & WiFi", "", lineStyle.getId());

		// line is controlled by the GPS
		line.setExtrude(true);
		line.setTessellate(true);
		line.setAltitudeMode("clampToGround");

		// add the line to the trail
		trail.setLineString(line);

		Placemark head = new Placemark("current location", "", lineStyle.getId());

		// register with the comment creator, so the comment matches the current
		// wifi status
		comments.registerKmlFeature(head);

		// point is controlled by the GPS
		head.setPoint(point);

		doc.addPlacemark(trail);
		doc.addPlacemark(head);

		data.setDocument(doc);

		return data;
	}

	protected static void setupGPSVis(NMEAReader gps)
	{
		line = new LineString();
		point = new Point();

		gps.addGPSDataListener(new GPSToKMLTrail(line, 2000, 100));
		gps.addGPSDataListener(new GPSToKMLPoint(point));
	}

	protected static void setupWiFiVis(RunnableWiFiAdapter wiFi)
	{
		wifiVis = WiFiColoredIcon.GREEN_RED_STANDARD;

		comments = new WiFiDataComment();

		wiFi.addListener(wifiVis);
		wiFi.addListener(comments);
	}
}
