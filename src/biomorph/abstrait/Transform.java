package biomorph.abstrait;

/**
 * 
 * <p>Les class impl�mentant cette interface sont destin�es � recevoir des variables
 * g�om�triques (par exemple les coefficiants d'une matrice de rotation).
 * Ces variables d�finissent la position, couleur, etc, des parties du
 * biomorph auxquelles la transformation s'applique.</p>
 * 
 * @author Mathieu Guinin
 * @see Biomorph pour l'explication de la g�n�ricit�: < ... >
 */
public interface Transform<E extends Transform<?,?>,S extends Transform<?,?>> {
	/**
	 * 
	 * @param trans : transfomation qui s'applique avant la transformation courante (this)
	 * @return S une nouvelle transformation r�sulta des transformation successive "trans" puis "this"
	 */
	public S concat(final E trans) ;
}
