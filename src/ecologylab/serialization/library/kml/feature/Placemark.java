/**
 * 
 */
package ecologylab.serialization.library.kml.feature;

import ecologylab.serialization.annotations.simpl_composite;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_tag;
import ecologylab.serialization.library.kml.geometry.LineString;
import ecologylab.serialization.library.kml.geometry.Point;
import ecologylab.serialization.library.kml.geometry.Polygon;

/**
 * @author Zach
 * 
 */
@simpl_inherit
@simpl_tag("Placemark")
public class Placemark extends KmlFeature
{
	@simpl_composite
	@simpl_tag("Point")
	Point				point				= null;

	@simpl_composite
	@simpl_tag("LineString")
	LineString	lineString	= null;

	@simpl_composite
	@simpl_tag("Polygon")
	Polygon			polygon			= null;

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
