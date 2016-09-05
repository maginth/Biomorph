package interfac.global;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import biomorph.abstrait.TauxMutation;
/**
 * Rassemble les paramètres globaux de l'utilisateur dans un JMenu.
 */
public class Parametres extends JMenu {
	
	private static final long serialVersionUID = 1L;
	
/** 
 * Nombre d'enfants générés lors du croisement simple entre deux biomorphs.
 */
	public static int nombreEnfant=5;
	
/** 
 * Précision du JSlider
 */
	private static final int N=100000;
	
/** 
 * Paramètres utilisateurs globaux de mutation 
 */	
	public static final TauxMutation X = new TauxMutation(0.80,0.50,0.80,0.50);

	private JSlider sProbaMutation,sprobaModifStructure,sAmplitudeMutation,sRecombinaison;
	
	private void afficheTaux() {
		setText("Taux Mutations "
				+(int) (X.getProbaMutation()*100)+"%"
				+(int) (X.getAmplitudeMutation()*100)+"%"
				+(int) (X.getProbaModifStructure()*100)+"%"
				+(int) (X.getProbaRecombinaison()*100)+"%");
	}
	public Parametres() {
		afficheTaux();
		sProbaMutation = new JSlider();
		sProbaMutation.setMaximum(N);
		sProbaMutation.setMinimum(0);
		sProbaMutation.setValue((int) (X.getProbaMutation()*N));
		sProbaMutation.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				X.setProbaMutation((double) sProbaMutation.getValue()/N);
				afficheTaux();
			}
		});
		sprobaModifStructure = new JSlider();
		sprobaModifStructure.setMaximum(N);
		sprobaModifStructure.setMinimum(0);
		sprobaModifStructure.setValue((int) (X.getProbaModifStructure()*N));
		sprobaModifStructure.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				X.setProbaModifStructure((double) sprobaModifStructure.getValue()/N);
				afficheTaux();
			}
		});
		sAmplitudeMutation = new JSlider();
		sAmplitudeMutation.setMaximum(N);
		sAmplitudeMutation.setMinimum(0);
		sAmplitudeMutation.setValue((int) (X.getAmplitudeMutation()*N));
		sAmplitudeMutation.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				X.setAmplitudeMutation((double) sAmplitudeMutation.getValue()/N);
				afficheTaux();
			}
		});
		
		sRecombinaison = new JSlider();
		sRecombinaison.setMaximum(N);
		sRecombinaison.setMinimum(0);
		sRecombinaison.setValue((int) (X.getProbaRecombinaison()*N));
		sRecombinaison.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				X.setProbaRecombinaison((double) sRecombinaison.getValue()/N);
				afficheTaux();
			}
		});
		
		JPanel panel_0_100 = new JPanel(); 
		JButton _0 = new JButton("Tout à 0%");
		JButton _100 = new JButton("Tout à 100%");
		_0.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sRecombinaison.setValue(0);
				sAmplitudeMutation.setValue(0);
				sprobaModifStructure.setValue(0);
				sProbaMutation.setValue(0);
			}
		});
		_100.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sRecombinaison.setValue(N);
				sAmplitudeMutation.setValue(N);
				sprobaModifStructure.setValue(N);
				sProbaMutation.setValue(N);
			}
		});
		panel_0_100.add(_0);
		panel_0_100.add(_100);
		
		setLayout(new FlowLayout());
		sProbaMutation.setBorder(new TitledBorder("Probalitité Mutations"));
		sProbaMutation.setToolTipText("Probabilité d'apparition d'une mutation lors de la copie d'un gène");
		sprobaModifStructure.setBorder(new TitledBorder("Proba Mutation Structure"));
		sprobaModifStructure.setToolTipText("Probabilité de modification des liens entres gènes (ils sont appelé dans un ordre différent)");
		sAmplitudeMutation.setBorder(new TitledBorder("Amplitude Mutations"));
		sAmplitudeMutation.setToolTipText("régler l'amplitude des changements lors d'une mutation");
		sRecombinaison.setBorder(new TitledBorder("fréquence de Recombinaison"));
		sRecombinaison.setToolTipText("fréquence des échanges de gènes entres chromosomes provenant de parents différents");
		
		add(panel_0_100);
		add(sProbaMutation);
		add(sAmplitudeMutation);
		add(sprobaModifStructure);
		add(sRecombinaison);
	}
}
