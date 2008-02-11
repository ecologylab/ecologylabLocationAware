/**
 * 
 */
package ecologylab.xml.library.kml.style;

import java.awt.Color;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.ElementState.xml_tag;

/**
 * @author Zach
 * 
 */
@xml_inherit @xml_tag("LineStyle") public class LineStyle extends ColorStyle
{
	@xml_leaf int	width;

	/**
	 * 
	 */
	public LineStyle()
	{
	}

	public LineStyle(Color color, String colorMode, int width)
	{
		super(color, colorMode);

		this.width = width;
	}
}
