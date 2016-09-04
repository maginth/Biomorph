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
 * Les classes h�ritant de Biomorph doivent contenir toutes les donn�s 
 * sur lesquels agiront leurs g�nes. Ces donn�s repr�sentent le ph�notype. 
 * Le g�notype est une ArrayListe de tous les g�nes succeptibles de s'exprimer
 * pour d�finir ce ph�notype.
 * 
 * </p>
 *<p> <h1><b>LA GENERICITE < ... ></b></h1>
 *
 *Les classes du package biomorph.abstrait d�finissent la structure g�n�rale des
 *biomorphs, elles sont �troitement li�es entre elles. Les classes h�ritants de 
 *cette structure devront pouvoir acc�der entres elles � leurs m�thodes/attribus 
 *rajout�, sans recourir sans arr�t � des "cast", d'o� l'utilisation des 
 *g�n�rics < ... > qui pr�cise les autres classes de la structure h�rit�. 
 *Ainsi plusieurs types tr�s diff�rent de biomorphs pourrons h�riter de toutes
 *les fonctions de croisement,duplication,division des g�nes, dessin sans 
 *les r�impl�menter totalements.
 *</p>
 * @author Mathieu Ginin
 */

public abstract class Biomorph implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	/** g�n�rateur l'al�atoire utile pour g�n�rer des mutation et des g�nes al�atoire
	 * Toute les g�n�rations al�atoire doivent utiliser ce g�n�rateur, 
	 * de mani�re � ce qu'en l'initialisant, les tests donnent les m�mes r�sultats
	 * */
	public static Random alea = new Random();
	
	
	
	private static int nombreTotalDeBiomorphsGeneres=0;
	/** genotype rassemble l'ensembe des g�nes de ce biomorph. 
	 * <i>En principe, le g�notype ne devrait pas subir de modification :
	 * Les biomorphs sont vus comme des organismes pluricellulaires, 
	 * la mutation d'une seule cellule n'a pas de cons�quence sur le
	 * g�notype global. Seul les enfants de ce biomorph h�riteront
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
	 * @param genotype d�finitif de ce biomorph
	 */
	protected Biomorph(Genotype genotype) {
		this.genotype = genotype;
		nombreTotalDeBiomorphsGeneres++;
		this.nom = ""+nombreTotalDeBiomorphsGeneres;
	}
	
	
	/**
	 * @author Mathieu Guinin
	 * @param genotypeEnfant
	 * @return une copie de ce biomorph avec un g�notype non encore mut� ou crois�
	 */
	public abstract <Biom extends Biomorph> Biom obtenirEnfant(Genotype genotypeEnfant) ;
	
	
	
	/**
	 *
	 * 
	 * La m�thode croisement construit un nouveau g�notype � partir de copie des
	 * g�nes de ce biomorph et de son partenaire.
	 * L'index des g�nes copi�s est le m�me que dans le g�notype parent et 
	 * provient d'un parents donn� avec une probabilit� de 50%.
	 * La fin du g�notype parent le plus long est recopi� tel quel � la suite.
	 * 
	 * @param partenaire : le biomorph avec lequel le g�notype sera crois�
	 * @param tauxMutation : nombre flotant entre 0 et 1 repr�sentant l'intensit� des mutations
	 * @return le biomorph issu du croisement et avec des mutations
	 */
	public <Biom extends Biomorph> Biom croisement(Biom partenaire,TauxMutation X) {
		
		Genotype genotypeEnfant = new Genotype(this.genotype, partenaire.genotype,X);
		Biom enfant = partenaire.obtenirEnfant(genotypeEnfant);
		
		// Met � jours les liens de parent� :
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
	 * Reproduction de ce biomorph par duplication : chacun des g�nes du g�notype
	 * est recopi� au m�me emplacement dans le g�notype de l'enfant. Au cours
	 * de la copie de chaque g�ne, des mutations surviennents.
	 * 
	 * @param tauxMutation : nombre flotant entre 0 et 1 repr�sentant l'intensit� des mutations
	 * @return le biomorph dupliqu� avec des mutations
	 */
	
	
	public <Biom extends Biomorph> Biom duplique(TauxMutation X) {
		Genotype genotypeEnfant = new Genotype(this.genotype, X);
		Biom enfant = obtenirEnfant(genotypeEnfant);
		
		// Met � jours les liens de parent� :
		enfants.add(enfant);
		enfant.parents.add(this);

		enfant.finaliser();
		return enfant;
	}
	
	
	/**
	 * 
	 * 
	 * m�thode permettant le dessin d'un biomorph. 
	 * Par d�faut la m�thode dessin initie l'expression des g�nes par l'appel du
	 * g�ne � l'index 0 dans le g�notype (par appel on entend expression du g�ne).
 	 * Le g�ne 0, que l'on appelera g�ne racine, pourra faire appel � d'autre g�ne
 	 * du g�notype, qui feront � leur tour appel � d'autre g�ne, et ainsi de suite.
 	 * 
 	 * Si ce m�canisme par d�faut ne convient � l'expression du g�nome, il suffit
 	 * de surgarger "dessine". Par, une boucle for peut permettre aux g�nes de 
 	 * s'exprimer � la chaine les uns � la suite des autres.
	 * 
	 * @param positionnement : Transformation qui repr�sente le positionnement
	 * dans l'objet d'affichage.
	 */
	public abstract void dessine(Transform<?,?> positionnement) ;
	
	
	/**
	 * Modification al�atoire d'un boolean
	 * @param b valeur de d�part du boolean
	 * @param d probabilit� de modification du boolean
	 * @return la valeur modifi� (ou non) du boolean
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
	
	// interface contenant une m�thode de g�n�ration al�atoire de Biomorph
	public interface BioGenerator {
		public Biomorph biomorphAleatoire(); 
	}
}


