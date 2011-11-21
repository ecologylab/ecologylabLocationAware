/**
 * 
 */
package ecologylab.serialization.library.kml.feature;

import ecologylab.serialization.annotations.Hint;
import ecologylab.serialization.annotations.simpl_composite;
import ecologylab.serialization.annotations.simpl_hints;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_scalar;
import ecologylab.serialization.annotations.simpl_tag;
import ecologylab.serialization.library.kml.KmlObject;
import ecologylab.serialization.library.kml.feature.container.Folder;
import ecologylab.serialization.library.kml.style.Style;
import ecologylab.serialization.library.kml.style.StyleSelector;

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

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) @simpl_tag("phoneNumber") String	phoneNumber;

	// TODO Snippet??

	@simpl_scalar @simpl_hints(Hint.XML_LEAF_CDATA) String									description;

	// TODO AbstractView??

	// TODO TimePrimitive??

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) @simpl_tag("styleUrl") String		styleUrl;

	@simpl_composite @simpl_tag("Style") Style			style;

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
	
	public void setStyle(Style st)
	{
		if (this instanceof Folder) return;
		
		this.style = st;
	}
}
