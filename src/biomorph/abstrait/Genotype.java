package biomorph.abstrait;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Le genotype est une ArrayList de chromosomes
 * Les chromosomes sont des ArrayList de GeneExpression
 * Dans en principe, les chromosomes contiennent des gènes qui travaille ensemble
 * On peut aussi utiliser les chromosomes pour rassembler des gènes de mêmes compatibilité
 * 
 * @author Mathieu Guinin
 *
 */
public class Genotype extends ArrayList<Chromosome> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Genotype(int nbChromosome) {
		super(nbChromosome);
	}
	
	public Genotype(int nbChromosome, int nbGene) {
		super(nbChromosome);
		for (int i=0;i<nbChromosome;i++) super.add(new Chromosome(nbGene,null,(char) 0));
	}
	
	public void add(int chromosome,GeneExpression<?> gene) {
		get(chromosome).add(gene);
	}
	
	
	
	@SuppressWarnings("unchecked")
	public <G extends GeneExpression<?>> G get(int indexChromosome,int indexGene) {
		if (indexChromosome >= size())
			return null;
		int len = get(indexChromosome).size();
		return (G) get(indexChromosome).get(indexGene % len);
	}
	
	public int aleaIndex(int indexChromosome) {
		return Biomorph.alea.nextInt(get(indexChromosome).size());
	}
	
	/**
	 * Construit un nouveau génotype par coisement simple de deux génotype parents 
	 * (la moitier des gènes des parents sont perdue, la longeur des chromosomes est le max des longueur de ceux des parents )
	 * @param genotype1 parent1
	 * @param genotype2 parent2
	 * @param X taux de mutation
	 */
	public Genotype(Genotype genotype1,Genotype genotype2,TauxMutation X) {
		
		super(Math.max(genotype1.size(), genotype2.size()));
		Chromosome chrom1,chrom2,chromosome;
		Iterator<Chromosome> geno1 = genotype1.iterator(), geno2 = genotype2.iterator();
		Iterator<GeneExpression<?>> igen1=null,igen2,igenA,igenB;
		// indique quel parent transmet ses gènes
		boolean recopieParent1;
		double probaRecomb = X.getProbaRecombinaison();
		
		while(geno1.hasNext() || geno2.hasNext()) {
			chrom1 = geno1.next(); chrom2 = geno2.next(); chromosome = null;
			recopieParent1 = Biomorph.alea.nextBoolean();
			if (chrom1 != null && chrom2 != null) {
				igen1 = chrom1.iterator(); igen2 = chrom2.iterator();
				chromosome = new Chromosome(Math.max(chrom1.size(), chrom2.size()),X,(char) (chrom1.flagMutation & chrom1.flagMutation));
				while(igen1.hasNext() && igen2.hasNext()) {
					recopieParent1 = Biomorph.mutBool(recopieParent1,probaRecomb);
					if (recopieParent1) {igenA = igen1;igenB = igen2;} else {igenB = igen1;igenA = igen2;}
						chromosome.ajouterCopie(igenA.next());
						igenB.next();
				}
				if (igen2.hasNext()) igen1 = igen2;
				
			} else {
				if (chrom1 == null) chrom1 = chrom2;
				if (chrom1 != null) {
					igen1 = chrom1.iterator();
					chromosome = new Chromosome(chrom1.size(),X,chrom1.flagMutation);
				} else break;
			}
			if (igen1 != null) while(igen1.hasNext()) chromosome.ajouterCopie(igen1.next());
			this.add(chromosome);
		}
		
	}
	
	
	/**
	 * Duplique le génotype parent avec des mutations.
	 * @param genotypeParent
	 * @param X taux de mutation
	 */
	public Genotype(Genotype genotypeParent,TauxMutation X) {
		
		super(genotypeParent.size());
		Chromosome chromosome = null;
		for (Chromosome chromParent : genotypeParent) {
			if (chromParent != null) {
				chromosome = new Chromosome(chromParent.size(),X,chromParent.flagMutation);
				for (GeneExpression<?> gene : chromParent)
					chromosome.ajouterCopie(gene);
			} else chromosome = null;
			this.add(chromosome);
		}
	}
	
	


	public void finaliser(Biomorph biomorph) {
		get(0,0).finaliser(biomorph);
	}
	
}
