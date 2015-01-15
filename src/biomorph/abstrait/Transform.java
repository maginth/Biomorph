package biomorph.abstrait;

/**
 * 
 * <p>Les class implémentant cette interface sont destinées à recevoir des variables
 * géométriques (par exemple les coefficiants d'une matrice de rotation).
 * Ces variables définissent la position, couleur, etc, des parties du
 * biomorph auxquelles la transformation s'applique.</p>
 * 
 * @author Mathieu Guinin
 * @see Biomorph pour l'explication de la généricité: < ... >
 */
public interface Transform<E extends Transform<?,?>,S extends Transform<?,?>> {
	/**
	 * 
	 * @param trans : transfomation qui s'applique avant la transformation courante (this)
	 * @return S une nouvelle transformation résulta des transformation successive "trans" puis "this"
	 */
	public S concat(final E trans) ;
}
