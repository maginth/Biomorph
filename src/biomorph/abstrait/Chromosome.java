package biomorph.abstrait;

import java.util.ArrayList;

public class Chromosome extends ArrayList<GeneExpression<?>> {
	
	private static final long serialVersionUID = 1L;
	
	public char flagMutation;
	transient TauxMutation X;
	
	public Chromosome(int nbGene,TauxMutation X,char flagMutation) {
		super(nbGene);
		this.flagMutation = flagMutation;
		if (X != null) setTauxMutation(X, nbGene);
	}
	
	public void ajouterCopie(GeneExpression<?> gene){
		add(gene.recopie(X));
	}
	
	private void setTauxMutation(TauxMutation X, int n){
		if (X == null) this.X = null;
		else this.X = new TauxMutation( 
				((flagMutation & 1) ==1)? 0:1 - Math.pow(1 - X.getProbaMutation(), 1.0 / (2 * n)),
				X.getAmplitudeMutation(),
				((flagMutation & 2) ==2)? 0:1 - Math.pow(1 - X.getProbaModifStructure(), 1.0 / (6 * n)),
				((flagMutation & 4) ==4)? 0: X.getProbaRecombinaison());
	}

}