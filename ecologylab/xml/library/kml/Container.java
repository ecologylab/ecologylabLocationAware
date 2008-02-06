/**
 * 
 */
package ecologylab.xml.library.kml;

import java.util.ArrayList;

import ecologylab.xml.xml_inherit;

/**
 * @author Zach
 *
 */
@xml_inherit
public abstract class Container extends KmlFeature
{
	@xml_collection ArrayList<KmlFeature> features = new ArrayList<KmlFeature>();

	/**
	 * 
	 */
	public Container()
	{
	}
}
