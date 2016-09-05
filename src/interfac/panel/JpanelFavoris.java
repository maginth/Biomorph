package interfac.panel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import biomorph.abstrait.Biomorph;
import biomorph.abstrait.TauxMutation;
import biomorph.forme2D.Biomorph2D;
import biomorph.forme2D.IconBiomorph2D;
import interfac.dragndrop.DragDrop;
import interfac.dragndrop.DropAdapter;
import interfac.global.AppletBiomorph;
import interfac.global.Parametres;
import interfac.util.IO;
import interfac.util.PopUpListener;

public class JpanelFavoris extends JPanel{
	/**
	 * Les onglets favoris, croisement et biblio sont de ce type.
	 * Il permet l'ajout, la suppression de biomorphs et le zoom.
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<IconBiomorph2D> listeIcon;
	private ArrayList<Biomorph> listeBiomorph;
	private int tailleIcone = 70;
	private boolean saveToFile = false; 
	public static Biomorph biomorphEnCour ;
	
	public JpanelFavoris(){
		this(null,true, false);
	}
	
	public JpanelFavoris(DropAdapter actionReceptionDrag){
		this(actionReceptionDrag,actionReceptionDrag==null? false:true, false);
	}
	
	public JpanelFavoris(DropAdapter actionReceptionDrag,boolean activer,final boolean saveToFile){
		this.saveToFile = saveToFile;
		listeIcon = new ArrayList<IconBiomorph2D>();
		listeBiomorph = new ArrayList<Biomorph>();
		setBorder(new EmptyBorder(15, 0, 0, 0));
		
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
			if(actionReceptionDrag == null) actionReceptionDrag = new DropAdapter(this,"Sauvegarder"){
				@Override
				public void mouseReleased(MouseEvent e) {
					Biomorph2D bio = (Biomorph2D) DragDrop.getContenuDrag();
					if (saveToFile) {
						saveBiomorph(bio);
					} else
						addBiomorph(bio);
				}
			};
			actionReceptionDrag.initPan(this);
			DragDrop.ajouterRecepteur(this,actionReceptionDrag, new String[]{"bioGenealogie","bioLab"});
		}
		
	}
	
	public void saveBiomorph(Biomorph2D bio) {
		Biomorph previous = findByName(bio.getName());
		if (previous == null || JOptionPane.showConfirmDialog(
				JpanelFavoris.this,
				"Écraser le biomorph " + bio.getName() +  " ?",
				"",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ) {
			removeBiomorph(previous);
			Biomorph freeze = bio.duplique(new TauxMutation(0, 0, 0, 0));
			freeze.setName(bio.getName());
			addBiomorph(freeze);
			IO.saveBiomorph(bio);
		}
	}
	
	private Biomorph findByName(String name) {
		for (Biomorph bio : listeBiomorph)
			if (bio.getName() == name)
				return bio;
		return null;
	}
	
	public JpanelFavoris(boolean b) {
		this(null,true, true);
	}

	/**
	 * Ajout d'un biomorph dans l'onglet.
	 * Si le biomorph existe et qu'il n'est pas déjà dans l'onglet, on l'ajoute dans une liste d'icônes et dans une liste de biomorph.
	 * La liste de biomorph sert à vérifier que le biomorph n'y est pas déjà.
	 * Lors de l'ajout, on ajoute à l'icône le menu, et la fonction de drag&drop.
	 * @param bio le biomorph à ajouter.
	 */
	public void addBiomorph(final Biomorph bio){
		if (bio != null){
			if(!listeBiomorph.contains(bio)){
				final IconBiomorph2D ico = (IconBiomorph2D) bio.getIcone(tailleIcone);
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
			addBiomorph(listeEnfant.get(i));
		}
		AppletBiomorph.getTabPan().setSelectedIndex(2);
			
	}
	
	/**
	 * Suppression d'un biomorph à partir de l'icône.
	 * @param ico icône qui représente le biomorph.
	 */
	public void removeBiomorph(IconBiomorph2D ico){
		if (saveToFile)
			new File("save/" + ico.getBiomorph().getName()).delete();
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
