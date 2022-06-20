package com.app.console;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

import com.app.util.Tools;

/**
 * POC Java du MSPR - EPSI 2021 - DevOps
 * 
 * 
 * @author Developpeur
 *
 */
public class Application {

	private static final String Version = "1.2";
	private static final String staffline = "<li><a href=\"XXXSTAFFXXX\">XXXNAMESXXX</a></li>";

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		if (args.length == 0)
			help();

		String param;
		String name = null, firstName = null, login = null;

		for (int i = 0; i < args.length; i++) {
			param = args[i];
			// System.out.println("arg[" + i + "] =" + param);
			switch (param) {
			case "--I":
				//method pour 
				interactive();
				i = args.length;
				break;
			case "--name":
				// System.out.println("Name : " + args[i]);
				name = args[++i];
				break;
			case "--firstname":
				// System.out.println("First Name : " + args[i]);
				firstName = args[++i];
				break;
			case "--liste":
				readListe();
				break;
			case "--mdp":
				login = args[++i];
				encrypt(login);
				break;
			default:
				System.out.println("Parametre inconnu :" + args[i]);
				break;
			}
		}
		if (name != null && firstName != null) {
			batch(firstName, name);
		}

	}

	private static void batch(String firstName, String name) {
		String login = formatLogin(firstName, false);
		login += formatLogin(name, true);
		System.out.println("Login is :" + login);
		writeFile(login);
	}

	private static void interactive() throws IOException, NoSuchAlgorithmException {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Entrez le pr√©nom :");
		String firstName = scanner.next();

		System.out.print("Entrez le nom :");
		String lastName = scanner.next();

		System.out.print("Entrez le mot de passe :");
		String passwd = scanner.next();

		scanner.close();

		String login = formatLogin(firstName, false);
		login += formatLogin(lastName, true);

		generateHtmlPage(login, firstName, lastName);
		updateHttpPasswordFile(login, passwd);
		generateHtmlIndex(login, firstName, lastName);
		addToStaff(login);
	}

	/**
	 * File Staff.txt Management Add new login
	 * 
	 * @param login
	 * @throws IOException
	 */
	private static void addToStaff(String login) throws IOException {
		String filePath = "files/staff.txt";
		Tools.modifyFile(filePath, login);
	}

	private static void updateHttpPasswordFile(String login, String passwd)
			throws NoSuchAlgorithmException, IOException {
		System.out.println("<<<Modify HttpPassword>>>");
		String filePath = "files/file.mdp";
		String p = login + ":" + encrypt(passwd);
		Tools.modifyFile(filePath, p);
//		FileWriter writer = Tools.openFileWriter("files/file.mdp",true);
//		String p = login+":"+encrypt(passwd) ;
//		writer.append(p+"\n");
//		writer.close();

	}

	private static void help() {
		System.out.println("=============================");
		System.out.println("  Users Managment Console (" + Version + ")\n");
		System.out.println("java -jar managment.jar --I");
		System.out.println("        OR");
		System.out.println("java -jar managment.jar --name [Name] --firstname [firstName]");
		System.out.println("=============================");
	}

	private static String formatLogin(String enter, boolean name) {
		String response = "";		
		
		//tfn = tableau qui va stocker les arguments entrÈs ("toto", false) par exemple 
		//divise l'argument en fonction de l'espace ou du tiret ("jean-toto) par exemple donnera tfn = ["jean", "toto"]
		String[] tfn = enter.split(" |-"); 
		
		//si la valeur du prenom est differente de celle du nom (false) alors il executera ce code
		if (!name) {
			
			//parcours le tableau tfn lettre par lettre
			for (int i = 0; i < tfn.length; i++) {
				//mets la lettre ‡ l'index 0 (la premiere lettre) en minuscule 
				response += tfn[i].substring(0, 1).toLowerCase();
			}
		} else {
			// Exemple :
			// DURAND-DUPONT -> Durantdupont
			//
			for (int i = 0; i < tfn.length; i++) {
				if (i == 0)
					//mets la premiere lettre (index 0) en minuscule et la concatene a partir de la 2eme dans la variable response
					// exemple : "Toto" => "t + oto" donc response = "toto";
					response += tfn[i].substring(0, 1).toLowerCase() + tfn[i].substring(1).toLowerCase();
				else
					// mets tout en minuscule
					response += tfn[i].toLowerCase();
			}
		}
		//renvoi la variable response
		return response;
	}

	private static boolean writeFile(String login) {
		// prend en parametre le login 
		
		//affiche le login entrÈ en paramatre
		System.out.println("Write new login :" + login);
		
		//ecrit les donnÈes dans le fichier staff.txt 
		FileWriter writer = Tools.openFileWriter("files/staff.txt", true);
		try {
			// \n saute ‡ la ligne 
			writer.append(login + "\n");
		} catch (IOException e) {
			System.err.println("ERROR While write file : " + e.getLocalizedMessage());
		}
		//ferme le fichier 
		Tools.closeFile(writer);
		return true;
	}

	private static boolean readListe() {
		System.out.println("ReadListe call");
		Scanner liste = Tools.openFileReaderFromResources("files/liste.txt");
		
		//affiche toutes les donnÈes de "liste"
		while (liste.hasNext()) {
			System.out.println(liste.nextLine());
		}
		return true;
	}

	private static void generateHtmlIndex(String login, String firstName, String lastName) throws IOException {
		String dir = "pages/";
		
		// ("pages/login.html")
		String page = dir + login + ".html";
		
		//dÈfinit un nom de fichier abstrait pour le fichier "index.html" spÈcifiÈ dans le rÈpertoire "site/"            
		File findex = new File("site/index.html");
		
		//dÈfinit un nom de fichier abstrait pour le fichier "index.html.new" spÈcifiÈ dans le rÈpertoire "site/"
		File newIndex = new File("site/index.html.new");
		
		// openFileReader() - dÈfinit un nom de fichier abstrait pour le fichier spÈcifiÈ dans le rÈpertoire entrÈe en parametre 
		//(ici => "index.html")
		Scanner index = Tools.openFileReader("site/index.html");
		
		//ecrit les donnÈes dans un fichier dans le chemin spÈcifiÈ (ici "site/index.html.new")
		FileWriter writer = Tools.openFileWriter("site/index.html.new", false);
		
		
		while (index.hasNextLine()) {
			
			//continue tant qu'il ya des donnÈes dans "index"
			String line = index.nextLine();
			
			//"indexOf" rÈcupÈre l'index de l'ÈlÈment en paramËtre (ici => la balise dont l'id est "idStaff\")
			// si l'index de l'argument 
			if (line.indexOf("<ul id=\"idStaff\"") > -1) {
				//permet d'aller a la ligne
				writer.append(line + "\n");
				
				//remplace la balise "XXXNAMESXXX" par "prenom, nom" ET "XXXSTAFFXXX" par "pages/login.html"
				line = staffline.replace("XXXNAMESXXX", (firstName + " " + lastName)).replace("XXXSTAFFXXX", page);
				
				//"\t" = tab horizontal, en gros ca decale vers la droite. "\n" saute a la ligne
				writer.append("\t\t" + line + "\n");
			} else
				writer.append(line + "\n");
		}

		index.close();
		writer.close();

		findex.delete();
		newIndex.renameTo(findex);

	}

	private static String li = "<li style=\"list-style-type: none\"><input type=\"checkbox\" xxxcheckedxxx >XXXMATERIELXXX</li>";

	private static void generateHtmlPage(String login, String firstName, String lastName) throws IOException {
		// Forme le path du fichier HTML de sortie
		String dirPages = "site/pages/";
		String dirImg = "../images/";
		
		//page = "site/pages/login.html";
		String page = dirPages + login + ".html";
		
		// writer = octets + "site/pages/login.html" 
		FileWriter writer = Tools.openFileWriter(page, false);
		// Accede au fichier template.html
		Scanner template = Tools.openFileReader("templates/template.html");
		// Lit le fichier template et remplace les parties variables
		while (template.hasNextLine()) {
			String line = template.nextLine();
			line = line.replace("XXXTITLEXXX", login + " Page").replace("XXXFNXXX", firstName)
					.replace("XXXLNXXX", lastName).replace("XXXPSXXX", "Agent de s√©curit√©")
					.replace("XXXIMGXXX", dirImg + login + ".jpg");
			writer.append(line + "\n");
			var hasMat = Tools.affectation(login + ".txt");
			if (line.contains("<ul id=\"materiels\"")) {
				writer.append(line + "\n");

				hasMat.forEach((k, v) -> {
					try {
						// remplace "XXXMATERIELXXX" par la valeur de l'argument k
						//verifie le 2eme argument qui est de type boolean, si il est true, l'attribut boolean "checked" sera true (donc cochÈ) 
						writer.append(li.replace("XXXMATERIELXXX", k).replace("xxxcheckedxxx", (v ? "checked" : "")));
						writer.append("\n");
					} catch (IOException e) {
						System.err.println("ERROR While generate materiels list :" + e.getLocalizedMessage());
					}
				});
			}
		}

		template.close();
		writer.close();
	}

	/**
	 * 
	 * @param mdp : Clear Text Password
	 * @return : Crypted password -> SHA-1 (@see
	 *         https://httpd.apache.org/docs/2.4/en/misc/password_encryptions.html)
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	private static String encrypt(String mdp) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		//Objet de signature "SHA-1"
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] crypto = md.digest(mdp.getBytes());
		String hash = "{SHA}";

		hash += new String(Base64.getEncoder().encode(crypto), "UTF-8");

		System.out.println(hash);

		return hash;
	}
}
