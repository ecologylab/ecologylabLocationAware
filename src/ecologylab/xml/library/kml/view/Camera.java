/**
 * 
 */
package ecologylab.xml.library.kml.view;

import ecologylab.serialization.Hint;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.ElementState.xml_tag;

/**
 * From http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#camera
 * 
 * @author Zach
 * 
 */
@simpl_inherit @xml_tag("Camera") public class Camera extends AbstractView
{
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									longitude;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									latitude;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									altitude;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									heading;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									tilt;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									roll;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) @xml_tag("altitudeMode") String	altitudeMode;	// enum values:
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
