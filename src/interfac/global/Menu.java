package interfac.global;


import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import biomorph.abstrait.Biomorph;
/**
 * Cette classe correspond à la barre du menu dans l'applet
 */


public class Menu extends JMenuBar {
	
	private static final long serialVersionUID = 1L;
	private JMenu profils,edition,transfert,aide,tutoriel;
	private JMenuItem desinscription,toutSupprimer,inscription,connexion,deconnexion,changer,sauvegarder,charger,listeAmis,echanger, ami, listeInvitation, tutorialInscription, tutorialDesinscription, tutorialConnexion, tutorialDeconnexion, tutorialChangerMdp, tutorialCharger, tutorialNettoyerEcran, tutorialEchange, tutorialDemandeAmi, tutorialAccepteInvitation;
	private static JTextField nomUtilisateur, nomUtilisateurAmi;
	private static JPasswordField motDePasse;
	private JPasswordField motDePasse1, motDePasse2;
	private JTextField nomUtilisateur1;
	private JButton boutonInscription,boutonConnexion,boutonChanger, boutonDemandeAmi;
	private JLabel messageBienvenue;
	private JCheckBoxMenuItem selectionDouble,selectionMultiple;
	private static String log;
	private String mdp, adresse1, ip, adresse,ip1;
	private static JPanel  PanTuto;
	private static JLabel phrase1,phrase2,phrase3,phrase4,phrase5,phrase6,phrase7,phrase8;
	
	
	public Menu(){


		/****************************************************************************************************/
		/***************************** Début du Menu *********************************************************/
		/****************************************************************************************************/

		this.profils = new JMenu("Profils");
		this.profils.setMnemonic('P');
		this.profils.setToolTipText("Gestions pour les comptes");
		this.edition = new JMenu("Edition");
		this.edition.setMnemonic('E');
		this.edition.setToolTipText("tout ce qui concerne l'édition des biomorphs");
		this.transfert = new JMenu("Transfert");
		this.transfert.setMnemonic('T');
		this.transfert.setToolTipText("Pour les échanges c'est par ici");
		this.aide = new JMenu("Aide");
		this.aide.setMnemonic('A');
		this.aide.setToolTipText("Besoin d'aide ?");


		this.selectionDouble = new JCheckBoxMenuItem("Sélection double");
		this.selectionDouble.setSelected(true);
		this.selectionMultiple = new JCheckBoxMenuItem("Sélection multiple");
		ButtonGroup bg = new ButtonGroup();
		bg.add(selectionDouble);
		bg.add(selectionMultiple);
		this.messageBienvenue = new JLabel("");
		this.messageBienvenue.setForeground(Color.red);


		this.inscription = new JMenuItem("Inscription");
		this.inscription.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,KeyEvent.CTRL_MASK + KeyEvent.SHIFT_DOWN_MASK));
		this.changer = new JMenuItem("Changer Mot de Passe");
		this.changer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,KeyEvent.CTRL_MASK + KeyEvent.SHIFT_DOWN_MASK));
		this.desinscription = new JMenuItem("Désinscription");
		this.desinscription.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U,KeyEvent.CTRL_MASK + KeyEvent.SHIFT_DOWN_MASK));
		this.deconnexion= new JMenuItem("Déconnexion");
		this.deconnexion.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,KeyEvent.CTRL_MASK + KeyEvent.SHIFT_DOWN_MASK));
		this.connexion= new JMenuItem("Connexion");
		this.connexion.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,KeyEvent.CTRL_MASK + KeyEvent.SHIFT_DOWN_MASK));
		this.sauvegarder= new JMenuItem("Sauvegarder");
		this.sauvegarder.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,KeyEvent.CTRL_MASK + KeyEvent.SHIFT_DOWN_MASK));
		this.charger = new JMenuItem("Charger");
		this.charger.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,KeyEvent.CTRL_MASK + KeyEvent.SHIFT_DOWN_MASK));	
		this.toutSupprimer = new JMenuItem("Nettoyer l'écran");
		this.toutSupprimer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,KeyEvent.CTRL_MASK + KeyEvent.SHIFT_DOWN_MASK));
		this.echanger = new JMenuItem("Echanger");
		this.echanger.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,KeyEvent.CTRL_MASK + KeyEvent.SHIFT_DOWN_MASK));
		this.ami = new JMenuItem("Demande d'ami");
		this.listeAmis = new JMenuItem("Voir vos amis");
		this.listeInvitation = new JMenuItem("Liste des invitations reçues");
		this.tutoriel = new JMenu("Tutoriel");
		this.tutorialInscription = new JMenuItem("Comment s'inscrire ");
		this.tutorialDesinscription = new JMenuItem("Comment se désinscrire");
		this.tutorialConnexion = new JMenuItem("Comment se connecter ");
		this.tutorialDeconnexion= new JMenuItem("Comment se déconnecter");
		this.tutorialChangerMdp= new JMenuItem("Comment changer de mot de passe");
		this.tutorialCharger= new JMenuItem("Comment charger des biomorphs sauvegardés");
		this.tutorialNettoyerEcran= new JMenuItem("Comment nettoyer la zone de travail");
		this.tutorialEchange= new JMenuItem("Comment échanger des biomorphs aves des amis");
		this.tutorialDemandeAmi= new JMenuItem("Comment faire une demande d'ami");
		this.tutorialAccepteInvitation= new JMenuItem("Comment voir la liste des invitations recues");

		this.profils.add(this.inscription);
		this.profils.add(this.desinscription);
		this.profils.add(this.connexion);
		this.profils.add(this.deconnexion);
		this.profils.add(this.changer);
		this.edition.add(this.sauvegarder);
		this.edition.add(this.charger);
		this.edition.add(this.toutSupprimer);
		this.transfert.add(this.echanger);
		this.transfert.add(this.ami);
		this.transfert.add(this.listeAmis);
		this.transfert.add(this.listeInvitation);
		this.tutoriel.add(tutorialInscription);
		this.tutoriel.add(tutorialDesinscription);
		this.tutoriel.add(tutorialConnexion);
		this.tutoriel.add(tutorialDeconnexion);
		this.tutoriel.add(tutorialChangerMdp);
		this.tutoriel.add(tutorialCharger);
		this.tutoriel.add(tutorialNettoyerEcran);
		this.tutoriel.add(tutorialEchange);
		this.tutoriel.add(tutorialDemandeAmi);
		this.tutoriel.add(tutorialAccepteInvitation);
		this.aide.add(this.tutoriel);

		this.add(this.profils);
		this.add(this.edition);
		this.add(this.transfert);
		this.add(this.aide);
		this.add(this.messageBienvenue);


		/****************************************************************************************************/
		/***************************** Fin du Menu *********************************************************/
		/****************************************************************************************************/


		this.transfert.setEnabled(false);
		this.charger.setEnabled(false);
		this.sauvegarder.setEnabled(false);

		

/**
*  action permettant l'inscription suite à l'utilisation du bouton inscription
*/
		this.inscription.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				final JDialog jsp = new JDialog();
				jsp.setLayout(new FlowLayout());
				nomUtilisateur = new JTextField(15);
				motDePasse = new JPasswordField(15);
				boutonInscription = new JButton("Inscription");
				nomUtilisateur.setName("ID : ");
				motDePasse.setName("MDP : ");
				boutonInscription.addActionListener(new ActionListener(){
					@SuppressWarnings({ "deprecation", "static-access" })
					public void actionPerformed(ActionEvent arg0){
						JOptionPane jop = new JOptionPane();
						if ((nomUtilisateur.getText().length() == 0) || (nomUtilisateur.getText().length() > 30) || (nomUtilisateur.getText().contains("µ"))) {
							jop.showMessageDialog(null, "Echec inscription!Nom vide ou trop long!");
						} else if ((motDePasse.getText().length() < 3) || (motDePasse.getText().length() > 30) || (motDePasse.getText().contains("µ"))){
							jop.showMessageDialog(null, "Echec inscription!Mot de passe vide ou trop long!");
						} else {
							log = nomUtilisateur.getText();
							mdp = motDePasse.getText();
							try {
								adresse = InetAddress.getLocalHost().toString();
								int index = adresse.lastIndexOf('/')+1;
								ip = adresse.substring(index, adresse.length());
								String resultat = BDDInscription(log, mdp, ip);
								if(resultat.contains("false")){
									messageBienvenue.setText("                                          Bienvenue "+log+" ! ");
									JOptionPane.showMessageDialog(null,"Bienvenue "+log);
									transfert.setEnabled(true);
									deconnexion.setEnabled(true);
									desinscription.setEnabled(true);
									connexion.setEnabled(false);
									inscription.setEnabled(false);
									changer.setEnabled(true);
									charger.setEnabled(true);
									sauvegarder.setEnabled(true);
								}else
									JOptionPane.showMessageDialog(null,resultat);
							} catch (UnknownHostException e) {
								e.printStackTrace();
							}
							jsp.setVisible(false);
						}
					}	
				});
				JLabel s = new JLabel(nomUtilisateur.getName().toString());
				JLabel s1 = new JLabel(motDePasse.getName().toString());
				jsp.add(s);
				jsp.add(nomUtilisateur);
				jsp.add(s1);
				jsp.add(motDePasse);
				jsp.add(boutonInscription);
				jsp.setVisible(true);
				jsp.setLocation(500, 300);
				jsp.pack();
			}
		});

/**
* 	action permettant la désinscription d'un utilisateur
*/		
		this.desinscription.setEnabled(false);
		this.desinscription.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				JOptionPane jop = new JOptionPane();
				@SuppressWarnings("static-access")
				int option = jop.showConfirmDialog(null, "Voulez-vous vous désinscrire ?", "Desincription", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
				if(option == JOptionPane.OK_OPTION){
					messageBienvenue.setText(null);
					connexion.setEnabled(true);
					inscription.setEnabled(true);
					desinscription.setEnabled(false);
					changer.setEnabled(false);
					deconnexion.setEnabled(false);
					transfert.setEnabled(false);
					charger.setEnabled(false);
					sauvegarder.setEnabled(false);
					AppletBiomorph.getLab().getListeIcoSelection().clear();
					AppletBiomorph.getLab().removeAll();
					AppletBiomorph.getFamille().removeAll();
					AppletBiomorph.getFav().removeAll();
					AppletBiomorph.getBiblio().removeAll();
					AppletBiomorph.getLab().updateUI();
					AppletBiomorph.getFamille().updateUI();
					AppletBiomorph.getFav().updateUI();
					AppletBiomorph.getBiblio().updateUI();
					log=null;
					nomUtilisateur=null;
					motDePasse=null;
				}
			}
		});

/**
 *  action permettant à un utilisateur de se connecter
 */
		this.connexion.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				final JDialog jsp1 = new JDialog();
				jsp1.setLayout(new FlowLayout());
				nomUtilisateur1 = new JTextField(15);
				motDePasse1 = new JPasswordField(15);
				boutonConnexion = new JButton("Connexion");
				nomUtilisateur1.setName("Login : ");
				motDePasse1.setName("Password : ");
				boutonConnexion.addActionListener(new ActionListener(){
					@SuppressWarnings("deprecation")
					public void actionPerformed(ActionEvent arg0){
						log = nomUtilisateur1.getText();
						mdp = motDePasse1.getText();
						try {
							adresse1 = InetAddress.getLocalHost().toString();
							int index = adresse1.lastIndexOf('/')+1;
							ip1 = adresse1.substring(index, adresse1.length());
							String resultat = BDDConnexion(log,mdp,ip1);
							if(resultat.contains("true")){
								messageBienvenue.setText("                                                            Bienvenue "+log+" ! ");
								JOptionPane.showMessageDialog(null,"Bienvenue "+log);
								deconnexion.setEnabled(true);
								desinscription.setEnabled(true);
								connexion.setEnabled(false);
								inscription.setEnabled(false);
								changer.setEnabled(true);
								transfert.setEnabled(true);
								charger.setEnabled(true);
								sauvegarder.setEnabled(true);
							}else{
								messageBienvenue.setText(null);
								deconnexion.setEnabled(false);
								desinscription.setEnabled(false);
								connexion.setEnabled(true);
								inscription.setEnabled(true);
								changer.setEnabled(false);
								transfert.setEnabled(false);
								charger.setEnabled(false);
								sauvegarder.setEnabled(false);
							}
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}
						jsp1.setVisible(false);
					}
				});

				JLabel s2 = new JLabel(nomUtilisateur1.getName().toString());
				JLabel s3 = new JLabel(motDePasse1.getName().toString());
				jsp1.add(s2);
				jsp1.add(nomUtilisateur1);
				jsp1.add(s3);
				jsp1.add(motDePasse1);
				jsp1.add(boutonConnexion);
				jsp1.setVisible(true);
				jsp1.setLocation(500, 300);
				jsp1.pack();
			}
		});

/**
 *  action permettant à un utilisateur connecté de se déconnecter de son compte
 */		
		this.deconnexion.setEnabled(false);
		this.deconnexion.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				int option = JOptionPane.showConfirmDialog(null, "Voulez-vous vous déconnecter ?", "Déconnexion...", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
				if(option == JOptionPane.OK_OPTION){
					messageBienvenue.setText(null);
					deconnexion.setEnabled(false);
					desinscription.setEnabled(false);
					connexion.setEnabled(true);
					inscription.setEnabled(true);
					changer.setEnabled(false);
					transfert.setEnabled(false);
					charger.setEnabled(false);
					sauvegarder.setEnabled(false);
					AppletBiomorph.getLab().getListeIcoSelection().clear();
					AppletBiomorph.getLab().removeAll();
					AppletBiomorph.getFamille().removeAll();
					AppletBiomorph.getFav().removeAll();
					AppletBiomorph.getBiblio().removeAll();
					AppletBiomorph.getLab().updateUI();
					AppletBiomorph.getFamille().updateUI();
					AppletBiomorph.getFav().updateUI();
					AppletBiomorph.getBiblio().updateUI();
					log=null;
					nomUtilisateur1=null;
					motDePasse1=null;
				}
			}
		});

/**
 * 	action qui permet de changer le mot de passe d'un utilisateur connecté		
 */	
		this.changer.setEnabled(false);
		this.changer.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				final JDialog jsp3 = new JDialog();
				jsp3.setLayout(new FlowLayout());
				motDePasse2 = new JPasswordField(15);
				boutonChanger = new JButton("Changer");
				motDePasse2.setName("Nouveau Password : ");
				boutonChanger.addActionListener(new ActionListener(){
					@SuppressWarnings("deprecation")
					public void actionPerformed(ActionEvent arg0){
						BDDChangerMotDePasse(log,motDePasse2.getText());
						jsp3.setVisible(false);
					}
				});
				JLabel s5 = new JLabel(motDePasse2.getName().toString());
				jsp3.add(s5);
				jsp3.add(motDePasse2);
				jsp3.add(boutonChanger);
				jsp3.setVisible(true);
				jsp3.setLocation(500, 300);
				jsp3.pack();
			}
		});

/**
 * 		action qui supprimer tout le contenu du panel laboratoire		
 */
		this.toutSupprimer.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				int i=0;
				while(!AppletBiomorph.getLab().getListeIco().isEmpty()){
					AppletBiomorph.getLab().getListeIco().get(i).supprimer();
				}
				AppletBiomorph.getLab().updateUI();
			}

		});

/**
 * 		action qui permet la sauvegarde de tous les biomorphs séléctionés dans le laboratoire		
 */
		this.sauvegarder.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				for(int i=0;i<AppletBiomorph.getLab().getListeIcoSelection().size();i++) {
					BDDSauvegardeBiomorph(Menu.getLog(),AppletBiomorph.getLab().getListeIcoSelection().get(i).getBiomorph());
				}
			}
		});

/**
 * 		action qui permet le chargement de la bibliothèque d'un utilisateur connecté
 */		
		this.charger.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				LinkedList<Biomorph> b = BDDChargerBiomorph(log);
				if(b.size()==0){
					AppletBiomorph.getBiblio().removeAll();
					AppletBiomorph.getBiblio().updateUI();
				}else{
					AppletBiomorph.getBiblio().removeAll();
					for(Biomorph bio:b){
						AppletBiomorph.getBiblio().addBiomorphBiblio(bio);
					}
					AppletBiomorph.getBiblio().updateUI();
				}
			}
		});

/**
 *		action qui permet de faire une demande d'ami à un utilisateur inscrit 
 */
		this.ami.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				final JDialog jsp = new JDialog();
				jsp.setTitle("Demande d'ami");
				jsp.setLayout(new FlowLayout());
				nomUtilisateurAmi = new JTextField(15);
				boutonDemandeAmi = new JButton("Envoyer la demande");
				nomUtilisateurAmi.setName("Nom : ");
				boutonDemandeAmi.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						String amitie = BDDDemandeAmi(log,nomUtilisateurAmi.getText());
						if(amitie.contains("true"))
							JOptionPane.showMessageDialog(null,"Demande envoyée à "+nomUtilisateurAmi.getText());
						else
							JOptionPane.showMessageDialog(null,nomUtilisateurAmi.getText()+" n'existe pas");
						jsp.setVisible(false);	
					}	
				});

				JLabel s = new JLabel(nomUtilisateurAmi.getName().toString());
				jsp.add(s);
				jsp.add(nomUtilisateurAmi);
				jsp.add(boutonDemandeAmi);
				jsp.setVisible(true);
				jsp.setLocation(500, 300);
				jsp.pack();
			}

		});
	
/**
 * 		action qui permet de consulter la liste de ses amis		
 */
		this.listeAmis.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				final JDialog jsp = new JDialog();
				jsp.setTitle("Vos amis :");
				ArrayList <JLabel> liste = BDDListeAmis(log);
				jsp.setLayout(new GridLayout(liste.size(),1));
				for(int i=0; i<liste.size();i++)
					jsp.add(liste.get(i));
				jsp.setVisible(true);
				jsp.setLocation(500, 300);
				jsp.pack();
			}

		});
	
/**
 * 		action qui permet de consulter la liste des invitations reçues		
 */
		this.listeInvitation.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				ArrayList <String> listeInvitation = new ArrayList<String>();
				listeInvitation = BDDlisteInvitation(log);
				int i=0;
				while (listeInvitation.isEmpty()!=true){
					JOptionPane invitation = new JOptionPane();
					@SuppressWarnings("static-access")
					int option = invitation.showConfirmDialog(null, "Accepter l'invitation de "+ listeInvitation.get(i)+"?", "", JOptionPane.YES_NO_OPTION, 
							JOptionPane.PLAIN_MESSAGE);

					if(option == JOptionPane.OK_OPTION){
						JOptionPane.showMessageDialog(null, "Invitation acceptée");
						BDDAcceptationInvitation(log, listeInvitation.get(i));
						i++;
					}
					else{
						JOptionPane.showMessageDialog(null, "Invitation refusée");
						BDDRefusInvitation(log, listeInvitation.get(i));
						i++;
					}
				}
			}	
		});

/**
 * 		action qui permet de faire des échanges d'un biomorph à un utilisateur avec lequel on est ami		
 */
		this.echanger.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				final JDialog jsp = new JDialog();
				jsp.setLayout(new FlowLayout());
				jsp.setTitle("Echange");
				nomUtilisateur = new JTextField(12);
				final JTextField message = new JTextField(12);
				JButton boutonEchange = new JButton("Echanger");
				nomUtilisateur.setName("Nom de l'ami destinataire : ");
				message.setName("Nom du biomorph à envoyer : ");
				boutonEchange.addActionListener(new ActionListener(){
					@SuppressWarnings("static-access")
					public void actionPerformed(ActionEvent arg0){
						JOptionPane jop = new JOptionPane();
						if ((nomUtilisateur.getText().length() == 0) || (nomUtilisateur.getText().length() > 30) || (nomUtilisateur.getText().contains("µ"))) {
							jop.showMessageDialog(null, "Echec échange! Nom vide ou trop long!");
						}
						else {
							String nomDestinataire = nomUtilisateur.getText();
							String contenuMessage = message.getText();
							jsp.setVisible(false);
							String resultat = BDDVerificationUtilisateurs(log, nomDestinataire);
							if(resultat.contains("true")){
								BDDEchangeBiomorph(log, nomDestinataire, contenuMessage);
							}	
							else{
								JOptionPane.showMessageDialog(null, "L'utilisateur: "+nomDestinataire+" n'est pas dans votre liste d'amis");
							}
						} 
					}	
				});
				JLabel s = new JLabel(nomUtilisateur.getName().toString());
				JLabel s1 = new JLabel(message.getName().toString());
				jsp.add(s);
				jsp.add(nomUtilisateur);
				jsp.add(s1);
				jsp.add(message);
				jsp.add(boutonEchange);
				jsp.setVisible(true);
				jsp.setLocation(500, 300);
				jsp.pack();
			}
		});		

/**
 * Tutoriel pour l'inscription
 */
		this.tutorialInscription.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PanTuto = new JPanel();
				PanTuto.setLayout(new GridLayout(5, 1));
				PanTuto.setBackground(Color.black);
				JScrollPane scrollpane = new JScrollPane(PanTuto);
				if((AppletBiomorph.getTabPan().getTabCount())==3){
					AppletBiomorph.getTabPan().addTab("Tutoriel inscription", scrollpane);
				}else if((AppletBiomorph.getTabPan().getTabCount())==4){
					AppletBiomorph.getTabPan().remove(3);
					AppletBiomorph.getTabPan().addTab("Tutoriel inscription", scrollpane);
				}
				AppletBiomorph.getTabPan().setSelectedIndex(AppletBiomorph.getTabPan().getTabCount()-1);
				phrase1 = new JLabel("1) Pour vous inscrire cliquez sur <Inscription> dans le menu <Profils>.");
				phrase2 = new JLabel("2) Remplissez le champ ID avec le nom de compte souhaité.");
				phrase3 = new JLabel("3) Remplissez le champ MDP avec le mot de passe de votre futur compte.");
				phrase4 = new JLabel("4) Une fois fait, valider à l'aide du bouton <Inscription>.");
				phrase5 = new JLabel("5) Si l'inscription a réussie, un message de bienvenue s'affiche et vous etes connecté sur le compte crée.");
				phrase1.setForeground(Color.white);
				phrase2.setForeground(Color.white);
				phrase3.setForeground(Color.white);
				phrase4.setForeground(Color.white);
				phrase5.setForeground(Color.white);
				PanTuto.add(phrase1);
				PanTuto.add(phrase2);
				PanTuto.add(phrase3);
				PanTuto.add(phrase4);
				PanTuto.add(phrase5);
			}
		});

/**
 * 		Tutoriel pour la désinscription		
 */
		this.tutorialDesinscription.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PanTuto = new JPanel();
				PanTuto.setLayout(new GridLayout(5, 1));
				PanTuto.setBackground(Color.black);
				JScrollPane scrollpane = new JScrollPane(PanTuto);
				if((AppletBiomorph.getTabPan().getTabCount())==3){
					AppletBiomorph.getTabPan().addTab("Tutoriel Désinscription", scrollpane);
				}else if((AppletBiomorph.getTabPan().getTabCount())==4){
					AppletBiomorph.getTabPan().remove(3);
					AppletBiomorph.getTabPan().addTab("Tutoriel Désinscription", scrollpane);
				}
				AppletBiomorph.getTabPan().setSelectedIndex(AppletBiomorph.getTabPan().getTabCount()-1);
				phrase1 = new JLabel("1) Pour vous désinscrire, connectez vous sur votre compte.");
				phrase2 = new JLabel("     Pour vous connecter, vous pouvez suivre le tutoriel associé à la connexion.");
				phrase3 = new JLabel("2) Une fois connecté, cliquez sur <Désinscription> dans le menu <Profils>.");
				phrase4 = new JLabel("3) Une pop-up de confirmation apparaît.");
				phrase5 = new JLabel("4) Si vous êtes sûr, appuyez sur <Oui> sinon annulez l'opération avec <Non>.");
				phrase1.setForeground(Color.white);
				phrase2.setForeground(Color.white);
				phrase3.setForeground(Color.white);
				phrase4.setForeground(Color.white);
				phrase5.setForeground(Color.white);
				PanTuto.add(phrase1);
				PanTuto.add(phrase2);
				PanTuto.add(phrase3);
				PanTuto.add(phrase4);
				PanTuto.add(phrase5);
			}
		});

/**
 * 		Tutoriel pour la connexion		
 */
		this.tutorialConnexion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PanTuto = new JPanel();
				PanTuto.setLayout(new GridLayout(5, 1));
				PanTuto.setBackground(Color.black);
				JScrollPane scrollpane = new JScrollPane(PanTuto);
				if((AppletBiomorph.getTabPan().getTabCount())==3){
					AppletBiomorph.getTabPan().addTab("Tutoriel Connexion", scrollpane);
				}else if((AppletBiomorph.getTabPan().getTabCount())==4){
					AppletBiomorph.getTabPan().remove(3);
					AppletBiomorph.getTabPan().addTab("Tutoriel Connexion", scrollpane);
				}
				AppletBiomorph.getTabPan().setSelectedIndex(AppletBiomorph.getTabPan().getTabCount()-1);
				phrase1 = new JLabel("1) Pour vous connecter, cliquez sur <Connexion> dans le menu <Profils>.");
				phrase2 = new JLabel("2) Entrez le nom du compte sur lequel vous voulez vous connecté.");
				phrase3 = new JLabel("3) Entrez le mot de passe du compte sur lequel vous voulez vous connecté.");
				phrase4 = new JLabel("4) Une fois fait, validez à l'aide du bouton <Connexion>.");
				phrase5 = new JLabel("5) Si la connexion a réussie, un message de bienvenue s'affiche et vous etes connecté.");
				phrase1.setForeground(Color.white);
				phrase2.setForeground(Color.white);
				phrase3.setForeground(Color.white);
				phrase4.setForeground(Color.white);
				phrase5.setForeground(Color.white);
				PanTuto.add(phrase1);
				PanTuto.add(phrase2);
				PanTuto.add(phrase3);
				PanTuto.add(phrase4);
				PanTuto.add(phrase5);
				
			}
		});

/**
 * 		Tutoriel pour la déconnexion		
 */
		this.tutorialDeconnexion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PanTuto = new JPanel();
				PanTuto.setLayout(new GridLayout(3, 1));
				PanTuto.setBackground(Color.black);
				JScrollPane scrollpane = new JScrollPane(PanTuto);
				if((AppletBiomorph.getTabPan().getTabCount())==3){
					AppletBiomorph.getTabPan().addTab("Tutoriel Déconnexion", scrollpane);
				}else if((AppletBiomorph.getTabPan().getTabCount())==4){
					AppletBiomorph.getTabPan().remove(3);
					AppletBiomorph.getTabPan().addTab("Tutoriel Déconnexion", scrollpane);
				}
				AppletBiomorph.getTabPan().setSelectedIndex(AppletBiomorph.getTabPan().getTabCount()-1);
				phrase1 = new JLabel("1) Pour vous déconnecter du compte, cliquez sur <Déconnexion> dans le menu <Profils>.");
				phrase2 = new JLabel("2) Une pop-up de confirmation apparaît.");
				phrase3 = new JLabel("3) Si vous êtes sûr, appuyez sur <Oui> sinon annulez l'opération avec <Non>.");
				phrase1.setForeground(Color.white);
				phrase2.setForeground(Color.white);
				phrase3.setForeground(Color.white);
				PanTuto.add(phrase1);
				PanTuto.add(phrase2);
				PanTuto.add(phrase3);
			}
		});

/**
 * 		Tutoriel pour le changement de mot de passe	
 */
		this.tutorialChangerMdp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PanTuto = new JPanel();
				PanTuto.setLayout(new GridLayout(3, 1));
				JScrollPane scrollpane = new JScrollPane(PanTuto);
				PanTuto.setBackground(Color.black);
				if((AppletBiomorph.getTabPan().getTabCount())==3){
					AppletBiomorph.getTabPan().addTab("Tuto Changer password", scrollpane);
				}else if((AppletBiomorph.getTabPan().getTabCount())==4){
					AppletBiomorph.getTabPan().remove(3);
					AppletBiomorph.getTabPan().addTab("Tuto Changer password", scrollpane);
				}
				AppletBiomorph.getTabPan().setSelectedIndex(AppletBiomorph.getTabPan().getTabCount()-1);
				phrase1 = new JLabel("1) Pour changer le mot de passe de votre compte, cliquez sur <Changer mot de passe> dans le menu <Profils>.");
				phrase2 = new JLabel("2) Entrez le nouveau mot de passe.");
				phrase3 = new JLabel("3) Si vous êtes sûr, confirmez en appuyant sur le bouton <Changer>.");
				phrase1.setForeground(Color.white);
				phrase2.setForeground(Color.white);
				phrase3.setForeground(Color.white);
				PanTuto.add(phrase1);
				PanTuto.add(phrase2);
				PanTuto.add(phrase3);
				
			}
		});

/**
 * 		Tutoriel pour le chargement des biomorphs		
 */	
		this.tutorialCharger.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PanTuto = new JPanel();
				PanTuto.setLayout(new GridLayout(3, 1));
				PanTuto.setBackground(Color.black);
				JScrollPane scrollpane = new JScrollPane(PanTuto);
				if((AppletBiomorph.getTabPan().getTabCount())==3){
					AppletBiomorph.getTabPan().addTab("Tuto charger biomorph", scrollpane);
				}else if((AppletBiomorph.getTabPan().getTabCount())==4){
					AppletBiomorph.getTabPan().remove(3);
					AppletBiomorph.getTabPan().addTab("Tuto charger biomorph", scrollpane);
				}
				AppletBiomorph.getTabPan().setSelectedIndex(AppletBiomorph.getTabPan().getTabCount()-1);
				phrase1 = new JLabel("1) Pour charger des biomorphs, connectez vous sur votre compte.");
				phrase2 = new JLabel("     Pour vous connecter, veuillez vous référer au tutoriel associé à la connexion.");
				phrase3 = new JLabel("2) Une fois connecté, cliquez sur <Charger> dans le menu <Edition>.");
				phrase4 = new JLabel("3) Vérifier votre bibliothèque.");
				phrase1.setForeground(Color.white);
				phrase2.setForeground(Color.white);
				phrase3.setForeground(Color.white);
				phrase4.setForeground(Color.white);
				PanTuto.add(phrase1);
				PanTuto.add(phrase2);
				PanTuto.add(phrase3);
			}
		}); 

/**
 * 		Tutoriel pour apprendre comment nettoyer le laboratoire
 */		
		this.tutorialNettoyerEcran.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PanTuto = new JPanel();
				PanTuto.setLayout(new GridLayout(3, 1));
				PanTuto.setBackground(Color.black);
				JScrollPane scrollpane = new JScrollPane(PanTuto);
				if((AppletBiomorph.getTabPan().getTabCount())==3){
					AppletBiomorph.getTabPan().addTab("Tutoriel Nettoyage", scrollpane);
				}else if((AppletBiomorph.getTabPan().getTabCount())==4){
					AppletBiomorph.getTabPan().remove(3);
					AppletBiomorph.getTabPan().addTab("Tutoriel Nettoyage", scrollpane);
				}
				AppletBiomorph.getTabPan().setSelectedIndex(AppletBiomorph.getTabPan().getTabCount()-1);
				phrase1 = new JLabel("1) Pour effectuer cette option, vérifiez que votre espace de travail contient au moins un biomorph.");
				phrase2 = new JLabel("2) Pour nettoyer l'écran, cliquez sur <Nettoyer l'écran> dans le menu <Edition> ou appuyez sur CTRL+SHIFT+F.");
				phrase3 = new JLabel("3) Nettoyage de l'espace de travail réussi!");
				phrase1.setForeground(Color.white);
				phrase2.setForeground(Color.white);
				phrase3.setForeground(Color.white);
				PanTuto.add(phrase1);
				PanTuto.add(phrase2);
				PanTuto.add(phrase3);
			}
		});

/**
 * 		Tutoriel pour effectuer des échanges		
 */		
		this.tutorialEchange.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PanTuto = new JPanel();
				PanTuto.setLayout(new GridLayout(8, 1));
				PanTuto.setBackground(Color.black);
				JScrollPane scrollpane = new JScrollPane(PanTuto);
				if((AppletBiomorph.getTabPan().getTabCount())==3){
					AppletBiomorph.getTabPan().addTab("Tuto Echanger un biomorph", scrollpane);
				}else if((AppletBiomorph.getTabPan().getTabCount())==4){
					AppletBiomorph.getTabPan().remove(3);
					AppletBiomorph.getTabPan().addTab("Tuto Echanger un biomorph", scrollpane);
				}
				AppletBiomorph.getTabPan().setSelectedIndex(AppletBiomorph.getTabPan().getTabCount()-1);
				phrase1 = new JLabel("1) Pour envoyer un biomorph à un ami, connectez vous sur votre compte.");
				phrase2 = new JLabel("     Pour vous connecter, veuillez vous référer au tutoriel associé à la connexion.");
				phrase3 = new JLabel("     Pour envoyer une demande d'ami, veuillez vous référer au tutoriel associé à celle-ci.");
				phrase4 = new JLabel("2) Une fois connecté, cliquez sur <Echanger> dans le menu <Transfert>.");
				phrase5 = new JLabel("3) Entrez le nom du compte de votre ami.");
				phrase6 = new JLabel("4) Entrez le nom du biomorph, présent sur votre écran, à envoyer.");
				phrase7 = new JLabel("5) Validez par la suite à l'aide du bouton <Echanger>.");
				phrase8 = new JLabel("6) Une pop-up vous avertira s'il y a échec de l'échange");
				phrase1.setForeground(Color.white);
				phrase2.setForeground(Color.white);
				phrase3.setForeground(Color.white);
				phrase4.setForeground(Color.white);
				phrase5.setForeground(Color.white);
				phrase6.setForeground(Color.white);
				phrase7.setForeground(Color.white);
				phrase8.setForeground(Color.white);
				PanTuto.add(phrase1);
				PanTuto.add(phrase2);
				PanTuto.add(phrase3);
				PanTuto.add(phrase4);
				PanTuto.add(phrase5);
				PanTuto.add(phrase6);
				PanTuto.add(phrase7);
				PanTuto.add(phrase8);
				
			}
		}); 

/**
 * 		Tutoriel pour faire une demande d'ami		
 */	
		this.tutorialDemandeAmi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PanTuto = new JPanel();
				PanTuto.setLayout(new GridLayout(6, 1));
				PanTuto.setBackground(Color.black);
				JScrollPane scrollpane = new JScrollPane(PanTuto);
				if((AppletBiomorph.getTabPan().getTabCount())==3){
					AppletBiomorph.getTabPan().addTab("Tuto Demande d'amis", scrollpane);
				}else if((AppletBiomorph.getTabPan().getTabCount())==4){
					AppletBiomorph.getTabPan().remove(3);
					AppletBiomorph.getTabPan().addTab("Tuto Demande d'amis", scrollpane);
				}
				AppletBiomorph.getTabPan().setSelectedIndex(AppletBiomorph.getTabPan().getTabCount()-1);
				phrase1 = new JLabel("1) Pour effectuer une demande d'ami, veuillez vous connecter sur votre compte.");
				phrase2 = new JLabel("     Pour vous connecter, veuillez vous référer au tutoriel associé à la connexion.");
				phrase3 = new JLabel("2) Une fois connecté, cliquez sur <Demande d'ami> dans le menu <Transfert>.");
				phrase4 = new JLabel("3) Entrez le nom du destinataire de la demande.");
				phrase5 = new JLabel("4) Validez ensuite à l'aide du bouton <Envoyer la demande>");
				phrase6 = new JLabel("5) Une pop-up vous indiquera si l'invitation a bien été envoyée.");
				phrase1.setForeground(Color.white);
				phrase2.setForeground(Color.white);
				phrase3.setForeground(Color.white);
				phrase4.setForeground(Color.white);
				phrase5.setForeground(Color.white);
				phrase6.setForeground(Color.white);
				PanTuto.add(phrase1);
				PanTuto.add(phrase2);
				PanTuto.add(phrase3);
				PanTuto.add(phrase4);
				PanTuto.add(phrase5);
				PanTuto.add(phrase6);
			}
		});

/**
 * 		Tutoriel pour accepter une invitation		
 */
		this.tutorialAccepteInvitation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PanTuto = new JPanel();
				PanTuto.setLayout(new GridLayout(5, 1));
				PanTuto.setBackground(Color.black);
				JScrollPane scrollpane = new JScrollPane(PanTuto);
				if((AppletBiomorph.getTabPan().getTabCount())==3){
					AppletBiomorph.getTabPan().addTab("Tuto Accepter Invitation", scrollpane);
				}else if((AppletBiomorph.getTabPan().getTabCount())==4){
					AppletBiomorph.getTabPan().remove(3);
					AppletBiomorph.getTabPan().addTab("Tuto Accepter Invitation", scrollpane);
				}
				AppletBiomorph.getTabPan().setSelectedIndex(AppletBiomorph.getTabPan().getTabCount()-1);
				phrase1 = new JLabel("1) Pour consulter votre liste d'invitations reçues, veuillez vous connecter.");
				phrase2 = new JLabel("     Pour vous connecter, veuillez vous référer au tutoriel <Connexion>.");
				phrase3 = new JLabel("2) Une fois connecté, cliquez sur <Liste des invitations reçues> dans le menu <Transfert>.");
				phrase4 = new JLabel("3) Si vous avez reçu une invitation: -[YES] Accepter l'invitation.");
				phrase5 = new JLabel("4) -[NO] Refuser l'invitation.");
				phrase1.setForeground(Color.white);
				phrase2.setForeground(Color.white);
				phrase3.setForeground(Color.white);
				phrase4.setForeground(Color.white);
				phrase5.setForeground(Color.white);
				PanTuto.add(phrase1);
				PanTuto.add(phrase2);
				PanTuto.add(phrase3);
				PanTuto.add(phrase4);
				PanTuto.add(phrase5);
				
			}
		});

		this.setVisible(true);
	}

	public static String getNomUtilisateur(){
		return nomUtilisateur.getText();
	}
	@SuppressWarnings("deprecation")
	public static String getMotDePasse(){
		return motDePasse.getText();
	}

	public int getSelectionType(){
		if(selectionDouble.isSelected()){
			return 1;
		}else
			return 2;
	}
	
/**	
 * Méthode appelée lors de l'inscription
 * @param identifiant
 * @param motdepasse
 * @param adresseIP
 * @return provoque l'affichage d'une pop-up disant si le nom d'utilisateur est déjà pris ou si le compte est bien crée 
 */	
	public String BDDInscription(String identifiant, String motdepasse, String adresseIP){
		String nom = identifiant;
		String mdp = motdepasse;
		String ip = adresseIP;
		try {
			URL url = new URL(AppletBiomorph.getAdresse(), "ServletInscription");
			URLConnection urlc = url.openConnection();
			urlc.setDoInput(true);
			urlc.setDoOutput(true);
			urlc.setUseCaches(false);
			urlc.setRequestProperty("Content-Type","application/x-java-serialized-object");
			OutputStream outstream = urlc.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(nom);
			oos.writeObject(mdp);
			oos.writeObject(ip);
			oos.flush();
			oos.close();
			InputStream instr = urlc.getInputStream();
			ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
			String result = (String) inputFromServlet.readObject();
			inputFromServlet.close();
			instr.close();
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return (e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return(e.getMessage());
		}
		catch (Exception e){
			e.printStackTrace();
			return (e.getMessage());
		}
	}
	
/**	
 * Méthode appelée lors de la désinscription d'un utilisateur
 * @param identifiant
 * @return provoque la suppression du compte et le retour à la page initiale
 */	
	public String BDDDesinscription(String identifiant){
		String nom = identifiant;
		try {
			URL url = new URL(AppletBiomorph.getAdresse(), "ServletDesinscription");
			URLConnection urlc = url.openConnection();
			urlc.setDoInput(true);
			urlc.setDoOutput(true);
			urlc.setUseCaches(false);
			urlc.setRequestProperty("Content-Type","application/x-java-serialized-object");
			OutputStream outstream = urlc.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(nom);
			oos.flush();
			oos.close();
			InputStream instr = urlc.getInputStream();
			ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
			String result = (String) inputFromServlet.readObject();
			inputFromServlet.close();
			instr.close();
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return (e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return(e.getMessage());
		}
		catch (Exception e){
			e.printStackTrace();
			return (e.getMessage());
		}
	}

/**
 * Méthode appelée lors de la connexion d'un utilisateur	
 * @param identifiant
 * @param motdepasse
 * @param adresseIP
 * @return provoque l'apparition d'une pop-up de bienvenue
 */	
	public String BDDConnexion(String identifiant, String motdepasse, String adresseIP){
		String nom = identifiant;
		String mdp = motdepasse;
		String ip = adresseIP;
		try {
			URL url = new URL(AppletBiomorph.getAdresse(), "ServletConnexion");
			URLConnection urlc = url.openConnection();
			urlc.setDoInput(true);
			urlc.setDoOutput(true);
			urlc.setUseCaches(false);
			urlc.setRequestProperty("Content-Type","application/x-java-serialized-object");
			OutputStream outstream = urlc.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(nom);
			oos.writeObject(mdp);
			oos.writeObject(ip);
			oos.flush();
			oos.close();
			InputStream instr = urlc.getInputStream();
			ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
			String result = (String) inputFromServlet.readObject();
			//		JOptionPane.showMessageDialog(null, result);
			inputFromServlet.close();
			instr.close();
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return (e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return(e.getMessage());
		}
		catch (Exception e){
			e.printStackTrace();
			return (e.getMessage());
		}
	}
	
/**
 *  Méthode appelée lors de la déconnexion d'un utilisateur	
 * @param identifiant
 * @return provoque le retour au mode déconnecté
 */	
	public String BDDDeconnexion(String identifiant){
		String nom = identifiant;
		try {
			URL url = new URL(AppletBiomorph.getAdresse(), "ServletDeconnexion");
			URLConnection urlc = url.openConnection();
			urlc.setDoInput(true);
			urlc.setDoOutput(true);
			urlc.setUseCaches(false);
			urlc.setRequestProperty("Content-Type","application/x-java-serialized-object");
			OutputStream outstream = urlc.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(nom);
			oos.flush();
			oos.close();
			InputStream instr = urlc.getInputStream();
			ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
			String result = (String) inputFromServlet.readObject();
			inputFromServlet.close();
			instr.close();
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return (e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return(e.getMessage());
		}
		catch (Exception e){
			e.printStackTrace();
			return (e.getMessage());
		}
	}
	
/**
 *  Méthode appelée lors de la sauvegarde d'un biomorph	
 * @param identifiant
 * @param biomorph
 * @return renvoie le résultat de la requête
 */	
	public static String BDDSauvegardeBiomorph(String identifiant,Biomorph biomorph){
		String nomUser = identifiant;
		Biomorph bio = biomorph;
		try {
			URL url = new URL(AppletBiomorph.getAdresse(), "ServletSauvegarderBiomorph");
			URLConnection urlc = url.openConnection();
			urlc.setDoInput(true);
			urlc.setDoOutput(true);
			urlc.setUseCaches(false);
			urlc.setRequestProperty("Content-Type","application/x-java-serialized-object");
			OutputStream outstream = urlc.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			// préécriture pour connaitre la taille du biomorph sérialisé
			ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
			ObjectOutputStream  out = new ObjectOutputStream(bos) ;
			out.writeObject(bio);
			
			int tailleBiomorphSerialise = bos.size();
		    
		    // nomUser | nomBiomorph | tailleBiomorphSerialise | data
			oos.writeObject(nomUser);
			oos.writeObject(bio.getName());
			oos.writeInt(tailleBiomorphSerialise);
			oos.write(bos.toByteArray());
			
			oos.flush();
			oos.close();
			out.close();
			InputStream instr = urlc.getInputStream();
			ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
			String result = (String) inputFromServlet.readObject();
			inputFromServlet.close();
			instr.close();
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return (e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return(e.getMessage());
		}
		catch (Exception e){
			e.printStackTrace();
			return (e.getMessage());
		}
	}
	public LinkedList<Biomorph> BDDChargerBiomorph(String identifiant){
		String nom = identifiant;
		try {
			URL url = new URL(AppletBiomorph.getAdresse(), "ServletChargerBiomorph");
			URLConnection urlc = url.openConnection();
			urlc.setDoInput(true);
			urlc.setDoOutput(true);
			urlc.setUseCaches(false);
			urlc.setRequestProperty("Content-Type","application/x-java-serialized-object");
			OutputStream outstream = urlc.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(nom);
			oos.flush();
			oos.close();

			InputStream instr = urlc.getInputStream();
			ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
			LinkedList<Biomorph> listeBiomorphCharger = new LinkedList<Biomorph>();
			Object bioCharge = null;
			while (true) {
				try {
					bioCharge = inputFromServlet.readObject();
				} catch (IOException e) {
					break;
				} catch (ClassNotFoundException e) {
					break;
				}
				if (bioCharge instanceof Biomorph) {
					listeBiomorphCharger.push((Biomorph) bioCharge);
				} else break;
			}
			if (bioCharge instanceof String) {
				System.out.println("message serveur : "+bioCharge);
			}
			inputFromServlet.close();
			instr.close();
			return listeBiomorphCharger;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} 
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

/**
 *  Méthode appelée lors du changement de mot de passe	
 * @param identifiant
 * @param nouveauMDP
 * @return renvoie le résultat de la requête
 */	
	public String BDDChangerMotDePasse(String identifiant, String nouveauMDP){
		String nom = identifiant;
		String newmdp = nouveauMDP;
		try {
			URL url = new URL(AppletBiomorph.getAdresse(), "ServletChangerMotDePasse");
			URLConnection urlc = url.openConnection();
			urlc.setDoInput(true);
			urlc.setDoOutput(true);
			urlc.setUseCaches(false);
			urlc.setRequestProperty("Content-Type","application/x-java-serialized-object");
			OutputStream outstream = urlc.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(nom);
			oos.writeObject(newmdp);
			oos.flush();
			oos.close();
			InputStream instr = urlc.getInputStream();
			ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
			String result = (String) inputFromServlet.readObject();
			inputFromServlet.close();
			instr.close();
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return (e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return(e.getMessage());
		}
		catch (Exception e){
			e.printStackTrace();
			return (e.getMessage());
		}
	}

/**
 *  Méthode appelée lors d'une demande d'ami	
 * @param nomUtilisateur
 * @param nomUtilisateurAmi
 * @return provoque l'ouverture d'une pop-up qui dépend du résultat de la requête 
 */
	public String BDDDemandeAmi(String nomUtilisateur, String nomUtilisateurAmi){
		try {
			URL url = new URL(AppletBiomorph.getAdresse(), "ServletDemandeAmi");
			URLConnection urlc = url.openConnection();
			urlc.setDoInput(true);
			urlc.setDoOutput(true);
			urlc.setUseCaches(false);
			urlc.setRequestProperty("Content-Type","application/x-java-serialized-object");
			OutputStream outstream = urlc.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(nomUtilisateur);
			oos.writeObject(nomUtilisateurAmi);
			oos.flush();
			oos.close();
			InputStream instr = urlc.getInputStream();
			ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
			String result = (String) inputFromServlet.readObject();
			inputFromServlet.close();
			instr.close();
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return (e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return(e.getMessage());
		}
		catch (Exception e){
			e.printStackTrace();
			return (e.getMessage());
		}
	}

/**
 *  Méthode appelée lors de la consultation de la liste des demandes d'amis	
 * @param nomUtilisateur
 * @return renvoie la liste des demandes d'ami s'il y en a
 */	
	public ArrayList<String> BDDlisteInvitation (String nomUtilisateur){
		try {
			URL url = new URL(AppletBiomorph.getAdresse(), "ServletReceptionListeAmi");
			URLConnection urlc = url.openConnection();
			urlc.setDoInput(true);
			urlc.setDoOutput(true);
			urlc.setUseCaches(false);
			urlc.setRequestProperty("Content-Type","application/x-java-serialized-object");
			OutputStream outstream = urlc.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(nomUtilisateur);
			oos.flush();
			oos.close();
			InputStream instr = urlc.getInputStream();
			ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
			@SuppressWarnings("unused")
			String resultt = (String) inputFromServlet.readObject();
			@SuppressWarnings("unchecked")
			ArrayList<String> result = (ArrayList<String>) inputFromServlet.readObject();
			inputFromServlet.close();
			instr.close();
			return result;
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}

	}


	public static String getLog(){
		return log;
	}

/**
 *  Méthode appelée après refus d'une invitation
 * @param log
 * @param nomUtilisateurRefus
 * @return provoque l'ouverture d'une pop-up indiquant le refus de l'invitation
 */
	public String BDDRefusInvitation (String log, String nomUtilisateurRefus){
		try {
			URL url = new URL(AppletBiomorph.getAdresse(), "ServletRefusInvitation");
			URLConnection urlc = url.openConnection();
			urlc.setDoInput(true);
			urlc.setDoOutput(true);
			urlc.setUseCaches(false);
			urlc.setRequestProperty("Content-Type","application/x-java-serialized-object");
			OutputStream outstream = urlc.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(log);
			oos.writeObject(nomUtilisateurRefus);
			oos.flush();
			oos.close();	
			InputStream instr = urlc.getInputStream();
			ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
			String result = (String) inputFromServlet.readObject();
			inputFromServlet.close();
			instr.close();
			return result;
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
			return e.getMessage();
		} 
		catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		catch (Exception e){
			e.printStackTrace();
			return e.getMessage();
		}

	}

/**
 * Méthode appelée pour consulter la liste de ses amis
 * @param identifiant
 * @return le résultat de la requête et l'ouverture d'une pop-up
 */
	public ArrayList<JLabel> BDDListeAmis(String identifiant){
		String nom = identifiant;
		try {
			URL url = new URL(AppletBiomorph.getAdresse(), "ServletListeAmis");
			URLConnection urlc = url.openConnection();
			urlc.setDoInput(true);
			urlc.setDoOutput(true);
			urlc.setUseCaches(false);
			urlc.setRequestProperty("Content-Type","application/x-java-serialized-object");
			OutputStream outstream = urlc.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(nom);
			oos.flush();
			oos.close();
			InputStream instr = urlc.getInputStream();
			ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
			@SuppressWarnings("unchecked")
			ArrayList<JLabel> result = (ArrayList<JLabel>) inputFromServlet.readObject();
			inputFromServlet.close();
			instr.close();
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

/**
 * Méthode appelée après l'acceptation d'une invitation d'ami	
 * @param log
 * @param nomUtilisateurAmi
 * @return provoque l'ouverture d'une pop-up indiquant l'acceptation de l'invitation
 */
	public String BDDAcceptationInvitation (String log, String nomUtilisateurAmi){
		try {
			URL url = new URL(AppletBiomorph.getAdresse(), "ServletAcceptationInvitation");
			URLConnection urlc = url.openConnection();
			urlc.setDoInput(true);
			urlc.setDoOutput(true);
			urlc.setUseCaches(false);
			urlc.setRequestProperty("Content-Type","application/x-java-serialized-object");
			OutputStream outstream = urlc.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(log);
			oos.writeObject(nomUtilisateurAmi);
			oos.flush();
			oos.close();	
			InputStream instr = urlc.getInputStream();
			ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
			@SuppressWarnings("unused")
			String resultt = (String) inputFromServlet.readObject();
			String result = (String) inputFromServlet.readObject();
			inputFromServlet.close();
			instr.close();
			return result;
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
			return e.getMessage();
		} 
		catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		catch (Exception e){
			e.printStackTrace();
			return e.getMessage();
		}

	}		

/**
 * Méthode appelée lors de l'échange dun biomorph entre utilisateurs amis 	
 * @param log
 * @param nomDestinataire
 * @param message
 * @return provoque l'ouverture d'une pop-up indiquant si l'échange s'est réalisée
 */
	public String BDDEchangeBiomorph (String log, String nomDestinataire, String message){
		try {
			URL url = new URL(AppletBiomorph.getAdresse(), "ServletEchangeBiomorph");
			URLConnection urlc = url.openConnection();
			urlc.setDoInput(true);
			urlc.setDoOutput(true);
			urlc.setUseCaches(false);
			urlc.setRequestProperty("Content-Type","application/x-java-serialized-object");
			OutputStream outstream = urlc.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(log);
			oos.writeObject(nomDestinataire);
			oos.writeObject(message);
			oos.flush();
			oos.close();			
			InputStream instr = urlc.getInputStream();
			ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
			String result = (String) inputFromServlet.readObject();
			JOptionPane.showMessageDialog(null,result);
			inputFromServlet.close();
			instr.close();
			return result;
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
			return e.getMessage();
		} 
		catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		catch (Exception e){
			e.printStackTrace();
			return e.getMessage();
		}

	}

/**
 * Méthode appelée avant l'échange d'un biomorph, elle vérifie si le destinataire est ami avec l'émetteur	
 * @param log
 * @param nomDestinataire
 * @return renvoie le résultat de la requête
 */
	public String BDDVerificationUtilisateurs (String log, String nomDestinataire){
		try {
			URL url = new URL(AppletBiomorph.getAdresse(), "ServletVerificationUtilisateursAmis");
			URLConnection urlc = url.openConnection();
			urlc.setDoInput(true);
			urlc.setDoOutput(true);
			urlc.setUseCaches(false);
			urlc.setRequestProperty("Content-Type","application/x-java-serialized-object");
			OutputStream outstream = urlc.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(log);
			oos.writeObject(nomDestinataire);
			oos.flush();
			oos.close();		
			InputStream instr = urlc.getInputStream();
			ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
			String result = (String) inputFromServlet.readObject();
			inputFromServlet.close();
			instr.close();
			return result;

		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return "false";
	}	

/**
 * Méthode appelée pour supprimer un biomorph de la bibliothèque de l'utilisateur	
 * @param identifiant
 * @param biom
 * @return le résultat de la requête
 */
	public String BDDSupprimerBiomorph(String identifiant, Biomorph biom){
		String nom = identifiant;
		try {
			URL url = new URL(AppletBiomorph.getAdresse(), "ServletSupprimerBiomorph");
			URLConnection urlc = url.openConnection();
			urlc.setDoInput(true);
			urlc.setDoOutput(true);
			urlc.setUseCaches(false);
			urlc.setRequestProperty("Content-Type","application/x-java-serialized-object");
			OutputStream outstream = urlc.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(nom);
			oos.writeObject(biom.getName());
			oos.flush();
			oos.close();
			InputStream instr = urlc.getInputStream();
			ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
			String result = (String) inputFromServlet.readObject();
			inputFromServlet.close();
			instr.close();
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return (e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return(e.getMessage());
		}
		catch (Exception e){
			e.printStackTrace();
			return (e.getMessage());
		}
	}

}






