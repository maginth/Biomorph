package interfac.panel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.CubicCurve2D;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JPanel;

import biomorph.abstrait.Biomorph;
import biomorph.abstrait.Biomorph.BioGenerator;
import biomorph.forme2D.IconBiomorph2D;
import interfac.dragndrop.DragDrop;
import interfac.dragndrop.DropAdapter;
import interfac.global.Parametres;
import interfac.util.PopUpListener;

public class Genealogie extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int tailleIcone;
	private int positionY; 
	private LinkedList<Shape> formes;
	private Biomorph centre;
	
	
	// bouton permettant d'accéder au menu de génération de généalogie
	JButton etoile;
	
	public Genealogie(int largeur,int hauteur,int tailleIco) {
		this.setPreferredSize(new Dimension(largeur,hauteur));
		this.setSize(largeur,hauteur);
		this.tailleIcone = tailleIco;
		setLayout(null);
		setBackground(new Color(0xddddff));
		addComponentListener(new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent e){
				actualiser();
			}
		});
		DropAdapter centrerSurBio = new DropAdapter(this,"Afficher la Généalogie"){
			@Override
			public void mouseReleased(MouseEvent e){
				centrerSurBiomorph((Biomorph) DragDrop.getContenuDrag());
			}
		};
		DragDrop.ajouterRecepteur(this, centrerSurBio, new String[]{"bioPanel","bioLab"});
		addMouseWheelListener(new MouseWheelListener(){
			int count =0;
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				count++;
				if (count > 1) {
					double Xzoom = (e.getUnitsToScroll()<0)? 4d/3d : 3d/4d;
					if ((Xzoom>1 && tailleIcone<150) || (Xzoom<1 && tailleIcone>60)) {
						tailleIcone *=Xzoom;
						actualiser();
					}
					count = 0;
				}
			}
		});
		final GenerateurGenealogie gengen = new GenerateurGenealogie(this);
		etoile = new JButton("֍");
		etoile.setFont(new Font("Arial", Font.PLAIN, 30));
		etoile.setSize(58, 58);
		etoile.setLocation(15, 15);
		etoile.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				Container parent = getParent();
				parent.remove(Genealogie.this);
				parent.add(gengen);
			}
		});
		etoile.setToolTipText("générer un arbre généalogique aléatoire");
		add(etoile);
	}
	

	public void setTailleIcone(int tailleIcone) {
		this.tailleIcone = tailleIcone;
	}
	
	public void setPositionY(int y) {
		positionY = y;
	}
	
	private BasicStroke lineStyle = new BasicStroke(2.0f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
	@Override
	protected void paintChildren(Graphics g) {
		Graphics2D graph = (Graphics2D) g;
		graph.setStroke(lineStyle);
		graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		if (formes!=null)
			for (Shape s : formes) {
			graph.setColor(new Color(Biomorph.alea.nextInt()));
			graph.draw(s); 
		}
		super.paintChildren(g);
	}
	
	private int[] afficheAncetres(LinkedList<ArrayList<Biomorph>> niveau, int yNiveauEnfant,int tailleIco) {
		
		int[] nbEmplacements = new int[niveau.size()+1];
		int yNiveau = yNiveauEnfant-(int)(tailleIco*1.3);
		int yInterNiveau = yNiveauEnfant-(int)(tailleIco*0.9);
		int yInterNiveau2 = yNiveauEnfant+(int)(tailleIco*0.1);
		
		LinkedList<ArrayList<Biomorph>> niveauParents = new LinkedList<ArrayList<Biomorph>>();
		
		for (ArrayList<Biomorph> conjoints : niveau) 
			if (conjoints == null || conjoints.size() == 0) niveauParents.add(null);
			else for(Biomorph bio : conjoints) niveauParents.add(bio.getParents());
		tailleIco = Math.min(tailleIco, getWidth()/niveauParents.size());
		
		if (yNiveau<0 || tailleIco<16) {
			for (int i=0;i<=niveau.size();i++) nbEmplacements[i]=i;
		} else {
			
			int[] nbEmplacementsAncetres = afficheAncetres(niveauParents,yNiveau,(int) (tailleIco * 0.75));
			
			int index =0,indexEnfant=0;
			
			for (ArrayList<Biomorph> conjoints : niveau) {
				indexEnfant++;
				index+= (conjoints == null || conjoints.size() == 0)? 1 : conjoints.size();
				nbEmplacements[indexEnfant]=nbEmplacementsAncetres[index];
			}
			int totalEmplacments = nbEmplacements[nbEmplacements.length-1];
			int xIcone,xIconeEnfant;
			index =0;indexEnfant=0;
			
			for (ArrayList<Biomorph> conjoints : niveau) {
				indexEnfant++;
				xIconeEnfant = (nbEmplacements[indexEnfant]+nbEmplacements[indexEnfant-1])*getWidth()/totalEmplacments/2;
				if (conjoints == null || conjoints.size() == 0) index++;
				else for(Biomorph bio : conjoints) {
						index++;
						xIcone = (nbEmplacementsAncetres[index]+nbEmplacementsAncetres[index-1])*getWidth()/totalEmplacments/2;
						IconBiomorph2D icone = (IconBiomorph2D) bio.getIcone(tailleIco);
						icone.setLocation(xIcone-tailleIco/2, yNiveau + (tailleIco-icone.getHeight())/2);
						add(icone);
						DragDrop.ajouterDragable(icone, "bioGenealogie",icone.copie,bio);
						MouseListener popupListener = new PopUpListener(icone);
						icone.addMouseListener(popupListener);
						formes.add(new CubicCurve2D.Float(xIcone,yInterNiveau,xIcone,yInterNiveau2,xIconeEnfant,yInterNiveau,xIconeEnfant,yInterNiveau2));
					}
			}
		}
		return nbEmplacements;
	}
	
	
	
	private int[] afficheProgeniture(LinkedList<ArrayList<Biomorph>> niveau, int yNiveauParent,int tailleIco) {
		
		int[] nbEmplacements = new int[niveau.size()+1];
		int yNiveau = yNiveauParent+(int)(tailleIco*1.3);
		int yInterNiveau = yNiveauParent-(int)(tailleIco*0.1);
		int yInterNiveau2 = yNiveauParent-(int)(tailleIco*1.1);
		
		LinkedList<ArrayList<Biomorph>> niveauEnfants = new LinkedList<ArrayList<Biomorph>>();
		for (ArrayList<Biomorph> progeniture : niveau) 
			if (progeniture == null || progeniture.size() == 0) niveauEnfants.add(null);
			else for(Biomorph bio : progeniture) niveauEnfants.add(bio.getEnfants());
		
		tailleIco = Math.min(tailleIco, getWidth()/niveauEnfants.size());
		
		if (yNiveau>getHeight() || tailleIco<16) {
			for (int i=0;i<=niveau.size();i++) nbEmplacements[i]=i;
		} else {
			
			

			int[] nbEmplacementsEnfants = afficheProgeniture(niveauEnfants,yNiveau,(int) (tailleIco * 0.6));
			
			int index =0,indexEnfant=0;
			
			for (ArrayList<Biomorph> progeniture : niveau) {
				index++;
				indexEnfant+= (progeniture == null || progeniture.size() == 0)? 1 : progeniture.size();
				nbEmplacements[index]=nbEmplacementsEnfants[indexEnfant];
			}
			int totalEmplacments = nbEmplacements[nbEmplacements.length-1];
			int xIcone,xIconeEnfant;
			index =0;indexEnfant=0;
			
			for (ArrayList<Biomorph> progeniture : niveau) {
				index++;
				xIcone = (nbEmplacements[index]+nbEmplacements[index-1])*getWidth()/totalEmplacments/2;
				if (progeniture == null || progeniture.size() == 0) indexEnfant++;
				else for(Biomorph bio : progeniture) {
						indexEnfant++;
						xIconeEnfant = (nbEmplacementsEnfants[indexEnfant]+nbEmplacementsEnfants[indexEnfant-1])*getWidth()/totalEmplacments/2;
						IconBiomorph2D icone = (IconBiomorph2D) bio.getIcone(tailleIco);
						icone.setLocation(xIconeEnfant-tailleIco/2, yNiveauParent + (tailleIco-icone.getHeight())/2);
						add(icone);
						DragDrop.ajouterDragable(icone, "bioGenealogie",icone.copie,bio);
						MouseListener popupListener = new PopUpListener(icone);
						icone.addMouseListener(popupListener);
						formes.add(new CubicCurve2D.Float(xIconeEnfant,yInterNiveau,xIconeEnfant,yInterNiveau2,xIcone,yInterNiveau,xIcone,yInterNiveau2));
					}
			}
		}
		return nbEmplacements;
	}


	public void centrerSurBiomorph(Biomorph biomorph) {
		centre = biomorph;
		actualiser();
	}
	

	public void actualiser() {
		formes = new LinkedList<Shape>();
		for (Component ico : getComponents()) 
			if (ico instanceof IconBiomorph2D) {
				remove(ico);
				((IconBiomorph2D) ico).supprimer();
			}
		positionY = (getHeight()-tailleIcone)/2;
		formes = new LinkedList<Shape>();
		LinkedList<ArrayList<Biomorph>> niveau0= new LinkedList<ArrayList<Biomorph>>();
		niveau0.add(centre.getParents());
		//affiche la partie supérieur de l'arbre généalogique (les ancètres du biomorph)
		if (centre.getParents().size()>0) {
			afficheAncetres(niveau0,positionY,Math.min(tailleIcone,getWidth()/centre.getParents().size()));
		}
		//affiche le biomorph 
		IconBiomorph2D icone = (IconBiomorph2D) centre.getIcone(tailleIcone);
		icone.setLocation(getWidth()/2-tailleIcone/2, positionY+ (tailleIcone-icone.getHeight())/2);
		add(icone);
		DragDrop.ajouterDragable(icone, "bioGenealogie",icone.copie,centre);
		MouseListener popupListener = new PopUpListener(icone);
		icone.addMouseListener(popupListener);
		//affiche la partie inférieur de l'arbre généalogique (les enfants du biomorph)
		if (centre.getEnfants().size()>0) {
			niveau0.set(0, centre.getEnfants());
			afficheProgeniture(niveau0,(int) (positionY+tailleIcone*1.3),Math.min(tailleIcone,getWidth()/centre.getEnfants().size()));
		}
		revalidate();
		repaint(100);
	}
	/*
	 * 
	 * 			FONCTIONS DE GENERATION D'UNE GENEALOGIE ALEATOIRE
	 * 
	 */
	
	public void genealogieAleatoire(BioGenerator rand){
		Biomorph centre = genererParent(1,rand);
		genererEnfants(1,centre,rand);
		centrerSurBiomorph(centre);
	}
	
	
	private static Biomorph genererParent(int niveau,BioGenerator rand) {
		Biomorph res;
		if (Biomorph.alea.nextFloat()*niveau<1 && niveau <5) res = genererParent(niveau+1,rand).croisement(genererParent(niveau+1,rand), Parametres.X);
		else res = rand.biomorphAleatoire();
		return res;
	}
	
	private static void genererEnfants(int niveau,Biomorph bio,BioGenerator rand) {
		while (Biomorph.alea.nextFloat()*niveau<0.8 && niveau <5) {
			genererEnfants(niveau+1,bio.croisement(rand.biomorphAleatoire(), Parametres.X), rand);
		}
	}
	
}
