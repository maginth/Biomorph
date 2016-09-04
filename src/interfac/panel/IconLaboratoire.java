package interfac.panel;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;

import biomorph.forme2D.Biomorph2D;
import biomorph.forme2D.IconBiomorph2D;
import interfac.dragndrop.DragDrop;
import interfac.global.Parametres;

public class IconLaboratoire extends IconBiomorph2D {
	
	public int x,y,taille;
	private static final long serialVersionUID = 1L;
	private PanelLaboratoire lab;
	LinkedList<IconLaboratoire> listeSelect;
	private LinkedList<IconLaboratoire> currentClones;
	protected boolean select = false;
	private static Border border = new BorderUIResource.LineBorderUIResource(Color.CYAN);
	
	public IconLaboratoire(Biomorph2D bio, int taille,final PanelLaboratoire lab) {
		super(bio, taille);
		this.taille = taille;
		this.lab = lab;
		this.listeSelect = lab.getListeIcoSelection();
		this.currentClones = new LinkedList<IconLaboratoire>();
		addMouseListener(new MouseAdapter(){
			int xs=0,ys=0;
			@Override
			public void mouseReleased(MouseEvent e) {
				int dx = e.getXOnScreen()-xs, dy = e.getYOnScreen()-ys;
				// si un drag involontaire de 20 pixels à été fait on le considère comme un click (20*20=400)
				if(SwingUtilities.isLeftMouseButton(e)) {
					if (dx*dx+dy*dy < 400) {
						if(select){
							deselect();
						}else{
							select();
						}
					}
					if (dx*dx+dy*dy > 10000)
						currentClones.clear();
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					xs = e.getXOnScreen(); ys = e.getYOnScreen();
				}
				if (SwingUtilities.isMiddleMouseButton(e)) {
					Biomorph2D enfant = biomorph.duplique(Parametres.X);
					for (IconLaboratoire ico : currentClones)
						ico.x += 220;
					lab.actualiserVue();
					currentClones.add(lab.addBiomorph(enfant,x ,y+220,0));
				}
			}
		});
	}
	
	public void deselect(){
		if (listeSelect != null) {
			setBorder(null);
			listeSelect.remove(this);
			select = false;
			DragDrop.changerDragDefaut(this);
		}
	}
	
	public void select(){
		if (listeSelect != null) {
			setBorder(border);
			listeSelect.add(this);
			select = true;
			DragDrop.changerObjetsDrag(this,listeSelect);
		}
	}
	
	
	
	@Override
	public void supprimer(){
		getLab().getListeIco().remove(this);
		deselect();
		getLab().remove(this);
		DragDrop.retirerDragable(this, "bioLab");
		DragDrop.retirerRecepteur(this,new String[]{"bioLab"});
		super.supprimer();
	}
	
	public PanelLaboratoire getLab() {
		return lab;
	}
	
	
}
