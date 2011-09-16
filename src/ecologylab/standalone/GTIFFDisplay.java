package ecologylab.standalone;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.media.jai.PlanarImage;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.InvalidGridGeometryException;
import org.geotools.data.DataSourceException;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.GeneralDirectPosition;
import org.geotools.referencing.crs.DefaultProjectedCRS;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.Projection;
import org.opengis.referencing.operation.TransformException;

import ecologylab.oodss.logging.playback.ExtensionFilter;

public class GTIFFDisplay
{
	public static void main(String[] args) throws IOException
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		FileFilter filter = new ExtensionFilter("tiff");
		chooser.setFileFilter(filter);
		
		int returnVal = chooser.showOpenDialog(null);
		
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		GeoTiffReader reader;
		try {
				File file = chooser.getSelectedFile();
		    reader = new GeoTiffReader(file, new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
		} catch (DataSourceException ex) {
		    ex.printStackTrace();
		    return;
		}

		GridCoverage2D coverage;
		try {
		    coverage = (GridCoverage2D) reader.read(null);
		} catch (IOException ex) {
		    ex.printStackTrace();
		    return;
		}

		// Using a GridCoverage2D
		DefaultProjectedCRS crs = (DefaultProjectedCRS) coverage.getCoordinateReferenceSystem2D();
		System.out.println(crs.toWKT());
		
		PlanarImage img;
		
		Projection proj = crs.getConversionFromBase();
		
		Envelope env = coverage.getEnvelope();
		RenderedImage image = coverage.getRenderedImage();

		GridGeometry2D geom = coverage.getGridGeometry();
		
		try
		{
			GeneralDirectPosition coord = new GeneralDirectPosition(2);
			GeneralDirectPosition coord2 = new GeneralDirectPosition(2);
			proj.getMathTransform().transform( new GeneralDirectPosition(-96.338842, 30.613672), coord);
			System.out.println(coord);
			//GridCoordinates2D gridPos = geom.worldToGrid(coord);
			System.out.println(geom.getCRSToGrid2D().transform(coord, null));
			
			System.out.println(geom.getEnvelope());
			
			System.out.println(coord2);
		}
		catch (InvalidGridGeometryException e)
		{
			e.printStackTrace();
		}
		catch (TransformException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Hello");
	}
}
