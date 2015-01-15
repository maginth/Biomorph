package biomorph.abstrait;

/**
 * Les diff�rents taux de mutation de cette classe sont interpr�t� 
 * diff�remment dans chaque impl�mentation des classes de Biomorph.abstrait
 * 
 * @author Mathieu Guinin
 *
 */
public class TauxMutation {
	private double probaMutation;
	private double amplitudeMutation;
	private double probaModifStructure;
	private double probaRecombinaison;
	
	/**
	 * 
	 * @param p probabilit� d'apparition d'une mutation
	 * @param a amplitude max d'une mutation 
	 * @param r taux de recombinaison du g�nome
	 */
	public TauxMutation(double p,double a,double s,double r) {
		probaMutation = p;
		amplitudeMutation = a;
		probaModifStructure = s;
		probaRecombinaison = r;
	}
	
	public void setProbaMutation(double x){
		probaMutation = x<0? 0 : x>1? 1 : x;
	}
	public void setAmplitudeMutation(double x){
		amplitudeMutation = x<0? 0 : x>1? 1 : x;
	}
	public void setProbaModifStructure(double x){
		probaModifStructure = x<0? 0 : x>1? 1 : x;
	}
	public void setProbaRecombinaison(double x){
		probaRecombinaison = x<0? 0 : x>1? 1 : x;
	}
	
	public double getProbaMutation(){
		return probaMutation;
	}
	public double getAmplitudeMutation(){
		return amplitudeMutation;
	}
	public double getProbaRecombinaison(){
		return probaRecombinaison;
	}
	public double getProbaModifStructure(){
		return probaModifStructure;
	}
}
