package interfac.global;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
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
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.BorderUIResource;

import biomorph.forme2D.Biomorph2D;
import biomorph.forme2D.Biomorph2D.BiomorphStructure;
import biomorph.forme2D.PanelGenome;
import interfac.panel.Genealogie;
import interfac.panel.JpanelFavoris;
import interfac.panel.PanelLaboratoire;
import interfac.util.IO;
import interfac.util.ScrollVerticalLayout;




public class AppletBiomorph extends JApplet {
	
	private static final long serialVersionUID = 1L;
	public static PanelLaboratoire panelLaboratoire;
	private static JTabbedPane tabbedPane;
	private static JSplitPane split1,split2,split3;
	private static JpanelFavoris PanFavoris,panCroisement;
	public static AppletBiomorph app;

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
	public static void main(String [] param) {
		final AppletBiomorph app = new AppletBiomorph();
		final JFrame win = new JFrame();
		win.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		win.setUndecorated(true);
		win.setContentPane(app);
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.init();
		win.pack();
		win.setVisible(true);
		//Hijack the keyboard manager
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher( new KeyEventDispatcher() {
			boolean isOpen = false;
			@Override
		    public boolean dispatchKeyEvent(KeyEvent e) {
		    	if (isOpen)
		    		return false;
		    	isOpen = true;
		    	if (e.getKeyCode()== KeyEvent.VK_ESCAPE &&
					JOptionPane.showConfirmDialog(
							app,
							"Exit biomorph lab?",
							"",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					win.setVisible(false);
					win.dispose();
				}
		    	isOpen = false;
		        return false;
		    }
		});
		app.requestFocusInWindow();
		AppletBiomorph.app = app;
	}
	
	public void init() {
		try {
	//		UIManager.put("nimbusBase", new Color(0xff0000));
	//		UIManager.put("nimbusBlueGrey", new Color(0x00ff00));
	//		UIManager.put("control", new Color(0x0000ff));
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		}
		setBackground(Color.DARK_GRAY);
		UIManager.put("nimbusBase", Color.BLUE);
		UIManager.put("nimbusBlueGrey", Color.DARK_GRAY);
		//UIManager.put("control", Color.GRAY);
		
		try {
			adresse = getCodeBase();
		} catch(NullPointerException e) {
			// this is not an actual applet in a browser but a java window app
		}
		
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
		struct.nbGenChrom = 20;
		struct.symetrie = true;
		arbre.genealogieAleatoire(struct);
		arbre.setBorder(border);
		
		
/****************************************************************************************************/
/***************************** Début de la partie Onglets*******************************************/
/****************************************************************************************************/		
		
		
	
		
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());
        
		PanFavoris = new JpanelFavoris(true);
		panCroisement = new JpanelFavoris(null);
		PanFavoris.setLayout(new ScrollVerticalLayout());
		panCroisement.setLayout(new ScrollVerticalLayout());
		PanFavoris.setBackground(new Color(0xa08abf));
		panCroisement.setBackground(new Color(0x5aaf80));
		
		PanFavoris.setName("favoris");
		panCroisement.setName("croisement");
		
		try {
			for (Biomorph2D bio : IO.loadSavedBiomorph()) {
				PanFavoris.addBiomorph(bio);
			}
		} catch(Exception e) {}
		
		JScrollPane scrollpane1 = new JScrollPane(PanFavoris);
		JScrollPane scrollpane2 = new JScrollPane(panCroisement);
		scrollpane1.setBorder(null);
		scrollpane2.setBorder(null);
		
		
		
		tabbedPane = new JTabbedPane();
		
		tabbedPane.addTab( "Favoris", scrollpane1);
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
		
		

		
		
		
		split1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,tabbedPane,arbre);
		tabbedPane.setPreferredSize(new Dimension(300,400));
		//split1.setDividerLocation(500);
		split2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,split1,panelLaboratoire);
		split3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,null,scrollpanelGenome);
		split1.setOneTouchExpandable(true);
		split2.setOneTouchExpandable(true);
		split3.setOneTouchExpandable(true);
		this.add(split2);
		split1.setBorder(null);
		split2.setBorder(null);
		split3.setBorder(null);
		scrollpanelGenome.setBorder(null);
		
		
		fermer = new JButton("X");
		fermer.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				panelLaboratoire.remove(fermer);
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
	
	public static void open_gen_hack() {
		if (split2.getRightComponent() == panelLaboratoire) {
			panelLaboratoire.add(fermer);
			split2.setRightComponent(split3);
			split3.setLeftComponent(panelLaboratoire);
			split3.setResizeWeight(0.8d);
		}
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
