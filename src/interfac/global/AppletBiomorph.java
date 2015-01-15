package interfac.global;

import interfac.dragndrop.DragDrop;
import interfac.dragndrop.DropAdapter;
import interfac.panel.Genealogie;
import interfac.panel.JpanelFavoris;
import interfac.panel.PanelLaboratoire;
import interfac.util.ScrollVerticalLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.BorderUIResource;

import biomorph.abstrait.Biomorph;
import biomorph.forme2D.Biomorph2D.BiomorphStructure;
import biomorph.forme2D.PanelGenome;

import javax.swing.UIManager.*;




public class AppletBiomorph extends JApplet {
	
	private static final long serialVersionUID = 1L;
	public static PanelLaboratoire panelLaboratoire;
	private static JTabbedPane tabbedPane;
	private static JSplitPane split1,split2,split3;
	private static JpanelFavoris PanFavoris,panCroisement,PanBiblio;

	private static JScrollPane scrollpanelGenome;
	public static PanelGenome panelGenome;
	
	private static JButton fermer; // fermer le gene hacking
	
	private static Genealogie arbre;
	public static JMenuBar menu;
	private static String login;
	private static String motDePasse;
	// url de l'applet
	static URL adresse;
	
	
	
	/**
	 * Initialise l'applet ainsi que ses composants.
	 * 
	 */
	
	public void init() {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		}
		/*
		UIManager.put("nimbusBase", new Color(0xff4444));
		UIManager.put("nimbusBlueGrey", new Color(0xffaaaa));
		UIManager.put("control", new Color(0xffbbaa));
		*/
		adresse = getCodeBase();
		
		//////////////////////////////////////////////////////////
		// construit la partie droite de la barre d'outils      //
		//////////////////////////////////////////////////////////
		Border border = new BorderUIResource.LineBorderUIResource(Color.black);
		panelLaboratoire = new PanelLaboratoire();
		menu =new JMenuBar();
		final JSpinner spinner = new JSpinner();
		spinner.setValue(5);
		// listener limitant le nombre d'enfant généré à 100
		spinner.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				int n = (Integer) spinner.getValue();
				if (n<0) spinner.setValue(n=0);
				if (n>100) spinner.setValue(n=100);
				Parametres.nombreEnfant = n;
			}});
		JPanel panSpin = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,4));
		JButton boutonCroisement = new JButton("Croisement Multiple");
		boutonCroisement.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				AppletBiomorph.getFamille().viderTotalement();
				AppletBiomorph.getFamille().addCroisement();
				AppletBiomorph.getFamille().updateUI();
			}
		});
		boutonCroisement.setToolTipText("croiser le groupe de biomorph sélectionné.\n Un groupe d'enfant sera généré par redistribution aléatoire des gènes\n");
		spinner.setPreferredSize(new Dimension(40,20));
		panSpin.add(boutonCroisement);
		JLabel description = new JLabel("             Enfants par croisement simple:");
		panSpin.add(description);
		description.setToolTipText("nombre d'enfant générés lors d'un croisement simple\n (pour croiser, placer deux biomorph l'un sur l'autre)");
		panSpin.add(spinner);
		panSpin.add(new JSeparator());
		menu.add(panSpin);
		menu.add(new Parametres());
		//this.setJMenuBar(menu);
		
		//////////////////////////////////////////////////////////
		// fin construction partie droite de la barre d'outils  //
		//////////////////////////////////////////////////////////
	
		panelLaboratoire.setBackground(Color.black);
		arbre = new Genealogie(400,400,75);
		BiomorphStructure struct = new BiomorphStructure();
		struct.nbChrom = 1;
		struct.nbChromHomeo = 0;
		struct.nbGenChrom = 10;
		struct.symetrie = true;
		arbre.genealogieAleatoire(struct);
		arbre.setBorder(border);
		
		
/****************************************************************************************************/
/***************************** Début de la partie Onglets*******************************************/
/****************************************************************************************************/		
		
		
		
		DropAdapter panBiblio = new DropAdapter("Sauvegarder"){
			@Override public void mouseReleased(MouseEvent e){
				if (Menu.getLog() == null) {
					JOptionPane.showMessageDialog(null, "Veuillez vous inscrire ou vous connecter pour sauvegarder un biomorph");
				} else {
					Menu.BDDSauvegardeBiomorph(Menu.getLog(),(Biomorph) DragDrop.getContenuDrag());
					super.mouseReleased(e);
				}
			}
		};
		
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());
        
		PanFavoris = new JpanelFavoris();
		panCroisement = new JpanelFavoris(null);
		PanBiblio = new JpanelFavoris(panBiblio);
		PanFavoris.setLayout(new ScrollVerticalLayout());
		panCroisement.setLayout(new ScrollVerticalLayout());
		PanBiblio.setLayout(new ScrollVerticalLayout());
		PanFavoris.setBackground(new Color(0xe0daff));
		panCroisement.setBackground(new Color(0xffe0da));
		PanBiblio.setBackground(new Color(0xdaffe0));
		
		PanFavoris.setName("favoris");
		panCroisement.setName("croisement");
		PanBiblio.setName("biblio");
		
		
		
		JScrollPane scrollpane1 = new JScrollPane(PanFavoris);
		JScrollPane scrollpane2 = new JScrollPane(panCroisement);
		JScrollPane scrollpane3 = new JScrollPane(PanBiblio);
		
		
		
		tabbedPane = new JTabbedPane();
		
		tabbedPane.addTab( "Favoris", scrollpane1);
		tabbedPane.addTab( "Biblio", scrollpane3);
		tabbedPane.addTab( "Croisement", scrollpane2);
		
		
		tabbedPane.setBounds(0,400,300,300);
/****************************************************************************************************/
/***************************** Fin de la partie Onglets*******************************************/
/****************************************************************************************************/
	
		
		
/************************************************************************************************************/
/******************************Ajout des Components dans la fenetre************************************************/
/*********************************************************************************************************/
		
		
		// création du panel pour bricoler dans le génome
		panelGenome = new PanelGenome();
		scrollpanelGenome = new JScrollPane(panelGenome);
		scrollpanelGenome.setColumnHeaderView(menu);
		//scrollpanelGenome.setOpaque(false);
		//scrollpanelGenome.getViewport().setOpaque(false);
		//scrollpanelGenome.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		//scrollpanelGenome.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		
		

		
		
		
		this.setBackground(new Color(0xeee077));
		split1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,tabbedPane,arbre);
		split2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,split1,panelLaboratoire);
		split3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,null,scrollpanelGenome);
		split1.setOneTouchExpandable(true);
		split2.setOneTouchExpandable(true);
		split3.setOneTouchExpandable(true);
		this.add(split2);
		
		
		fermer = new JButton("X");
		fermer.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				panelLaboratoire.remove(fermer);
				dividerloc = split3.getDividerLocation();
				split2.setRightComponent(panelLaboratoire);
			}
		});
		fermer.setPreferredSize(new Dimension(40,30));
		fermer.setSize(40,30);
		
		split3.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (evt.getPropertyName().equals("dividerLocation")) {
					fermer.setLocation(panelLaboratoire.getWidth()-fermer.getWidth()+5,split3.getDividerLocation()-fermer.getHeight()+5);
				}
					
			}
		});
		
		this.setSize(1100,700);
	}
	private static int dividerloc = 0;
	
	public static void open_gen_hack() {
		panelLaboratoire.add(fermer);
		split2.setRightComponent(split3);
		split3.setLeftComponent(panelLaboratoire);
		if (dividerloc == 0) {
			split3.setDividerLocation(0.7d);
		}
		split3.setDividerLocation(dividerloc);
	}
	
	private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
            	if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            		panelLaboratoire.videSelection();
            	}
            }
            return false;
        }
    }
	
	public static JTabbedPane getJTabbedPane(){
		return tabbedPane;
	}
	public static JpanelFavoris getFav(){
		return PanFavoris;
	}
	public static JpanelFavoris getFamille(){
		return panCroisement;
	}
	public static JpanelFavoris getBiblio(){
		return PanBiblio;
	}
	public static PanelLaboratoire getLab(){
		return panelLaboratoire;
	}
	public static Genealogie getArbre(){
		return arbre;
	}
	public static String getLogin(){
		return login;
	}
	public static String getMotDePasse(){
		return motDePasse;
	}
	public static URL getAdresse(){
		return adresse;
	}
	
	public static JTabbedPane getTabPan(){
		return tabbedPane;
	}
}
