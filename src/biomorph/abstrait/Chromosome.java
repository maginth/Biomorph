package biomorph.abstrait;

import java.util.ArrayList;

public class Chromosome extends ArrayList<GeneExpression<?>> {
	
	private static final long serialVersionUID = 1L;
	
	public char flagMutation;
	transient TauxMutation X;
	
	public Chromosome(int nbGene,TauxMutation X,char flagMutation) {
		super(nbGene);
		this.flagMutation = flagMutation;
		if (X != null) setTauxMutation(X);
	}
	
	public void ajouterCopie(GeneExpression<?> gene){
		add(gene.recopie(X));
	}
	
	private void setTauxMutation(TauxMutation X){
		if (X == null) this.X = null;
		else if (flagMutation==0) this.X = X;
		else this.X = new TauxMutation( 
				((flagMutation & 1) ==1)? 0:X.getProbaMutation(),
				X.getAmplitudeMutation(),
				((flagMutation & 2) ==2)? 0:X.getProbaModifStructure(),
				((flagMutation & 4) ==4)? 0:X.getProbaRecombinaison());
	}

	
}