package ecologylab.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.vividsolutions.jts.geom.Coordinate;

import ecologylab.generic.ResourcePool;

public class GTTiledMap extends JPanel 
{
	private int tileDimension;
	
	private TileContext tileMatrix[][];
	
	private Coordinate center;
	
	private float worldToScreenScale;
	
	private float rotation;
	
	private float radius;
	
	private TileContextPool tilePool;
	
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
					dy = 1;
					dx = 0;
					break;
				case 'w':
					dy = -1;
					dx = 0;
					break;
				case 'a':
					dy = 0;
				dx = 1;
				break;
			case 'd':
				dy = 0;
				dx = -1;
				break;
			default:
				return;
			}
			
			Point2D.Double pnt = new Point2D.Double(dx, dy);
			
			AffineTransform t = AffineTransform.getRotateInstance(-rotation / 180 * Math.PI);
			
			t.transform(pnt,pnt);
			
			Coordinate coord = new Coordinate();
			coord.x = center.x + pnt.x * 10;
			coord.y = center.y + pnt.y * 10;
			
			setCenter(coord);
			
		}
		
	}
	
	public GTTiledMap(Coordinate center, float worldToScreenScale, int tileDimension)
	{
		super();
		
		this.tileDimension = tileDimension;
				
		this.center = center;
		
		tilePool = new TileContextPool(9, 9, tileDimension);
		
		this.radius = (float) (tileDimension * 1.5);
		
		this.worldToScreenScale = worldToScreenScale;
		
		this.addMouseWheelListener(new rotateListener());
		
		this.addKeyListener(new moveListener());
		this.setFocusable(true);
		
		completeReset();
	}
	
	private TileContext getCenterTile()
	{
		return tileMatrix[tileMatrix.length / 2][tileMatrix.length / 2];
	}
	
	private void render(TileContext cntx)
	{
		cntx.done = true;
	}
	
	public void setCenter(Coordinate newCenter)
	{
		TileContext middle = getCenterTile();
		double difX = (newCenter.x - middle.center.x) * worldToScreenScale;
		double difY = (newCenter.y - middle.center.y) * worldToScreenScale;
		
		double dxf = (-difX / tileDimension);
		double dyf = (-difY / tileDimension);
		 
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
					tileMatrix[x][y].center.y = tile.center.y - (dy * tileDimension / worldToScreenScale);
					render(tileMatrix[x][y]);
				}
				
				if(release)
				{
					tilePool.release(tile);
				}
				
			}
		}
		this.repaint();
	}
	
	public GTTiledMap(Coordinate center, float worldToScreenScale)
	{
		this(center, worldToScreenScale, 256);
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
						tilePool.release(tileMatrix[x][y]);
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
				tile.center.y = this.center.y + (y - tileMatrix[x].length / 2) * tileDimension / worldToScreenScale;
				render(tileMatrix[x][y]);
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
		if(newRadius > radius )
		{
			radius = newRadius + tileDimension;
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
		
		g2.translate(this.getSize().width / 2.0, this.getSize().height / 2.0);
		
		g2.rotate(rotation / 180 * Math.PI);
		
		g2.translate(difX, difY);
		
		//g2.scale(0.25f, 0.25f);
		
		AffineTransform afterRotation = g2.getTransform();
		
		for(int x = 0; x < tileMatrix.length; x++)
		{
			for(int y = 0; y < tileMatrix[x].length; y++)
			{
				int dX = (x - midCoord) * tileDimension;
				int dY = (y - midCoord) * tileDimension;
				
				g2.drawRect(-tileDimension / 2 + dX, -tileDimension / 2 + dY,
										tileDimension, tileDimension);
				
				g2.drawString(tileMatrix[x][y].myCount + "", dX, dY);
			}
		}
		
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
		
		testFrame.add(new GTTiledMap(new Coordinate(), 1));
		
		testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		testFrame.setVisible(true);
	}
}
