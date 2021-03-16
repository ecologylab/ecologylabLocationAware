/**
 * 
 */
package ecologylab.serialization.library.kml.feature.container;

import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_tag;
import ecologylab.serialization.library.kml.style.StyleSelector;

/**
 * @author Zach
 * 
 */
@simpl_inherit @simpl_tag("Document") public class Document extends Container // TODO
																									// -
																									// unclear,
																									// does
																									// this
																									// subclass
																									// container,
																									// or
																									// feature??
{
	// TODO Schema

	/**
	 * 
	 */
	public Document()
	{
	}

	public Document(String name, String description, StyleSelector selector)
	{
		super(name, description, selector);
	}
}
