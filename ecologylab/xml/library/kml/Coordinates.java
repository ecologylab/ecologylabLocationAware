/**
 * 
 */
package ecologylab.xml.library.kml;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ecologylab.sensor.gps.data.GeoCoordinate;
import ecologylab.xml.ElementState;
import ecologylab.xml.types.scalar.CoordinatesType;
import ecologylab.xml.types.scalar.TypeRegistry;

/**
 * The Coordinates object is a special case specifically for KML. Because KML
 * nests a list of coordinates as comma-delimited values within an XML element,
 * we need this special, hybrid object.
 * 
 * The Coordinates object stores TWO representations of its data: one in the
 * String, comma-delimited form (field:coords) for going to/from KML and the
 * other in the form of an ArrayList<GeoCoordinate> for use by programmers
 * (field:coordinateList).
 * 
 * When a Coordinates object is translated FROM KML, the coordinateList is
 * automatically filled in using the the coords String. When it is translated TO
 * KML, the values in the coordinateList are serialized to the coords String.
 * 
 * @author Zach
 * 
 */
public class Coordinates extends ElementState
{
	static
	{
		TypeRegistry.register(CoordinatesType.class);
	}

	private static final String	COORDINATE_PATTERN_STRING	= "([+-]?\\d+(?:\\.\\d+)?)[\\\\z,]?";

	private static final Pattern	PATTERN							= Pattern
																						.compile(COORDINATE_PATTERN_STRING);

	@xml_leaf String					coords;

	/**
	 * Parses and adds the geographic coordinates in coords to the
	 * coordinateList; called automatically immediately after translating FROM
	 * KML.
	 */
	@Override protected void postTranslationProcessingHook()
	{
		super.postTranslationProcessingHook();

		this.appendStringRepresentation(this.coords);
	}

	/**
	 * Loads coords with the current values from coordinateList; called
	 * automatically immediately before translating this TO KML.
	 */
	@Override protected void preTranslationProcessingHook()
	{
		super.preTranslationProcessingHook();

		this.loadCoordsFromCoordinateList();
	}

	private void loadCoordsFromCoordinateList()
	{
		// make a stringbuilder for the intermediate values; estimate needed size
		StringBuilder newCoord = new StringBuilder(
				this.coordinateList.size() * 15);

		for (int i = 0; i < this.coordinateList.size(); i++)
		{
			newCoord.append(this.coordinateList.get(i));

			if (i < (this.coordinateList.size() - 1))
			{ // every time but the last time, we need a comma
				newCoord.append(',');
			}
		}
		
		this.coords = newCoord.toString();
	}

	ArrayList<GeoCoordinate>	coordinateList	= new ArrayList<GeoCoordinate>();

	/**
	 * 
	 */
	public Coordinates()
	{
	}

	/**
	 * Initialize with a string of coordinates, which should be comma-delimited
	 * triples (latitude, longitude, altitude).
	 * 
	 * @param coords
	 */
	public Coordinates(String coords)
	{
		this.coords = coords;
	}

	@Override public String toString()
	{
		return coords;
	}

	/**
	 * Parses a String of coordinates, expressed as comma-delimited triples
	 * (longitude, latitude, altitude). If the last triple in the list is
	 * incomplete, it is not appended unless it is a special case of having
	 * exactly two, in which case the altitude is assumed to be 0.
	 * 
	 * @param coords
	 */
	protected void appendStringRepresentation(String coords)
	{
		int i = 0;
		int timesThrough = 0;

		double readValue;

		double lon = 0;
		double lat = 0;
		double alt = 0;

		GeoCoordinate g;

		Matcher m = PATTERN.matcher(coords);

		while (m.find())
		{
			timesThrough++;
			readValue = Double.parseDouble(m.group(1));

			switch (i)
			{ // we're reading one coordinate at a time, and they go lon, lat, alt
			case (0):
				lon = readValue;
				i++;
				break;
			case (1):
				lat = readValue;
				i++;
				break;
			case (2):
				alt = readValue;
				i = 0;

				g = new GeoCoordinate(lat, lon, alt);
				this.coordinateList.add(g);

				break;
			}
		}

		if (timesThrough == 2)
		{ // special case for a Point, where we can JUST specify lon and lat
			// in this case, since we have not yet had 3 values come out, we have
			// added NO coordinates to the coordinate list. We add them now.
			g = new GeoCoordinate(lat, lon, 0);
			this.coordinateList.add(g);
		}
	}

	public ArrayList<GeoCoordinate> getCoordinateList()
	{
		return coordinateList;
	}
}
