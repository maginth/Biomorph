package interfac.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import biomorph.forme2D.IconBiomorph2D;
import interfac.global.AppletBiomorph;

/**
 * Création du menu pop-up accessible via clic droit sur un biomorph.
 */

public class PopUpListener extends MouseAdapter{
	
	
	
	
	
	public final static JPopupMenu popupMenuBiomorph = new JPopupMenu();
	
	final static JMenuItem 
	sauvegarder = new JMenuItem("Sauvegarder dans les Favoris")
	//, afficher = new JMenuItem("Afficher la généalogie")
	, supprimer = new JMenuItem("Supprimer")
	, modifGene = new JMenuItem("~Modifier Les Gènes~");
	// icone sur lequel sont fait les traitement, icone est mis à jour lors de l'appelle au JPopupMenu
	private static IconBiomorph2D icone;
	 
	
	static{
		//sauvegarder.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,KeyEvent.CTRL_MASK));
		//afficher.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,KeyEvent.CTRL_MASK));
		//supprimer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		popupMenuBiomorph.add(sauvegarder);
		popupMenuBiomorph.add(modifGene);
		//popupMenuBiomorph.add(afficher);
		popupMenuBiomorph.add(supprimer);

		/**
		 * Permet d'ajouter un biomorph dans l'onglet Favoris
		 */
		sauvegarder.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				AppletBiomorph.getFav().saveBiomorph(icone.getBiomorph());
				AppletBiomorph.getFav().updateUI();
			}
		});
		
		/**
		 * Permet d'afficher la généalogie du biomorph.
		 */
		/*afficher.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				AppletBiomorph.getArbre().centrerSurBiomorph(icone.getBiomorph());
			}
		});*/
		
		/**
		 * Permet de supprimer un biomorph dans les différents onglets ainsi que dans le laboratoire.
		 */
		supprimer.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				if(icone.getParent().toString().contains("PanelLaboratoire")){
					if (AppletBiomorph.getLab().getListeIcoSelection().contains(icone))
						AppletBiomorph.getLab().videSelection();
					else
						icone.supprimer();
					AppletBiomorph.getLab().actualiserVue();
				}
				else if(icone.getParent().toString().contains("JpanelFavoris")){
					if(icone.getParent().getName().contains("croisement")){
						AppletBiomorph.getFamille().removeBiomorph(icone);
						AppletBiomorph.getFamille().updateUI();
					}else if(icone.getParent().getName().contains("favoris")){
						AppletBiomorph.getFav().removeBiomorph(icone);
						AppletBiomorph.getFav().updateUI();
					}
				}
			}
		});
		
		modifGene.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				AppletBiomorph.getLab().showHackPanel(icone);
			}
		});
		
	}
	
	private IconBiomorph2D iconeBiomorph;
	
	public PopUpListener(final IconBiomorph2D icone){
		iconeBiomorph = icone;
	}
	

	/**
	 * Affiche le menu pop-up
	 */
	public void mouseReleased(MouseEvent e){
		if(SwingUtilities.isRightMouseButton(e)){
			icone = iconeBiomorph;
			popupMenuBiomorph.show(e.getComponent(), e.getX(), e.getY());	
		}
	}
	
	
	/**
	 * Permet de centrer sur le biomorph ciblé dans la généalogie, .
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e))
			AppletBiomorph.getArbre().centrerSurBiomorph(iconeBiomorph.getBiomorph());
	}
	
}