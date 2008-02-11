/**
 * 
 */
package ecologylab.xml.library.kml.style;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.ElementState.xml_tag;

/**
 * @author Zach
 *
 */
@xml_inherit
@xml_tag("Style")
public class Style extends StyleSelector
{
	@xml_nested @xml_tag("LineStyle") LineStyle lineStyle;
	@xml_nested @xml_tag("PolyStyle") PolyStyle polyStyle;

	// TODO iconstyle, labelstyle, balloonstyle, liststyle
	
	
	/**
	 * 
	 */
	public Style()
	{
	}

	public Style(String id, LineStyle lineStyle, PolyStyle polyStyle)
	{
		super(id);
		this.lineStyle = lineStyle;
		this.polyStyle = polyStyle;
	}
}