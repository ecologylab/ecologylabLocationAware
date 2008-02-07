/**
 * 
 */
package ecologylab.xml.library.kml.view;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.ElementState.xml_leaf;
import ecologylab.xml.ElementState.xml_tag;

/**
 * From http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#camera
 * 
 * @author Zach
 * 
 */
@xml_inherit @xml_tag("Camera") public class Camera extends AbstractView
{
	@xml_leaf double									longitude;

	@xml_leaf double									latitude;

	@xml_leaf double									altitude;

	@xml_leaf double									heading;

	@xml_leaf double									tilt;

	@xml_leaf double									roll;

	@xml_leaf @xml_tag("altitudeMode") String	altitudeMode;	// enum values:
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
