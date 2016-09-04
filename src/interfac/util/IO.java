package interfac.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import biomorph.abstrait.Genotype;
import biomorph.forme2D.Biomorph2D;

public class IO {
	public static void saveBiomorph(Biomorph2D bio) {
		try {
			File file = new File("save/" + bio.getName());
			file.getParentFile().mkdirs();
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(out));
			oos.writeObject(bio.genotype);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Biomorph2D[] loadSavedBiomorph() {
		try {
			File[] files = new File("save").listFiles();
			Biomorph2D[] res = new Biomorph2D[files.length];
			for (int i=0; i < files.length; i++) {
					ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(files[i])));
					res[i] = new Biomorph2D((Genotype)ois.readObject());
					res[i].setName(files[i].getName());
					res[i].finaliser();
					ois.close();
			}
			return res;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
