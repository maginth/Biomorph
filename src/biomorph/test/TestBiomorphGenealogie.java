package biomorph.test;

import javax.swing.JApplet;
import javax.swing.JFrame;

import biomorph.abstrait.Biomorph;
import biomorph.abstrait.TauxMutation;
import biomorph.forme2D.Biomorph2D;
import interfac.panel.Genealogie;

public class TestBiomorphGenealogie extends JApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static TestBiomorphGenealogie fenetre;
	static int NBbiomorphs=0;
	
	public void init() {
		fenetre = this;
		long t = System.currentTimeMillis();
	    main(new String[]{""});
	    System.out.println("NOMBRE DE BIOMORPHS : "+NBbiomorphs);
		System.out.println("DUREE TOTALE :"+(System.currentTimeMillis()-t)+" ms");
	  }
	
	static TauxMutation mut = new TauxMutation(0.05,1,0.05,0.05);
	
	public static Biomorph2D genererParent(int niveau) {
		Biomorph2D res;
		NBbiomorphs++;
		if (Biomorph.alea.nextInt(3)!=0 && niveau <5) res = genererParent(niveau+1).croisement(genererParent(niveau+1), mut);
		else res = Biomorph2D.aleaBiomorph1(10);
		//if (Biomorph.alea.nextInt(4)!=0) res.parents.add(genererParent(niveau+1));
		return res;
	}
	
	public static void genererEnfants(int niveau,Biomorph2D bio) {
		while (Biomorph.alea.nextInt(3)!=0 && niveau <5) {
			NBbiomorphs+=2;
			genererEnfants(niveau+1,bio.croisement(Biomorph2D.aleaBiomorph1(10), mut));
		}
	}
	
	public static void main(String[] arg){
		
		if (fenetre == null) {
			fenetre = new TestBiomorphGenealogie();
			JFrame fen = new JFrame();
			fen.add(fenetre);
			fen.setVisible(true);
			fen.pack();
		}
		
		Genealogie arbre = new Genealogie(800,800,100);
		Biomorph2D bioMilieu = genererParent(0);
		genererEnfants(0, bioMilieu);
		arbre.centrerSurBiomorph(bioMilieu);
		arbre.actualiser();
		fenetre.add(arbre);
	}
}
