package biomorph.abstrait;

import java.awt.Component;
import java.io.Serializable;

/**
 * 
 * <p>Les classes implemantant cette interface permettent de d�finir le ph�notype
 * r�sultant de l'expression d'un g�ne. L'impl�mentation de la m�thode 
 * "expression" permet de construire ce ph�notype (partie du biomorph).</p>
 * 
 * @author Mathieu Guinin
 * @see Biomorph pour l'explication de la g�n�ricit�: < ... >
 */
public interface GeneExpression
					< Trans extends Transform<?,?>> extends Serializable
{
	/**
	 * @author Mathieu Guinin
	 * @param Trans transformation r�sultant de l'expression de g�nes pr�c�dents,
	 * 			elle conditionne la mani�re dont sera exprim� ce g�ne  
	 * 			(exemple: changement de couleur, rotation de la forme r�sultante...) 
	 */
	public void expression(Trans trans) ;
	
	/**
	 * trouverLesBords est simililaire � "expression", 
	 * mais cherche les extr�mit�s du biomorph au lieu de le dessiner.
	 * @param trans @see expression
	 */
	public void trouverLesBords(Trans trans);
	
	
	/**
	 * @author Mathieu Guinin
	 * 
	 * Cette fonction permet � un enfant d'un biomorph d'h�riter de ce g�ne.
	 * Il est recopi� avec des erreurs control�es par tauxMutation.
	 * 
	 * @param X	nombre flotant entre 0 et 1 repr�sentant l'intensit� des mutations
	 * @param destinataire	le biomorph enfant du biomorph qui contient ce g�ne
	 */
	public GeneExpression<Trans> recopie(TauxMutation X);
	/**
	 * @author Mathieu Guinin
	 * 
	 * @see Biomorph.croisement & Biomorph.duplique 
	 * 
	 * Cette m�thode est appel� sur chacun des g�nes d'un biomorph, 
	 * apr�s que le g�notype ait �t� remplis de tous ses g�nes.
	 * 
	 * PS: cette m�thode n'apporte rien conceptuellement, 
	 * elle r�pond � la difficult� d'initialiser des liens avec d'autre g�nes 
	 * avant qu'ils ne soient plac�s dans le g�notype.
	 */
	public void finaliser(Biomorph destinataire);
	
	public Component getPanelGeneticien();
}
