package biomorph.forme2D;



import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import biomorph.abstrait.Biomorph;
import biomorph.abstrait.NoeudGene;
import biomorph.forme2D.PanelGenome.ChromPanel;
import interfac.util.ColorPicker;
import interfac.util.Curs;


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
	private Color disk_color_over,disk_color,left_link_color,left_link_color_over,right_link_color,right_link_color_over;
	private int couleur1,couleur2;
	private char chr1,chr2,gen1,gen2;
	private float rotation = 1.5f;
	private boolean s1,s2,o1,o2;
	GeneDivisionLimite gene;
	public Color color_request = null;
	public boolean moveFocus_request = false;
	
	/*
	 * xa,ya : coordonné du point sur le cercle 
	 * xc,yc : coordonné du centre du cercle
	 * x1,yc et x1,yc : coordonné des deux extrémités du diamètre du cercle
	 * xs1,ys et xs2,ys : coordonné du centre des deux flèches
	 */
	private int x1,x2,xs1,xs2,ys,xa,ya,xc,yc,r;
	private boolean mouseS1,mouseS2,mouseO1,mouseO2,signature;
	private int hemisphere_link = -1; //0 -> left, 1 -> right, other -> neither
	private int r1,r2,sqr1,sqr2;
	public static final PanelGeneticienDivision noLink = new PanelGeneticienDivision(new GeneDivisionLimite('x', 'x', 'x', 'x', 0.0f, false, false, false, false, 0, 0 ));
	PanelGeneticienDivision panLink[];
	
	
	
	public PanelGeneticienDivision(GeneDivisionLimite gene){
		this(gene.getIndex()[0]);
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
	
	public void requestColor(Color c) {
		if (c == color_request) return;
		color_request = c;
		repaint();
	}
	
	private void actualiseCouleur() {
		int degre_entrant = (gene.getNoeud()==null)? 0 : gene.getNoeud().degre_entrant();
		left_link_color = new Color(couleur1);
		left_link_color_over = new Color(blanchir(couleur1,1));
		right_link_color = new Color(couleur2);
		right_link_color_over = new Color(blanchir(couleur2,1));
		if (gene == null || gene.getNoeud() == null || degre_entrant == 0) {
			disk_color = Color.DARK_GRAY;
			disk_color_over = Color.GRAY;
		} else {
			couleur = blanchir(0x102110, degre_entrant);
			disk_color = new Color(blanchir(couleur,0));
			disk_color_over = new Color(blanchir(couleur, 1));
		}
	}

	int y_shift = 0;
	void verticalShift(PanelGeneticienDivision pan, int dy) {
		if (pan == this)
			return;
		Point p = pan.getLocation();
		p.y += dy - pan.y_shift;
		pan.y_shift = dy;
		pan.setLocation(p);
	}
	static int count = 0;
	static boolean drag = false;
	static MouseAdapter inside;
	static PanelGeneticienDivision inside_pan; 
	static PanelGeneticienDivision moveFocus_pan;
	static MouseEvent lastEvent;
	static JPanel overlay = new JPanel();
	static {
		overlay.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e))
					ColorPicker.colorPicker.callback.rightClick();
			}
		});
		overlay.setOpaque(false);;
	}
	
	
	public PanelGeneticienDivision(int indexChromosome){
		setOpaque(false);
		chr1 = chr2 = (char) indexChromosome;
		couleur = Biomorph.alea.nextInt();
		couleur1 = Biomorph.alea.nextInt();
		couleur2 = Biomorph.alea.nextInt();
		setPreferredSize(new Dimension(90,90));
		setSize(90,90);
		
		final MouseAdapter mouseAdapter = new MouseAdapter(){
			private boolean is_up;
		
			@Override
			public void mouseMoved(MouseEvent e){
				lastEvent = e;
				int x=e.getX(),y=e.getY();
				signature = mouseS1 == mouseS2 == mouseO1 == mouseO2;
				if (!(mouseS1 =(x-xs1)*(x-xs1)+(y-ys)*(y-ys)<r1))
				if (!(mouseS2 =(x-xs2)*(x-xs2)+(y-ys)*(y-ys)<r2))
				if (!(mouseO1 = Math.abs((x-xa)*(ya-yc)-(y-ya)*(xa-x1))<r1*3))
				mouseO2= Math.abs((x-xa)*(ya-yc)-(y-ya)*(xa-x2))<r2*3;
				int hem = hemisphere_link;
				hemisphere_link = x > 0 && x < 2* r ? x < r ? 0 : 1 : -1;
				if (mouseS1 || mouseS2 || mouseO1 || mouseO2) setCursor(Curs.hand);
				else setCursor(Curs.pointer);
				if (signature != (mouseS1 == mouseS2 == mouseO1 == mouseO2) || hem != hemisphere_link)
					repaint();
				if (hem != hemisphere_link) {
					if (moveFocus_pan != null) {
						verticalShift(moveFocus_pan, 0);
						moveFocus_pan.requestMoveFocus(false);
					}
					if (hemisphere_link != -1) {
						panLink[hemisphere_link].requestMoveFocus(true);
						verticalShift(panLink[hemisphere_link], -30);
						verticalShift(panLink[1-hemisphere_link], -25);
					}
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				inside = this;
				inside_pan = PanelGeneticienDivision.this;
				if (drag || is_up)
					return;
				panLink[0].requestColor(left_link_color);
				panLink[1].requestColor(right_link_color);
				verticalShift(panLink[0], -25);
				verticalShift(panLink[1], -25);
				is_up = true;
			}
			@Override
			public void mouseExited(MouseEvent e) {
				if (inside == this)
					{inside = null; inside_pan = null;}
				if (drag || !is_up)
					return;
				hemisphere_link = -1;
				panLink[0].requestColor(null);
				panLink[1].requestColor(null);
				verticalShift(panLink[0], 0);
				verticalShift(panLink[1], 0);
				is_up = false;
			} 
			@Override
			public void mousePressed(final MouseEvent e){
				if (SwingUtilities.isRightMouseButton(e) && hemisphere_link != -1) {
					ColorPicker.colorPicker.setLocation(getX() - 19, getY() - 19);
					ColorPicker.colorPicker.centerColor(hemisphere_link == 0 ? couleur1 : couleur2);
					if (ColorPicker.colorPicker.callback != null) {
						ColorPicker.colorPicker.callback.rightClick();
					}
					ColorPicker.colorPicker.callback = new ColorPicker.Callback() {
						@Override
						public void changeColor(Color color) {
							if (hemisphere_link == 0) {
								couleur1 = color.getRGB();
							} else {
								couleur2 = color.getRGB();
							}
							modif();
							actualiseCouleur();
							panLink[hemisphere_link].requestColor(hemisphere_link == 0 ? left_link_color : right_link_color);
						}
						@Override
						public void rightClick() {
							is_up = true;
							ColorPicker.colorPicker.callback = null;
							mouseExited(e);
							panGen.remove(ColorPicker.colorPicker);
							panGen.remove(overlay);
							panGen.repaint();
						}
					};
					is_up = false;
					panGen.add(ColorPicker.colorPicker,0);
					overlay.setSize(panGen.getWidth(), panGen.getHeight());
					panGen.add(overlay, 1);
					panGen.repaint();
					return;
				}
				boolean changement = true;
				if (mouseS1) s1 = !s1;
				else if (mouseS2) s2 = !s2;
				else  if (mouseO1) o1 = !o1;
				else if (mouseO2) o2 = !o2;
				else {
					drag = true;
					int x=e.getX(),y=e.getY();
					rotation = (float) Math.atan2(y-yc, x-xc);
					changement = false;
				} 
				if (changement) modif();
			}
			@Override
			public void mouseDragged(MouseEvent e){
				int x=e.getX(),y=e.getY();
				rotation = (float) Math.atan2(y-yc, x-xc);
				modif();
			}
			@Override
			public void mouseReleased(MouseEvent e){
				if (drag) {
					drag = false;
					if (inside != this) {
						mouseExited(e);
						if (inside != null)
							inside.mouseEntered(e);
					}
				}
			}
		};
		addMouseMotionListener(mouseAdapter);
		addMouseListener(mouseAdapter);
		addMouseWheelListener(new MouseWheelListener(){
			int count =0;
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				count++;
				if (count > 1) {
					if (hemisphere_link != -1) {
						int hem = hemisphere_link;
						mouseAdapter.mouseExited(lastEvent);
						boolean is1 = hem == 0;
						if (hem == 0)
							gen1 += e.getUnitsToScroll() < 0 ? 1 : -1;
						else
							gen2 += e.getUnitsToScroll() < 0 ? 1 : -1;
						modif();
						panLink[hem] = getPanAt(is1 ? chr1 : chr2, is1 ? gen1 : gen2);
						mouseAdapter.mouseEntered(lastEvent);
						mouseAdapter.mouseMoved(lastEvent);
						for (ChromPanel chrom : panGen.listeChrom)
							for (PanelGeneticien pan : chrom.listeGene)
								if (pan instanceof PanelGeneticienDivision)
									((PanelGeneticienDivision)pan).actualiseCouleur();
					}
					count = 0;
				}
			}
		});
		actualise();
	}
	
	protected void requestMoveFocus(boolean b) {
		moveFocus_request = b;
		moveFocus_pan = b ? this : null;
		repaint();
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

	private static BasicStroke lineStyle = new BasicStroke(3.0f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
	private static BasicStroke lineStyle3 = new BasicStroke(10.0f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
	private static BasicStroke lineStyle2 = new BasicStroke(0.5f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER, 10.0f, new float[]{6f,4f}, 0.0f);
	
	PanelGeneticienDivision getPanAt(char ichrom, char igene) {
		ChromPanel list = panGen.listeChrom.get(ichrom % panGen.listeChrom.size());
		PanelGeneticien res = list.listeGene.get(igene % list.listeGene.size());
		return res instanceof PanelGeneticienDivision ? (PanelGeneticienDivision)res : noLink;
	}
	
	@Override 
	protected void paintChildren(Graphics g) {
		super.paintChildren(g);
		Graphics2D graph = (Graphics2D) g;
		graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		graph.setColor(disk_color);
		graph.fillArc(xc-r, yc-r, r*2, r*2, 0, 360);
		if (hemisphere_link != -1) {
			graph.setColor(disk_color_over);
			graph.fillArc(xc-r, yc-r, r*2, r*2, hemisphere_link == 0 ? 90 : -90, 180);
		}
		graph.setColor(Color.black);

		if (color_request != null) {
			graph.setStroke(lineStyle3);
			graph.setColor(color_request);
			graph.drawArc(xc-r+4, yc-r+4, r*2-9, r*2-9, 0, 360);
			if (moveFocus_request) {
				dessineTeteFleche(r/3, r, -r/2, 0,  g);
				dessineTeteFleche(5*r/3, r, r/2, 0, g);
			}
			graph.setStroke(lineStyle);
		}
		graph.setStroke(lineStyle2);
		dessineFleche(x1, yc, x2, yc, graph);
		graph.drawArc((5*x1+x2*3)/8, yc-r/4, r/2, r/2, 0, 180);
		graph.setStroke(lineStyle);
		int angle2=(int) (((rotation<0)?90:-90)-180/3.14159*rotation/2),
		angle1=(int) (180/3.14159*(6.283-rotation/2));
		if (mouseS1) graph.setColor(left_link_color_over);
		else graph.setColor(left_link_color);
		graph.drawArc(xs1-sqr1, ys-sqr1, 2*sqr1, 2*sqr1, angle1, (s1==o1)?-180:180);
		if (mouseS2) graph.setColor(right_link_color_over);
		else graph.setColor(right_link_color);
		graph.drawArc(xs2-sqr2, ys-sqr2, 2*sqr2, 2*sqr2, angle2, (s2==o2)?-180:180);
		if (mouseO1) graph.setColor(left_link_color_over);
		else graph.setColor(left_link_color);
		if (o1) dessineFleche(x1,yc,xa,ya,graph);
		else dessineFleche(xa,ya,x1,yc,graph);
		if (mouseO2) graph.setColor(right_link_color_over);
		else graph.setColor(right_link_color);
		if (o2) dessineFleche(xa,ya,x2,yc,graph);
		else dessineFleche(x2,yc,xa,ya,graph);
	}
	
	
	public static void dessineFleche(int x,int y,int xx,int yy,Graphics g){
		g.drawLine(x, y, (5*x+3*xx)/8, (5*y+3*yy)/8);
		g.drawLine((3*x+5*xx)/8, (3*y+5*yy)/8,xx, yy);
		dessineTeteFleche(xx, yy, (xx-x) / 4, (yy-y) / 4, g);
	}
	
	public static void dessineTeteFleche(int x,int y, int fx,int fy, Graphics g){
		g.fillPolygon(new int[]{x,x-fx+fy/3,x-fx/2,x-fx-fy/3},
					new int[]{y,y-fy-fx/3,y-fy/2,y-fy+fx/3}, 4);
	}

	@Override
	public GeneDivisionLimite creerGene() {
		return new GeneDivisionLimite(chr1,chr2,gen1,gen2,rotation,o1,o2,s1,s2,couleur1,couleur2);
	}
	
}
