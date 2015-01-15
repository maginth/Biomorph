package interfac.util;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

/**
 * Cette classe a �t� cr�e pour pouvoir acc�der de mani�re efficace et direct aux pixels d'une image (par un tableau d'int)
 * tout en conservant les capacit�s de dessin de l'objet Graphics. Les pixels et les capacit�s de dessin sont accessibles
 * n'importe quand, il n'est donc pas n�cessaire d'attendre le rendu � l'�cran pour r�cup�rer un objet Graphics
 */

public class ImageAccessible implements Cloneable {
	
	/** largeur de l'image
	 */
	public final int width;
	/** hauteur de l'image */
	public final int height;
	/** tableau des pixels de l'image */
	protected int[] pixels;
	/** objet graphic contenant les fonctionalit�s de dessin */
	protected Graphics2D dessin;
	/** image compatible awt reli� aux pixels et au dessin*/
	protected BufferedImage img;
	
	public ImageAccessible(int width,int height) {
		this(width,height,new int[width*height]);
	}
	
/**
 * Cr�e une ImageAccessible et construit les objets internes n�cessaires � son fonctionnement
 * @param width hauteur
 * @param height largeur
 * @param pixels tableau de pixels
 */
	public ImageAccessible(int width,int height,int[] pixels) {
		this.width = width;
		this.height = height;
		this.pixels = pixels;
		DataBufferInt dataBuffer = new DataBufferInt(pixels,pixels.length);
		SampleModel sm = new SinglePixelPackedSampleModel(
				dataBuffer.getDataType(),
				width,
				height,
				new int[]{0xff0000, 0xff00, 0xff, 0xff000000});
		WritableRaster raster = Raster.createWritableRaster(sm, dataBuffer, null);
		img = new BufferedImage(ColorModel.getRGBdefault(),raster,true,null);
	}

	
	public Graphics2D getDrawingTools() {
		if (dessin == null) dessin = img.createGraphics();
		return dessin;
	}
	
	public int[] getAccessPixels() {
		return pixels;
	}
	
	public BufferedImage getImageAWT() {
		return img;
	}
	
	public void draw(Graphics gc,int x,int y) {
		gc.drawImage(img,x,y,null);
	}
	
	public ImageAccessible clone() {
		return new ImageAccessible(width,height,pixels.clone());
	}
	
	/** 
	 * pr�pare l'objet pour le ramasse miette (Garbage collector)
	 */
	public void delet() {
		pixels = null;
		if (dessin != null) dessin.dispose();
		dessin = null;
		img.flush();
		img = null;
	}
		
}
