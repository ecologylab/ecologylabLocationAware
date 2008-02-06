/**
 * 
 */
package ecologylab.xml.library.kml;

import java.util.ArrayList;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.ElementState.xml_tag;

/**
 * @author Zach
 *
 */
@xml_inherit
@xml_tag("Document")
public class Document extends Container
{
	@xml_collection ArrayList<KmlFeature> features = new ArrayList<KmlFeature>();
	// TODO StyleSelector
	// TODO Schema

	/**
	 * 
	 */
	public Document()
	{
		// TODO Auto-generated constructor stub
	}

}
