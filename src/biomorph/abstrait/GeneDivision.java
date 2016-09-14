package biomorph.abstrait;

/**
 * 
 * <p>cette classe abstraite "GeneDivision" définie un type important de gène.
 * En effet son expression déclenche l'expression de deux autres gènes 1 et 2.
 * Si le gène présent code le dessin d'une forme, alors cette forme peut être 
 * décomposée en deux plus petites correspondant à l'expression des gènes 1 et 2.
 * </p>
 * @author Mathieu Guinin
 * @see GeneExpression  
 * @see Biomorph pour l'explication de la généricité: < ... >
 */


public abstract class GeneDivision 
		< E extends Transform<?,?>,
		S1 extends Transform<?,?>,
		S2 extends Transform<?,?>>
	implements GeneExpression<E>,NoeudGene.Lien
	 

{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Réferences vers les gènes 1 et 2 appelés lors de l'espression de ce gène*/
	protected transient GeneExpression<S1> lienGene1;
	protected transient GeneExpression<S2> lienGene2;
	/** position des gènes 1 et 2 dans le génotype du biomorph "cible"*/
	public char indexGene1;
	public char indexGene2;
	public char indexChromosome1;
	public char indexChromosome2;
	/** transformations conditionnant l'expression des gènes 1 et 2*/
	protected transient Transform<E,S1> transGene1;
	protected transient Transform<E,S2> transGene2;

	
	
	
	
	/**
	 * @author Mathieu Guinin
	 * Constructeur de GeneDivision
	 */
	public GeneDivision() {};
	
	public GeneDivision(char indexChromosome1,char indexGene1,char indexChromosome2,char indexGene2,Transform<E,S1> trans1,Transform<E,S2> trans2) {
		this.indexChromosome1 = indexChromosome1;
		this.indexChromosome2 = indexChromosome2;
		this.indexGene1 = indexGene1;
		this.indexGene2 = indexGene2;
		this.transGene1 = trans1;
		this.transGene2 = trans2;
	}
	
	
	/**
	 * @return 
	 * @see GeneExpression.finaliser
	 */
	
	@Override
	public void finaliser(Biomorph biom) {
		lienGene1 = biom.genotype.get(indexChromosome1,indexGene1);
		lienGene2 = biom.genotype.get(indexChromosome2,indexGene2);	
	}
	
	
	
	/**
	 * Permet à des appels récursifs infini entre gènes (L-system) de s'arrêter
	 * @param trans contient d'information nécessaire pour savoir quand s'arrêter
	 * 		(exemple: la forme à dessiné est plus petite qu'un pixel)
	 * @return true si l'appelle aux gènes 1 et 2 ne doit pas se faire
	 */
	protected abstract boolean conditionArret(E trans);
	
	/**
	 * Permet d'effectuer une action sur le biomorph cible avant l'appel aux gènes 1 et 2
	 * @param trans la transformation de l'expression de ce gène
	 * @param trans1 la transformation de l'expression du gène 1
	 * @param trans2 la transformation de l'expression du gène 2
	 */
	protected abstract void aChaqueDivision(E trans, S1 trans1, S2 trans2);
	
	/**
	 * Action final à effectuer au biomorph cible si la condition d'arrêt est remplis
	 * @param trans la transformation de l'expression de ce gène
	 */
	protected abstract void aLaFin(E trans);
	
	
	/**
	 * @see GeneExpression.expression
	 * Si la condition d'arrêt est remplis aLaFin(trans) est appelé
	 * Sinon aChaqueDivision est appelé, suivie de l'expression des gènes 1 et 2
	 */
	@Override 
	public void expression(E trans) {
		if (conditionArret(trans)) {
			aLaFin(trans);
		} else {
			S1 trans1 = transGene1.concat(trans) ;
			S2 trans2 = transGene2.concat(trans) ;
			aChaqueDivision(trans,trans1,trans2);
			lienGene1.expression(trans1);
			lienGene2.expression(trans2);
		}
	}

	private transient NoeudGene noeud;
	@Override
	public NoeudGene getNoeud() {return noeud;}
	@Override
	public void setNoeud(NoeudGene noeud) {this.noeud = noeud;}
	@Override 
	public Object[] getLienGene() {return new Object[]{lienGene1,lienGene2};}

}
