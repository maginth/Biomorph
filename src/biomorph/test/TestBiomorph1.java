package biomorph.test;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.LinkedList;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import biomorph.abstrait.Biomorph;
import biomorph.abstrait.NoeudGene;
import biomorph.forme2D.Biomorph2D;
import biomorph.forme2D.Similitude;
import interfac.util.ImageAccessible;

public class TestBiomorph1 extends JApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static TestBiomorph1 fenetre;
	static ImageAccessible bmp = new ImageAccessible(1280, 1024);
	static Image img;
	
	public void init() {
		fenetre = this;
	    main(new String[]{});
	  }
	
	
	private static Biomorph2D test(long i){
		for (int j=0;j<1280*1024;j++) bmp.getAccessPixels()[j]=0;
		Biomorph.alea.setSeed(i);
		return Biomorph2D.aleaBiomorph1(10);
	}
	
	public static void main(String[] arg){
		
		
	
		
		/*
		biom1.dessine(new Similitude(200f,200f,200f,0,1f,0));
		 for (int i=1;i<6;i++) {
			biom2.dessine(new Similitude(200f + (i%3)*350,200f+(i/3)*400f,200f,0,1f,0));
			Biomorph2D biom3 = biom1.croisement(biom2, 0.1f);
			biom3.setPixels(bmp,1280,1024);
			biom1=biom2;
			biom2=biom3;
		}//*/
		/*
		for (int i=0;i<6;i++) {
			biom2.dessine(new Similitude(200f + (i%3)*350,200f+(i/3)*400f,200f,0,1f,0),bmp);
			biom2 = biom2.duplique(0.01f);
		}
		*/
		/*
		for (int i=0;i<100;i++) {
			test(i);
		}
		//*/
		Biomorph2D test = test(5757537L);
		Serialisation.serialUnserial(test);
		test = Serialisation.biomorph;
		test.dessine(new Similitude(600f,500f,1000f,0,true,0),bmp);
		LinkedList<NoeudGene.Lien> geneClefs = new LinkedList<NoeudGene.Lien>();
		NoeudGene.algoTarjanAmeliore(new NoeudGene((NoeudGene.Lien) test.genotype.get(0, 0)),geneClefs);
		/*for (NoeudGene.Lien gene:geneClefs) {
			System.out.println("Clef: n°"+(gene.getNoeud().num));
		}*/
		
		
		JPanel pan = new JPanel(){
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				g.drawImage(bmp.getImageAWT(), 0, 0, null);
				
			}
		};
		//PanelGenome pang = new PanelGenome(test);
		//pang.setPreferredSize(new Dimension(1000,300));
		//pan.add(pang);

		if (fenetre == null) {
			fenetre = new TestBiomorph1();
			JFrame fen = new JFrame();
			fen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			fen.add(fenetre);
			fen.pack();
			fen.setVisible(true);
			fen.pack();
		}
		pan.setPreferredSize(new Dimension(1280, 1024));
		fenetre.add(pan);
		/*
		Runtime runtime = Runtime.getRuntime();
		System.out.print("mémoire libre :"+runtime.freeMemory()+"\n");
		long t = System.currentTimeMillis();
		runtime.gc();
		System.out.print("durée gc :"+(System.currentTimeMillis()-t)+"\n");
		System.out.print("mémoire libre après gc:"+runtime.freeMemory()+"\n");
		*/
		// Test similitudes:
		//Similitude stest = new Similitude(new float[]{53.245f,12.1544f,0.215453f,0.255f,452.224f,154f,norme2(0.215453f,0.255f)});
		//System.out.println("\n"+stest+"\n"+stest.getInverse()+"\n"+stest.concat(stest.getInverse())+"\n"+stest.getInverse().concat(stest));
	}
}
