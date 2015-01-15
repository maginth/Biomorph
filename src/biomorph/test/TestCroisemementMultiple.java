package biomorph.test;

import interfac.global.Parametres;

import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import biomorph.abstrait.Biomorph;
import biomorph.forme2D.Biomorph2D;

public class TestCroisemementMultiple extends JApplet {
	
	private static final long serialVersionUID = 1L;
	static TestCroisemementMultiple fenetre;
	static JPanel pan;
	
	public void init() {
		fenetre = this;
	    main(new String[]{});
	  }
	
	
	private static Biomorph2D test(long i,int gene){
		Biomorph.alea.setSeed(i);
		return Biomorph2D.aleaBiomorph1(gene);
	}
	
	public static void testCroisement(int nb_parent) {
		ArrayList<Biomorph2D> parents = new ArrayList<Biomorph2D>(nb_parent);
		for (int i=0;i<nb_parent;i++) {
			Biomorph2D biom = test(Biomorph.alea.nextLong(),10);
			parents.add(biom);
			pan.add(biom.getIcone(pan.getWidth()/nb_parent));
		}
		ArrayList<Biomorph2D> enfants = Biomorph.croisementMultiple(parents, Parametres.X);
		for (Biomorph2D bio : enfants) pan.add(bio.getIcone(pan.getWidth()/nb_parent));
	}
	
	public static void main(String[] arg){
		
		
		if (fenetre == null) {
			fenetre = new TestCroisemementMultiple();
			JFrame fen = new JFrame();
			fen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			fen.add(fenetre);
			fen.pack();
			fen.setVisible(true);
			fen.pack();
		}
		pan = new JPanel();
		pan.setPreferredSize(new Dimension(1024, 800));
		fenetre.add(pan);
		testCroisement(30);
		}
}
