package ecologylab.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContext;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.RenderListener;
import org.geotools.renderer.lite.StreamingRenderer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.Envelope;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.vividsolutions.jts.geom.Coordinate;

import ecologylab.generic.ResourcePool;

public class GTTiledMap extends JPanel implements RenderListener
{
	private int tileDimension;
	
	private TileContext tileMatrix[][];
	
	private Coordinate center;
	
	private float worldToScreenScale;
	
	private float rotation;
	
	private float radius;
	
	private TileContextPool tilePool;
	
	private MapContext map;
	
	private GTRenderer gRender;
	
	private BlockingQueue<TileContext> queue = new LinkedBlockingQueue<TileContext>();
	
	class Consumer implements Runnable 
	{
	   
	   public void run() {
	     try {
	       while (true) 
	       { 
	      	 if(queue.isEmpty())
	      		 repaint();
	      	 consume(queue.take());
	       }
	     } 
	     catch (InterruptedException ex) 
	     {
	    	 
	     }
	   }
	   
	   void consume(TileContext x)
	   {
	  	 synchronized(x)
	  	 {
	  		 if(!x.isCancled())
	  		 {
	  			 render(x);
	  			 x.markDone();
	  		 }
	  		 else
	  		 {
	  			 tilePool.release(x);
	  		 }
	  	 }
	   }
	 }

	
	public class rotateListener implements MouseWheelListener
	{

		@Override
		public void mouseWheelMoved(MouseWheelEvent arg0)
		{
			double rot = arg0.getWheelRotation() * 5.0;

			rotation += rot;
			repaint();
		}
		
	}
	
	public class moveListener implements KeyListener
	{

		@Override
		public void keyPressed(KeyEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyReleased(KeyEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent arg0)
		{
			float dx,dy;
			//System.out.println(arg0.getKeyChar());
			switch(arg0.getKeyChar())
			{
				case 's':
					dy = -1;
					dx = 0;
					break;
				case 'w':
					dy = 1;
					dx = 0;
					break;
				case 'a':
					dy = 0;
				dx = -1;
				break;
			case 'd':
				dy = 0;
				dx = 1;
				break;
			default:
				return;
			}
			
			Point2D.Double pnt = new Point2D.Double(dx, dy);
			
			AffineTransform t = AffineTransform.getRotateInstance(rotation / 180 * Math.PI);
			
			t.transform(pnt,pnt);
			
			Coordinate coord = new Coordinate();
			coord.x = center.x + pnt.x * 10 / worldToScreenScale;
			coord.y = center.y + pnt.y * 10 / worldToScreenScale;
			
			setCenter(coord);
			
		}
		
	}
	
	public GTTiledMap(Coordinate center, MapContext map, float worldToScreenScale, int tileDimension)
	{
		super();
		
		this.tileDimension = tileDimension;
				
		this.center = center;
		
		tilePool = new TileContextPool(9, 9, tileDimension);
		
		this.radius = (float) (tileDimension * 1.5);
		
		this.worldToScreenScale = worldToScreenScale;
		
		this.addMouseWheelListener(new rotateListener());
		
		this.map = map;
		
		gRender = new StreamingRenderer();
		gRender.setContext(map);
		gRender.addRenderListener(this);

    RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    gRender.setJava2DHints(hints);
		
		this.addKeyListener(new moveListener());
		this.setFocusable(true);
		
		Thread t = new Thread(new Consumer());
		t.start();
		
		completeReset();
	}
	
	private TileContext getCenterTile()
	{
		return tileMatrix[tileMatrix.length / 2][tileMatrix.length / 2];
	}
	
	private void render(TileContext cntx)
	{
		cntx.done = true;
		
		Graphics2D g2 = cntx.getGraphics();
		
		double x1 = cntx.center.x - this.tileDimension / 2.0 / worldToScreenScale;
		double x2 = cntx.center.x + this.tileDimension / 2.0 / worldToScreenScale;
		double y1 = cntx.center.y - this.tileDimension / 2.0 / worldToScreenScale;
		double y2 = cntx.center.y + this.tileDimension / 2.0 / worldToScreenScale;
		
		ReferencedEnvelope env = new ReferencedEnvelope(x1, x2, y1, y2, map.getCoordinateReferenceSystem());		
		
		gRender.paint(g2, new Rectangle(0,0,tileDimension,tileDimension), env);
	}
	
	private void queueRender(TileContext cntx)
	{
		queue.offer(cntx);
	}
	
	public void release(TileContext cntx)
	{
		synchronized(cntx)
		{
			if(!cntx.isDone())
			{
				cntx.cancle();
			}
			else
			{
				tilePool.release(cntx);
			}
		}
	}
	
	public void setCenter(Coordinate newCenter)
	{
		//System.out.println(newCenter.x + ", " + newCenter.y);
		
		TileContext middle = getCenterTile();
		double difX = (newCenter.x - middle.center.x) * worldToScreenScale;
		double difY = (newCenter.y - middle.center.y) * worldToScreenScale;
		
		double dxf = (-difX / tileDimension);
		double dyf = (difY / tileDimension);
		 
		dxf = Math.round(Math.abs(dxf)) * Math.signum(dxf);
		dyf = Math.round(Math.abs(dyf)) * Math.signum(dyf);
		
		int dx = (int) dxf;
		int dy = (int) dyf;
				
		center = newCenter;
		
		if(dx == 0 && dy == 0)
		{
			this.repaint();
			return;
		}
		
		/* cycle backwards over the matrix */
		for(int x = (dx > 0)?tileMatrix.length - 1:0;
				x < tileMatrix.length && x >= 0;
				x+=(dx > 0)?-1:1)
		{
			for(int y = (dy > 0)?tileMatrix[x].length - 1:0;
					y < tileMatrix[x].length && y >= 0;
					y+=(dy > 0)?-1:1)
			{
				int nx = x + dx;
				int ny = y + dy;
				int cx = x - dx;
				int cy = y - dy;
				
				TileContext tile = tileMatrix[x][y];
				
				boolean release = false;
				
				if(nx >= 0 && nx < tileMatrix.length &&
					 ny >= 0 && ny < tileMatrix[nx].length)
				{
					/*
					 * tile still in context
					 */
					tileMatrix[nx][ny] = tile;
					
					release = false;
				}
				else
				{
					release = true;
				}
				
				if(cx >= 0 && cx < tileMatrix.length &&
						 cy >= 0 && cy < tileMatrix[cx].length)
				{
					tileMatrix[x][y] = null;
				}
				else
				{
					tileMatrix[x][y] = tilePool.acquire();
					tileMatrix[x][y].center.x = tile.center.x - (dx * tileDimension / worldToScreenScale);
					tileMatrix[x][y].center.y = tile.center.y + (dy * tileDimension / worldToScreenScale);
					queueRender(tileMatrix[x][y]);
				}
				
				if(release)
				{
					release(tile);
				}
				
			}
		}
		this.repaint();
	}
	
	public GTTiledMap(Coordinate center, MapContext map, float worldToScreenScale)
	{
		this(center, map, worldToScreenScale, 256);
	}
	
	/**
	 * Resets the imageMatrix buffer and rerenders all the tiles.
	 */
	private void completeReset()
	{
		int tiles = (int) Math.ceil(radius * 2 / tileDimension);
		
		/* 
		 * make sure we always have an odd number of tiles
		 * that way we can make sure to have a center tile
		 */
		if(tiles % 2 == 0)
		{
			tiles++;
		}
		
		if(tileMatrix != null)
		{
			for(int x = 0; x < tileMatrix.length; x++)
			{
				for(int y = 0; y < tileMatrix[x].length; y++)
				{
					if(tileMatrix[x][y] != null)
					{
						release(tileMatrix[x][y]);
						tileMatrix[x][y] = null;
					}
				}
			}
		}
		
		if(tileMatrix == null || tileMatrix.length != tiles)
		{
			tileMatrix = new TileContext[tiles][tiles];
		}	
		
		for(int x = 0; x < tileMatrix.length; x++)
		{
			for(int y = 0; y < tileMatrix[x].length; y++)
			{
				TileContext tile = tilePool.acquire();
				tileMatrix[x][y] = tile;
				tile.center.x = this.center.x + (x - tileMatrix.length / 2) * tileDimension / worldToScreenScale;
				tile.center.y = this.center.y - (y - tileMatrix[x].length / 2) * tileDimension / worldToScreenScale;
				queueRender(tileMatrix[x][y]);
			}
		}
		
	}
	
	/**
	 * @see java.awt.Component#setBounds(int, int, int, int)
	 */
	@Override
	public void setBounds(int x, int y, int width, int height)
	{
		float newRadius = (float) Math.sqrt(width * width + height * height) / 2.0f;
		if(newRadius > radius + tileDimension )
		{
			radius = newRadius;
			completeReset();
		}
		else if(newRadius < radius - tileDimension)
		{
			radius = newRadius;
			completeReset();
		}
		super.setBounds(x, y, width, height);
	}
	
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		g2.clearRect(0, 0, this.getWidth(), this.getHeight());
		
		int midCoord = tileMatrix.length / 2;
		
		Coordinate middleCoordinate = getCenterTile().center;
		
		double difX = (middleCoordinate.x - this.center.x) * worldToScreenScale;
		double difY = (middleCoordinate.y - this.center.y) * worldToScreenScale;
		
		AffineTransform starting = g2.getTransform();
		AffineTransform change = new AffineTransform();
		
		change.translate(this.getSize().width / 2.0, this.getSize().height / 2.0);
		
		AffineTransform save = g2.getTransform();
		
		change.rotate(rotation / 180 * Math.PI);
			
		change.translate(difX, -difY);
		
		g2.transform(change);
		//g2.scale(0.25f, 0.25f);
		
		Shape clip = g2.getClip();
		
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		int count = 0;
		
		for(int x = 0; x < tileMatrix.length; x++)
		{
			for(int y = 0; y < tileMatrix[x].length; y++)
			{
				int dX = (x - midCoord) * tileDimension;
				int dY = (y - midCoord) * tileDimension;
				
				
				
				int tileX = -tileDimension / 2 + dX;
				int tileY = -tileDimension / 2 + dY;
				
				if(clip.intersects(tileX, tileY, tileDimension, tileDimension))
				{
					count++;
					g2.drawImage(tileMatrix[x][y].getTile(), tileX, tileY, this);
				}
				
				/*g2.drawRect(-tileDimension / 2 + dX, -tileDimension / 2 + dY,
										tileDimension, tileDimension);
				
				g2.drawString(tileMatrix[x][y].myCount + "", dX, dY);*/
				
			}
		}
		//System.out.println(count + " of " + tileMatrix.length * tileMatrix[0].length + " drawn ");
		g2.setTransform(save);
		
		//g2.drawRect(-this.getWidth()/2, -this.getHeight() / 2, this.getWidth(), this.getHeight());
		//g2.draw(circle);
		
		g2.setTransform(starting);
	}
	
	/**
	 * @see java.awt.Component#setBounds(java.awt.Rectangle)
	 */
	@Override
	public void setBounds(Rectangle r)
	{
		this.setBounds(r.x, r.y, r.width, r.height);
	}
	
	public static void main (String [] args) 
	{
		JFrame testFrame = new JFrame("Test");
		testFrame.setSize(500, 500);
		
		testFrame.add(new GTTiledMap(new Coordinate(), null, 1));
		
		testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		testFrame.setVisible(true);
	}

	@Override
	public void errorOccurred(Exception arg0)
	{
		System.out.println("Couldn't render completely: " + arg0.getLocalizedMessage());
	}

	@Override
	public void featureRenderer(SimpleFeature arg0)
	{
		// TODO Auto-generated method stub
		
	}
}
