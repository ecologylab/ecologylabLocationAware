/**
 * 
 */
package ecologylab.xml.library.kml.style;

import ecologylab.xml.simpl_inherit;
import ecologylab.xml.ElementState.xml_tag;

/**
 * @author Zach
 *
 */
@simpl_inherit
@xml_tag("Style")
public class Style extends StyleSelector
{
	@simpl_composite @xml_tag("LineStyle") LineStyle lineStyle;
	@simpl_composite @xml_tag("PolyStyle") PolyStyle polyStyle;
	@simpl_composite @xml_tag("IconStyle") IconStyle iconStyle;

	// TODO labelstyle, balloonstyle, liststyle
	
	
	/**
	 * 
	 */
	public Style()
	{
	}

	public Style(String id, LineStyle lineStyle, PolyStyle polyStyle, IconStyle iconStyle)
	{
		super(id);
		
		this.lineStyle = lineStyle;
		this.polyStyle = polyStyle;
		this.iconStyle = iconStyle;
	}
}