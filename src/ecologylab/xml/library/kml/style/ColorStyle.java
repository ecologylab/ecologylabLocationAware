/**
 * 
 */
package ecologylab.xml.library.kml.style;

import java.awt.Color;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.library.kml.KmlObject;

/**
 * @author Zach
 * 
 */
@xml_inherit public class ColorStyle extends KmlObject
{
	@xml_leaf Color								color;

	@xml_leaf @xml_tag("colorMode") String	colorMode;	// either normal or random

	/**
	 * 
	 */
	public ColorStyle()
	{
	}

	public ColorStyle(Color color, String colorMode)
	{
		this.color = color;
		this.colorMode = colorMode;
	}
}
