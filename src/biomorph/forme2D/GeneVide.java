package biomorph.forme2D;
import java.awt.Component;

import biomorph.abstrait.Biomorph;
import biomorph.abstrait.GeneExpression;
import biomorph.abstrait.TauxMutation;



/**
 * Gène qui ne dessine rien, peut être intéressant pour créer des discontinuité de la
 * dans le dessin du biomorph (si on n'utilise que des GeneExpressionLimite, on obtient 
 * une ligne brisée continue).
 * @author Mathieu Guinin
 *
 */
public class GeneVide implements GeneExpression<Similitude> {
	
	
	private static final long serialVersionUID = 1L;
	
	
	
	public static final GeneVide genVide = new GeneVide();
	

	@Override
	public void expression(Similitude trans) {}
	@Override
	public void trouverLesBords(Similitude trans) {}
	@Override
	public GeneVide recopie(TauxMutation X) {return new GeneVide();}
	@Override
	public void finaliser(Biomorph destinataire) {}
	@Override
	public Component getPanelGeneticien() {return new PanelGeneticienVide();}
	
}
