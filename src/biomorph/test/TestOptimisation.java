package biomorph.test;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.LinkedList;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import biomorph.abstrait.Biomorph;
import biomorph.abstrait.NoeudGene;
import biomorph.forme2D.Biomorph2D;
import biomorph.forme2D.GeneDivisionLimite;
import biomorph.forme2D.Similitude;
import interfac.util.ImageAccessible;

public class TestOptimisation extends JApplet {
	
	private static final long serialVersionUID = 1L;
	static TestOptimisation fenetre;
	static JPanel pan;
	
	public void init() {
		fenetre = this;
	    main(new String[]{});
	  }
	
	
	private static Biomorph2D test(long i){
		Biomorph.alea.setSeed(i);
		return Biomorph2D.aleaBiomorph1(20);
	}
	
	
	public static void main(String[] arg){
		
		//Biomorph2D biom = test(876785677);
		//Biomorph2D biom = test(545344);
		//Biomorph2D biom = test(37463784);
		//Biomorph2D biom = test(7584);
		Biomorph2D biom = test(8);
		
		if (fenetre == null) {
			fenetre = new TestOptimisation();
			JFrame fen = new JFrame();
			fen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			fen.add(fenetre);
			fen.pack();
			fen.setVisible(true);
			fen.pack();
		}
		pan = new JPanel(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				g.drawImage(bmp.getImageAWT(), 0, 0, null);
				
			}
		};
		pan.setPreferredSize(new Dimension(1024, 800));
		fenetre.add(pan);
		LinkedList<NoeudGene.Lien> geneClefs = new LinkedList<NoeudGene.Lien>();
		NoeudGene.algoTarjanAmeliore(new NoeudGene((NoeudGene.Lien) biom.genotype.get(0, 0)),geneClefs);
		/*for (NoeudGene.Lien gene:geneClefs) {
			System.out.println("Clef: n°"+(gene.getNoeud().num));
		}*/
		for (NoeudGene.Lien gene : geneClefs) {
			if (gene instanceof GeneDivisionLimite) {
				((GeneDivisionLimite) gene).optimiser();
			}
		}				
		long t = System.currentTimeMillis();
		Similitude identite = new Similitude(500,1000,400000,0,true,0);
		for (int i=0;i<2;i++) for (int j=0;j<2;j++) {
		GeneDivisionLimite.xm=i*500;
		GeneDivisionLimite.xM=i*500+500;
		GeneDivisionLimite.ym=j*500;
		GeneDivisionLimite.yM=j*500+500;
		GeneDivisionLimite.testRect = true;
		biom.dessine(identite,bmp);
		}
		System.out.println("totale :"+(System.currentTimeMillis()-t));
	}
	static ImageAccessible bmp = new ImageAccessible(1280, 1024);

}
