package biomorph.forme2D;



import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import biomorph.abstrait.Biomorph;
import biomorph.abstrait.NoeudGene;


/**
 * Panel permettant de modifier l'information génétique d'un GeneDivisionLimite
 * @author Mathieu Guinin
 *
 */
public class PanelGeneticienDivision extends PanelGeneticien {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color color,colorp,colorpp,color1,color1p,color2,color2p;
	private int couleur1,couleur2;
	private char chr1,chr2,gen1,gen2;
	private float rotation = 1.5f;
	private boolean s1,s2,o1,o2;
	private GeneDivisionLimite gene;
	
	/*
	 * xa,ya : coordonné du point sur le cercle 
	 * xc,yc : coordonné du centre du cercle
	 * x1,yc et x1,yc : coordonné des deux extrémités du diamètre du cercle
	 * xs1,ys et xs2,ys : coordonné du centre des deux flèches
	 */
	private int x1,x2,xs1,xs2,ys,xa,ya,xc,yc,r;
	private boolean mouseS1,mouseS2,mouseA,mouseO1,mouseO2,signature;
	private int r1,r2,sqr1,sqr2;
	private static Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
	private static Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	
	
	
	public PanelGeneticienDivision(GeneDivisionLimite gene){
		this(gene.getIndex()[0]);
		couleur = (gene.getNoeud()==null)? 0 : (0xffffff-0x001100*(gene.getNoeud().degre_entrant()));
		this.gene = gene;
		couleur1 = gene.couleur1;
		couleur2 = gene.couleur2;
		this.rotation = gene.angle;
		this.s1 = gene.direct1;
		this.s2 = gene.direct2;
		this.o1 = gene.sens1;
		this.o2 = gene.sens2;
		int[] liens = gene.getIndex();
		this.chr1 = (char) liens[0];
		this.gen1 = (char) liens[1];
		this.chr2 = (char) liens[2];
		this.gen2 = (char) liens[3];
		actualiseCouleur();
		actualise();
	}
	
	private void actualiseCouleur() {
		color1 = new Color(couleur1);
		color1p = new Color(blanchir(couleur1,1));
		color2 = new Color(couleur2);
		color2p = new Color(blanchir(couleur2,1));
		color = new Color(couleur);
		colorp = new Color(blanchir(couleur,1));
		colorpp = new Color(blanchir(couleur1,3));
	}
	
	static int count = 0;
	public PanelGeneticienDivision(int indexChromosome){
		setOpaque(false);
		chr1 = chr2 = (char) indexChromosome;
		// Il y a des bug dans l'échange de position par drag&drop 
		/*
		JPanel panDrag = new JPanel();
		panDrag.setSize(new Dimension(30,30));
		panDrag.setLocation(30,30);
		add(panDrag);
		DragDrop.ajouterDragable(panDrag, "changer Position", this, this);
		*/
		couleur = Biomorph.alea.nextInt();
		couleur1 = Biomorph.alea.nextInt();
		couleur2 = Biomorph.alea.nextInt();
		actualiseCouleur();
		setPreferredSize(new Dimension(90,90));
		setSize(90,90);
		
		MouseAdapter mouseAdapter = new MouseAdapter(){
			private boolean drag;
			@Override
			public void mouseMoved(MouseEvent e){
				int x=e.getX(),y=e.getY();
				signature = mouseS1 == mouseS2 == mouseA == mouseO1 == mouseO2 ;
				if (!(mouseS1 =(x-xs1)*(x-xs1)+(y-ys)*(y-ys)<r1))
				if (!(mouseS2 =(x-xs2)*(x-xs2)+(y-ys)*(y-ys)<r2))
				if (!(mouseA =Math.abs((x-xc)*(x-xc)+(y-yc)*(y-yc)-r*r)<r*r/20
				|| Math.abs((x-xa)*(x-xa)+(y-ya)*(y-ya))<r*r/25))
				if (!(mouseO1 = Math.abs((x-xa)*(ya-yc)-(y-ya)*(xa-x1))<r1*3))
				mouseO2= Math.abs((x-xa)*(ya-yc)-(y-ya)*(xa-x2))<r2*3;
				if (mouseS1 || mouseS2 || mouseA) setCursor(handCursor);
				else setCursor(defaultCursor);
				if (signature != (mouseS1 == mouseS2 == mouseA == mouseO1 == mouseO2)) repaint();
			}
			@Override
			public void mousePressed(MouseEvent e){
				boolean changement = true;
				if (mouseS1) s1 = !s1;
				else if (mouseS2) s2 = !s2;
				else if (drag = mouseA) {
					int x=e.getX(),y=e.getY();
					rotation = (float) Math.atan2(y-yc, x-xc);
				} else  if (mouseO1) o1 = !o1;
				else if (mouseO2) o2 = !o2;
				else  changement = false;
				if (changement) modif();
			}
			public void mouseDragged(MouseEvent e){
				if (drag) {
					int x=e.getX(),y=e.getY();
					rotation = (float) Math.atan2(y-yc, x-xc);
					modif();
				}
			}
			@Override
			public void mouseReleased(MouseEvent e){
				drag = false;
			}
		};
		addMouseMotionListener(mouseAdapter);
		addMouseListener(mouseAdapter);
		actualise();
	}
	
	private void modif() {
		actualise();
		repaint();
		gene.reinit(chr1,chr2,gen1,gen2,rotation,o1,o2,s1,s2,couleur1,couleur2);
		if (gene.getNoeud() != null) {
			gene.finaliser(panGen.biomorph);
			gene.getNoeud().rafrechirOptimisation();
			((NoeudGene.Lien) panGen.biomorph.genotype.get(0,0)).getNoeud().remetVisite();
			panGen.construireBiomorph();
		}
	}
	
	private void actualise(){
		xc = getWidth()/2;
		yc = getHeight()/2;
		r = getWidth()/2-1;
		x1 = xc-r;
		x2 = xc+r;
		xa = xc + (int) (r*Math.cos(rotation));
		ya = yc + (int) (r*Math.sin(rotation));
		xs1 = (xa+x1)/2;
		ys = (ya+yc)/2;
		xs2 = (xa+x2)/2;
		r1 = ((xa-x1)*(xa-x1)+(ya-yc)*(ya-yc))/64;
		r2 = ((xa-x2)*(xa-x2)+(ya-yc)*(ya-yc))/64;
		sqr1 = (int) Math.sqrt(r1);
		sqr2 = (int) Math.sqrt(r2);
	}
	
	private BasicStroke lineStyle = new BasicStroke(3.0f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
	private BasicStroke lineStyle2 = new BasicStroke(0.5f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER, 10.0f, new float[]{6f,4f}, 0.0f);
	@Override 
	protected void paintChildren(Graphics g) {
		super.paintChildren(g);
		Graphics2D graph = (Graphics2D) g;
		graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		graph.setStroke(lineStyle);
		graph.setColor(colorpp);
		graph.fillArc(xc-r, yc-r, r*2, r*2, 0, 360);
		int angle2=(int) (((rotation<0)?90:-90)-180/3.14159*rotation/2),
		angle1=(int) (180/3.14159*(6.283-rotation/2));
		if (mouseS1) graph.setColor(color1p);
		else graph.setColor(color1);
		graph.drawArc(xs1-sqr1, ys-sqr1, 2*sqr1, 2*sqr1, angle1, (s1==o1)?-180:180);
		if (mouseS2) graph.setColor(color2p);
		else graph.setColor(color2);
		graph.drawArc(xs2-sqr2, ys-sqr2, 2*sqr2, 2*sqr2, angle2, (s2==o2)?-180:180);
		if (mouseO1) graph.setColor(color1p);
		else graph.setColor(color1);
		if (o1) dessineFleche(x1,yc,xa,ya,graph);
		else dessineFleche(xa,ya,x1,yc,graph);
		if (mouseO2) graph.setColor(color2p);
		else graph.setColor(color2);
		if (o2) dessineFleche(xa,ya,x2,yc,graph);
		else dessineFleche(x2,yc,xa,ya,graph);
		
		graph.setStroke(lineStyle2);
		if (mouseA) graph.setColor(colorp);
		else graph.setColor(color);
			
		graph.fillArc(xa-r/10, ya-r/10, r/5, r/5, 0, 360);
		graph.drawArc(x1, yc-r, 2*r, 2*r, 0, 360);
		dessineFleche(x1, yc, x2, yc, graph);
		graph.drawArc((5*x1+x2*3)/8, yc-r/4, r/2, r/2, 0, 180);
	}
	
	
	public static void dessineFleche(int x,int y,int xx,int yy,Graphics g){
		g.drawLine(x, y, (5*x+3*xx)/8, (5*y+3*yy)/8);
		g.drawLine((3*x+5*xx)/8, (3*y+5*yy)/8,xx, yy);
		int fx = (xx-x),fy = (yy-y);
		g.fillPolygon(new int[]{xx,xx-fx/4+fy/12,xx-fx/8,xx-fx/4-fy/12},
					new int[]{yy,yy-fy/4-fx/12,yy-fy/8,yy-fy/4+fx/12}, 4);
	}

	@Override
	public GeneDivisionLimite creerGene() {
		return new GeneDivisionLimite(chr1,chr2,gen1,gen2,rotation,o1,o2,s1,s2,couleur1,couleur2);
	}
	
}
