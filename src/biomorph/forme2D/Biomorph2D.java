package biomorph.forme2D;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;

import biomorph.abstrait.Biomorph;
import biomorph.abstrait.GeneExpression;
import biomorph.abstrait.Genotype;
import biomorph.abstrait.NoeudGene;
import biomorph.abstrait.NoeudGene.Lien;
import biomorph.abstrait.Transform;
import interfac.util.ImageAccessible;


/**
 * 
 * Cette classe rassemble les objets graphiques (bmp et graph) sur lesquels
 * les gènes vont agir pour dessiner le biomorph2D.
 * 
 *@see Biomorph
 *@author Mathieu
 */

public class Biomorph2D extends Biomorph implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	/*
	 * 		ATTRIBUTS GRAPHIQUES NECESSAIRE A L'EXPRESSION DES GENES
	 */
	// pixels de l'image du biomorph utilisé par les gènes (GeneExpression)
	protected transient int[] pixels;
	// addresse du dernier pixel de l'image
	protected transient int fin;
	// largeur de l'image
	protected transient int widthPixels;
	// hauteur de l'image
	protected transient int heightPixels;
	// object graphic contenant les méthode pour le dessin (=null si besoinGraph = false)
	// contrairement aux objet graphic des méthode paint, celui si est disponible tout le temps
	protected transient static Graphics2D graph;
	// mettre à true si on a besoin d'objet graphic
	protected transient boolean besoinGraph = false;
	// numéro du chromosome racine (chromosome contenant le premier gène appelé)
	public static final int chromosomeRacine = 0;
	
	// temps de départ du dessin, sert à stopper le dessin si il est trop long
	static long temps;
	
	/**
	 * construit le biomorph
	 * @param genotype génotype du biomorph
	 */
	public Biomorph2D(Genotype genotype) {
		super(genotype);
	}

	
	/*
	 * 		FONCTION DE NORMALISATION DE LA TAILLE DU BIOMORPH
	 */
	
	// sert au calculer le cadre dans lequel s'inscrit le biomorph pour le normaliser
	protected transient float minX,minY,maxX,maxY;
	
	// précision de la recherche des bords, plus precisionBord est petit, meilleur est la précision
	protected static final float precisionBord = 0.00001f;
	
	// similitude combiné avec la similitude passé à la méthode dessin pour normaliser la taille du biomorph
	protected Similitude normalisation;
	
	// rapport hauteur/largeur du cadre dans lequel s'inscrit le biomorph
	protected float scaleY;
	
	/**
	 * _Initialise la variable normalisation que le biomorph rentre dans un cadre de dimension choisie.
	 * _Optimise les gènes qui peuvent être optimisés pour accélérer le rendu de l'image.
	 */
	protected void normaliser() {
		//long t = System.currentTimeMillis();
		System.out.print("NORMALISATION !\n");
		GeneExpression<Similitude> racine = genotype.get(chromosomeRacine,0);
		NoeudGene.optimiserGraph((Lien) racine);
		
		//System.out.println("Optimisation:"+(System.currentTimeMillis()-t)+" ms");
		
		//t = System.currentTimeMillis();
		minX=0;minY=0;maxX=0;maxY=0;
		racine.trouverLesBords(new Similitude(0,0,1,0,true,0));
		
		//System.out.println("trouver bords :"+(System.currentTimeMillis()-t)+" ms");
		
		float l = maxX-minX, h = maxY-minY;
		float a = 1/Math.max(l,h);
		normalisation = new Similitude(-(minX+0.5f*l)*a,-(minY+0.5f*h)*a,a,0,true,0x101010);
		scaleY = h*a;
		
	}
	
	/*
	 * 		FONCTIONS DE DESSIN DU BIOMORPH
	 */
	/**
	 * change l'objet graphic pour le dessin
	 */
	public void setGraphics(Graphics g) {
		Biomorph2D.graph = (Graphics2D) g;
	}
	
	/**
	 * Initialise l'image où est dessiné le biomorph
	 * @param pixels
	 * @param width
	 * @param height
	 */
	public void setPixels(int[] pixels,int width,int height) {
		this.widthPixels = width;
		this.heightPixels = height;
		this.pixels = pixels;
		if (pixels != null) this.fin = Math.min(pixels.length,width*height);
	}
	/**
	 * Dessine le biomorph avec les paramètre de la Similitude positionnement,
	 * @param positionnement contient l'information sur la taille, la rotation,la position, la couleur initiale
	 */
	@Override public void dessine(Transform<?,?> positionnement) {
		temps = System.currentTimeMillis();
		finaliser();
		if (normalisation ==null) normaliser();
		 if (pixels != null) {
			 GeneExpression<Similitude> racine = genotype.get(chromosomeRacine,0);
			racine.expression(normalisation.concat((Similitude) positionnement));
		 }
	}
	/*
	 *  variables pour calculer l'efficacité du dessin
	 *  count1 : nombre de point placé sur un pixels vide de l'image
	 *  count2 : nombre de point calculé (le point n'est pas placé si le pixel est déjà occupé)
	 *  t : temps mis par le dessin
	 */
	//public static int count1,count2,t;
	/**
	 * Dessine le biomorph 
	 * @param positionnement @see dessine(positionnement)
	 * @param image image où est dessiné le biomorph
	 */
	public void dessine(Similitude positionnement,ImageAccessible image) {
	//	count1=count2=t=0;
	//	long tt = System.currentTimeMillis();
		setPixels(image.getAccessPixels(),image.width,image.height);
		//besoinGraph = true;
		if (besoinGraph) setGraphics(image.getDrawingTools());
		 if (pixels != null) dessine(positionnement);
		// t+=(System.currentTimeMillis()-tt);
	//	System.out.println("COUNT1 "+count1+" COUNT2 "+count2+"\n perf: "+(int) (((double) count1)/((double) count2)*100)+"%");
	//	System.out.println("durée totale:"+t+" ms");
	//	System.out.println("points/msec :"+(count2/(t+1))+"\n");
	}

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * @see obtenirEnfant de Biomorph
	 */
	public Biomorph2D obtenirEnfant(Genotype genomTransmis) {
		return new Biomorph2D(genomTransmis);
	}

	/*
	 * 		FONCTION DE CREATION D'UN ICONE REPRESENTANT LE BIOMORPH
	 * @see biomorph.abstrait.Biomorph#getIcone(int)
	 */
	
	protected transient ArrayList<IconBiomorph2D> liste_icones;
	
	@Override
	public IconBiomorph2D getIcone(final int largeur) {
		return new IconBiomorph2D(this,largeur);
	}

	public void rafrechir_icons() {
		if (liste_icones == null) return;
    	for (IconBiomorph2D ico : liste_icones) {
    		ico.initTaille();
    		ico.revalidate();
    		ico.repaint();
		}
	}
	/*
	 * 		FONCTIONS DE GENERATION D'UN BIOMORPH ALEATOIRE
	 */
	public static Biomorph2D aleaBiomorph1 (int nbGene) {
		Biomorph2D biom = new Biomorph2D(new Genotype(2,nbGene)); 
		biom.genotype.add(0,new GeneSymetrie(1,0));
		for (int i=1;i<nbGene;i++) biom.genotype.add(1,GeneDivisionLimite.aleatoire('\1','\1'));
		biom.finaliser();
		return biom;
	}

	/**
	 * Créé un nouveaux biomorph aléatoire avec une structure particulière du génome
	 * @param symetrie Symétrie du biomorph (ajout d'un chromosome si il y a symétrie)
	 * @param nbChromHomeo Nombre de "chromosomes homéotiques" dans le génome (les chromosomes homéotique ne sont pas autoréférents,
	 * ils sont appelés en premiers, et chaque gènes homéotique appelle deux gènes sur le chromosome suivant si il est homéotique) 
	 * @param nbChrom Nombre de chromosomes autoréférents (appelés par le derniers niveaux de chromosomes homéotiques)
	 * @param nbGenChrom Nombre de gènes par chromosome
	 * @return le biomorph généré.
	 */
	public static Biomorph2D aleaBioStructure(boolean symetrie,int nbChromHomeo,int nbChrom,int nbGenChrom) {
		Biomorph2D biom = new Biomorph2D(new Genotype((symetrie?1:0) + nbChromHomeo + nbChrom,nbGenChrom)); 
		int indexChrom = 0;
		// placement ou non d'un chromosome contenant un seul gène (gène de symétrie)
		if (symetrie) {
			biom.genotype.add(0,new GeneSymetrie(1,0));
			indexChrom++;
		}
		// création de nbChromHomeo-1 niveau de "chromosomes homéotique" (chaque niveau fait référence au suivant)
		for (int k=0;k<nbChromHomeo-1;k++) {
			int maxGen = Math.min(2<<k,nbChrom);
			for (int i=0;i<maxGen;i++){
				biom.genotype.add(indexChrom,GeneDivisionLimite.aleatoire((char) (indexChrom+1),(char) (indexChrom+1)));
			}
			indexChrom++;
		}
		// placement du dernier niveau de chromosomes homéotique faisant référence aux différents chromosomes suivants
		if (nbChromHomeo>0) {
			int maxGen = Math.min(2<<(nbChromHomeo-1),nbChrom);
			for (int i=0;i<maxGen;i++) {
				biom.genotype.add(indexChrom,GeneDivisionLimite.aleatoire(
						(char) (indexChrom+1+(2*i)%nbChrom),
						(char) (indexChrom+1+(2*i+1)%nbChrom)));	
			}
			indexChrom++;
		}
		// placement des nbChrom chromomosomes autoréférents
		for (int k=0;k<nbChrom;k++) {
			for (int i=0;i<nbGenChrom;i++) {
				biom.genotype.add(indexChrom,GeneDivisionLimite.aleatoire((char) indexChrom,(char) indexChrom));	
			}
			indexChrom++;
		}
		biom.finaliser();
		return biom;
	}
	
	/**
	 * Générateur de biomorph avec une structure particulière paramétrable
	 * @author Mathieu Guinin
	 *
	 */
	public static class BiomorphStructure implements BioGenerator {
		
		public boolean symetrie;
		public int nbChromHomeo;
		public int nbChrom;
		public int nbGenChrom;
		
		
		public Biomorph biomorphAleatoire(){
			return Biomorph2D.aleaBioStructure( symetrie, nbChromHomeo, nbChrom, nbGenChrom);
		}
	}
	
}


