package biomorph.forme2D;

import java.awt.Component;

import biomorph.abstrait.Biomorph;
import biomorph.abstrait.GeneExpression;
import biomorph.abstrait.NoeudGene;
import biomorph.abstrait.TauxMutation;

/**
 * G�ne appelant deux fois un g�ne appel� g�ne racine, la deuxi�me fois la 
 * sym�trie est chang� dans la similitude pass� en argument.
 * Ce g�ne construit donc la forme construite par le g�ne racine et sont reflet
 * par rapport � la base de la forme.
 * 
 * Ajouter un GeneSymetrie dans le g�nome peut augmenter beaucoup la complexit� du dessin
 * (car il n'y a pas de r�duction d'�chelle de la forme dessin� deux fois par le g�ne racine)
 * 
 * @see GeneDivisionLimite
 * 
 * @author Mathieu Guinin
 *
 */
public class GeneSymetrie implements GeneExpression<Similitude>, NoeudGene.Lien {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// les similitudes pass�s en param�tre au g�ne racine
	static Similitude sim1 = new Similitude(0,0,0,1,true,0);
	static Similitude sim2 = new Similitude(0,0,0,1,false,0);
	
	// l'objet NoeudGene pour d�terminer la topologie du g�nome (et trouver les g�nes � optimiser)
	private transient NoeudGene noeud ;
	
	GeneExpression<Similitude> racine;
	int indexChromosome=1, indexGene=0;
	
	public GeneSymetrie(int indexChromosome,int indexGene ){
		this.indexChromosome = indexChromosome;
		this.indexGene = indexGene;
	}
	@Override
	public void expression(Similitude trans) {
		racine.expression(sim1.concat(trans));
		racine.expression(sim2.concat(trans));
	}

	@Override
	public void trouverLesBords(Similitude trans) {
		racine.trouverLesBords(sim1.concat(trans));
		racine.trouverLesBords(sim2.concat(trans));
	}


	@Override
	public GeneExpression<Similitude> recopie(TauxMutation X) {
		return new GeneSymetrie(indexChromosome, indexGene);
	}

	private transient boolean finalise=false;
	@Override
	/**
	 * lors de la finalisation, on s'assure qu'il n'y a pas de boucle infinie avec
	 * quand racine est aussi un G�ne Sym�trie;
	 */
	public void finaliser(Biomorph destinataire) {
		if (!finalise) {
			int index = indexGene;
			do {
				this.racine = destinataire.genotype.get(indexChromosome,index);
				GeneSymetrie rac;
				while(racine instanceof GeneSymetrie && racine != this) {
					rac = (GeneSymetrie) racine;
					this.racine = destinataire.genotype.get(rac.indexChromosome,rac.indexGene);
				}
				if (racine == this) index = (index+1) % destinataire.genotype.get(indexChromosome).size();
				else break;
			} while (index != indexGene);
			indexGene = index;
			if (racine == this) racine = GeneVide.genVide;
			else this.racine.finaliser(destinataire);
			finalise = true;
		}
	}
	
	@Override
	public NoeudGene getNoeud() {
		return noeud;
	}
	
	@Override
	public void setNoeud(NoeudGene noeud) {this.noeud = noeud;}
	
	@Override
	public Object[] getLienGene() {
		return new Object[]{racine};
	}
	
	
	private transient PanelGeneticienSymetrie panelGene;
	@Override
	public Component getPanelGeneticien() {
		if (panelGene == null) panelGene = new PanelGeneticienSymetrie(this);
		return panelGene;
	}
	

}
