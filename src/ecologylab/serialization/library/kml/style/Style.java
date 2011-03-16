/**
 * 
 */
package ecologylab.serialization.library.kml.style;

import ecologylab.serialization.ElementState.xml_tag;
import ecologylab.serialization.simpl_inherit;

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