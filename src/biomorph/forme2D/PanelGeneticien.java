package biomorph.forme2D;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;

import biomorph.abstrait.GeneExpression;
import biomorph.forme2D.PanelGenome.ChromPanel;
import interfac.dragndrop.DragDrop;

/**
 * Classe à implémenter par les panels représentant les gènes dans le "gène hacking"
 * @author Mathieu Guinin
 *
 */
public abstract class PanelGeneticien extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	
	protected PanelGenome panGen;
	protected ChromPanel chrom;
	protected int couleur;
	private Border border = new BorderUIResource.LineBorderUIResource(Color.black);
	
	public PanelGeneticien() {
		
		MouseAdapter drop = new MouseAdapter() {
			Border defautBorder;
			@Override
			public void mouseEntered(MouseEvent e) {
				defautBorder = PanelGeneticien.this.getBorder();
				PanelGeneticien.this.setBorder(border);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				PanelGeneticien.this.setBorder(defautBorder);
			}
			@Override
			public void mouseReleased(MouseEvent e){
				PanelGeneticien pan = (PanelGeneticien) DragDrop.getContenuDrag();
				PanelGeneticien.this.echanger(pan);
			}
			/*@Override 
			public void mouseClicked(MouseEvent e){
				if (! (DragDrop.focusFin() instanceof PanelGeneticien)) {
					PanelGeneticien pan = (PanelGeneticien) DragDrop.getContenuDrag();
					pan.echanger(pan);
				}
			}*/
		};
		
		DragDrop.ajouterRecepteur(this,drop, new String[]{"changer Position"});
		
		addMouseListener(new MouseAdapter() {
			int zorder;
			@Override
			public void mouseEntered(MouseEvent e) {
				zorder = panGen.getComponentZOrder(PanelGeneticien.this);
				panGen.setComponentZOrder(PanelGeneticien.this, 0);
				PanelGeneticien.this.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				panGen.setComponentZOrder(PanelGeneticien.this, zorder);
				PanelGeneticien.this.repaint();
			}
		});
		
	}
	
	// atténue la couleur c plusieur fois
	static int blanchir(int c,int nbFois) {
		while (nbFois != 0) {
			nbFois--;
			c = (c & 0xfefefe)/2 + 0x7f7f7f;
		}
		return c;
	}
	
	
	public void echanger(PanelGeneticien p) {
		if (p != this) {
			PanelGenome temp0 = panGen;
			ChromPanel temp1 = chrom;
			int index1 = p.chrom.listeGene.indexOf(p), index2 = chrom.listeGene.indexOf(this);
			p.chrom.listeGene.set(index1, this);
			chrom.listeGene.set(index2, p);
			chrom = p.chrom;
			p.chrom = temp1;
			panGen = p.panGen;
			p.panGen = temp0;
			panGen.actualiser();
			if (p.panGen != panGen) p.panGen.actualiser();
			panGen.construireBiomorph();
		}
	}

	public abstract GeneExpression<?> creerGene();
	
	public void supprimer() {
		DragDrop.retirerRecepteur(this,new String[]{"changer Position"});
	}
	
	
}
