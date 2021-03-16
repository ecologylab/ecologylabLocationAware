/**
 * 
 */
package ecologylab.serialization.library.kml.view;

import ecologylab.serialization.annotations.Hint;
import ecologylab.serialization.annotations.simpl_hints;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_scalar;
import ecologylab.serialization.annotations.simpl_tag;

/**
 * From http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#camera
 * 
 * @author Zach
 * 
 */
@simpl_inherit @simpl_tag("Camera") public class Camera extends AbstractView
{
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									longitude;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									latitude;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									altitude;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									heading;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									tilt;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									roll;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) @simpl_tag("altitudeMode") String	altitudeMode;	// enum values:
																				// "clampToGround",
																				// "relativeToGround",
																				// or "absolute"

	/**
	 * 
	 */
	public Camera()
	{
		// TODO Auto-generated constructor stub
	}

}
