/**
 * 
 */
package ecologylab.xml.library.kml.view;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.ElementState.xml_tag;

/**
 * @author Zach
 *
 */
@xml_inherit
@xml_tag("LookAt")
public class LookAt extends AbstractView
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
	public LookAt()
	{
		// TODO Auto-generated constructor stub
	}

}
