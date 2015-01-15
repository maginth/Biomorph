package interfac.panel;

import interfac.dragndrop.DragDrop;
import interfac.global.Parametres;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;

import biomorph.forme2D.Biomorph2D;
import biomorph.forme2D.IconBiomorph2D;

public class IconLaboratoire extends IconBiomorph2D {
	
	public int x,y,taille;
	private static final long serialVersionUID = 1L;
	private PanelLaboratoire lab;
	
	public IconLaboratoire(Biomorph2D bio, int taille,PanelLaboratoire lab) {
		super(bio, taille,lab.getListeIcoSelection());
		this.taille = taille;
		this.lab = lab;
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isMiddleMouseButton(e)) {
					Biomorph2D enfant = biomorph.duplique(Parametres.X);
					getLab().addBiomorph(enfant,x,y+220,0);
				}
			}
		});
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
