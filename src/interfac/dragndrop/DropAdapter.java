package interfac.dragndrop;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
/**
 * Un exemple de MouseAdapter adapté au DragDrop
 */
public class DropAdapter extends MouseAdapter {
	private JPanel dropPan;
	private Border defautBorder = null;
	private Color defautBackGround = null;
	private String message;
	
	public DropAdapter (JPanel dropPan,String message) {
		this.dropPan = dropPan;
		this.message = message;
	}
	
	public DropAdapter (String message) {
		this.message = message;
	}
	
	public void initPan(JPanel dropPan){
		if (this.dropPan == null) this.dropPan = dropPan;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		defautBorder = dropPan.getBorder();
		dropPan.setBorder(new TitledBorder(message));
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		dropPan.setBorder(defautBorder);
	}
	
	
	@Override
	public void mouseEntered(MouseEvent e) {
		defautBackGround = dropPan.getBackground();
		int rgb = defautBackGround.getRGB();
		dropPan.setBackground(new Color((rgb & 0xfefefe)/2 + 0x7f7f7f));
	}

	@Override
	public void mouseExited(MouseEvent e) {
		dropPan.setBackground(defautBackGround);
	}
}
