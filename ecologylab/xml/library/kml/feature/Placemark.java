/**
 * 
 */
package ecologylab.xml.library.kml.feature;

import java.util.ArrayList;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.ElementState.xml_tag;
import ecologylab.xml.library.kml.geometry.Geometry;
import ecologylab.xml.library.kml.geometry.LineString;
import ecologylab.xml.library.kml.geometry.Point;

/**
 * @author Zach
 * 
 */
@xml_inherit @xml_tag("Placemark") public class Placemark extends KmlFeature
{
	@xml_collection() @xml_classes(
	{ LineString.class, Point.class }) ArrayList<Geometry>	geometries	= new ArrayList<Geometry>(
																				1);

	// TODO LineString, LinearRing, Polygon, MultiGeometry, Model

	/**
	 * 
	 */
	public Placemark()
	{
	}
}
