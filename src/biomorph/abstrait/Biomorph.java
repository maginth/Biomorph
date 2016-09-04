package biomorph.abstrait;



import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import interfac.global.AppletBiomorph;



/**
 *<p><h1><b>LA CLASSE ABSTRAITE BIOMORPH</b></h1>
 *
 * Les classes héritant de Biomorph doivent contenir toutes les donnés 
 * sur lesquels agiront leurs génes. Ces donnés représentent le phénotype. 
 * Le génotype est une ArrayListe de tous les génes succeptibles de s'exprimer
 * pour définir ce phénotype.
 * 
 * </p>
 *<p> <h1><b>LA GENERICITE < ... ></b></h1>
 *
 *Les classes du package biomorph.abstrait définissent la structure générale des
 *biomorphs, elles sont étroitement liées entre elles. Les classes héritants de 
 *cette structure devront pouvoir accéder entres elles à leurs méthodes/attribus 
 *rajouté, sans recourir sans arrét à des "cast", d'où l'utilisation des 
 *générics < ... > qui précise les autres classes de la structure hérité. 
 *Ainsi plusieurs types trés différent de biomorphs pourrons hériter de toutes
 *les fonctions de croisement,duplication,division des génes, dessin sans 
 *les réimplémenter totalements.
 *</p>
 * @author Mathieu Ginin
 */

public abstract class Biomorph implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	/** générateur l'aléatoire utile pour générer des mutation et des génes aléatoire
	 * Toute les générations aléatoire doivent utiliser ce générateur, 
	 * de maniére à ce qu'en l'initialisant, les tests donnent les mémes résultats
	 * */
	public static Random alea = new Random();
	
	
	
	private static int nombreTotalDeBiomorphsGeneres=0;
	/** genotype rassemble l'ensembe des génes de ce biomorph. 
	 * <i>En principe, le génotype ne devrait pas subir de modification :
	 * Les biomorphs sont vus comme des organismes pluricellulaires, 
	 * la mutation d'une seule cellule n'a pas de conséquence sur le
	 * génotype global. Seul les enfants de ce biomorph hériteront
	 * de mutations survenues pendant la reproduction</i> */
	public final Genotype genotype;
	/** le ou les biomorphs parents de ce biomorph **/
	private transient ArrayList<Biomorph> parents = new ArrayList<Biomorph>(2);
	/** les enfants de ce biomorphs **/
	private transient ArrayList<Biomorph> enfants = new ArrayList<Biomorph>(10);
	
	public ArrayList<Biomorph> getParents() {
		if (parents==null) parents = new ArrayList<Biomorph>(2);
		return parents;
	}
	public ArrayList<Biomorph> getEnfants() {
		if (enfants==null) enfants = new ArrayList<Biomorph>(10);
		return enfants;
	}
	/** le nom de ce biomorph **/
	private String nom = "";
	public void setName(String nomBiomorph) {
		if (nomBiomorph != nom) {
			if (nomBiomorph.length()>=1) nom = nomBiomorph;
			for(JTextComponent text : nomsModifiables) if (text != null) text.setText(nom);
		}
	}
	public String getName() {return nom;}
	/** la liste des champs permettant de modifier le nom du biomorph **/
	private transient ArrayList<JTextComponent> nomsModifiables = new ArrayList<JTextComponent>(4);


	
	/** associerNom permet d'affecter le text d'un JTextField au nom du biomorph **/ 
	public void associerNom(final JTextComponent jtext) {
		if (nomsModifiables==null) nomsModifiables = new ArrayList<JTextComponent>(5);
		nomsModifiables.add(jtext);
		jtext.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (jtext.getParent().getParent() == AppletBiomorph.getFav()) {
					new File("save/" + getName()).renameTo(new File("save/" + jtext.getText()));
				}
				setName(jtext.getText());
			}
		});
	}
	
	
	
	/** 
	 * @param genotype définitif de ce biomorph
	 */
	protected Biomorph(Genotype genotype) {
		this.genotype = genotype;
		nombreTotalDeBiomorphsGeneres++;
		this.nom = ""+nombreTotalDeBiomorphsGeneres;
	}
	
	
	/**
	 * @author Mathieu Guinin
	 * @param genotypeEnfant
	 * @return une copie de ce biomorph avec un génotype non encore muté ou croisé
	 */
	public abstract <Biom extends Biomorph> Biom obtenirEnfant(Genotype genotypeEnfant) ;
	
	
	
	/**
	 *
	 * 
	 * La méthode croisement construit un nouveau génotype à partir de copie des
	 * génes de ce biomorph et de son partenaire.
	 * L'index des génes copiés est le méme que dans le génotype parent et 
	 * provient d'un parents donné avec une probabilité de 50%.
	 * La fin du génotype parent le plus long est recopié tel quel à la suite.
	 * 
	 * @param partenaire : le biomorph avec lequel le génotype sera croisé
	 * @param tauxMutation : nombre flotant entre 0 et 1 représentant l'intensité des mutations
	 * @return le biomorph issu du croisement et avec des mutations
	 */
	public <Biom extends Biomorph> Biom croisement(Biom partenaire,TauxMutation X) {
		
		Genotype genotypeEnfant = new Genotype(this.genotype, partenaire.genotype,X);
		Biom enfant = partenaire.obtenirEnfant(genotypeEnfant);
		
		// Met à jours les liens de parenté :
		enfants.add(enfant);
		partenaire.enfants.add(enfant);
		enfant.parents.add(this);
		enfant.parents.add(partenaire);

		enfant.finaliser();
		return enfant;
		
	}
	
	

@SuppressWarnings("unchecked")

public static <Biom extends Biomorph> ArrayList<Biom> croisementMultiple(ArrayList<Biom> partenaires,TauxMutation X) {
		
		if (partenaires == null) return null;
		ArrayList<Biom> enfants = new ArrayList<Biom>(partenaires.size());
		for (int i=0;i<partenaires.size();i++) enfants.add((Biom) partenaires.get(i).obtenirEnfant(new Genotype(partenaires.get(0).genotype.size())));
		LinkedList<Integer> indexRestants;
		
		int iChrom,current,fini=0,maxIndex=0;
		for (iChrom=0;;iChrom++) {
			
			char flag = 0;
			
			for (Biomorph biom : partenaires) {
				if (biom.genotype.size()<=iChrom) fini++;
				else if (biom.genotype.get(iChrom) != null) {
						flag |= biom.genotype.get(iChrom).flagMutation;
						if ((current = biom.genotype.get(iChrom).size()) >maxIndex) maxIndex=current;
					}
				
			}
			
			if (fini<partenaires.size()){
				for (Biomorph enfant :enfants) enfant.genotype.add(new Chromosome(maxIndex,X,flag));
				int j,iGen,jGen;
				GeneExpression<?> gene;
				indexRestants = null;
				for (iGen=0;iGen<maxIndex;iGen++) {
					if (indexRestants == null || mutBool(false,X.getProbaRecombinaison())) {
						indexRestants = new LinkedList<Integer>(); 
						for (j=0;j<partenaires.size();j++) {
							int k = Biomorph.alea.nextInt(j+1);
							indexRestants.add(k, j);
						}
					}
					j=0;
					for (Biomorph biom : partenaires) {
						gene = biom.genotype.get(iChrom,iGen);
						jGen = indexRestants.get(j);
						if (gene != null) enfants.get(jGen).genotype.get(iChrom).ajouterCopie(gene);
						j++;
					}
				}
			} else break;
		}
		for (Biom enfant : enfants) {
			for (Biom parent:partenaires) {
				enfant.parents.add(parent);
				parent.enfants.add(enfant);
			}
			enfant.finaliser();
		}
		return enfants;
	}
	
	/**
	 *
	 * 
	 * Reproduction de ce biomorph par duplication : chacun des génes du génotype
	 * est recopié au méme emplacement dans le génotype de l'enfant. Au cours
	 * de la copie de chaque géne, des mutations surviennents.
	 * 
	 * @param tauxMutation : nombre flotant entre 0 et 1 représentant l'intensité des mutations
	 * @return le biomorph dupliqué avec des mutations
	 */
	
	
	public <Biom extends Biomorph> Biom duplique(TauxMutation X) {
		Genotype genotypeEnfant = new Genotype(this.genotype, X);
		Biom enfant = obtenirEnfant(genotypeEnfant);
		
		// Met à jours les liens de parenté :
		enfants.add(enfant);
		enfant.parents.add(this);

		enfant.finaliser();
		return enfant;
	}
	
	
	/**
	 * 
	 * 
	 * méthode permettant le dessin d'un biomorph. 
	 * Par défaut la méthode dessin initie l'expression des génes par l'appel du
	 * géne à l'index 0 dans le génotype (par appel on entend expression du géne).
 	 * Le géne 0, que l'on appelera géne racine, pourra faire appel à d'autre géne
 	 * du génotype, qui feront à leur tour appel à d'autre géne, et ainsi de suite.
 	 * 
 	 * Si ce mécanisme par défaut ne convient à l'expression du génome, il suffit
 	 * de surgarger "dessine". Par, une boucle for peut permettre aux génes de 
 	 * s'exprimer à la chaine les uns à la suite des autres.
	 * 
	 * @param positionnement : Transformation qui représente le positionnement
	 * dans l'objet d'affichage.
	 */
	public abstract void dessine(Transform<?,?> positionnement) ;
	
	
	/**
	 * Modification aléatoire d'un boolean
	 * @param b valeur de départ du boolean
	 * @param d probabilité de modification du boolean
	 * @return la valeur modifié (ou non) du boolean
	 */
	public static boolean mutBool(boolean b,double d) {
		if (Biomorph.alea.nextFloat()<d) return !b; else return b;
	}
	
	
	public void finaliser() {
		if (parents==null) parents = new ArrayList<Biomorph>(2);
		if (enfants==null) enfants = new ArrayList<Biomorph>(10);
		if (nomsModifiables==null) nomsModifiables = new ArrayList<JTextComponent>(5);
		genotype.finaliser(this);
	}
	
	public abstract JComponent getIcone(final int largeur);
	
	// interface contenant une méthode de génération aléatoire de Biomorph
	public interface BioGenerator {
		public Biomorph biomorphAleatoire(); 
	}
}


