/**
 * 
 */
package ecologylab.xml.library.kml.feature;

import ecologylab.xml.Hint;
import ecologylab.xml.simpl_inherit;
import ecologylab.xml.library.kml.KmlObject;
import ecologylab.xml.library.kml.style.Style;
import ecologylab.xml.library.kml.style.StyleSelector;

/**
 * @author Zach
 * 
 */
@simpl_inherit public abstract class KmlFeature extends KmlObject
{
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) String									name;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) int										visibility;	// actually a boolean

	// -- can only be 0 or
	// 1

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) int										open;			// actually a boolean

	// -- can only be 0 or
	// 1

	// TODO atom:author??

	// TODO atom:link??

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) String									address;

	// TODO xal:AddressDetails??

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) @xml_tag("phoneNumber") String	phoneNumber;

	// TODO Snippet??

	@simpl_scalar @simpl_hints(Hint.XML_LEAF_CDATA) String									description;

	// TODO AbstractView??

	// TODO TimePrimitive??

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) @xml_tag("styleUrl") String		styleUrl;

	@simpl_composite @xml_tag("Style") Style			style;

	// TODO StyleMap

	// TODO Region??
	// TODO ExtendData??

	/**
	 * 
	 */
	public KmlFeature()
	{
	}

	public KmlFeature(String name, String description, StyleSelector sSelector)
	{
		this(name, description);

		// because we can only have either a style or a stylemap, we have to set
		// the right one
		if (sSelector instanceof Style)
		{
			this.style = (Style) sSelector;
		}
		else
		{
			// set stylemap instead.
		}
	}
	
	public KmlFeature(String name, String description, String styleUrl)
	{
		this(name, description);
		
		this.styleUrl = styleUrl;
	}
	
	public KmlFeature(String name, String description)
	{
		this.name = name;
		this.description = description;
	}

	public String getStyleUrl()
	{
		return styleUrl;
	}

	public void setStyleUrl(String styleUrl)
	{
		this.styleUrl = styleUrl;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public String getName()
	{
		return name;
	}
}
