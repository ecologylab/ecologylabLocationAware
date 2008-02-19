/**
 * 
 */
package ecologylab.xml.library.kml.style.icon;

import ecologylab.xml.ElementState;

/**
 * Created according to
 * http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#iconstyle
 * 
 * Specifies the position within the Icon that is "anchored" to the <Point>
 * specified in the Placemark. The x and y values can be specified in three
 * different ways: as pixels ("pixels"), as fractions of the icon ("fraction"),
 * or as inset pixels ("insetPixels"), which is an offset in pixels from the
 * upper right corner of the icon. The x and y positions can be specified in
 * different ways�for example, x can be in pixels and y can be a fraction. The
 * origin of the coordinate system is in the lower left corner of the icon.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu) (Java classes only)
 */
public class HotSpot extends ElementState
{
	public static final String		FRACTION				= "fraction";

	public static final String		PIXELS				= "pixels";

	public static final String		INSET_PIXELS		= "insetPixels";

	public static final HotSpot	CENTERED_HOTSPOT	= new HotSpot(.5f, .5f,
			FRACTION, FRACTION);
	
	/**
	 * Either the number of pixels, a fractional component of the icon, or a
	 * pixel inset indicating the x component of a point on the icon.
	 */
	@xml_attribute private float	x;

	/**
	 * Either the number of pixels, a fractional component of the icon, or a
	 * pixel inset indicating the y component of a point on the icon.
	 */
	@xml_attribute private float	y;

	/**
	 * xunits - Units in which the x value is specified. A value of fraction
	 * indicates the x value is a fraction of the icon. A value of pixels
	 * indicates the x value in pixels. A value of insetPixels indicates the
	 * indent from the right edge of the icon.
	 */
	@xml_attribute private String	xunits;

	/**
	 * yunits - Units in which the y value is specified. A value of fraction
	 * indicates the y value is a fraction of the icon. A value of pixels
	 * indicates the y value in pixels. A value of insetPixels indicates the
	 * indent from the top edge of the icon.
	 */
	@xml_attribute private String	yunits;

	/**
	 * 
	 */
	public HotSpot()
	{
	}

	public HotSpot(float x, float y, String xunits, String yunits)
	{
		this.x = x;
		this.y = y;
		this.xunits = xunits;
		this.yunits = yunits;
	}
}