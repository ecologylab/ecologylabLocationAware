/**
 * 
 */
package ecologylab.xml.library.kml.style;

import java.awt.Color;

import ecologylab.xml.XMLTranslationException;
import ecologylab.xml.xml_inherit;
import ecologylab.xml.library.kml.overlay.Vec2;

/**
 * Created according to
 * http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#iconstyle
 * 
 * Specifies how icons for point Placemarks are drawn, both in the Places panel
 * and in the 3D viewer of Google Earth. The <Icon> element specifies the icon
 * image. The <scale> element specifies the x, y scaling of the icon. The color
 * specified in the <color> element of <IconStyle> is blended with the color of
 * the <Icon>.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net) (Java classes only)
 */
@xml_inherit public class IconStyle extends ColorStyle
{
	/** Resizes the icon. */
	@xml_leaf private float									scale;

	/**
	 * Direction (that is, North, South, East, West), in degrees. Default=0
	 * (North). (See diagram.) Values range from 0 to 360 degrees.
	 */
	@xml_leaf private float									heading;

	/** A custom Icon. In <IconStyle>, the only child element of <Icon> is <href> */
	@xml_nested @xml_tag("Icon") private Icon			icon;

	/**
	 * Specifies the position within the Icon that is "anchored" to the <Point>
	 * specified in the Placemark. The x and y values can be specified in three
	 * different ways: as pixels ("pixels"), as fractions of the icon
	 * ("fraction"), or as inset pixels ("insetPixels"), which is an offset in
	 * pixels from the upper right corner of the icon. The x and y positions can
	 * be specified in different ways—for example, x can be in pixels and y can
	 * be a fraction. The origin of the coordinate system is in the lower left
	 * corner of the icon.
	 */
	@xml_nested @xml_tag("hotSpot") private Vec2	hotSpot;

	/**
	 * 
	 */
	public IconStyle()
	{
		super();
		
		this.icon = new Icon();
	}

	/**
	 * Constructs a new IconStyle with an icon's URL, an id, the hotspot centered
	 * in the icon, the icon oriented north, and scaled at 1 to 1.
	 * 
	 * @param color
	 * @param colorMode
	 * @param iconURL
	 */
	public IconStyle(String id, String iconURL)
	{
		this(id, iconURL, 1.0f, 0f, Icon.CENTERED_HOTSPOT, Color.WHITE,
				"normal");
	}

	public IconStyle(String id, String iconURL, float scale, float heading,
			Vec2 hotSpot, Color color, String colorMode)
	{
		super(id, color, colorMode);

		this.icon = new Icon(iconURL);

		this.scale = scale;
		this.heading = heading;
		this.hotSpot = hotSpot;
	}

	/**
	 * Test case that should generate the following KML:
	 * 
	 * <Style id="randomColorIcon"> <IconStyle> <color>ff00ff00</color>
	 * <colorMode>random</colorMode> <scale>1.1</scale> <Icon>
	 * <href>http://maps.google.com/mapfiles/kml/pal3/icon21.png</href> </Icon>
	 * </IconStyle> </Style>
	 * 
	 * @param args
	 * @throws XMLTranslationException 
	 */
	public static void main(String args[]) throws XMLTranslationException
	{
		IconStyle iS = new IconStyle(null,
				"http://maps.google.com/mapfiles/kml/pal3/icon21.png", 1.1f, 0f,
				null, new Color(0x00, 0xff, 0x00, 0xff), "random");
		
		Style s = new Style("randomColorIcon", null, null, iS);
		
		s.translateToXML(System.out);
	}

	public void setHref(String iconURL)
	{
		this.icon.setHref(iconURL);
	}
}
