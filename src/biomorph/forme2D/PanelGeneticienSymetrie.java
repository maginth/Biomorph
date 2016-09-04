package biomorph.forme2D;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import biomorph.abstrait.Biomorph;



public class PanelGeneticienSymetrie extends PanelGeneticien {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color color,colorp;
	private int xc,yc,r;
	private int lienChrom,lienGene;
	
	
	
	public PanelGeneticienSymetrie(GeneSymetrie gene){
		this(gene.indexChromosome);
		lienChrom = gene.indexChromosome;
		lienGene = gene.indexGene;
	}
	
	
	public PanelGeneticienSymetrie(int indexChromosome) {
		lienChrom = indexChromosome;
		couleur = Biomorph.alea.nextInt();
		color = new Color(couleur);
		colorp = new Color(blanchir(couleur,3));
		setOpaque(false);
		setPreferredSize(new Dimension(90,90));
		setSize(90,90);
		actualise();
	}
	
	private void actualise(){
		xc = getWidth()/2;
		yc = getHeight()/2;
		r = getWidth()/2-1;
	}
	
	private BasicStroke lineStyle = new BasicStroke(3.0f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
	private BasicStroke lineStyle2 = new BasicStroke(0.5f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER, 10.0f, new float[]{6f,4f}, 0.0f);
	@Override 
	protected void paintChildren(Graphics g) {
		super.paintChildren(g);
		Graphics2D graph = (Graphics2D) g;
		graph.setColor(colorp);
		graph.fillArc(xc-r, yc-r, r*2, r*2, 0, 360);
		graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		graph.setStroke(lineStyle);
		graph.setColor(color);
		graph.drawArc(xc-r/4, yc-r/4, r/2, r/2, 0, 360);
		PanelGeneticienDivision.dessineFleche(xc-r, yc, xc+r, yc,graph);
		
		graph.setStroke(lineStyle2);
		graph.setColor(color);
			
		graph.drawArc(xc-r, yc-r, 2*r, 2*r, 0, 360);
	}

	@Override
	public GeneSymetrie creerGene() {
		return new GeneSymetrie(lienChrom,lienGene);
	}
}
