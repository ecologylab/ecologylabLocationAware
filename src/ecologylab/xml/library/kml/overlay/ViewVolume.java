/**
 * 
 */
package ecologylab.xml.library.kml.overlay;

import ecologylab.xml.ElementState;

/**
 * According to
 * http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#photooverlay
 * 
 * For very large images, you'll need to construct an image pyramid, which is a
 * hierarchical set of images, each of which is an increasingly lower resolution
 * version of the original image. Each image in the pyramid is subdivided into
 * tiles, so that only the portions in view need to be loaded. Google Earth
 * calculates the current viewpoint and loads the tiles that are appropriate to
 * the user's distance from the image. As the viewpoint moves closer to the
 * PhotoOverlay, Google Earth loads higher resolution tiles. Since all the
 * pixels in the original image can't be viewed on the screen at once, this
 * preprocessing allows Google Earth to achieve maximum performance because it
 * loads only the portions of the image that are in view, and only the pixel
 * details that can be discerned by the user at the current viewpoint.
 * 
 * When you specify an image pyramid, you also modify the <href> in the <Icon>
 * element to include specifications for which tiles to load.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net) (Java classes only)
 */
public class ViewVolume extends ElementState
{
	/** valid value for gridOrigin. */
	public static final String							LOWER_LEFT	= "lowerLeft";

	/** valid value for gridOrigin. */
	public static final String							LOWER_RIGHT	= "lowerRight";

	/**
	 * Size of the tiles, in pixels. Tiles must be square, and <tileSize> must be
	 * a power of 2. A tile size of 256 (the default) or 512 is recommended. The
	 * original image is divided into tiles of this size, at varying resolutions.
	 */
	@xml_leaf @xml_tag("tileSize") public int		tileSize;

	/** Width in pixels of the original image. */
	@xml_leaf @xml_tag("maxWidth") public int		maxWidth;

	/** Height in pixels of the original image. */
	@xml_leaf @xml_tag("maxHeight") public int	maxHeight;

	/**
	 * Specifies where to begin numbering the tiles in each layer of the pyramid.
	 * A value of lowerLeft specifies that row 1, column 1 of each layer is in
	 * the bottom left corner of the grid.
	 */
	@xml_leaf @xml_tag("gridOrigin") public int	gridOrigin;

	/**
	 * 
	 */
	public ViewVolume()
	{
	}
}
