package biomorph.forme2D;

import interfac.dragndrop.DragDrop;
import interfac.util.ImageAccessible;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;

/**
 * Construit un icone o� est dessin� un biomorph avec son nom en dessous
 * 
 * Les icones ne peuvent d�passer la taille "maxTailleIcon" affin de sauver 
 * de la ram.
 * 
 * @author Mathieu Guinin
 *
 */
public class IconBiomorph2D extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static int totalMem = 0;
	private static int memUsed = 0;
	private static final int maxMem = 2000000;
	
	public static final double minTailleIcon = 10, maxTailleIcon = 300;
	public static final int tailleAfficheText = 40; // en dessous le nom n'est pas affich�
	
	/*
	 *  les pixels des images des icones sont recycl� pour de nouveau icones
	 *  (le garbage collector semble ne pas y arriver)
	 */
	static final LinkedList<int[]> pixelsDispo = new LinkedList<int[]>();
	static final LinkedList<IconBiomorph2D> pixelsUser = new LinkedList<IconBiomorph2D>();
	
	private static FlowLayout flow;
	static {
		flow = new FlowLayout();
		flow.setVgap(0);
	}
	private static Border border = new BorderUIResource.LineBorderUIResource(Color.black);

	protected Biomorph2D biomorph;
	public final JLabel imageLabel,copie;
	private final JTextArea jtext;
	
	// hxl = largeur fois hauteur = nombre de pixels mininum de l'image
	protected int largeur,hauteur,hxl;
	float scaleY=1;
	protected final ImageIcon imageIcon = new ImageIcon();
	protected int[] pixels;
	protected ImageAccessible image; 

	protected boolean select=false;
	LinkedList<IconBiomorph2D> listeSelect;
	
	public IconBiomorph2D(Biomorph2D biomorph,int large,LinkedList<IconBiomorph2D> listeSelect) {
		this(biomorph,large);
		this.listeSelect = listeSelect;
		addMouseListener(new MouseAdapter(){
			int xs=0,ys=0;
			@Override
			public void mouseReleased(MouseEvent e) {
				int dx = e.getXOnScreen()-xs, dy = e.getYOnScreen()-ys;
				// si un drag involontaire de 20 pixels � �t� fait on le consid�re comme un click (20*20=400)
				if(SwingUtilities.isLeftMouseButton(e) &&  dx*dx+dy*dy<400 ) {
					if(select){
						deselect();
					}else{
						select();
					}
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					xs = e.getXOnScreen(); ys = e.getYOnScreen();
				}
			}
		});
	}
	
	
	public IconBiomorph2D(Biomorph2D biomorph,int large) {
		this.biomorph = biomorph;
		if (biomorph.liste_icones == null) biomorph.liste_icones = new ArrayList<IconBiomorph2D>();
		biomorph.liste_icones.add(this);
		
		if (biomorph.normalisation !=null) this.scaleY = biomorph.scaleY;
		//***************************
		//long t = System.currentTimeMillis();

		jtext = new JTextArea(biomorph.getName());
		jtext.setFont(jtext.getFont().deriveFont(1,9f));
		
		
		biomorph.associerNom(jtext);
		copie = new JLabel(imageIcon);
		imageLabel= new JLabel(imageIcon);
		changerTaille(large);
		add(imageLabel);
		
		setLayout(flow);
		setOpaque(false);

		add(jtext);
		
		
		
		//*****************************
		//System.out.println("cr�ation icone :\n\tBiomorph :"+biomorph+"\n\tlargeur :"+largeur+" pixels\n\tdur�e :"+(System.currentTimeMillis()-t)+" ms\n\t");
	}
	
	/**
	 * Change la largeur de l'icone
	 * @param large largeur de l'icone 
	 */
	public void changerTaille(int large) {
		if (large != largeur) {
			largeur = large;
			initTaille();
		}
	}
	
	private void initTaille() {
		freeMem();
		hauteur = (int) (scaleY*largeur);
		setSize(largeur,hauteur+jtext.getPreferredSize().height);
		copie.setSize(largeur,hauteur);
		setPreferredSize(new Dimension(largeur,hauteur+jtext.getPreferredSize().height));
		if (largeur<tailleAfficheText) jtext.setVisible(false) ;
		else jtext.setVisible(true) ;
	}
	
	/**
	 * Ajoute les pixels de l'image � la liste des pixels � recycler
	 */
	public void freeMem() {
		if (pixels != null) {
			memUsed -= pixels.length;
			for (int i=0;i<hxl;i++) pixels[i] = 0;
			int k = 0;
			for (int[] i : pixelsDispo) {
				if (pixels.length<=i.length) {
					pixelsDispo.add(k,pixels);
					pixels = null;
					break;
				}
				k++;
			}
			if (pixels != null) pixelsDispo.addLast(pixels);
			pixelsUser.remove(this);
			pixels = null;
		}
	}
	
	@Override public void paint(Graphics g) {
		if (largeur<maxTailleIcon) {
			if (biomorph.normalisation ==null) {
				biomorph.normaliser();
				freeMem();
			}
			if (biomorph.scaleY != scaleY) this.scaleY = biomorph.scaleY;
			
			if (pixels == null) {
				initTaille();
				hxl = hauteur*largeur;
				for (int[] i : pixelsDispo) {
					if (i.length>=hxl) {
						if (i.length<hxl*3*2) {
							pixels = i;
							pixelsDispo.remove(i);
							break;
						}
					}
				}
				if (pixels==null) {
					totalMem += hxl;
					pixels = new int[hxl] ;
					System.out.println("totalMem  "+totalMem);
				}
				memUsed += pixels.length;
				pixelsUser.addLast(this);
				
				while (memUsed>maxMem) pixelsUser.getFirst().freeMem();
				
				if (largeur>0 && hauteur>0) {
					image = new ImageAccessible(largeur,hauteur,pixels);
					GeneDivisionLimite.testRect = false;
					biomorph.dessine(new Similitude(largeur/2,hauteur/2,largeur,0,true,0),image);
					imageIcon.setImage(image.getImageAWT());
					invalidate();
					validate();
				}
			}
			super.paint(g);
		}
	}
	public void deselect(){
		if (listeSelect != null) {
			setBorder(null);
			listeSelect.remove(this);
			select = false;
			DragDrop.changerDragDefaut(this);
		}
	}
	
	public void select(){
		if (listeSelect != null) {
			setBorder(border);
			listeSelect.add(this);
			select = true;
			DragDrop.changerObjetsDrag(this,listeSelect);
		}
	}
	
	
	public Biomorph2D getBiomorph() {
		return biomorph;
	}
	
	public void supprimer() {
		freeMem();
		if (image != null) image.delet();
		System.out.println(biomorph);
		System.out.println(biomorph.liste_icones);
		biomorph.liste_icones.remove(this);
		biomorph = null;
	}
	
	public int hauteur() {
		return hauteur;
	}
	public int largeur() {
		return largeur;
	}
}
