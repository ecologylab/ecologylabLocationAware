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
 * http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#photooverlay
 * 
 * The <PhotoOverlay> element allows you to geographically locate a photograph
 * on the Earth and to specify viewing parameters for this PhotoOverlay. The
 * PhotoOverlay can be a simple 2D rectangle, a partial or full cylinder, or a
 * sphere (for spherical panoramas). The overlay is placed at the specified
 * location and oriented toward the viewpoint.
 * 
 * Because <PhotoOverlay> is derived from <Feature>, it can contain one of the
 * two elements derived from <AbstractView>�either <Camera> or <LookAt>. The
 * Camera (or LookAt) specifies a viewpoint and a viewing direction (also
 * referred to as a view vector). The PhotoOverlay is positioned in relation to
 * the viewpoint. Specifically, the plane of a 2D rectangular image is
 * orthogonal (at right angles to) the view vector. The normal of this
 * plane�that is, its front, which is the part with the photo�is oriented toward
 * the viewpoint.
 * 
 * The URL for the PhotoOverlay image is specified in the <Icon> tag, which is
 * inherited from <Overlay>. The <Icon> tag must contain an <href> element that
 * specifies the image file to use for the PhotoOverlay. In the case of a very
 * large image, the <href> is a special URL that indexes into a pyramid of
 * images of varying resolutions (see ImagePyramid).
 * 
 * For more information, see the "Topics in KML" page on PhotoOverlay.
 * 
 * @author Z O. Toups (zach@ecologylab.net) (Java classes only)
 */
@simpl_inherit public class PhotoOverlay extends Overlay
{
	/**
	 * Adjusts how the photo is placed inside the field of view. This element is
	 * useful if your photo has been rotated and deviates slightly from a desired
	 * horizontal view.
	 */
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) protected int													rotation;

	/**
	 * Defines how much of the current scene is visible. Specifying the field of
	 * view is analogous to specifying the lens opening in a physical camera. A
	 * small field of view, like a telephoto lens, focuses on a small part of the
	 * scene. A large field of view, like a wide-angle lens, focuses on a large
	 * part of the scene.
	 */
	@simpl_composite @simpl_tag("ViewVolume") protected ViewVolume		viewVolume;

	/**
	 * For very large images, you'll need to construct an image pyramid, which is
	 * a hierarchical set of images, each of which is an increasingly lower
	 * resolution version of the original image. Each image in the pyramid is
	 * subdivided into tiles, so that only the portions in view need to be
	 * loaded. Google Earth calculates the current viewpoint and loads the tiles
	 * that are appropriate to the user's distance from the image. As the
	 * viewpoint moves closer to the PhotoOverlay, Google Earth loads higher
	 * resolution tiles. Since all the pixels in the original image can't be
	 * viewed on the screen at once, this preprocessing allows Google Earth to
	 * achieve maximum performance because it loads only the portions of the
	 * image that are in view, and only the pixel details that can be discerned
	 * by the user at the current viewpoint.
	 * 
	 * When you specify an image pyramid, you also modify the <href> in the
	 * <Icon> element to include specifications for which tiles to load.
	 */
	@simpl_composite @simpl_tag("ImagePyramid") protected ImagePyramid	imagePyramid;

	/**
	 * The <Point> element acts as a <Point> inside a <Placemark> element. It
	 * draws an icon to mark the position of the PhotoOverlay. The icon drawn is
	 * specified by the <styleUrl> and <StyleSelector> fields, just as it is for
	 * <Placemark>.
	 */
	@simpl_composite @simpl_tag("Point") protected Point					point;

	/**
	 * The PhotoOverlay is projected onto the <shape>. The <shape> can be one of
	 * the following:
	 * 
	 * RECTANGLE - rectangle (default) - for an ordinary photo
	 * 
	 * CYLINDER - cylinder - for panoramas, which can be either partial or full
	 * cylinders
	 * 
	 * SPHERE - sphere - for spherical panoramas
	 */
	@simpl_composite protected String											shape;

	/** valid value for shape. */
	public static final String												RECTANGLE	= "rectangle";

	/** valid value for shape. */
	public static final String												CYLINDER		= "cylinder";

	/** valid value for shape. */
	public static final String												SPHERE		= "sphere";

	/**
	 * 
	 */
	public PhotoOverlay()
	{
		super();
	}

	/**
	 * @param name
	 * @param description
	 * @param selector
	 */
	public PhotoOverlay(String name, String description, StyleSelector selector)
	{
		super(name, description, selector);
	}

	/**
	 * @param name
	 * @param description
	 * @param styleUrl
	 */
	public PhotoOverlay(String name, String description, String styleUrl)
	{
		super(name, description, styleUrl);
	}

	/**
	 * @param name
	 * @param description
	 */
	public PhotoOverlay(String name, String description)
	{
		super(name, description);
	}
}
