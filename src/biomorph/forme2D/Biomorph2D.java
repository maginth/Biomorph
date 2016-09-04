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
 * les g�nes vont agir pour dessiner le biomorph2D.
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
	// pixels de l'image du biomorph utilis� par les g�nes (GeneExpression)
	protected transient int[] pixels;
	// addresse du dernier pixel de l'image
	protected transient int fin;
	// largeur de l'image
	protected transient int widthPixels;
	// hauteur de l'image
	protected transient int heightPixels;
	// object graphic contenant les m�thode pour le dessin (=null si besoinGraph = false)
	// contrairement aux objet graphic des m�thode paint, celui si est disponible tout le temps
	protected transient static Graphics2D graph;
	// mettre � true si on a besoin d'objet graphic
	protected transient boolean besoinGraph = false;
	// num�ro du chromosome racine (chromosome contenant le premier g�ne appel�)
	public static final int chromosomeRacine = 0;
	
	// temps de d�part du dessin, sert � stopper le dessin si il est trop long
	static long temps;
	
	/**
	 * construit le biomorph
	 * @param genotype g�notype du biomorph
	 */
	public Biomorph2D(Genotype genotype) {
		super(genotype);
	}

	
	/*
	 * 		FONCTION DE NORMALISATION DE LA TAILLE DU BIOMORPH
	 */
	
	// sert au calculer le cadre dans lequel s'inscrit le biomorph pour le normaliser
	protected transient float minX,minY,maxX,maxY;
	
	// pr�cision de la recherche des bords, plus precisionBord est petit, meilleur est la pr�cision
	protected static final float precisionBord = 0.00001f;
	
	// similitude combin� avec la similitude pass� � la m�thode dessin pour normaliser la taille du biomorph
	protected Similitude normalisation;
	
	// rapport hauteur/largeur du cadre dans lequel s'inscrit le biomorph
	protected float scaleY;
	
	/**
	 * _Initialise la variable normalisation que le biomorph rentre dans un cadre de dimension choisie.
	 * _Optimise les g�nes qui peuvent �tre optimis�s pour acc�l�rer le rendu de l'image.
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
	 * Initialise l'image o� est dessin� le biomorph
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
	 * Dessine le biomorph avec les param�tre de la Similitude positionnement,
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
	 *  variables pour calculer l'efficacit� du dessin
	 *  count1 : nombre de point plac� sur un pixels vide de l'image
	 *  count2 : nombre de point calcul� (le point n'est pas plac� si le pixel est d�j� occup�)
	 *  t : temps mis par le dessin
	 */
	//public static int count1,count2,t;
	/**
	 * Dessine le biomorph 
	 * @param positionnement @see dessine(positionnement)
	 * @param image image o� est dessin� le biomorph
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
	//	System.out.println("dur�e totale:"+t+" ms");
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
	 * Cr�� un nouveaux biomorph al�atoire avec une structure particuli�re du g�nome
	 * @param symetrie Sym�trie du biomorph (ajout d'un chromosome si il y a sym�trie)
	 * @param nbChromHomeo Nombre de "chromosomes hom�otiques" dans le g�nome (les chromosomes hom�otique ne sont pas autor�f�rents,
	 * ils sont appel�s en premiers, et chaque g�nes hom�otique appelle deux g�nes sur le chromosome suivant si il est hom�otique) 
	 * @param nbChrom Nombre de chromosomes autor�f�rents (appel�s par le derniers niveaux de chromosomes hom�otiques)
	 * @param nbGenChrom Nombre de g�nes par chromosome
	 * @return le biomorph g�n�r�.
	 */
	public static Biomorph2D aleaBioStructure(boolean symetrie,int nbChromHomeo,int nbChrom,int nbGenChrom) {
		Biomorph2D biom = new Biomorph2D(new Genotype((symetrie?1:0) + nbChromHomeo + nbChrom,nbGenChrom)); 
		int indexChrom = 0;
		// placement ou non d'un chromosome contenant un seul g�ne (g�ne de sym�trie)
		if (symetrie) {
			biom.genotype.add(0,new GeneSymetrie(1,0));
			indexChrom++;
		}
		// cr�ation de nbChromHomeo-1 niveau de "chromosomes hom�otique" (chaque niveau fait r�f�rence au suivant)
		for (int k=0;k<nbChromHomeo-1;k++) {
			int maxGen = Math.min(2<<k,nbChrom);
			for (int i=0;i<maxGen;i++){
				biom.genotype.add(indexChrom,GeneDivisionLimite.aleatoire((char) (indexChrom+1),(char) (indexChrom+1)));
			}
			indexChrom++;
		}
		// placement du dernier niveau de chromosomes hom�otique faisant r�f�rence aux diff�rents chromosomes suivants
		if (nbChromHomeo>0) {
			int maxGen = Math.min(2<<(nbChromHomeo-1),nbChrom);
			for (int i=0;i<maxGen;i++) {
				biom.genotype.add(indexChrom,GeneDivisionLimite.aleatoire(
						(char) (indexChrom+1+(2*i)%nbChrom),
						(char) (indexChrom+1+(2*i+1)%nbChrom)));	
			}
			indexChrom++;
		}
		// placement des nbChrom chromomosomes autor�f�rents
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
	 * G�n�rateur de biomorph avec une structure particuli�re param�trable
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


