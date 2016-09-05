package interfac.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.BorderUIResource;

import biomorph.forme2D.Biomorph2D;
import biomorph.forme2D.GeneDivisionLimite;
import biomorph.forme2D.IconBiomorph2D;
import biomorph.forme2D.Similitude;
import interfac.dragndrop.DragDrop;
import interfac.dragndrop.DropAdapter;
import interfac.global.AppletBiomorph;
import interfac.global.Parametres;
import interfac.util.ImageAccessible;
import interfac.util.PopUpListener;


public class PanelLaboratoire extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 21L;
	protected float xVue=0,yVue=0;
	protected double zoom=1;
	public static final float maxZoom =1000001, minZoom = 0.099f;
	public static final double coefZoom = Math.pow(10,1d/16);
	private  LinkedList<IconLaboratoire> listeIco;
	private  LinkedList<IconLaboratoire> listeIcoSelection = new LinkedList<IconLaboratoire>();

	protected ImageAccessible arrierePlan; 
	private final JLabel arrierePlanComponent;
	private final ImageIcon arrierePlanIcon;
	private static final Pattern zoomRegex = Pattern.compile("(0\\.0*[1-9]{1,2})|(.\\.[^0])|([1-9][0-9]*)");
	JPanel cadreSelection; 
	
	public PanelLaboratoire() {
		
		// désactive le positionnement automatique des icones
		setLayout(null);
		
		// création du cadre de sélection des biomorphs
		cadreSelection= new JPanel();
		cadreSelection.setOpaque(false);
		cadreSelection.setBorder(new BorderUIResource.LineBorderUIResource(Color.ORANGE));
		add(cadreSelection);
		
		
		
		
		listeIco = new LinkedList<IconLaboratoire>();
		arrierePlanIcon = new ImageIcon();
		redimension();
		arrierePlanComponent = new JLabel(arrierePlanIcon);
		add(arrierePlanComponent);
		DragDrop.ajouterRecepteur(this,lablab,new String[]{"bioLab"});
		DragDrop.ajouterRecepteur(this,labDrop,new String[]{"bioGenealogie","bioPanel"});
		
		
		addComponentListener(new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent e){
				redimension();
			}
		});
		addMouseListener(vueLab);
		addMouseMotionListener(vueLab);
		addMouseWheelListener(new MouseWheelListener(){
			int count =0;
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				count++;
				if (count > 1) {
					Point p = getMousePosition();
					double Xzoom= (e.getUnitsToScroll()<0)? coefZoom : (1/coefZoom);
					if (zoom *Xzoom >maxZoom || zoom *Xzoom<minZoom) return;
					
					xVue = (float) (((Xzoom-1)*(p.x/zoom+xVue)+xVue)/Xzoom);
					yVue = (float) (((Xzoom-1)*(p.y/zoom+yVue)+yVue)/Xzoom);
					zoom *= Xzoom;
					actualiserVue();
					count = 0;
					Matcher match = zoomRegex.matcher(Double.toString(zoom*1.0000001d));
					match.find();
					System.out.println("ZOOM "+match.group());
				}
			}
		});
	}

	private void redimension() {
		if (arrierePlan == null || getWidth()>arrierePlan.width || getHeight()>arrierePlan.height) {
			if (getWidth()>100 && getHeight()>100) {
				if (arrierePlan != null) arrierePlan.delet();
				arrierePlan = new ImageAccessible(getWidth()+100,getHeight()+100);
				arrierePlanIcon.setImage(arrierePlan.getImageAWT());
				arrierePlanComponent.setSize(getWidth()+100,getHeight()+100);
			}
		}
	}
	DropAdapter labDrop = new DropAdapter(this,"Ajouter au laboratoire"){
		@Override
		public void mouseReleased(MouseEvent e){
			Point p = getMousePosition();
			if (p != null) {
				addBiomorph((Biomorph2D) DragDrop.getContenuDrag(), (int) (p.x/zoom+xVue), (int) (p.y/zoom+yVue),200);
			}
		}
	};
	
	public IconLaboratoire addBiomorph(final Biomorph2D bio,int x,int y,int taille) {
		final IconLaboratoire ico = new IconLaboratoire(bio,200,this);
		PopUpListener popupListener = new PopUpListener(ico);
		
		ico.addMouseListener(popupListener);
		
		DragDrop.ajouterDragable(ico, "bioLab", ico, ico.getBiomorph());
		DragDrop.ajouterRecepteur(ico, reproductionLab, new String[]{"bioLab"});
		ico.x = x-taille/2;
		ico.y = y-taille/2;
		listeIco.addFirst(ico);
		add(ico,0);
		drawBiomorphIcon(ico);
		return ico;
	}
	
	public void showHackPanel(IconBiomorph2D icone){
		AppletBiomorph.panelGenome.bricolerGenome(icone.getBiomorph());
		AppletBiomorph.open_gen_hack();
		AppletBiomorph.panelGenome.actualiser();
	}
	
	private void setScreenIcone(IconLaboratoire ico,Point positionScreen){
		SwingUtilities.convertPointFromScreen(positionScreen,PanelLaboratoire.this);
		ico.x =(int) (positionScreen.x/zoom+xVue);
		ico.y =(int) (positionScreen.y/zoom+yVue);
	}
	
	MouseAdapter lablab = new MouseAdapter(){
		@Override
		public void mousePressed(MouseEvent e) {
			actualiserVue();
		}
		@Override
		public void mouseReleased(MouseEvent e){
			for (Component ico : DragDrop.getListDrag())
				setScreenIcone((IconLaboratoire) ico,DragDrop.positionFin(ico));
		}
		@Override
		public void mouseClicked(MouseEvent e){
			GeneDivisionLimite.testRect = true;
			if (DragDrop.getListDrag() != null)
				for (Component ico : DragDrop.getListDrag())
					drawBiomorphIcon((IconLaboratoire) ico);
		}
	};
	
	MouseAdapter reproductionLab = new MouseAdapter(){
		@Override
		public void mouseReleased(MouseEvent e){
			setScreenIcone((IconLaboratoire) e.getComponent(),DragDrop.positionFin(e.getComponent()));
			drawBiomorphIcon((IconLaboratoire) e.getComponent());
			IconLaboratoire cible = (IconLaboratoire) DragDrop.focusFin();
			IconLaboratoire source = (IconLaboratoire) DragDrop.getSourceDrag();
			if (Math.abs(cible.x-source.x)<20 && Math.abs(cible.y-source.y)<100) {
				Biomorph2D parent1 = source.getBiomorph();
				Biomorph2D parent2 = cible.getBiomorph();
				double tour=1;
				for (int i=0;i<Parametres.nombreEnfant;i++) {
					tour += 0.15/tour;
					Biomorph2D enfant = parent1.croisement(parent2, Parametres.X);
					addBiomorph(enfant, (int) (cible.x+(100+tour*200)*Math.cos(2*Math.PI*tour)),
										(int) (cible.y+(100+tour*200)*Math.sin(2*Math.PI*tour)), 0);
				}
			}
		}
	};
	
	
	MouseAdapter vueLab = new MouseAdapter(){
		private Point p0;
		private float xVue0,yVue0;
		private int x0,y0;
		
		@Override
		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				p0 = e.getLocationOnScreen();
				xVue0=xVue;yVue0=yVue;
			} else if(SwingUtilities.isRightMouseButton(e)){
				deselectTout();
				x0 = e.getX(); y0 = e.getY();
				cadreSelection.setLocation(x0,y0);
				cadreSelection.setVisible(true);
				cadreSelection.setSize(0,0);
			}
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				Point p = e.getLocationOnScreen();
				xVue = (float) (xVue0 - (p.x-p0.x)/zoom);
				yVue = (float) (yVue0 - (p.y-p0.y)/zoom);
				actualiserVue();
			} else if(SwingUtilities.isRightMouseButton(e)){
				deselectTout();
				int dx=e.getX()-x0,dy=e.getY()-y0,xx0=x0,yy0=y0;
				if (dx<0) {
					xx0 = x0+dx;
					dx = -dx;
				}
				if (dy<0) {
					yy0 = y0+dy;
					dy = -dy;
				}
				for (IconLaboratoire ico : listeIco) {
					if (xx0<ico.getX() && ico.getX()<xx0+dx && yy0<ico.getY() && ico.getY()<yy0+dy)
						ico.select();
				}
				cadreSelection.setSize(dx,dy);
				cadreSelection.setLocation(xx0,yy0);
			}
		}
		@Override
		public void mouseReleased(MouseEvent e){
			AppletBiomorph.app.requestFocusInWindow();
			cadreSelection.setVisible(false);
		}
	};
	
	public void drawBiomorphIcon(IconLaboratoire ico) {
		int xscreenIco = (int) ((ico.x-xVue)*zoom), yscreenIco = (int) ((ico.y-yVue)*zoom);
		ico.setLocation(xscreenIco,yscreenIco);
		ico.changerTaille((int) (ico.taille*zoom));
		if (ico.largeur()>IconBiomorph2D.maxTailleIcon) {
			ico.setVisible(false);
		} else ico.setVisible(true);
		ico.getBiomorph().dessine(new Similitude(xscreenIco+ico.largeur()/2,yscreenIco+ico.hauteur()/2,ico.largeur(),0,true,0),arrierePlan);
	}
	
	public void actualiserVue() {
				
		int[] pixels = arrierePlan.getAccessPixels();
		for (int i=0;i<pixels.length;i++) pixels[i] = 0;

		GeneDivisionLimite.testRect = true;
		GeneDivisionLimite.xm = 0;
		GeneDivisionLimite.xM = getWidth();
		GeneDivisionLimite.ym = 0;
		GeneDivisionLimite.yM = getHeight();
		for (IconLaboratoire ico : listeIco) {
			if (DragDrop.getListDrag() == null || !DragDrop.getListDrag().contains(ico))
				drawBiomorphIcon(ico);
		}
		arrierePlanComponent.repaint();
		/*int i,j,jh,jj,zp = (int) (10/zoom);
		int[] pixels = arrierePlan.getAccessPixels();
		for (j=0;j<arrierePlan.height;j++) {
			jh = arrierePlan.width*j;
			jj = j*zp*zp;
			for (i=0;i<arrierePlan.width;i++) 
				pixels[i+jh] = 0xffc0c0c0 | (i*jj);
		}*/
	}
	
	public void videSelection() {
		while (listeIcoSelection.size()>0) listeIcoSelection.get(listeIcoSelection.size()-1).supprimer();
		actualiserVue();
	}
	public void deselectTout() {
		while (listeIcoSelection.size()>0) listeIcoSelection.get(listeIcoSelection.size()-1).deselect();
	}
	
	public LinkedList<IconLaboratoire> getListeIco(){
		return listeIco;
	}
	public LinkedList<IconLaboratoire> getListeIcoSelection(){
		return listeIcoSelection;
	}
	
	
	
}
