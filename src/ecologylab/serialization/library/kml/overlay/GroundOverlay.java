/**
 * 
 */
package ecologylab.serialization.library.kml.overlay;

import ecologylab.serialization.annotations.Hint;
import ecologylab.serialization.annotations.simpl_composite;
import ecologylab.serialization.annotations.simpl_hints;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_scalar;
import ecologylab.serialization.annotations.simpl_tag;
import ecologylab.serialization.library.kml.geometry.Point;
import ecologylab.serialization.library.kml.style.StyleSelector;

/**
 * According to
 * http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#groundoverlay
 * 
 * This element draws an image overlay draped onto the terrain. The <href> child
 * of <Icon> specifies the image to be used as the overlay. This file can be
 * either on a local file system or on a web server. If this element is omitted
 * or contains no <href>, a rectangle is drawn using the color and size defined
 * by the screen overlay.
 * 
 * @author Z O. Toups (zach@ecologylab.net) (Java classes only)
 */
@simpl_inherit public class GroundOverlay extends Overlay
{
	/**
	 * Specifies the distance above the earth's surface, in meters, and is
	 * interpreted according to <altitudeMode>.
	 */
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) protected double										altitude;

	/**
	 * Specifies how the <altitude>is interpreted. Possible values are
	 * 
	 * CLAMP_TO_GROUND - clampToGround - (default) Indicates to ignore the
	 * altitude specification and drape the overlay over the terrain.
	 * 
	 * ABSOLUTE - absolute - Sets the altitude of the overlay relative to sea
	 * level, regardless of the actual elevation of the terrain beneath the
	 * element. For example, if you set the altitude of an overlay to 10 meters
	 * with an absolute altitude mode, the overlay will appear to be at ground
	 * level if the terrain beneath is also 10 meters above sea level. If the
	 * terrain is 3 meters above sea level, the overlay will appear elevated
	 * above the terrain by 7 meters.
	 * 
	 */
	@simpl_composite @simpl_tag("altitudeMode") protected String	altitudeMode;

	/** valid value for shape. */
	public static final String										CLAMP_TO_GROUND	= "clampToGround";

	/** valid value for shape. */
	public static final String										ABSOLUTE				= "absolute";

	/**
	 * Specifies where the top, bottom, right, and left sides of a bounding box
	 * for the ground overlay are aligned.
	 */
	@simpl_composite @simpl_tag("LatLonBox") protected LatLonBox	latLonBox;

	/**
	 * The <Point> element acts as a <Point> inside a <Placemark> element. It
	 * draws an icon to mark the position of the PhotoOverlay. The icon drawn is
	 * specified by the <styleUrl> and <StyleSelector> fields, just as it is for
	 * <Placemark>.
	 */
	@simpl_composite @simpl_tag("Point") protected Point			point;

	/**
	 * 
	 */
	public GroundOverlay()
	{
		super();
	}

	/**
	 * @param name
	 * @param description
	 * @param selector
	 */
	public GroundOverlay(String name, String description, StyleSelector selector)
	{
		super(name, description, selector);
	}

	/**
	 * @param name
	 * @param description
	 * @param styleUrl
	 */
	public GroundOverlay(String name, String description, String styleUrl)
	{
		super(name, description, styleUrl);
	}

	/**
	 * @param name
	 * @param description
	 */
	public GroundOverlay(String name, String description)
	{
		super(name, description);
	}
}
