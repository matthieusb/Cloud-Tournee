package fr.eisti.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

import fr.eisti.modele.Bar;

public class DatastoreDAO {
	
	//Selection de tous les bars dans la base de données 
	public static List<Entity> selection_bars() {
		List<Entity> results = new ArrayList<>(); 
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Query q = new Query("Bar");
		results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		return results;
	}
	
	public static ArrayList<Bar> listEntity_to_listBar(List<Entity> listEntity) {
		ArrayList<Bar> listBars = new ArrayList<>();
		
		for (Entity e : listEntity) {
			Bar barTmp = new Bar(KeyFactory.keyToString(e.getKey()), e.getProperty("nom").toString(), e.getProperty("adresse").toString(), (Double) e.getProperty("latitude"), (Double) e.getProperty("longitude"));
			listBars.add(barTmp);
		}
		
		return listBars;
	}
	
	public static ArrayList<Bar> getBarsAccueil() {
		List<Entity> allBars = DatastoreDAO.selection_bars();	
		ArrayList<Bar> listBars = DatastoreDAO.listEntity_to_listBar(allBars);
		
		return listBars;
	}
	
	//Effectue l'ajout dans la base
	public static void ajout_bars_datastore (ArrayList<Bar> listeBars) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		for (Bar bar : listeBars) {
			Entity barTmp = new Entity("Bar");
			barTmp.setProperty("nom",bar.getNom());
			barTmp.setProperty("adresse",bar.getAdresse());
			barTmp.setProperty("latitude",bar.getLatitude());
			barTmp.setProperty("longitude",bar.getLongitude());
			datastore.put(barTmp);
		}
	}
	
	public static void ajout_bar_datastore(Bar bar) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity barTmp = new Entity("Bar");
		
		barTmp.setProperty("nom",bar.getNom());
		barTmp.setProperty("adresse",bar.getAdresse());
		barTmp.setProperty("latitude",bar.getLatitude());
		barTmp.setProperty("longitude",bar.getLongitude());
		datastore.put(barTmp);
	}
	
	//Suppression de tous les bars de la liste
	public static void supprimer_bars (List<Entity> listeBars) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		for (Entity bar : listeBars) {
			datastore.delete(bar.getKey());
		}
	}
	
	//Supression d'un bar en particulier avec sa clé
	public static void supprimer_bar_cle (String key) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Key keyReEncoded = KeyFactory.stringToKey(key);		
		datastore.delete(keyReEncoded);
	}
	
	public static String generateTableBars(ArrayList<Bar> listBars) {
		String toReturn="Nombre de bars : "+listBars.size()
				+ "<div class=\"table-responsive\">"
				+ "<br><table class=\"table table-bordered\"><tr> <th> Nom </th> <th> Adresse </th> <th> Latitude </th> <th> Longitude </th> <th> Supprimer </th></tr>";
		for (Bar bTmp : listBars) {
			toReturn+="<tr>";
			toReturn+="<td>"+bTmp.getNom()+"</td>";
			toReturn+="<td>"+bTmp.getAdresse()+"</td>";
			toReturn+="<td>"+bTmp.getLatitude()+"</td>";
			toReturn+="<td>"+bTmp.getLongitude()+"</td>";
			toReturn+="<td><a href=\"javascript:supprBarAccueil('"+bTmp.getKey()+"')\"><img alt=\"cercle\" src=\"./imgs/croix_supprimer.png\" width=\"20px\" height=\"20px\"/></a></td>";
			
			toReturn+="</tr>";
		}
		
		toReturn+="</table> </div>";
		return toReturn;
	}
	
	
	//Récupération du code renvoyé par une requéte sur API GOOGLE
	public static String getCode(String url){
		String code = "";

		//On verif si l'URL existe bien 
		if(urlExists(url)){
			BufferedReader in = null;
			try{
				URL site = new URL(url);
				in = new BufferedReader(new InputStreamReader(site.openStream()));

				String inputLine;
				while ((inputLine = in.readLine()) != null){
					code = code + "\n" + (inputLine);//Permets de lire ligne par ligne
				}
				in.close();
			}
			catch (IOException ex){
				System.out.println("Erreur dans l'ouverture de l'URL : " + ex);
			}
			finally{
				try{
					in.close(); //On ferme notre buffer
				}
				catch (IOException ex){
					System.out.println("Erreur dans la fermeture du buffer : " + ex);
				}
			}
		}
		else{
			System.out.println("Le site n'existe pas ! : "+url);
		}
//		System.out.println("URL demandée API : "+url);
//		System.out.println("Retour API : "+code);
		return code;
	}
	
	//Fonction qui verif si l'URL entree existe bien
	public static boolean urlExists(String url){
		try{
			URL site = new URL(url);
			try{
				site.openStream(); //Si on peut ouvrir un Stream alors forcement l'URL existe
				return true;
			} catch (IOException ex) {
				return false;
			}
		} catch (MalformedURLException ex) {
			return false;
		}
	}

}
