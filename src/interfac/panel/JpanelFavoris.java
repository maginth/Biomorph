package interfac.panel;

import interfac.dragndrop.DragDrop;
import interfac.dragndrop.DropAdapter;
import interfac.global.AppletBiomorph;
import interfac.global.Parametres;
import interfac.util.PopUpListener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import biomorph.abstrait.Biomorph;
import biomorph.forme2D.IconBiomorph2D;

public class JpanelFavoris extends JPanel{
	/**
	 * Les onglets favoris, croisement et biblio sont de ce type.
	 * Il permet l'ajout, la suppression de biomorphs et le zoom.
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<IconBiomorph2D> listeIcon;
	private ArrayList<Biomorph> listeBiomorph;
	private int tailleIcone = 70;
	public static Biomorph biomorphEnCour ;
	
	public JpanelFavoris(){
		this(null,true);
	}
	
	public JpanelFavoris(DropAdapter actionReceptionDrag){
		this(actionReceptionDrag,actionReceptionDrag==null? false:true);
	}
	
	public JpanelFavoris(DropAdapter actionReceptionDrag,boolean activer){
		listeIcon = new ArrayList<IconBiomorph2D>();
		listeBiomorph = new ArrayList<Biomorph>();
		
		addMouseWheelListener(new MouseWheelListener(){
			int count =0;
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				count++;
				if (count > 1) {
					double Xzoom = (e.getUnitsToScroll()<0)? 4d/3d : 3d/4d;
					if ((Xzoom>1 && tailleIcone<150) || (Xzoom<1 && tailleIcone>70)) {
						tailleIcone *=Xzoom;
						for (IconBiomorph2D ico : listeIcon) ico.changerTaille(tailleIcone);
						validate();
						invalidate();
						if (getParent() != null) getParent().validate();
					}
					count = 0;
				}
			}
		});
		if (activer) {
			if(actionReceptionDrag == null) actionReceptionDrag = new DropAdapter(this,"ajouter"){
				@Override
				public void mouseReleased(MouseEvent e) {
					addBiomorphFavoris((Biomorph) DragDrop.getContenuDrag());
				}
			};
			actionReceptionDrag.initPan(this);
			DragDrop.ajouterRecepteur(this,actionReceptionDrag, new String[]{"bioGenealogie","bioLab"});
		}
		
	}
	
	/**
	 * Ajout d'un biomorph dans l'onglet.
	 * Si le biomorph existe et qu'il n'est pas déjà dans l'onglet, on l'ajoute dans une liste d'icônes et dans une liste de biomorph.
	 * La liste de biomorph sert à vérifier que le biomorph n'y est pas déjà.
	 * Lors de l'ajout, on ajoute à l'icône le menu, et la fonction de drag&drop.
	 * @param bio le biomorph à ajouter.
	 */
	public void addBiomorphCroisement(Biomorph bio){
		if (bio != null){
			if(!listeBiomorph.contains(bio)){
				IconBiomorph2D ico = (IconBiomorph2D) bio.getIcone(tailleIcone);
				listeIcon.add(ico);
				listeBiomorph.add(bio);
				MouseListener popupListener = new PopUpListener(ico);
				ico.addMouseListener(popupListener);
				DragDrop.ajouterDragable(ico, "bioPanel", ico.copie, bio);
				add(ico);
			}
		}
	}

/**
 * Ajoute le biomorph dans le panel Favoris.	
 * @param bio
 */
	public void addBiomorphFavoris(Biomorph bio){
		if (bio != null){
			if(!listeBiomorph.contains(bio)){
				IconBiomorph2D ico = (IconBiomorph2D) bio.getIcone(tailleIcone);
				listeIcon.add(ico);
				listeBiomorph.add(bio);
				MouseListener popupListener = new PopUpListener(ico);
				ico.addMouseListener(popupListener);
				DragDrop.ajouterDragable(ico, "bioPanel", ico.copie, bio);
				add(ico);
				if(!ico.getParent().getName().contains("favoris")){
					removeBiomorph(ico);
					updateUI();
				}
			}
		}
	}

/**
 * Ajoute le biomorph dans le panel bibliothèque.
 * @param bio
 */
	public void addBiomorphBiblio(Biomorph bio){
		if (bio != null){
			if(!listeBiomorph.contains(bio)){
				IconBiomorph2D ico = (IconBiomorph2D) bio.getIcone(tailleIcone);
				listeIcon.add(ico);
				listeBiomorph.add(bio);
				MouseListener popupListener = new PopUpListener(ico);
				ico.addMouseListener(popupListener);
				DragDrop.ajouterDragable(ico, "bioPanel", ico.copie, bio);
				add(ico);
			}
		}
	}
	
	/**
	 * Fonction spécifique pour l'onglet croisement.
	 * Il permet d'ajouter les biomorphs générés par un croisement à l'aide du bouton "croisement" à côté du menu.
	 * Il y aura autant de biomorphs ajoutés que de parents.
	 * @param arrayList contient les enfants biomorphs issus du croisement multiple.
	 */
	public void addCroisement(){
		ArrayList<Biomorph> parents = new ArrayList<Biomorph>(AppletBiomorph.getLab().getListeIcoSelection().size());
		for (IconBiomorph2D ico : AppletBiomorph.getLab().getListeIcoSelection()){
			parents.add(ico.getBiomorph());
		}
		ArrayList<Biomorph> listeEnfant = Biomorph.croisementMultiple(parents,Parametres.X);
		for(int i=0;i<listeEnfant.size();i++){
			addBiomorphCroisement(listeEnfant.get(i));
		}
		AppletBiomorph.getTabPan().setSelectedIndex(2);
			
	}
	
	/**
	 * Suppression d'un biomorph à partir de l'icône.
	 * @param ico icône qui représente le biomorph.
	 */
	public void removeBiomorph(IconBiomorph2D ico){
		listeIcon.remove(ico);
		listeBiomorph.remove(ico.getBiomorph());
		DragDrop.retirerDragable(ico, "bioPanel");
		remove(ico);
		ico.supprimer();
	}
	
	public void viderTotalement(){
		while(! listeIcon.isEmpty()) {
			removeBiomorph(listeIcon.get(0));
		}
	}
	
	/**
	 * Suppression d'un biomorph.
	 * @param bio le biomorph à supprimer 
	 */
	public void removeBiomorph(Biomorph bio){
		for (IconBiomorph2D ico : listeIcon) {
			if (ico.getBiomorph() == bio) {
				removeBiomorph(ico);
				break;
			}
		}
	}
	
	
	/**
	 * Permet de modifier la taille des icônes.
	 * @param tailleIcone la taille de l'icône du biomorph.
	 */
	public void setTailleIcone(int tailleIcone) {
		this.tailleIcone = tailleIcone;
	}
}
