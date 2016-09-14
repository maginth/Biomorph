package interfac.dragndrop;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
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
	private TitledBorder title;
	private Color defautBackGround = null;
	
	@SuppressWarnings("serial")
	public DropAdapter (JPanel dropPan,String message) {
		this.dropPan = dropPan;
		title = new TitledBorder(message){
		    Insets overridenInset = new Insets(15, 0, 0, 0);
		    @Override
		    public Insets getBorderInsets(Component c) {
		        return overridenInset;
		    }
		};
		title.setTitleJustification(TitledBorder.CENTER);
	}
	
	public void initPan(JPanel dropPan){
		if (this.dropPan == null) this.dropPan = dropPan;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		defautBorder = dropPan.getBorder();
		dropPan.setBorder(title);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		dropPan.setBorder(defautBorder);
	}
	
	
	@Override
	public void mouseEntered(MouseEvent e) {
		defautBackGround = dropPan.getBackground();
		int rgb = defautBackGround.getRGB() & 0xf8f8f8;
		dropPan.setBackground(new Color(rgb/2 + rgb/4 + rgb/8 + 0x1f1f1f));
	}

	@Override
	public void mouseExited(MouseEvent e) {
		dropPan.setBackground(defautBackGround);
	}
}
