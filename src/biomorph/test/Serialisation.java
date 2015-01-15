package biomorph.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import biomorph.forme2D.Biomorph2D;


public class Serialisation {

	public static Biomorph2D biomorph;
	static public void serialUnserial(Biomorph2D biom) {
		   
	    try {
	      FileOutputStream fichier = new FileOutputStream("biomorph Serialisé.txt");
	      ObjectOutputStream oos = new ObjectOutputStream(fichier);
	      oos.writeObject(biom);
	      oos.flush();
	      oos.close();
	    }
	    catch (java.io.IOException e) {
	      e.printStackTrace();
	    }
	    
	    try {
	        FileInputStream fichier = new FileInputStream("biomorph Serialisé.txt");
	        ObjectInputStream ois = new ObjectInputStream(fichier);
	        Biomorph2D res = (Biomorph2D) ois.readObject();
	        biomorph = res;
	        System.out.println(res);
	      } 
	      catch (java.io.IOException e) {
	        e.printStackTrace();
	      }
	      catch (ClassNotFoundException e) {
	        e.printStackTrace();
	      }
	}
	
	

}
