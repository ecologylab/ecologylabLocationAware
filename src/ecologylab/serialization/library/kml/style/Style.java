/**
 * 
 */
package ecologylab.serialization.library.kml.style;

import ecologylab.serialization.annotations.simpl_composite;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_tag;

/**
 * @author Zach
 *
 */
@simpl_inherit
@simpl_tag("Style")
public class Style extends StyleSelector
{
	@simpl_composite @simpl_tag("LineStyle") LineStyle lineStyle;
	@simpl_composite @simpl_tag("PolyStyle") PolyStyle polyStyle;
	@simpl_composite @simpl_tag("IconStyle") IconStyle iconStyle;

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