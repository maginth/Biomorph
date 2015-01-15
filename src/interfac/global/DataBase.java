package interfac.global;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

import biomorph.abstrait.Biomorph;
import biomorph.forme2D.Biomorph2D;

public class DataBase {
	
	public static ArrayList<String> listeServeur = new ArrayList<String>();
	public static String adresseServeur,nomUtilisateur,password,email;
	private static byte[] cpw,pw0,tempPass,incrPW;
	public static int idUtilisateur;
	private static int flagURL,flagLOGIN;
	private static URL url;
	private static MessageDigest md;
	private static final int mdlength = 32;
	private static boolean loged;
	
	
	static {
		try {
	        md = MessageDigest.getInstance("SHA-256");
	    }
	    catch(NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } 
		BufferedReader liste_serveur;
		try {
			liste_serveur = new BufferedReader(new FileReader("liste_serveurs"));
			String line;
			try {
				while ((line = liste_serveur.readLine()) != null) listeServeur.add(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		adresseServeur = listeServeur.get(0);
		new Thread(){
			@Override public void run() {
				chargeURLServeur();
			}
		}.start();
	}
	
	static byte[] UTF8(String s) {
		try {
			return s.getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void setPassword() {
		cpw = new byte[3];
		pw0 = UTF8(password);
		cpw[1] = pw0[1];
		cpw[2] = pw0[2];
		cpw[3] = pw0[3];
		pw0[1] = pw0[2] = pw0[3] = 0;
	}

	private static byte[] randomd() {
		byte[] res = new byte[mdlength];
		Random r = new Random();
		r.nextBytes(res);
		return res;
	}
	
	private static void xormd(byte[] b1 ,byte[] b2) {
		for (int i=0;i<mdlength;i++) b1[i] = (byte) (b1[i]^b2[i]);
	}
	
	public static int chargeURLServeur() {
		try {
			if (adresseServeur==null) return flagURL = Flag.PBurl | Flag.PBnotdefined;
			url = new URL(adresseServeur);
			return flagURL = 0;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return Flag.PBurl;
		}
	}
	
	/* LOGIN
	 * request : 
	 * 		property : "X-biomorph"="getid"
	 * 		data : nomUtilisateur | rand1 (byte[64])
	 * response : flag (char) | idUtilisateur (int) | H[CPW|H[PW0]|idUt|rand1]
	 * request : 
	 * 		property : "X-biomorph"="init"
	 * 		data : id | PW0^rand2 | H[CPW | PW0^rand2] | rand2
	 * response : *** les biomorphs perso ***
	 *
	 */
	
	public static int login() throws IOException {
		int flag = flagURL;
		if (nomUtilisateur == null) flag |= Flag.PBuser | Flag.PBnotdefined;
		if (password == null) flag |= Flag.PBpw | Flag.PBnotdefined;
		if (flag != 0) return flag;
		setPassword();
		idUtilisateur = 0;
		tempPass = new byte[mdlength];
		Requete req = new Requete(Flag.getid);
		req.out.writeUTF(nomUtilisateur+"\n");
		req.closeOut();
		if (req.getFlag() == 0) {	
			idUtilisateur = req.in.readInt();
			loged = true;
		}
		return req.flag;
	}
	
	static byte[] makeHash(byte flag) {
		md.reset();
		md.update(new byte[]{(byte) idUtilisateur,(byte) (idUtilisateur>>8),(byte) (idUtilisateur>>16),(byte) (idUtilisateur>>24)});
		md.update(flag);
		md.update(cpw);
		xormd(tempPass,incrPW);
		md.update(tempPass);
		return md.digest();
	}
	
	static private class Requete {
		public Requete(byte requete) throws IOException {
			if (!loged) if ((flag = login()) != 0) return;
			conn = url.openConnection();
			conn.setDoOutput(true);
			//conn.setRequestProperty("X-biomorph", requete);
			out = new ObjectOutputStream(conn.getOutputStream());

			out.write(idUtilisateur);
			out.write(requete);
			incrPW = randomd();
			out.write(incrPW);
			out.write(makeHash(requete));
		}
		ObjectOutputStream out;
		ObjectInputStream in;
		URLConnection conn;
		int flag=0;
		void closeOut() throws IOException {
			out.flush();
			out.close();
		}
		void closeIn() throws IOException {
			in.close();
		}
		int getFlag() throws IOException {
			if (in == null && flag==0) {
				in = new ObjectInputStream(conn.getInputStream());
				if (!conn.getContentType().equals("biomorph-server")) flag=Flag.PBserver;
				else flag=in.read();
				if (flag != 0) return flag;
				in.read(incrPW);
				byte[] hash = new byte[mdlength];
				in.read(hash);
				if (!MessageDigest.isEqual(makeHash((byte) flag),hash)) {
					flag |= Flag.PBintegrite;
					loged = false;
				}
			}
			return flag;
		}
			
	}
	
	
	public static void creerCompte() throws IOException {
		Requete req = new Requete(Flag.creercompte);
		req.out.write(UTF8(nomUtilisateur+'\n'+password+'\n'+email+'\n'));
		req.closeOut();
		req.getFlag();
		req.closeIn();
	}
	
	public static void sendBiomorph(Biomorph bio) throws IOException {
		Requete req = new Requete(Flag.sendbiomorph);
		req.out.write(UTF8(bio.getName()+'\n'));
		req.out.writeObject(bio);
		req.closeOut();
		req.getFlag();
		req.closeIn();
	}
	
	
	public static void modifBio(int idBio,int attribut,String valeur) throws IOException {
		Requete req = new Requete(Flag.modifbio);
		req.out.writeInt(idBio);
		req.out.writeInt(attribut);
		req.out.write(UTF8(valeur));
		req.closeOut();
		req.getFlag();
		req.closeIn();
	}
	
	public static void noteBio(int idBio,int note) throws IOException {
		Requete req = new Requete(Flag.noteBio);
		req.out.writeInt(idBio);
		req.out.writeInt(note);
		req.closeOut();
		req.getFlag();
		req.closeIn();
	}
	
	public static ArrayList<Biomorph2D> getBiomorphs(String NomRecherche,int classementAttribut[],int index,int nb) throws IOException, ClassNotFoundException {
		Requete req = new Requete(Flag.noteBio);
		ArrayList<Biomorph2D> res = null;
		req.out.write(UTF8(NomRecherche+'\n'));
		req.out.writeInt(classementAttribut.length);
		for (int i=0;i<classementAttribut.length;i++) req.out.writeInt(classementAttribut[i]);
		req.closeOut();
		if (req.getFlag() == 0) {
			res = new ArrayList<Biomorph2D>();
			Object obj;
			while ((obj = req.in.readObject()) != null) res.add((Biomorph2D) obj);
		}
		req.closeIn();
		return res;
	}

	
	static public class Flag {
		static final byte getid=0,init=1,getbiomorph=2,sendbiomorph=3,modifbio=4,noteBio=5,creercompte=6;
		public static final int PBpw=1,PBuser=2,PBbiomorph=4,PBrequest=8,PBurl=16,PBnotdefined=32,PBserver=64,PBintegrite=128;
	}
}




