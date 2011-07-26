package ecologylab.serialization.types.scalar;

import java.text.ParseException;

import org.geotools.referencing.wkt.Parser;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import ecologylab.serialization.ScalarUnmarshallingContext;
import ecologylab.serialization.TranslationContext;
import ecologylab.serialization.types.ScalarType;
import ecologylab.serialization.types.TypeRegistry;

public class CoordinateReferenceSystemType extends ScalarType<CoordinateReferenceSystem>
{
	private static final Parser WKT_PARSER = new Parser();
	
	protected CoordinateReferenceSystemType()
	{
		super(CoordinateReferenceSystem.class, null, null, null);
	}


	@Override
	public CoordinateReferenceSystem getInstance(String value, String[] formatStrings,
			ScalarUnmarshallingContext scalarUnmarshallingContext)
	{
		CoordinateReferenceSystem crs = null;
		synchronized(WKT_PARSER)
		{
			try
			{
				crs = WKT_PARSER.parseCoordinateReferenceSystem(value);
			}
			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return crs;
	}
	
	/* (non-Javadoc)
	 * @see ecologylab.serialization.types.scalar.ScalarType#marshall(java.lang.Object, ecologylab.serialization.SerializationContext)
	 */
	@Override
	public String marshall(CoordinateReferenceSystem instance,
			TranslationContext serializationContext)
	{
		return instance.toWKT();
	}

	
	public static void register()
	{
		//TODO -- put this and KMLColorType into a class called LocationAwareTypes, and ensure its early initialization
		new CoordinateReferenceSystemType();	
	}

}
