/**
 * 
 */
package ecologylab.serialization.library.kml.overlay;

import ecologylab.serialization.ElementState.simpl_composite;
import ecologylab.serialization.ElementState.simpl_hints;
import ecologylab.serialization.ElementState.simpl_scalar;
import ecologylab.serialization.ElementState.xml_tag;
import ecologylab.serialization.Hint;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.library.kml.style.StyleSelector;

/**
 * According to
 * http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#screenoverlay
 * 
 * This element draws an image overlay fixed to the screen. Sample uses for
 * ScreenOverlays are compasses, logos, and heads-up displays. ScreenOverlay
 * sizing is determined by the <size> element. Positioning of the overlay is
 * handled by mapping a point in the image specified by <overlayXY> to a point
 * on the screen specified by <screenXY>. Then the image is rotated by
 * <rotation> degrees about a point relative to the screen specified by
 * <rotationXY>.
 * 
 * The <href> child of <Icon> specifies the image to be used as the overlay.
 * This file can be either on a local file system or on a web server. If this
 * element is omitted or contains no <href>, a rectangle is drawn using the
 * color and size defined by the screen overlay.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net) (Java classes only)
 */
@simpl_inherit public class ScreenOverlay extends Overlay
{
	/**
	 * Specifies a point on (or outside of) the overlay image that is mapped to
	 * the screen coordinate (<screenXY>). It requires x and y values, and the
	 * units for those values. The x and y values can be specified in three
	 * different ways: as pixels ("pixels"), as fractions of the image
	 * ("fraction"), or as inset pixels ("insetPixels"), which is an offset in
	 * pixels from the upper right corner of the image. The x and y positions can
	 * be specified in different ways—for example, x can be in pixels and y can
	 * be a fraction. The origin of the coordinate system is in the lower left
	 * corner of the image.
	 */
	@simpl_composite @xml_tag("overlayXY") protected Vec2	overlayXY;

	/**
	 * Specifies a point relative to the screen origin that the overlay image is
	 * mapped to. The x and y values can be specified in three different ways: as
	 * pixels ("pixels"), as fractions of the screen ("fraction"), or as inset
	 * pixels ("insetPixels"), which is an offset in pixels from the upper right
	 * corner of the screen. The x and y positions can be specified in different
	 * ways—for example, x can be in pixels and y can be a fraction. The origin
	 * of the coordinate system is in the lower left corner of the screen.
	 */
	@simpl_composite @xml_tag("screenXY") protected Vec2		screenXY;

	/** Point relative to the screen about which the screen overlay is rotated. */
	@simpl_composite @xml_tag("rotationXY") protected Vec2	rotationXY;

	/**
	 * Specifies the size of the image for the screen overlay, as follows:
	 * 
	 * A value of -1 indicates to use the native dimension
	 * 
	 * A value of 0 indicates to maintain the aspect ratio
	 * 
	 * A value of n sets the value of the dimension
	 */
	@simpl_composite protected Vec2									size;

	/**
	 * Indicates the angle of rotation of the parent object. A value of 0 means
	 * no rotation. The value is an angle in degrees counterclockwise starting
	 * from north. Use ±180 to indicate the rotation of the parent object from 0.
	 * The center of the <rotation>, if not (.5,.5), is specified in
	 * <rotationXY>.
	 */
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) protected float									rotation;

	/**
	 * 
	 */
	public ScreenOverlay()
	{
		super();
	}

	/**
	 * @param name
	 * @param description
	 * @param selector
	 */
	public ScreenOverlay(String name, String description, StyleSelector selector)
	{
		super(name, description, selector);
	}

	/**
	 * @param name
	 * @param description
	 * @param styleUrl
	 */
	public ScreenOverlay(String name, String description, String styleUrl)
	{
		super(name, description, styleUrl);
	}

	/**
	 * @param name
	 * @param description
	 */
	public ScreenOverlay(String name, String description)
	{
		super(name, description);
	}
}
