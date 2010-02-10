/**
 * 
 */
package ecologylab.xml.library.kml.overlay;

import ecologylab.xml.ElementState;

/**
 * According to
 * http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#groundoverlay
 * 
 * Specifies where the top, bottom, right, and left sides of a bounding box for
 * the ground overlay are aligned.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net) (Java classes only)
 */
public class LatLonBox extends ElementState
{
	/**
	 * Specifies the latitude of the north edge of the bounding box, in decimal
	 * degrees from 0 to ±90.
	 */
	@xml_leaf public double	north;

	/**
	 * Specifies the latitude of the south edge of the bounding box, in decimal
	 * degrees from 0 to ±90.
	 */
	@xml_leaf public double	south;

	/**
	 * Specifies the longitude of the east edge of the bounding box, in decimal
	 * degrees from 0 to ±180. (For overlays that overlap the meridian of 180°
	 * longitude, values can extend beyond that range.)
	 */
	@xml_leaf public double	east;

	/**
	 * Specifies the longitude of the west edge of the bounding box, in decimal
	 * degrees from 0 to ±180. (For overlays that overlap the meridian of 180°
	 * longitude, values can extend beyond that range.)
	 */
	@xml_leaf public double	west;

	/**
	 * Specifies a rotation of the overlay about its center, in degrees. Values
	 * can be ±180. The default is 0 (north). Rotations are specified in a
	 * counterclockwise direction.
	 */
	@xml_leaf public double	rotation;

	/**
	 * 
	 */
	public LatLonBox()
	{
	}

}
