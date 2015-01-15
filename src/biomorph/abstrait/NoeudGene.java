package biomorph.abstrait;

import java.util.LinkedList;

/**
 * Cette classe d�termine la structure d'un g�nome o� il y a des appels r�cursif 
 * de g�nes (les g�ne doivent impl�menter NoeudGene.Lien pour �tre compatible avec 
 * cette classe).
 * En particulier cette classe extrait des g�nes clef de mani�re � ce qu'il ne 
 * reste aucune boucle dans le graph des appels entre g�nes si on �limine ces
 * g�nes clefs.
 * 
 * @author Mathieu Guinin
 *
 */
public class NoeudGene {
	private boolean visite=false;
	private final Lien gene;
	private final LinkedList<NoeudGene> arcs = new LinkedList<NoeudGene>();
	private final NoeudGene inverse;
	//public int num=numMax++;
	//private static int numMax=0;
	
	public NoeudGene(Lien gene){
		this.gene = gene;
		gene.setNoeud(this);
		inverse = new NoeudGene(gene,this);
		inverse.visite = true;
		for (Object obj:gene.getLienGene()) {
			if (obj instanceof Lien) {
				Lien recup = (Lien) obj;
				if (recup.getNoeud()==null) new NoeudGene(recup);
				NoeudGene noeud = recup.getNoeud();
				arcs.addFirst(noeud);
				noeud.inverse.arcs.addFirst(this.inverse);
			}
		}
		/*
		System.out.println("\nNoeud n�"+num);
		for (NoeudGene lien:arcs) {
			System.out.println("\t--> "+lien.num);
		}
		*/
	}
	
	public int degre_entrant() {
		return inverse.arcs.size();
	}
	private NoeudGene(Lien gene,NoeudGene inverse){
		this.gene = gene;
		this.inverse = inverse;
	}
	
	public static void optimiserGraph(Lien racine){
		LinkedList<NoeudGene.Lien> geneClefs = new LinkedList<NoeudGene.Lien>();
		if (racine.getNoeud() == null) new NoeudGene(racine);
		
		NoeudGene.algoTarjanAmeliore(racine.getNoeud(),geneClefs);
		
		for (Lien gene : geneClefs) {
			if (gene instanceof LienOptimise) {
				((LienOptimise) gene).optimiser();
			}
		}
	}
	/*
	 * annule l'optimisation des genes d�pendant du noeudGene (this)
	 * il faudra penser � redessiner le biomorph (normalisation = null)
	 */
	public void rafrechirOptimisation(){
		inverse.retroDesoptimiser();
		inverse.remetVisite();
		// suppression des anciens arcs
		while(!arcs.isEmpty()) {
			arcs.removeFirst().inverse.arcs.remove(inverse);
		}
		// ajout des nouveaux arcs
		for (Object obj:gene.getLienGene()) {
			if (obj instanceof Lien) {
				Lien recup = (Lien) obj;
				if (recup.getNoeud()==null) new NoeudGene(recup);
				NoeudGene noeud = recup.getNoeud();
				arcs.addFirst(noeud);
				noeud.inverse.arcs.addFirst(inverse);
			}
		}
	}
	
	private void retroDesoptimiser(){
		visite = true;
		inverse.visite=false;
		if (gene instanceof LienOptimise) ((LienOptimise) gene).desoptimiser();
		for (NoeudGene noeud:arcs) {
			if (noeud.inverse.visite) {
				noeud.retroDesoptimiser();
			}
		}
	}
	
	public void remetVisite(){
		visite = false;
		inverse.visite= true;
		//System.out.print("num "+num);
		for (NoeudGene noeud:arcs) {
			if (noeud.visite) {
				noeud.remetVisite();
			}
		}
	}
	/**
	 * 
	 * Cette algorithme d�termine les composantes connexe d'un graph avec l'ago de Trajan
	 * puis �limine le noeud de degr� entrant le plus �lev� dans chaque composante 
	 * connexe. Ensuite l'algo est r�apliqu� dans le reste de chacunes des composantes
	 * connexes. (les composantes connexes d'un seul �l�ment sont ignor�s).
	 * Une liste des g�nes �limin�s (g�nes clefs) est construite et renvoy�.
	 * 
	 * @param racine premier noeud du parcours en profondeur
	 * @param geneClefs liste de g�ne, elle doit �tre pass� vide et sera remplis par l'algorithme 
	 */
	public static void algoTarjanAmeliore(NoeudGene racine,LinkedList<Lien> geneClefs){
		
		/*
		 * invariant de boucle : � la fin de algoTarjanAmeliore, 
		 * tout les noeuds du sous graphe connexe de "racine"
		 * sont visite=true et inverse.visite = true
		 */
		
		LinkedList<NoeudGene> dateFin = new LinkedList<NoeudGene>();
		racine.parcoursProfondeur(dateFin);
		for (NoeudGene noeud:dateFin){
			if (!noeud.visite){
				LinkedList<NoeudGene> noeudsConnex = new LinkedList<NoeudGene>();
				noeud.parcoursProfondeur(noeudsConnex);
				if (noeudsConnex.size()==1) {
					NoeudGene first = noeudsConnex.peekFirst();
					first.visite=true;
					first.inverse.visite=true;
					if(first.arcs.contains(first) && first.gene instanceof LienOptimise) geneClefs.addLast(first.gene);
				} else {
					NoeudGene noeudMax = null;
					int degreEntrant = -1;
					for (NoeudGene nod:noeudsConnex){
						if (nod.inverse.arcs.size()>degreEntrant && nod.gene instanceof LienOptimise) {
							noeudMax = nod;
							degreEntrant = nod.inverse.arcs.size();
						}
					}
					if (noeudMax == null) {
						for (NoeudGene nod:noeudsConnex) {
							nod.visite = true;
							nod.inverse.visite = true;
						}
					} else {
						geneClefs.addLast(noeudMax.gene);
						noeudMax.visite=true;
						noeudMax.inverse.visite=true;
						/*
						 * invariant de boucle: tous les inverses des noeuds
						 * de noeudsConnex sont visite=true � la fin d'une
						 * it�ration
						 */
						for (NoeudGene nod:noeudsConnex) {
							if (!nod.visite) {
								algoTarjanAmeliore(nod,geneClefs);
							}
						}
					}
					
				}
			}
		}
	}
	//static int count=0;
	/**
	 * Parcours en profondeur avec rengement des noeuds suivant leur date de fin d�croissante
	 * @param dateFin liste � passer vide pour �tre remplis.
	 */
	private void parcoursProfondeur(LinkedList<NoeudGene> dateFin){
		visite = true;
		inverse.visite=false;
		//if (count++<100) System.out.println("entr� "+num);
		for (NoeudGene noeud:arcs) {
			if (!noeud.visite) {
				//if (count++<100) System.out.println("\t"+num+"->"+noeud.num);
				noeud.parcoursProfondeur(dateFin);
			} //else if (count++<100) System.out.println("\t(visit�)"+num+"->"+noeud.num);
		}
		dateFin.addFirst(inverse);
		//if (count++<100) System.out.println("fin "+num);
	}
	
	/**
	 *  interface � impl�menter pour les g�nes faisant des appels r�cursif et ne poss�dant pas d'optimisation
	 * @author Mathieu Guinin
	 *
	 */
	public interface Lien {
		NoeudGene getNoeud();
		void setNoeud(NoeudGene noeud);
		Object[] getLienGene();
	}
	/**
	 * Interface � impl�menter pour les g�nes faisant des appels r�cursif et  poss�dant des optimisations
	 * Les optimisations doivent acc�l�rer l'expression du g�nes ET DE TOUT CEUX APPELE RECURSIVEMENT 
	 * Notons que la m�thode optimisation ne sera pas forc�ment appel� (en principe appel� sur les g�nes clefs uniquement)
	 * @author Mathieu Guinin
	 *
	 */
	public interface LienOptimise extends Lien {
		public void optimiser();
		public void desoptimiser();
	}
}
