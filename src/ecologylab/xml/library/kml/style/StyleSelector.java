/**
 * 
 */
package ecologylab.xml.library.kml.style;

import ecologylab.serialization.simpl_inherit;
import ecologylab.xml.library.kml.KmlObject;

/**
 * @author Zach
 *
 */
@simpl_inherit
public class StyleSelector extends KmlObject
{
	/**
	 * 
	 */
	public StyleSelector()
	{
	}
	
	protected StyleSelector(String id)
	{
		super(id);
	}
}
