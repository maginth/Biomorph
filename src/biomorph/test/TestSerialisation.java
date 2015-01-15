package biomorph.test;

import interfac.global.Parametres;

import java.awt.Dimension;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import biomorph.abstrait.Biomorph;
import biomorph.forme2D.Biomorph2D;

public class TestSerialisation extends JApplet {
	
	private static final long serialVersionUID = 1L;
	static TestSerialisation fenetre;
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
		
		Biomorph2D p1 = test(6535365);
		Biomorph2D p2 = test(5471476);
		Biomorph2D biom = p1.croisement(p2, Parametres.X);
		
		if (fenetre == null) {
			fenetre = new TestSerialisation();
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
		pan.add(biom.getIcone(200));
		Serialisation.serialUnserial(biom);
		
		// !!!!!!!!!! ne pas oublier de finaliser le biomorph deserialisé !!!!!!!!!!
		Serialisation.biomorph.finaliser();
		pan.add(Serialisation.biomorph.getIcone(200));
		}
}
