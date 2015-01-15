package biomorph.forme2D;



import biomorph.abstrait.Transform;

/**
 * 
 * @author Guinin Mathieu
 * 
 * Classe de transformation représentant des similitudes direct ou indirect,
 * avec un décalage colorimétrique. On peut voir les attributs de cette classe
 * comme des nombres complexes: si on pose t = tx + i*ty ; f = fx + i*fy
 * et les coordonnées des points z = x + i*y alors l'image zp des points 
 * s'exprime : zp = f*z + t (notons que si la similitude est indirect, c'est le
 * conjugué de z qui intervient dans le calcul).
 * 
 *
 */
public class Similitude implements Transform<Similitude,Similitude> {
	/**
	 * 
	 */
	/** norme est la norme du vecteur (fx,fy) */
	public float tx,ty,fx,fy,norme;
	public boolean direct;
	public int color;
	private Similitude suite;
	private static Similitude free;
	static {
		for (int i=0;i<50;i++) new Similitude().delet();
	}
	
	public Similitude (float tx,float ty,float fx,float fy,boolean direct,int color){
		recycle(tx,ty,fx,fy,direct,color,(float) Math.sqrt(fx*fx+fy*fy));
	}
	
	private Similitude() {}
	
	public void delet() {
		suite = free;
		free = this;
	}
	
	public void recycle (float tx,float ty,float fx,float fy,boolean direct,int color,float norme) {
		this.tx=tx;
		this.ty=ty;
		this.fx=fx;
		this.fy=fy;
		this.direct=direct;
		this.color=color;
		this.norme=norme;
	}
	/**
	 * concat représente la transformation des complexes comme décrite ci-dessus.
	 */
	@Override
	public Similitude concat(final Similitude t) {
		Similitude res; 
		if (free==null) res = new Similitude();
		else { res = free; free = free.suite;}
		
		float tfy = t.direct? fy:-fy;
		float tty = t.direct? ty:-ty;
		
		res.tx = t.tx+t.fx*tx-t.fy*tty;
		res.ty = t.ty+t.fx*tty+t.fy*tx;
		res.fx = t.fx*fx-t.fy*tfy;
		res.fy = t.fx*tfy+t.fy*fx;
		res.direct = direct==t.direct;
		res.color = color+t.color;
		res.norme = norme*t.norme;
		
		return res;
	}

	
	public String toString() {
		return "tx="+tx+" ty="+ty+" fx="+fx+" fy="+fy+" direct="+direct+" color="+color+" norme="+norme;
	}
}
	


