package biomorph.test;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 * @author: Mathieu Guinin
 */

public class TestBMP extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	public Pan pan;
	public TestBMP() {
		super("Pixel test");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1280, 1024));
		pan = new Pan();
		add(pan);
	}

	class Pan extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int m =0;
		int mMax = 100;
		double t = System.currentTimeMillis();
		final int[] image = new int[1280*1024];
		final int[] mBitMasks = {0xff0000, 0xff00, 0xff, 0xff000000};
		Random rand = new Random();
		//ToolkitImage img;
		//MemoryImageSource source = new MemoryImageSource(1280, 1024, image, 0, 1280);
		DataBufferInt dataBuffer = new DataBufferInt(image,image.length);
		SampleModel sm = new SinglePixelPackedSampleModel(dataBuffer.getDataType(),
		          1280, 1024, mBitMasks);
		WritableRaster raster = Raster.createWritableRaster(sm, dataBuffer, null);
		BufferedImage img2 = new BufferedImage(ColorModel.getRGBdefault(),raster,true,null);
		Graphics2D gg = img2.createGraphics();
		
		public void fill() {
			int i,j;
			m++;
			for(i=0;i<1280;i++) for(j=0;j<1024;j++){
				image[i+j*1280] = 0xff000000 | (i*j*m);
			}
			//img = new ToolkitImage(source);
			//gg.drawImage(img, 0, 0, null);
			for (int k=0;k<100;k++) {
				gg.setColor(new Color(rand.nextInt()));
				gg.fillOval(rand.nextInt(1280), rand.nextInt(1024), rand.nextInt(100), rand.nextInt(100));
				}
			repaint();
		}
		@Override
		public void paintComponent(Graphics g) {
			g.drawImage(img2, 0, 0, null);
			if (m<mMax) fill();
			else System.out.print(mMax/(System.currentTimeMillis()-t)*1000+" images par seconde");
		}
	}
	
	public static void main(String[] args) {
		TestBMP demo = new TestBMP();
		demo.pack();
		demo.setVisible(true);
	}
}