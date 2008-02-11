/**
 * 
 */
package ecologylab.xml.library.kml.feature;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.ElementState.xml_tag;
import ecologylab.xml.library.kml.geometry.LineString;
import ecologylab.xml.library.kml.geometry.Point;

/**
 * @author Zach
 * 
 */
@xml_inherit @xml_tag("Placemark") public class Placemark extends KmlFeature
{
	@xml_nested @xml_tag("Point") Point					point			= null;

	@xml_nested @xml_tag("LineString") LineString	lineString	= null;

	// TODO LineString, LinearRing, Polygon, MultiGeometry, Model

	/**
	 * 
	 */
	public Placemark()
	{
	}

	public Point getPoint()
	{
		return point;
	}

	public void setPoint(Point point)
	{
		this.point = point;

		this.lineString = null;
	}

	public LineString getLineString()
	{
		return lineString;
	}

	public void setLineString(LineString lineString)
	{
		this.lineString = lineString;

		this.point = null;
	}
}
