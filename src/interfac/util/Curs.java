package interfac.util;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;


public class Curs {
	
	static public final Cursor open_hand, closed_hand, pointer, hidden, hand, cross;
	
	static {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		ClassLoader cl = Curs.class.getClassLoader();
		open_hand = toolkit.createCustomCursor(toolkit.getImage(cl.getResource("asset/open_hand_cursor.png")) , new Point(16, 16), "open_hand");
		closed_hand = toolkit.createCustomCursor(toolkit.getImage(cl.getResource("asset/closed_hand_cursor.png")) , new Point(16, 16), "closed_hand");
		hidden = toolkit.createCustomCursor(new BufferedImage(1, 1, BufferedImage.TRANSLUCENT) , new Point(0,0), "InvisibleCursor");
		pointer = new Cursor(Cursor.DEFAULT_CURSOR);
		hand = new Cursor(Cursor.HAND_CURSOR);
		cross = new Cursor(Cursor.CROSSHAIR_CURSOR);
	};
}
