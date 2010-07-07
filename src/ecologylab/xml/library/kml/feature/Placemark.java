/**
 * 
 */
package ecologylab.xml.library.kml.feature;

import ecologylab.xml.simpl_inherit;
import ecologylab.xml.ElementState.xml_tag;
import ecologylab.xml.library.kml.geometry.LineString;
import ecologylab.xml.library.kml.geometry.Point;
import ecologylab.xml.library.kml.geometry.Polygon;

/**
 * @author Zach
 * 
 */
@simpl_inherit @xml_tag("Placemark") public class Placemark extends KmlFeature
{
	@simpl_composite @xml_tag("Point") Point					point			= null;

	@simpl_composite @xml_tag("LineString") LineString	lineString	= null;
	
	@simpl_composite @xml_tag("Polygon") Polygon			polygon		=	null;

	// TODO LinearRing, Polygon, MultiGeometry, Model

	/**
	 * 
	 */
	public Placemark()
	{
	}
	
	public Placemark(String name, String description, String styleUrl)
	{
		super(name, description, styleUrl);
	}

	public Point getPoint()
	{
		return point;
	}

	public void setPoint(Point point)
	{
		this.point = point;

		this.lineString = null;
		this.polygon = null;
	}

	public LineString getLineString()
	{
		return lineString;
	}

	public void setLineString(LineString lineString)
	{
		this.lineString = lineString;

		this.point = null;
		this.polygon = null;
	}
	
	public Polygon getPolygon()
	{
		return polygon;
	}
	
	public void setPolygon(Polygon polygon)
	{
		this.polygon = polygon;
		
		this.point = null;
		this.lineString = null;
	}
}
