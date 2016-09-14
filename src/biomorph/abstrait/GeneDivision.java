package biomorph.abstrait;

/**
 * 
 * <p>cette classe abstraite "GeneDivision" d�finie un type important de g�ne.
 * En effet son expression d�clenche l'expression de deux autres g�nes 1 et 2.
 * Si le g�ne pr�sent code le dessin d'une forme, alors cette forme peut �tre 
 * d�compos�e en deux plus petites correspondant � l'expression des g�nes 1 et 2.
 * </p>
 * @author Mathieu Guinin
 * @see GeneExpression  
 * @see Biomorph pour l'explication de la g�n�ricit�: < ... >
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
	/** R�ferences vers les g�nes 1 et 2 appel�s lors de l'espression de ce g�ne*/
	protected transient GeneExpression<S1> lienGene1;
	protected transient GeneExpression<S2> lienGene2;
	/** position des g�nes 1 et 2 dans le g�notype du biomorph "cible"*/
	public char indexGene1;
	public char indexGene2;
	public char indexChromosome1;
	public char indexChromosome2;
	/** transformations conditionnant l'expression des g�nes 1 et 2*/
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
	 * Permet � des appels r�cursifs infini entre g�nes (L-system) de s'arr�ter
	 * @param trans contient d'information n�cessaire pour savoir quand s'arr�ter
	 * 		(exemple: la forme � dessin� est plus petite qu'un pixel)
	 * @return true si l'appelle aux g�nes 1 et 2 ne doit pas se faire
	 */
	protected abstract boolean conditionArret(E trans);
	
	/**
	 * Permet d'effectuer une action sur le biomorph cible avant l'appel aux g�nes 1 et 2
	 * @param trans la transformation de l'expression de ce g�ne
	 * @param trans1 la transformation de l'expression du g�ne 1
	 * @param trans2 la transformation de l'expression du g�ne 2
	 */
	protected abstract void aChaqueDivision(E trans, S1 trans1, S2 trans2);
	
	/**
	 * Action final � effectuer au biomorph cible si la condition d'arr�t est remplis
	 * @param trans la transformation de l'expression de ce g�ne
	 */
	protected abstract void aLaFin(E trans);
	
	
	/**
	 * @see GeneExpression.expression
	 * Si la condition d'arr�t est remplis aLaFin(trans) est appel�
	 * Sinon aChaqueDivision est appel�, suivie de l'expression des g�nes 1 et 2
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
