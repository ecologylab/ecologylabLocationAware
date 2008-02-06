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
@xml_tag("Placemark")
public class Placemark extends KmlFeature
{
	@xml_collection("Point") ArrayList<Point> points = new ArrayList<Point>(1);

	// TODO LineString, LinearRing, Polygon, MultiGeometry, Model
	
	/**
	 * 
	 */
	public Placemark()
	{
	}

}
