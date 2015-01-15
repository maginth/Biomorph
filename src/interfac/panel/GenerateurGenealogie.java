package interfac.panel;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import biomorph.forme2D.Biomorph2D.BiomorphStructure;


public class GenerateurGenealogie extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Genealogie genealogie;
	BiomorphStructure generateur;
	
	public GenerateurGenealogie(Genealogie genealogie) {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		
		add(new JPanel());
		this.genealogie = genealogie;
		generateur = new BiomorphStructure();
		
		JButton ok = new JButton("Générer une généalogie aléatoire");
		JButton annuler = new JButton("annuler");

		
		JPanel pnbChromHomeo = new JPanel();
		JPanel pnbChrom = new JPanel();
		JPanel pnbGeneChrom = new JPanel();
		JPanel psymetrie = new JPanel();
		final JCheckBox symetrie = new JCheckBox("Biomorph symétrique",true);
		JLabel nbChromHomeo = new JLabel("Chromosomes homeotiques :");
		JLabel nbChrom = new JLabel("Chromosomes autoréférents :");
		JLabel nbGeneChrom = new JLabel("Gènes par Chromosome :");
		final JSpinner snbChrom = new JSpinner();
		snbChrom.setValue(1);
		snbChrom.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				if ((Integer) (snbChrom.getValue()) <1) snbChrom.setValue(1);
				if ((Integer) (snbChrom.getValue()) >10) snbChrom.setValue(10);
				validate();
			}
		});
		final JSpinner snbChromHomeo = new JSpinner();
		snbChromHomeo.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				if ((Integer) (snbChromHomeo.getValue()) <0) snbChromHomeo.setValue(0);
				if ((Integer) (snbChromHomeo.getValue()) >6) snbChromHomeo.setValue(6);
				validate();
			}
		});
		final JSpinner snbGeneChrom = new JSpinner();
		snbGeneChrom.setValue(10);
		snbGeneChrom.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				if ((Integer) (snbGeneChrom.getValue()) <1) snbGeneChrom.setValue(1);
				if ((Integer) (snbGeneChrom.getValue()) >256) snbGeneChrom.setValue(256);
				validate();
			}
		});
		psymetrie.add(symetrie);
		pnbChromHomeo.add(nbChromHomeo);
		pnbChromHomeo.add(snbChromHomeo);
		pnbChrom.add(nbChrom);
		pnbChrom.add(snbChrom);
		pnbGeneChrom.add(nbGeneChrom);
		pnbGeneChrom.add(snbGeneChrom);

		
		add(pnbChromHomeo);
		add(pnbChrom);
		add(pnbGeneChrom);
		add(psymetrie);


		
		JPanel ok_an = new JPanel();
		ok_an.add(ok);
		ok_an.add(annuler);
		add(ok_an);
		
		ok.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				generateur.nbChrom = (Integer) snbChrom.getValue();
				generateur.nbChromHomeo = (Integer) snbChromHomeo.getValue();
				generateur.nbGenChrom = (Integer) snbGeneChrom.getValue();
				generateur.symetrie = symetrie.isSelected();
				Container parent = getParent();
				parent.remove(GenerateurGenealogie.this);
				parent.add(GenerateurGenealogie.this.genealogie);
				GenerateurGenealogie.this.genealogie.genealogieAleatoire(generateur);
			}
		});
		annuler.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Container parent = getParent();
				parent.remove(GenerateurGenealogie.this);
				parent.add(GenerateurGenealogie.this.genealogie);
			}
		});
	}
	
}
