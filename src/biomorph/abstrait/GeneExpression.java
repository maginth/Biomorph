package biomorph.abstrait;

import java.awt.Component;
import java.io.Serializable;

/**
 * 
 * <p>Les classes implemantant cette interface permettent de définir le phénotype
 * résultant de l'expression d'un gène. L'implémentation de la méthode 
 * "expression" permet de construire ce phénotype (partie du biomorph).</p>
 * 
 * @author Mathieu Guinin
 * @see Biomorph pour l'explication de la généricité: < ... >
 */
public interface GeneExpression
					< Trans extends Transform<?,?>> extends Serializable
{
	/**
	 * @author Mathieu Guinin
	 * @param Trans transformation résultant de l'expression de gènes précédents,
	 * 			elle conditionne la manière dont sera exprimé ce gène  
	 * 			(exemple: changement de couleur, rotation de la forme résultante...) 
	 */
	public void expression(Trans trans) ;
	
	/**
	 * trouverLesBords est simililaire à "expression", 
	 * mais cherche les extrémités du biomorph au lieu de le dessiner.
	 * @param trans @see expression
	 */
	public void trouverLesBords(Trans trans);
	
	
	/**
	 * @author Mathieu Guinin
	 * 
	 * Cette fonction permet à un enfant d'un biomorph d'hériter de ce gène.
	 * Il est recopié avec des erreurs controlées par tauxMutation.
	 * 
	 * @param X	nombre flotant entre 0 et 1 représentant l'intensité des mutations
	 * @param destinataire	le biomorph enfant du biomorph qui contient ce gène
	 */
	public GeneExpression<Trans> recopie(TauxMutation X);
	/**
	 * @author Mathieu Guinin
	 * 
	 * @see Biomorph.croisement & Biomorph.duplique 
	 * 
	 * Cette méthode est appelé sur chacun des gènes d'un biomorph, 
	 * après que le génotype ait été remplis de tous ses gènes.
	 * 
	 * PS: cette méthode n'apporte rien conceptuellement, 
	 * elle répond à la difficulté d'initialiser des liens avec d'autre gènes 
	 * avant qu'ils ne soient placés dans le génotype.
	 */
	public void finaliser(Biomorph destinataire);
	
	public Component getPanelGeneticien();
}
