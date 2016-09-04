package biomorph.forme2D;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import biomorph.abstrait.Chromosome;
import biomorph.abstrait.GeneExpression;
import interfac.global.AppletBiomorph;

public class PanelGenome extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Biomorph2D biomorph;
	//public PanelLaboratoire lab;
	private ArrayList<ChromPanel> listeChrom = new ArrayList<ChromPanel>(maxChromosome);
	int nombreChromosomes;
	//final JButton fermer;
	
	public final static int hauteurLigne = 100;
	public final static int maxChromosome = 20;
	
	
	
	public PanelGenome() {
		setLayout(null);
		//setOpaque(false);
		
		addComponentListener(new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent e){
				actualiser();
			}
		});
		/*fermer = new JButton("X");
		fermer.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				lab.scrollpanelGenome.setVisible(false);
			}
		});
		add(fermer);*/
		
		for (int i=0; i<maxChromosome;i++) listeChrom.add(new ChromPanel(i));
			
	}
	
	
	
	
	
	
	public void bricolerGenome(Biomorph2D bio) {
		
		this.biomorph = bio;
		for (ChromPanel chrom : listeChrom) {
			for(PanelGeneticien p : chrom.listeGene) {
				remove(p);
				p.supprimer();
			}
			while(chrom.listeGene.size()>0) chrom.listeGene.remove(0);
		}
		setVisible(true);
		nombreChromosomes = biomorph.genotype.size();
		
		int index = 0;
		for (Chromosome chromosome : biomorph.genotype) {
			ChromPanel pan = listeChrom.get(index);
			for (GeneExpression<?> gene : chromosome) {
				pan.ajouterPanelGeneticien((PanelGeneticien) gene.getPanelGeneticien());
			}
			pan.flag = chromosome.flagMutation;
			index++;
		}
	}
	
	
	
	public void actualiser() {
		if (getWidth() > 100) {
			//fermer.setSize(35,30);
			//fermer.setLocation(getWidth()-50,0);
			int index = 0;
			for (ChromPanel chrom : listeChrom) {
				int y = hauteurLigne*index;
				chrom.plus.setLocation(getWidth()-50,y+20);
				chrom.moins.setLocation(getWidth()-50,y+50);
				chrom.opt.setLocation(getWidth()-50,y+10);
				if (chrom.listeGene.size()>0) {
					int Dx = 0,dx = Math.min(100, (getWidth()-100)/chrom.listeGene.size());
					for(PanelGeneticien p : chrom.listeGene) {
						p.setLocation(Dx, y);
						setComponentZOrder(p, index);
						Dx += dx;
					}
				}
				index++;
			}
			setPreferredSize(new Dimension(100,hauteurLigne*(nombreChromosomes+2)));
			repaint();
		}
		
	}
	
	public void construireBiomorph() {
		biomorph.normalisation = null;
		biomorph.rafrechir_icons();
		AppletBiomorph.panelLaboratoire.actualiserVue();
	}
	
	static String[] typeGene = new String[]{"Gène Division","Gène Symétrique","Gène Vide"};
	
	
	final static JLabel labelMut = new JLabel(" Les mutations affectent:");
	final static JCheckBox modifMutation = new JCheckBox("Forme des gènes",true);
	final static JCheckBox modifStruct = new JCheckBox("Liens entre gènes",true);
	final static JCheckBox modifRecomb = new JCheckBox("Recombinaison",true);
	
	final static JPopupMenu choisirtypeGene = new JPopupMenu();
	final static JPopupMenu typeMut = new JPopupMenu();
	
	static {
		for (final String s : typeGene) {
			JMenuItem choix = new JMenuItem(s);
			choix.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					PanelGeneticien pangenet = 
							(s == "Gène Symétrique")? new PanelGeneticienSymetrie(chromPanActif.index) :
								(s == "Gène Division")? new PanelGeneticienDivision(chromPanActif.index) :
									(s == "Gène Vide")? new PanelGeneticienVide():
										null;
					chromPanActif.ajouterPanelGeneticien(pangenet);
					chromPanActif.panGen.actualiser();
					chromPanActif.panGen.construireBiomorph();
				}
			});
			choisirtypeGene.add(choix);
		}
		typeMut.add(labelMut);
		typeMut.add(modifMutation);
		typeMut.add(modifStruct);
		typeMut.addSeparator();
		typeMut.add(modifRecomb);
		
		modifMutation.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (modifMutation.isSelected()) chromPanActif.flag &= ~1;
				else chromPanActif.flag |= 1;
				chromPanActif.panGen.biomorph.genotype.get(chromPanActif.index).flagMutation = chromPanActif.flag;
			}
		});
		modifStruct.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (modifStruct.isSelected()) chromPanActif.flag &= ~2;
				else chromPanActif.flag |= 2;
				chromPanActif.panGen.biomorph.genotype.get(chromPanActif.index).flagMutation = chromPanActif.flag;			}
		});
		modifRecomb.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (modifRecomb.isSelected()) chromPanActif.flag &= ~4;
				else chromPanActif.flag |= 4;
				chromPanActif.panGen.biomorph.genotype.get(chromPanActif.index).flagMutation = chromPanActif.flag;			}
		});
		
	}
	
	static ChromPanel chromPanActif;

	class ChromPanel {
		
		
		protected ArrayList<PanelGeneticien> listeGene = new ArrayList<PanelGeneticien>();
		JButton plus;
		JButton moins;
		JButton opt;
		int index;
		char flag;
		PanelGenome panGen = PanelGenome.this;
		JPanel panel;
		
	
		
		
		public ChromPanel(int indx) {
			
			panel = new JPanel();
			
			plus = new JButton("+");
			moins = new JButton("-");
			opt = new JButton();
			
			this.index = indx;
			
			opt.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					chromPanActif = ChromPanel.this;
					typeMut.show(opt,0,0);
					modifMutation.setSelected((flag & 1) == 0);
					modifStruct.setSelected((flag & 2) == 0);
					modifRecomb.setSelected((flag & 4) == 0);
				}
			});
			
			plus.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					chromPanActif = ChromPanel.this;
					choisirtypeGene.show(plus,0,0);
				}
			});
			
			moins.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (listeGene.size()>0) {
						remove(listeGene.remove(listeGene.size()-1));
						plus.setEnabled(true);
						actualiser();
						// on s'assure que le dernier chromosome n'est pas vide
						if (listeGene.size() == 0 && index == nombreChromosomes-1) {
							int i = index;
							while (panGen.listeChrom.get(i).listeGene.size() == 0) {
								nombreChromosomes--;
								i--;
							}
						}
						if (listeGene.size()==0) moins.setEnabled(false);
					} 
					actualiser();
					construireBiomorph();
				}
			});
			plus.setSize(35,30);
			moins.setSize(35,30);
			opt.setSize(35,10);
			add(plus);
			add(moins);
			add(opt);
		}
		
		private void ajouterPanelGeneticien(PanelGeneticien panGenet) {
			if (panGenet != null){
				if (index>=nombreChromosomes) nombreChromosomes=index+1;
				// Un char est utilisé pour coder l'emplacement des gènes, on limite donc à 256 la taille des chromosomes.
				if (listeGene.size()<256) {
					panGenet.panGen = panGen;
					panGenet.chrom = this;
					listeGene.add(panGenet);
					add(panGenet);
					if (listeGene.size()==256) plus.setEnabled(false);
					moins.setEnabled(true);
				}
			}
		}
	}

}

