package ecologylab.standalone.MapTracker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.measure.unit.BaseUnit;
import javax.measure.unit.Unit;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.data.DataSourceException;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.GeneralDirectPosition;
import org.geotools.referencing.crs.DefaultProjectedCRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Point;

import ecologylab.oodss.logging.playback.ExtensionFilter;
import ecologylab.sensor.location.compass.CompassDatum;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.services.messages.LocationDataResponse;
import ecologylab.standalone.GeoClient;
import ecologylab.standalone.ImageGeotagger.ImageGeotagger;
import ecologylab.standalone.ImageGeotagger.DirectoryMonitor.ImageDirectoryMonitor;

public class MapPanel extends JPanel
{
	private Envelope env;
	
	private MathTransform projTrans;
	private MathTransform2D geomTrans;
	private GridGeometry2D geom;
	
	private GeneralDirectPosition latLon = new GeneralDirectPosition(2);
	private GeneralDirectPosition projCoords = new GeneralDirectPosition(2);
	private GeneralDirectPosition gridCoords = new GeneralDirectPosition(2);
	
	private Point2D gridLocation;
	private Point2D.Double center;
	
	private ArrayList<CompassDatum> compassData;
	private ArrayList<GPSDatum> gpsData;
	
	private BufferedImage mapImage;
	
	private Arc2D.Double hdopArc = new Arc2D.Double();

	private float	heading;
	
	private boolean noFix = false;
	private static final Color noFixGrey = new Color(.33f,.33f,.33f, 0.7f);
	
	public MapPanel(File geotiff) throws IOException
	{
		GeoTiffReader reader = new GeoTiffReader(geotiff, new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
		GridCoverage2D coverage = (GridCoverage2D) reader.read(null);
		env = coverage.getEnvelope();
		
		/*
		 * Conversion from geographic coordinates to coordinate system
		 * (i.e.) lat/lon to projection
		 */
		DefaultProjectedCRS crs = (DefaultProjectedCRS) coverage.getCoordinateReferenceSystem2D();
		projTrans = crs.getConversionFromBase().getMathTransform();
		
		geom = coverage.getGridGeometry();
		geomTrans = geom.getCRSToGrid2D();
		
		RenderedImage rImage = coverage.getRenderedImage();
		
		mapImage = convertRenderedImage(rImage);
		
		center = new Point2D.Double(mapImage.getWidth()/2, mapImage.getHeight()/2);
		
		gridLocation = center;
		
	}
	
	private BufferedImage convertRenderedImage(RenderedImage img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage)img;	
		}	
		ColorModel cm = img.getColorModel();
		int width = img.getWidth();
		int height = img.getHeight();
		WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		Hashtable properties = new Hashtable();
		String[] keys = img.getPropertyNames();
		if (keys!=null) {
			for (int i = 0; i < keys.length; i++) {
				properties.put(keys[i], img.getProperty(keys[i]));
			}
		}
		BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
		img.copyData(raster);
		return result;
	}
	
	public void setGeoLocation(GPSDatum gpsData, CompassDatum compassDatum)//double lat, double lon, float hdop, float heading)
	{
		if(gpsData.getGpsQual() != 0)
		{
			noFix = false;
			gridLocation = updateLocation(gpsData.getLat(), gpsData.getLon(), gpsData.getHdop());
		}
		else
		{
			noFix = true;
		}
	  
	  this.heading = compassDatum.getHeading();
	  
	  this.repaint();
	}
	
	private Point2D convertLatLonToGridPos(double lat, double lon)
	{
		GeneralDirectPosition latLonPos = new GeneralDirectPosition(lon, lat);
		Point2D ret = null;
		try
		{
			DirectPosition pos = projTrans.transform(latLonPos, null);
			if (pos.getOrdinate(0) <= env.getMaximum(0)
					&& pos.getOrdinate(0) >= env.getMinimum(0)
					&& pos.getOrdinate(1) <= env.getMaximum(1)
					&& pos.getOrdinate(1) >= env.getMinimum(1))
			{
				DirectPosition gridPos = geomTrans.transform(pos, null);
				ret = new Point2D.Double(gridPos.getOrdinate(0), gridPos.getOrdinate(1));
			}
			
		}
		catch (MismatchedDimensionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TransformException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	private Point2D updateLocation(double lat, double lon, float hdop)
	{
		latLon.setOrdinate(0, lon);
		latLon.setOrdinate(1, lat);
		
		Point2D ret = center;
		
		float radius = (float) (hdop * 8.20209974);
		
		try
		{
			projTrans.transform(latLon, projCoords);

			if (projCoords.getOrdinate(0) <= env.getMaximum(0)
					&& projCoords.getOrdinate(0) >= env.getMinimum(0)
					&& projCoords.getOrdinate(1) <= env.getMaximum(1)
					&& projCoords.getOrdinate(1) >= env.getMinimum(1))
			{
				geomTrans.transform(projCoords, gridCoords);
				ret = new Point2D.Double(gridCoords.getOrdinate(0), gridCoords.getOrdinate(1));
				
				this.hdopArc.setArcByCenter(projCoords.getOrdinate(0), projCoords.getOrdinate(1), radius, 0, 360, Arc2D.OPEN);			
			}
		}
		catch (MismatchedDimensionException e)
		{
			e.printStackTrace();
		}
		catch (TransformException e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public void paint(Graphics g)
	{
		int cx = this.getWidth() / 2;
		int cy = this.getHeight() / 2;
		
		int sx1 = (int) Math.max(gridLocation.getX() - this.getWidth()/2, 0);
		int sy1 = (int) Math.max(gridLocation.getY() - this.getHeight()/2, 0);
		int sx2 = (int) Math.min(gridLocation.getX() + this.getWidth()/2, mapImage.getWidth());
		int sy2 = (int) Math.min(gridLocation.getY() + this.getHeight()/2, mapImage.getHeight());
		
		int dx1 = (int) (cx - (gridLocation.getX() - sx1));
		int dy1 = (int) (cy - (gridLocation.getY() - sy1));
		int dx2 = (int) (cx + (sx2 - gridLocation.getX()));
		int dy2 = (int) (cy + (sy2 - gridLocation.getY()));
		
		BufferedImage subImage = mapImage.getSubimage(sx1, sy1, sx2-sx1, sy2-sy1);
		
		g.drawImage(subImage, dx1, dy1, this);
		
		Graphics2D g2 = (Graphics2D) g;
		
		if(noFix)
		{
			g2.setColor(noFixGrey);
			
			g2.fillRect(0,0, this.getWidth(), this.getHeight());
		}
		
		g2.setColor(Color.yellow);
		g2.fillOval(cx - 5, cy - 5, 10, 10);
		
		AffineTransform aft = g2.getTransform();
		
		g2.translate(cx, cy);
		
		g2.translate(-gridLocation.getX(), -gridLocation.getY());
		
		try
		{
			Shape ring = geomTrans.createTransformedShape(this.hdopArc);
			g2.draw(ring);
		}
		catch (TransformException e)
		{
			e.printStackTrace();
		}
		
		g2.setTransform(aft);
		
		/*g.drawImage(mapImage, dx1, dy1, dx2, dy2,
				sx1, sy1, sx2, sy2, this);*/
		
		/* draw heading line */
		
		double radHeading = Math.toRadians(heading + 90);
		
		g2.drawLine(cx, cy, (int)(cx - Math.cos(radHeading) * 50), (int)(cy - Math.sin(radHeading)*50));
		
		g2.setStroke(new BasicStroke(3));
		
		g2.setColor(Color.RED);
		if(gpsData != null && compassData != null)
		{
			for(int x = 0; x < gpsData.size(); x++)
			{
				GPSDatum gdatum = gpsData.get(x);
				CompassDatum cdatum = compassData.get(x);
				
				if(gdatum != null)
				{
					Point2D pnt = convertLatLonToGridPos(gdatum.getLat(), gdatum.getLon());
					if(pnt != null)
					{
						int px = (int)(pnt.getX() - gridLocation.getX()) + cx;
						int py = (int)(pnt.getY() - gridLocation.getY()) + cy;
						//System.out.println(px + ", " + py);
						g2.fillArc( px - 4, py - 4, 8, 8, 0, 360);
						if(cdatum != null)
						{
							
							double heading = Math.toRadians(cdatum.getHeading() + 90);
							g2.drawLine(px, py, (int)(px - Math.cos(heading) * 20), (int)(py - Math.sin(heading)*20));
						}
					}
				}
			}
		}
	}
	
	public static void main (String [] args) throws IOException
	{
		
		GeoClient client = new GeoClient();
		
		client.connect();
		
		ImageDirectoryMonitor monitor = ImageGeotagger.startMonitor(client);
				
		if(!client.connected())
		{
			return;
		}
		
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		
		FileFilter filter = new ExtensionFilter("tiff");
		chooser.setFileFilter(filter);
		
		int returnVal = chooser.showOpenDialog(null);
		
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			client.disconnect();
			return;
		}
		
		JFrame frame = new JFrame("Map Window");
		
		MapPanel mapPanel = new MapPanel(chooser.getSelectedFile());
		
		frame.add(mapPanel);
		
		frame.setSize(400, 400);
		
		frame.setVisible(true);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//mapPanel.setGeoLocation(30.613672, -96.338842, 50);
		
		mapPanel.compassData = monitor.getCompassData();
		mapPanel.gpsData = monitor.getGPSData();
		
		while(true)
		{
			LocationDataResponse resp = client.updateLocation();
			
			if(resp.gpsData != null && resp.compassData != null)
			{
				mapPanel.compassData = monitor.getCompassData();
				mapPanel.gpsData = monitor.getGPSData();
				mapPanel.setGeoLocation(resp.gpsData, resp.compassData);
				//System.out.println("Heading: " + resp.compassData.getHeading());
			}
			
			try
			{
				Thread.sleep(200);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
