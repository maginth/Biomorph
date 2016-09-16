package interfac.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class ColorPicker extends JLabel {
	
	private static final long serialVersionUID = 1L;
	ImageAccessible imageAcessible;
	int[] pix;
	ImageIcon icon;
	int hueColor, r, width;
	int picX, picY, hueX, hueY;
	float hue;
	final float x1, y1, x2, y2, x3, y3, r_in;
	static final float sqr3_2 = (float)Math.sqrt(3) * 0.5f;  
	public static ColorPicker colorPicker = new ColorPicker(128);
	public Callback callback;
	Color color;
	
	public interface Callback {
		public void changeColor(Color color);
		public void rightClick();
	}
	
	public void pickColorAt(int x, int y) {
		picX = x;
		picY = y;
		float s = x == x2 ? 0 : 1.0f / (0.5f - sqr3_2 * (y - y2) / (x - x2));
		float v = (x - x2 - 2 * sqr3_2 * (y - y2)) / (3 * r_in);
		if (v > 1.0f)
			v = 1.0f;
		if (s < 0)
			s = 0;
		color = Color.getHSBColor(hue, s, v);
		if (callback != null)
			callback.changeColor(color);
		ColorPicker.this.repaint();
	}
	
	public void setHue(float h) {
		float[] hsv = {0,0,0};
		hue = h;
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
		color = Color.getHSBColor(h, hsv[1], hsv[2]);
		if (callback != null)
			callback.changeColor(color);
		hueColor = Color.HSBtoRGB(h, 1.0f, 1.0f);
		hueX = (int)(r + (r_in + r - 2) * 0.5 * Math.cos(h * 2 * Math.PI));
		hueY = (int)(r + (r_in + r - 2) * 0.5 * Math.sin(-h * 2 * Math.PI));
		repaint();
	}
	
	public void centerColor(int c) {
		float[] hsv = {0,0,0};
		this.color = new Color(c);
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
		hue = hsv[0];
		hueColor = Color.HSBtoRGB(hue, 1.0f, 1.0f);
		hueX = (int)(r + (r_in + r - 2) * 0.5 * Math.cos(hsv[0] * 2 * Math.PI));
		hueY = (int)(r + (r_in + r - 2) * 0.5 * Math.sin(-hsv[0] * 2 * Math.PI));
		float h = r_in * hsv[2];
		picX = (int)(x2 + h * 1.5f * hsv[1]);
		picY = (int)(y2 + h * sqr3_2 * (-2f + hsv[1]));
		repaint();
	}
	
	void paintCircle(Graphics2D g, int x, int y) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL));
		int rr = (int) (r * 0.1);
		g.drawArc(x - rr, y - rr, 2 * rr, 2 * rr, 0, 360);
		g.setColor(Color.WHITE);
		rr--;
		g.drawArc(x - rr, y - rr, 2 * rr, 2 * rr, 0, 360);
	}
	
	@Override
	public void paint(Graphics g2) {
		super.paint(g2);
		Graphics2D g = (Graphics2D)g2;
		g.setPaint(new GradientPaint(x1, y3, new Color(hueColor & 0xffffff, true), x3, y3, new Color(hueColor)));
		g.fillPolygon(new int[]{(int)x1,(int)x2,(int)x3}, new int[]{(int)y1,(int)y2,(int)y3}, 3);
		paintCircle(g, picX, picY);
		paintCircle(g, hueX, hueY);
	}
	
	public ColorPicker(int w) {
		super();
		setPreferredSize(new Dimension(w,w));
		setSize(w,w);
		r = w / 2;
		width = w;
		r_in = 0.8f * r;
		x1 = r - r_in * 0.5f;
		y1 = r - r_in * sqr3_2;
		x2 = x1;
		y2 = 2 * r - y1;
		x3 = r + r_in;
		y3 = r;
		imageAcessible = new ImageAccessible(w, w);
		pix = imageAcessible.getAccessPixels();
		setIcon(icon = new ImageIcon(imageAcessible.getImageAWT()));
		setCursor(Curs.cross);
		MouseAdapter mouseAdapter = new MouseAdapter() {
			boolean hue_drag, sv_drag;
			
			boolean isInsideTriangle(int x, int y) {
				return (x-x1)*(y2-y1) + (y1-y)*(x2-x1) > 0
						&& (x-x2)*(y3-y2) + (y2-y)*(x3-x2) > 0
						&& (x-x3)*(y1-y3) + (y3-y)*(x1-x3) > 0;
			}
			void sideProjection(Point p, float xa, float ya, float xb, float yb) {
				float scal = (p.x-xa)*(yb-ya) + (ya-p.y)*(xb-xa);
				if (scal <= 0) {
					double norm2 = (xb-xa)*(xb-xa) + (yb-ya)*(yb-ya);
					p.x -= scal*(yb - ya) / norm2;
					p.y -= scal*(xa - xb) / norm2;
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					int xx = e.getX(), yy = e.getY(), x = xx - r, y = yy - r;
					if (sv_drag = isInsideTriangle(xx, yy))
						pickColorAt(xx, yy);
					hue_drag = !sv_drag && x*x + y*y < r * r;
				} else if (SwingUtilities.isRightMouseButton(e)) {
					if (callback != null)
						callback.rightClick();
				}
				
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				int xx = e.getX(), yy = e.getY(), x = xx - r, y = yy - r;
				if (hue_drag) {
					setHue((float)(0.5 * Math.atan2(-y, x) / Math.PI));
				} else if (sv_drag) {
					Point p = new Point(xx, yy);
					sideProjection(p, x1, y1, x2, y2);
					sideProjection(p, x2, y2, x3, y3);
					sideProjection(p, x3, y3, x1, y1);
					if (p.y < y1) {p.x = (int) x1; p.y = (int) y1;}
					if (p.y > y2) {p.x = (int) x2; p.y = (int) y2;}
					if (p.x > x3) {p.x = (int) x3; p.y = (int) y3;}
					pickColorAt(p.x, p.y);
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				hue_drag = sv_drag = false;
			}
		};
		addMouseMotionListener(mouseAdapter);
		addMouseListener(mouseAdapter);
		Graphics2D g = imageAcessible.getDrawingTools();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL));
		int color = 0xffff0000, base_color, step_size = (int)(r * Math.PI/3);
		char shift_bits = 8;
		float var_color, shift_color = 256f / step_size;
		double angle = 2 * Math.PI / (6 * step_size); 
		double m = r-0.5;
		double cos = Math.cos(angle), sin = Math.sin(angle), x = r-1, y = 0, xp;
		/* Hue circle */
		for (int i=0; i < 6; i++) {
			base_color = color;
			var_color = 0;
			for (int j = 0; j < step_size; j++) {
				color = base_color + ((int)var_color << shift_bits);
				g.setColor(new Color(color));
				g.drawLine((int)(m + 0.8f*x), (int)(m + 0.8f*y), (int)(m + x), (int)(m + y));
				xp = x * cos + y * sin;
				y = y * cos - x * sin;
				x = xp;
				var_color += shift_color;
			}
			shift_color = -shift_color;
			shift_bits = (char) ((shift_bits + 8) % 24);
		}
		/* Saturation conic gradient */
		g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
		float c = 0, incr_color = 255f / (y2 - y1);
		for (int i = (int)y2-2; i > (int)y1; i--) {
			g.setColor(new Color(0x010101 * (int)c));
			g.drawLine((int)x1, (int)i, (int)x3-3, (int)y3);
			c += incr_color;
		}
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		centerColor(0xff818080);
	}
}
