package biomorph.abstrait;

import java.util.LinkedList;

/**
 * Cette classe détermine la structure d'un génome où il y a des appels récursif 
 * de génes (les gène doivent implémenter NoeudGene.Lien pour être compatible avec 
 * cette classe).
 * En particulier cette classe extrait des gènes clef de manière à ce qu'il ne 
 * reste aucune boucle dans le graph des appels entre gènes si on élimine ces
 * gènes clefs.
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
		System.out.println("\nNoeud n°"+num);
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
	 * annule l'optimisation des genes dépendant du noeudGene (this)
	 * il faudra penser à redessiner le biomorph (normalisation = null)
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
	 * Cette algorithme détermine les composantes connexe d'un graph avec l'ago de Trajan
	 * puis élimine le noeud de degré entrant le plus élevé dans chaque composante 
	 * connexe. Ensuite l'algo est réapliqué dans le reste de chacunes des composantes
	 * connexes. (les composantes connexes d'un seul élément sont ignorés).
	 * Une liste des gènes éliminés (gènes clefs) est construite et renvoyé.
	 * 
	 * @param racine premier noeud du parcours en profondeur
	 * @param geneClefs liste de gène, elle doit être passé vide et sera remplis par l'algorithme 
	 */
	public static void algoTarjanAmeliore(NoeudGene racine,LinkedList<Lien> geneClefs){
		
		/*
		 * invariant de boucle : à la fin de algoTarjanAmeliore, 
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
						 * de noeudsConnex sont visite=true à la fin d'une
						 * itération
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
	 * Parcours en profondeur avec rengement des noeuds suivant leur date de fin décroissante
	 * @param dateFin liste à passer vide pour être remplis.
	 */
	private void parcoursProfondeur(LinkedList<NoeudGene> dateFin){
		visite = true;
		inverse.visite=false;
		//if (count++<100) System.out.println("entré "+num);
		for (NoeudGene noeud:arcs) {
			if (!noeud.visite) {
				//if (count++<100) System.out.println("\t"+num+"->"+noeud.num);
				noeud.parcoursProfondeur(dateFin);
			} //else if (count++<100) System.out.println("\t(visité)"+num+"->"+noeud.num);
		}
		dateFin.addFirst(inverse);
		//if (count++<100) System.out.println("fin "+num);
	}
	
	/**
	 *  interface à implémenter pour les gènes faisant des appels récursif et ne possédant pas d'optimisation
	 * @author Mathieu Guinin
	 *
	 */
	public interface Lien {
		NoeudGene getNoeud();
		void setNoeud(NoeudGene noeud);
		Object[] getLienGene();
	}
	/**
	 * Interface à implémenter pour les gènes faisant des appels récursif et  possédant des optimisations
	 * Les optimisations doivent accélérer l'expression du gènes ET DE TOUT CEUX APPELE RECURSIVEMENT 
	 * Notons que la méthode optimisation ne sera pas forcément appelé (en principe appelé sur les gènes clefs uniquement)
	 * @author Mathieu Guinin
	 *
	 */
	public interface LienOptimise extends Lien {
		public void optimiser();
		public void desoptimiser();
	}
}
