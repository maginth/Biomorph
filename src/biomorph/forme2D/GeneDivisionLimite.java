package biomorph.forme2D;

import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;

import biomorph.abstrait.Biomorph;
import biomorph.abstrait.GeneDivision;
import biomorph.abstrait.GeneExpression;
import biomorph.abstrait.NoeudGene;
import biomorph.abstrait.TauxMutation;

/**
 * 
 * Un génotype constitué uniquement de GeneDivisionLimite produira un biomorph
 * fractal. Le préfixe "Limite" signifie que la méthode achaqueDividion ne 
 * fait rien: les pixels du biomorph représente la limite d'un nombre théoriquement
 * infinie d'itération de la méthode expression (comme pour le Flocon de Koch). 
 * 
 * En fait pour des question d'optimisation, la méthode expression est complètement réécrite
 * Pour le principe, les méthodes abstraites de GeneDivision ont quand même été implémenté,
 * même si elle ne sont pas appelés.
 * 
 * Cette class est très optimisé pour le rendu graphique.
 * 
 * @author Mathieu Guinin
 * @see GeneDivision*
 */


public class GeneDivisionLimite 
	extends GeneDivision<Similitude,Similitude,Similitude> 
	implements Serializable,  NoeudGene.LienOptimise {
	/*
	 * 
	 * 		INFORMATION GENETIQUE DE CE GENE :
	 * 
	 */
	
	 
	
	/*
	 * angle : déterminant la forme des vecteur (fx,fy) des Similitude. 
	 * 			Sur le cercle unité, Les vecteurs arrive (respectivement 
	 * 			partent si sens==false) du point de coordonné (cos(angle),sin(angle))
	 * 			et parte (respectivement arrive) d'un point à une extrémité du diamètre
	 * 			du cercle sur l'axe x.
	 * sens1 : rotation de 180° ou non lors de l'expression du gène 1
	 * sens2 : rotation de 180° ou non lors de l'expression du gène 2
	 * direct1 : symétrie ou non lors de l'expression du gène 1
	 * direct2 : symétrie ou non lors de l'expression du gène 2
	 * couleur1 : décalage colorimétrique lors de l'expression du gène 1
	 * couleur2 : décalage colorimétrique lors de l'expression du gène 2
	 */
	protected float angle;
	protected boolean sens1,sens2,direct1,direct2;
	protected int couleur1,couleur2;

	/*
	 * Notons que les liens vers les gènes 1 et 2  (n° chromosome,n° gène) sont 
	 * hérité de la classe GeneDivision
	 */
	
	
	
	/*
	 * 
	 *  FIN INFORMATION GENETIQUE DE CE GENE
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	/* 
	 * biomorph où le gène s'exprime
	 */
	private transient Biomorph2D cible;
	/*
	 * limite :  taille à ne pas dépasser par la norme de la similitude 
	 * passé à expression pour dessiner les gène 1 et 2 
	 * px, py :  coordonnés du point sur le cercle 
	 */
	protected transient float limite1,limite2,px,py;
	
	
	/* taille de la base de la fractale précalculé, il vaux mieux ne pas
	 * dépasser 32 pixels car le précalcul se fait dans une petite fenêtre
	 * de 256x256 pixels, il y a un vrai risque de dépassement (la fractale
	 * apparait alors coupé) 
	 */
	static final float taillePrecalcul = 32;
	/*
	 * les points précalculé sont dans un premier temps placés
	 * dans un tableau de tableau d'int, chaque tableau représente les
	 * point apparaissant à une certaine échelle croissante en 
	 * 1/groupeFactor d'un tableau ) l'autre
	 */
	static final float groupeFactor = 0.9f;
	
	static final float invLogFactor = 1/(float) Math.log(groupeFactor);
	static final float invtaille = 1/taillePrecalcul;
	static final int len = (int) (Math.log(invtaille)*invLogFactor)+5;

	// image de test de supperposition des points (les points superposés sont éliminés) 
	static final char[] miniMap = new char[256*256];
	static char couleur = 0;
	static int totalPoint;
	/*
	 *  utilisé pour le calcul d'une enveloppe convexe approximative
	 *  huit frontières (2 horizontales, 2 verticale, 4 en diagonale) sont calculés 
	 */
	private static float xmax,xmin,ymax,ymin,xpymin,xpymax,xmymin,xmymax;
	
	// indique si les points doivent être enregistrés (true) ou dessinés (false)
	static boolean enregistrer;
	
	
	/*
	 *  permet de dire aux genes appelé si l'inclusion dans le rectangle doit 
	 *  continuer à être testé ou non
	 */
	public static boolean testRect=true;

	/*
	 *  limites du rectangle contenant le biomorph à dessiner
	 *  (utilisé quand testRect==true)
	 */
	public static float xm, xM, ym, yM; 
	
	//voir la description de "groupeFactor"
	static final private byte[][] pointsEchelle = new byte[len][];
	static final private int[][] pointsCouleur = new int[len][];
	// nombre de point dans chaque tableau
	static private int[] nbPoints;
	/*
	 *  initialisation des tableaux (si il y a des erreurs de dépassement
	 *  on pourra augmenter leur taille)
	 */
	static {
		for (int i=0;i<len;i++) {
			pointsEchelle[i] = new byte[500*(i+1)];
			pointsCouleur[i] = new int[250*(i+1)];
		}
	}
	/*
	 *  similitude passé en paramètre à expression lors de l'optimisation
	 */
	static Similitude transInit = new Similitude(0,0,taillePrecalcul,0,true,0);
	
	
	
	
	// listes des points précalculé rangé par échelle, de la moins précise à la plus précise
	private transient byte[] pointsPrecalcules;
	// idem pour les couleurs
	private transient int[] couleursPrecalcules;
	// listes des index de fin jusqu'où recopier les points suivant l'écelle
	private transient int[] pointsMax;
	// coordonné des sommets de l'enveloppe convexe approximative
	private transient float[] polygoneX;
	private transient float[] polygoneY;
	// indique si se gène à été optimisé par public void optimiser()
	private transient boolean optimise = false;
	
	// taux de mutation maximum pour générer un gène totalement aléatoire
	static TauxMutation mutationMax = new TauxMutation(1,1,1,1);
	/**
	 * 
	 * @param indexChromosome1 emplacement du chromosome 1
	 * @param indexChromosome2 emplacement du chromosome 2
	 * @return GeneDivisionLimite totalement aléatoire
	 */
	static GeneDivisionLimite aleatoire(char indexChromosome1,char indexChromosome2) {
		GeneDivisionLimite res = new GeneDivisionLimite(new GeneDivisionLimite(),mutationMax);
		res.indexChromosome1 = indexChromosome1;
		res.indexChromosome2 = indexChromosome2;
		return res ;
	}
	
	// constructeur
	private GeneDivisionLimite() {
		super();
	}


	
	
	
	public GeneDivisionLimite(char indexChromosome1,
			char indexChromosome2,
			char indexGene1,
			char indexGene2,
			float angle,
	        boolean sens1,boolean sens2,boolean direct1,boolean direct2,
	        int couleur1,int couleur2) {
		this.indexChromosome1 = indexChromosome1;
		this.indexChromosome2 = indexChromosome2;
		this.indexGene1 = indexGene1;
		this.indexGene2 = indexGene2;
		this.angle =  angle;
		this.sens1 = sens1;
		this.sens2 = sens2;
		this.direct1 = direct1;
		this.direct2 = direct2;
		this.couleur1 = couleur1;
		this.couleur2 = couleur2;
		finalise = false;
	}
	
	public void reinit(char indexChromosome1,
			char indexChromosome2,
			char indexGene1,
			char indexGene2,
			float angle,
	        boolean sens1,boolean sens2,boolean direct1,boolean direct2,
	        int couleur1,int couleur2) {
		this.indexChromosome1 = indexChromosome1;
		this.indexChromosome2 = indexChromosome2;
		this.indexGene1 = indexGene1;
		this.indexGene2 = indexGene2;
		this.angle =  angle;
		this.sens1 = sens1;
		this.sens2 = sens2;
		this.direct1 = direct1;
		this.direct2 = direct2;
		this.couleur1 = couleur1;
		this.couleur2 = couleur2;
		finalise = false;
	}


	public GeneDivisionLimite(GeneDivisionLimite gen,TauxMutation X) {
		indexChromosome1 = gen.indexChromosome1;
		indexChromosome2 = gen.indexChromosome2;
		indexGene1 = Biomorph.mutBool(true,X.getProbaModifStructure())? gen.indexGene1 :(char) Biomorph.alea.nextInt();
		indexGene2 = Biomorph.mutBool(true,X.getProbaModifStructure())? gen.indexGene2 :(char) Biomorph.alea.nextInt();
		double p = X.getProbaMutation(),a = X.getAmplitudeMutation();
		this.angle =  gen.angle + (float) (Biomorph.mutBool(true,p)? 0 : 2*Math.PI*a*(Biomorph.alea.nextFloat()-0.5));
		sens1 = Biomorph.mutBool(gen.sens1,p*a*a*0.5);
		sens2 = Biomorph.mutBool(gen.sens2,p*a*a*0.5);
		direct1 = Biomorph.mutBool(gen.direct1,p*a*a*0.5);
		direct2 = Biomorph.mutBool(gen.direct2,p*a*a*0.5);
		couleur1 = Biomorph.mutBool(true,p)? gen.couleur1 : Biomorph.alea.nextInt();
		couleur2 = Biomorph.mutBool(true,p)? gen.couleur2 : Biomorph.alea.nextInt();
		finalise = false;
	}
	
	/**
	 * Décale les bits fort de chaque composante (R,G et B) vers la droite ,
	 *  le décalage est plus grand si il y a beaucoup de chromosomes 
	 *  (pour éviter les couleurs bariolés)
	 * @param nbBhromosome nombre de chromosomes
	 * @param couleur couleur de départ
	 * @return la couleur atténué.
	 */
	private int transformCouleur(int n,int couleur) {
		int a = (((1<<n)-1)<<(8-n))*0x010101 , b=((1<<(n-1))-1)*0x010101;
		return (((couleur & a)>>(8-n))-b)<<1;
	}
	

	private transient boolean finalise;
	/**
	 * La finalisation créé les similitudes
	 */
	@Override 
	public void finaliser(Biomorph biom) {
		if (!finalise) {
			System.out.println("FINALISE ");
			cible = (Biomorph2D) biom;
			indexGene1 %= cible.genotype.get(indexChromosome1).size();
			indexGene2 %= cible.genotype.get(indexChromosome2).size();
			super.finaliser(biom);
			float a = (float) ((angle > 0 ? 1 : -1) * (Math.abs(angle % Math.PI) % 2.9 + 0.12));
			px = (float) (0.5 * Math.cos(a) + 0.5); 
			py = (float) (0.5 * Math.sin(a));
			int c1 = transformCouleur(4,couleur1);
			int c2 = transformCouleur(4,couleur2);
			
			Similitude trans1 = sens1? 
				new Similitude(0, 0, px, py, direct1, c1)
				:new Similitude(px, py, -px, -py, direct1, c1);
			Similitude trans2 = sens2?
				new Similitude(px, py, 1-px, -py, direct2, c2)
				:new Similitude(1, 0, px-1, py, direct2, c2);
			if (trans1.norme>trans2.norme) {
				transGene1 = trans1;
				transGene2 = trans2;
			} else {
				GeneExpression<Similitude> temp = lienGene1;
				transGene2 = trans1;
				transGene1 = trans2;
				lienGene1 = lienGene2;
				lienGene2 = temp;
			}
			limite1 = 1/((Similitude) transGene1).norme;
			limite2 = 1/((Similitude) transGene2).norme;
			
			finalise =true;
			lienGene1.finaliser(biom);
			lienGene2.finaliser(biom);
		}
		
	}
	
	
	@Override
	public GeneDivisionLimite recopie(TauxMutation X) {
		return new GeneDivisionLimite(this,X) ;
	}

	
	/**
	 * Les appels récursifs d'expression s'arrêtent quand la forme à dessiner est plus petite que 2 pixels
	 * @see biomorph.abstrait.GeneDivision#conditionArret(guinin.biomorph.abstrait.Transform)
	 */
	@Override
	protected boolean conditionArret(Similitude trans) {
		return trans.norme<1;
	}
	
	/* aChaqueDivision n'est pas utilisé ici */
	@Override
	protected void aChaqueDivision(Similitude trans, Similitude trans1,
			Similitude trans2) {}

	/* Un point est placé dans le tableau bitmap à la fin de la récursion */
	@Override
	protected void aLaFin(Similitude trans) {
		// j est la coordonnée toujours positive du point à placer
		int j = ((int) (trans.tx+0.5*trans.fx)+ 1280* (int) (trans.ty+0.5*trans.fy)) & 0x7fffffff ;
		// cible.fin - 1 est la coordonnée du dernier pixel de tableau bitmap
		if(j<cible.fin) cible.pixels[j] = 0xff000000 | trans.color ;
	}
	
	

	
	
	/**
	 * détermine le cadre dans lequel s'inscrit la fractale.
	 * Ce cadre est utilisé ensuite pour normaliser la taille 
	 * du biomorph @see Biomorph2D
	 */
	@Override
	public final void trouverLesBords(Similitude trans) {
		if (optimise) {
			float tx=trans.tx ,ty=trans.ty, fx=trans.fx, fy=trans.fy;
			float ffx = trans.direct?fx:-fx,ffy = trans.direct?fy:-fy;
			float x,y,xp,yp;
			for (int a=0;a<8;a++) {
				x = polygoneX[a]; y = polygoneY[a];
				xp = tx+x*fx-y*ffy; yp = ty+x*fy+y*ffx;
				if (xp<cible.minX) cible.minX = xp;
				if (yp<cible.minY) cible.minY = yp;
				if (xp>cible.maxX) cible.maxX = xp;
				if (yp>cible.maxY) cible.maxY = yp;
			}
		} else {
			if (trans.norme<Biomorph2D.precisionBord) {
				if (trans.tx<cible.minX) cible.minX = trans.tx;
				if (trans.ty<cible.minY) cible.minY = trans.ty;
				if (trans.tx>cible.maxX) cible.maxX = trans.tx;
				if (trans.ty>cible.maxY) cible.maxY = trans.ty;
			} else {
				Similitude transp;
				transp = transGene1.concat(trans);
				lienGene1.trouverLesBords(transp);
				transp.delet();
				transp = transGene2.concat(trans);
				lienGene2.trouverLesBords(transp);
				transp.delet();
			}
		}
	}

	/**
	 * L'expression sert à la fois à enregister les points pour l'optimisation
	 * et à dessiner la fractale
	 */
	@Override 
	public final void expression(Similitude trans) {
		if (optimise) expressionOptimise(trans); else {
			if (trans.norme>limite1) {
				if (enregistrer) {
					float ppy = trans.direct? py:-py;
					float x = trans.tx+px*trans.fx-ppy*trans.fy;
					float y = trans.ty+px*trans.fy+ppy*trans.fx;
					int j = (128+(int) x+ ((128+(int) y)<<8))  & 0xffff ;
					int count=10;
						while (miniMap[j] != couleur && count-- > 0) {
							j = (128+(int) x+ ((128+(int) y)<<8))  & 0xffff ;
							y++;
						}
						miniMap[j] = couleur;
						int k = (int) (Math.log(trans.norme*invtaille)*invLogFactor+0.5);
						int l = nbPoints[k]++;
						pointsEchelle[k][2*l] = (byte) x;
						pointsEchelle[k][2*l+1] = (byte) y;
						pointsCouleur[k][l] = trans.color;
						//pointsCouleur[k][l] = 541651565*couleur;
						totalPoint++;
				} /*else {
					int j = ((int) x+ cible.widthPixels* (int) y) & 0x7fffffff ;
					if(j<cible.fin) {
						if (cible.pixels[j] == 0) {
							cible.pixels[j] =0xff000000 | trans.color;
						}
					}
				}*/
				if (System.currentTimeMillis()>500+Biomorph2D.temps) return;
				Similitude transp;
				transp = transGene1.concat(trans);
				lienGene1.expression(transp);
				transp.delet();
				if (trans.norme>limite2) {
					transp = transGene2.concat(trans);
					lienGene2.expression(transp);
					transp.delet();
				} else {
					checkMinMax(trans.tx,trans.ty);
				}
				
			}
		}
	}
	/**
	 * L'expression optimisé applique une transformation à tout les points
	 * préenregistré, jusqu'à la précision nécessaire.
	 * @param trans
	 */
	//private int colour = 0xff000000 | Biomorph.alea.nextInt();
	
	public void expressionOptimise(Similitude trans) {
		//System.out.println("début expres opt");
		/* suivant la rotation de la forme par rapport à l'enregistrement,
		 * il y a un risque de manquer des pixels. On augmente alors la
		 * précision.
		 */
		//double echelle =Math.abs(trans.fx)+Math.abs(trans.fy); 
		double echelle = trans.norme;
		if (echelle<1) return;
		boolean temp = testRect;
		if (testRect) {
			byte intersection = intersectRect(trans.tx,trans.ty,trans.fx,trans.fy,trans.direct?trans.fx:-trans.fx,trans.direct?trans.fy:-trans.fy);
			if (intersection == 2) testRect = false; 
			else if (intersection == 0) return;
		}
		if (echelle>taillePrecalcul) {
			if (System.currentTimeMillis()>500+Biomorph2D.temps) return;
			Similitude transp;
			transp = transGene1.concat(trans);
			lienGene1.expression(transp);
			transp.delet();
			transp = transGene2.concat(trans);
			lienGene2.expression(transp);
			transp.delet();
			testRect=temp;
			return;
		}
		testRect=temp;
		int kmax = (int) (-Math.log(echelle)*invLogFactor);
		float tx=trans.tx ,ty=trans.ty, fx=trans.fx*invtaille, fy=trans.fy*invtaille;
		float ffx = trans.direct?fx:-fx,ffy = trans.direct?fy:-fy;
		int i,j;
		float x,y;
		// lors de l'optimisation d'un autre gène, les point pré enregistré de ce gène sont recopiés
		if (enregistrer) {
			//System.out.println("\t enreg. expres opt");
			byte xp,yp;
			int l, ikmax = 0;
	
			for (int a=0;a<8;a++) {
				x = polygoneX[a]; y = polygoneY[a];
				checkMinMax(tx+(x*fx-y*ffy)*taillePrecalcul,ty+(x*fy+y*ffx)*taillePrecalcul);
			}
			int k,kp = len-1-kmax;
			for (k=0;k<=kmax;k++) {
				i = ikmax;
				ikmax = pointsMax[k];
				for(;i<ikmax;i++){
					x = pointsPrecalcules[2*i];
					y = pointsPrecalcules[2*i+1];
					xp = (byte) (tx+x*fx-y*ffy);
					yp = (byte) (ty+x*fy+y*ffx);
					j = (128+xp+ ((128+yp)<<8))  & 0xffff ;
					if (miniMap[j] != couleur) {
						miniMap[j] = couleur;
						l = nbPoints[kp]++;
						pointsEchelle[kp][2*l] = xp;
						pointsEchelle[kp][2*l+1] = yp;
						pointsCouleur[kp][l] = trans.color+couleursPrecalcules[i];
						totalPoint++;
					}
				}
				kp++;
			}
			//System.out.println("\t fin enrg. expres opt");
		} else {
			//System.out.println("\t restitutuin expres opt");
			int c,m,imax = pointsMax[kmax],
			w = cible.widthPixels; 
			for(i=0;i<imax;i++){
				x = pointsPrecalcules[2*i];
				y = pointsPrecalcules[2*i+1];
				j = ((int) (tx+x*fx-y*ffy)
				      +w*(int) (ty+x*fy+y*ffx)) & 0x7fffffff ;
				if(j<cible.fin) { //&& cible.pixels[j] == 0) {
				c = trans.color+couleursPrecalcules[i];
				m = c & 0x01010100;
				cible.pixels[j] =0xff000000 | ((((c^(m-(m>>8)))&0xfefefe)+(cible.pixels[j]&0xfefefe))>>1);
				//cible.pixels[j] = colour;
				//Biomorph2D.count1++;
				} 
				//Biomorph2D.count2++;
			}
			//System.out.println("\t fin restitution expres opt");
		}
	}
	
	@Override
	/**
	 * Appelé si la fractale doit être précalculé 
	 */
	public void optimiser() {

		if (optimise) return;
		System.out.println("Debut OPTIMISATION ");
		
		couleur++;
		if (couleur == 0) 
			for (int i=0;i<256*256;i++) miniMap[i] = 0;
		totalPoint = 0;
		nbPoints = new int[len];
		testRect = false;
		xmax=xmin=ymax=ymin=xpymin=xpymax=xmymin=xmymax=0;
		Biomorph2D.temps = System.currentTimeMillis();
		enregistrer = true;
		expression(transInit);
		enregistrer = false;
		pointsPrecalcules = new byte[totalPoint*2];
		couleursPrecalcules = new int[totalPoint];
		pointsMax = new int[len];
		int i,j,index=0;
		byte[] pp;
		int[] cc;
		for(i=0;i<len;i++) {
			int jmax = nbPoints[i];
			pp = pointsEchelle[i];
			cc = pointsCouleur[i];
			for (j=0;j<jmax;j++) {
				pointsPrecalcules[2*index]=pp[2*j];
				pointsPrecalcules[2*index+1]=pp[2*j+1];
				couleursPrecalcules[index] = cc[j];
				index++;
			}
			pointsMax[i] = index;
		}
		
		
		float x12 = xmax*invtaille;float y1 = xpymax*invtaille-x12, y2 = x12-xmymax*invtaille;
		float x34 = xmin*invtaille;float y3 = xpymin*invtaille-x34, y4 = x34-xmymin*invtaille;
		float y56 = ymax*invtaille; float x5 = xpymax*invtaille-y56, x6 = y56+xmymin*invtaille; 
		float y78 = ymin*invtaille; float x7 = xpymin*invtaille-y78, x8 = y78+xmymax*invtaille; 
		polygoneX = new float[]{x12,x12,x8,x7,x34,x34,x6,x5};
		polygoneY = new float[]{y1,y2,y78,y78,y3,y4,y56,y56};
		
		optimise = true;
		
		//System.out.println("FIN OPTIMISATION");
	}
	
	public void desoptimiser() {
		System.out.println("DESOPTIMISE ");
		pointsPrecalcules = null;
		couleursPrecalcules = null;
		pointsMax = null;
		polygoneX = null;
		polygoneY = null;
		
		optimise = false;
	}

	/**
	 *  test les bords de l'enveloppe convexe approximative
	 * @param x 
	 * @param y
	 */
	private static void checkMinMax(float x,float y) {
		if (x<xmin) xmin = x;
		if (y<ymin) ymin = y;
		if (x>xmax) xmax = x;
		if (y>ymax) ymax = y;
		float xpy = x+y, xmy = x-y;
		if (xpy<xpymin) xpymin = xpy;
		if (xpy>xpymax) xpymax = xpy;
		if (xmy<xmymin) xmymin = xmy;
		if (xmy>xmymax) xmymax = xmy;
	}
	
	
	// attention intersection imparfaite!
	private byte intersectRect(float tx,float ty, float fx,float fy,float ffx,float ffy) {
		float x,y,xp=0,yp=0;
	//	float x0=0,y0=0,xx=0,yy=0;
		int d=0,g=0,h=0,b=0; // droite, gauche, haut, bas;
		for (int i=0; i<8 ; i++) {
			x = polygoneX[i]; y = polygoneY[i];
			xp = tx+x*fx-y*ffy; yp = ty+x*fy+y*ffx; 
		/*	if (i==0) {xx=x0=xp;yy=y0=yp;} 
			else {
			Biomorph2D.graph.drawLine((int) xx, (int) yy, (int) xp, (int) yp);
			xx = xp; yy = yp;
			}*/
			if (xp<xm) d++; else if (xp>xM) g++;
			if (yp<ym) b++; else if (yp>yM) h++;
		}
		/*Biomorph2D.graph.setColor(Color.red);
		Biomorph2D.graph.drawLine((int) xx, (int) yy, (int) x0, (int) y0);
		Biomorph2D.graph.setColor(Color.white);*/
		if (d==8 || g==8 || h==8 || b==8) return 0; //le polygone n'est pas dans le rectangle
		if (d==0 && g==0 && h==0 && b==0) return 2; //le polygone est inclu dans le rectangle
		return 1; //certains points sont à l'extérieur du rectangle
	}
	
	
	
	private transient PanelGeneticienDivision panelGeneticien;
	@Override
	/**
	 * @return un PanelGeneticienDivision représentant ce gène
	 */
	public Component getPanelGeneticien() {
		if (panelGeneticien == null) {
			panelGeneticien = new PanelGeneticienDivision(this);
		}
		return panelGeneticien;
	}

	
	/**
	 * retourne les positions dans le génotype des gènes appelés par ce gène
	 * @return un tableau des positions des deux gènes
	 */
	public int[] getIndex(){
		return new int[]{indexChromosome1,indexGene1,indexChromosome2,indexGene2};
	}
	/**
	 * 
	 * @param un tableau des positions à modifier des deux gènes appelés par ce gène
	 */
	public void setIndex(char[] index){
		 indexGene1 = index[1];
		 indexGene2 = index[3];
		 indexChromosome1 = index[0];
		 indexChromosome2 = index[2];
	}


	public String toString() {
		return "\ncible: "+cible+"\nposition genotype: "+cible.genotype.indexOf(this)+"\nindexGene1: "+indexGene1+"\n";
	}
	
}
