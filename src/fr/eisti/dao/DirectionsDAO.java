package fr.eisti.dao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class DirectionsDAO {

	//################## CALCUL ITINERAIRES CHEMINS BARS ############
	//Fonction générale de création du geoJson résultat
	public static String outputJsonFromRecuit(double[][] matriceCoord, ArrayList<Integer> resultRecuit, String[] matriceNoms) throws UnsupportedEncodingException, JSONException, InterruptedException {
		int nbWaypoints = resultRecuit.size();
		String jsonRes = "[{ \"type\":\"Feature\","
		+ "\"geometry\":{\"type\":\"MultiLineString\","
		+ "\"coordinates\":[";
				
		jsonRes = constructJsonCallApi(matriceCoord, resultRecuit, nbWaypoints, jsonRes);
		jsonRes+="]},"
				+ "\"properties\":{\"nom\":\"Chemin entre les bars\"}"
				+ "},";
		jsonRes = ajoutPointsOrdreNomJsonRetour(resultRecuit, matriceCoord, matriceNoms, jsonRes);
		jsonRes+="]";
//		affDivers(matriceCoord, resultRecuit, jsonRes);		
		return jsonRes;
	}
	
	//Ajoute les marqueurs de position avec nom des bars et ordre à suivre pour le chemin
	public static String ajoutPointsOrdreNomJsonRetour(ArrayList<Integer> resultRecuit, double[][] matriceCoord, String[] matriceNoms, String jsonRes) {
		int nbRes, nbNoms, i;
		nbRes = resultRecuit.size();
		nbNoms = matriceNoms.length;
		
		if (nbRes == nbNoms) {
			for (i = 0 ; i < nbRes ; i++) {//Pour chaque bar
				Integer ordreVisite = resultRecuit.get(i);
				double latitude = matriceCoord[ordreVisite-1][0];
				double longitude = matriceCoord[ordreVisite-1][1];
				String nom = matriceNoms[ordreVisite-1];
				
				jsonRes+="{ \"type\":\"Feature\","
						+ "\"geometry\":{\"type\":\"Point\",\"coordinates\":["+longitude+","+latitude+"]},"
						+ "\"properties\":{\"Ordre visite\":\""+ordreVisite+"\","
								+ "\"nom\":\""+nom+"\"}}";
				if (i != (nbRes-1)) {//Si ce n'est pas le dernier point
					jsonRes+=",";
				}
			}
		}
		
		return jsonRes;
	}
	
	//Fontion d'appel de l'API tous les 8 bars, construit le json pour afficher la ligne
	public static String constructJsonCallApi(double[][] matriceCoord, ArrayList<Integer> resultRecuit, int nbWaypoints, String jsonRes) throws UnsupportedEncodingException, JSONException, InterruptedException {
//		int nbIt10 = nbWaypoints/10;//Nombre d'itérations par sauts de 10 points (Origine, Destination, 8 waypoints maxi)
//		int nbIt10Reel = nbIt10*9;
//		int nbItRest = nbWaypoints - nbIt10*10;//Nombre d'itérations restantes après avoir fait tous les cas à 10 points
		
//		jsonRes = appelBoucleSelonNbWaypoints(matriceCoord, resultRecuit, jsonRes, nbWaypoints, nbIt10, nbIt10Reel, nbItRest);
		jsonRes = appelBoucleDeuxPoints(matriceCoord, resultRecuit, jsonRes, nbWaypoints);
		
		return jsonRes;
	}
	
	//Fonction d'appel de la boucle tous les 2 points
	public static String appelBoucleDeuxPoints(double[][] matriceCoord, ArrayList<Integer> resultRecuit, String jsonRes, int nbWaypoints) throws InterruptedException, JSONException {
		int i;
		
		for (i = 0 ; i < nbWaypoints-1 ; i++) {
			Integer ordreVisite = resultRecuit.get(i)-1;
			double latitudeStart = matriceCoord[ordreVisite][0];
			double longitudeStart = matriceCoord[ordreVisite][1];
			double latitudeStop = matriceCoord[ordreVisite+1][0];
			double longitudeStop = matriceCoord[ordreVisite+1][1];
			
			String apiCall="https://maps.googleapis.com/maps/api/directions/json?origin="+latitudeStart+","+longitudeStart+"&destination="+latitudeStop+","+longitudeStop+"&mode=walking&key=AIzaSyDUkya_hOxVHDqHvpxQ366Acm2zXj5-vq0";
			jsonRes = boucleAttenteAppelApiGoogle(apiCall, jsonRes, (i==(nbWaypoints-2)));
		}
		
		return jsonRes;
	}
	
	//Effectue l'appel de la boucle pour générer l'url selon le nombre de waypoints
	public static String appelBoucleSelonNbWaypoints(double[][] matriceCoord, ArrayList<Integer> resultRecuit, String jsonRes, int nbWaypoints, int nbIt10, int nbIt10Reel, int nbItRest) throws UnsupportedEncodingException, InterruptedException, JSONException {
		String apiCall;
		int nbItEnCours;
		
		if (nbIt10 == 0) {//Si on a pas besoin d'itérer de 10 en 10 car on a moins de 10 points
			apiCall = constructUrlAppelApi(matriceCoord, resultRecuit, 0, nbWaypoints-1);
			jsonRes = boucleAttenteAppelApiGoogle(apiCall, jsonRes, true);
		} else {
			for (nbItEnCours = 0 ; nbItEnCours < nbIt10Reel ; nbItEnCours+=9) {
				apiCall = constructUrlAppelApi(matriceCoord, resultRecuit, nbItEnCours, nbItEnCours+9);//Construction de l'url pour 10 points (1 origin, 1 destination, 8 waypoints)
				jsonRes = boucleAttenteAppelApiGoogle(apiCall, jsonRes, false);//Appel de le boucle pour contacter l'api google
			}
		
			//Construction de l'url pour les points restants
			apiCall = constructUrlAppelApi(matriceCoord, resultRecuit, nbIt10Reel-1, nbWaypoints-1);
			jsonRes = boucleAttenteAppelApiGoogle(apiCall, jsonRes, true);
		}
		return jsonRes;
	}
	
	//Effectue l'appel de l'api google selon l'url donnée, retourne le json modifié selon le parsing du json retourné par l'api
	public static String boucleAttenteAppelApiGoogle(String apiCall, String jsonRes, boolean lastPoints) throws InterruptedException, JSONException {
		String jsonLine;
		do {
			jsonLine = DatastoreDAO.getCode(apiCall);//On récupère le résultat de la requete en cours que l'on va parser et ajouter au json
			if (!DistanceDAO.checkStatusJsonApi(jsonLine)) {//Si le statut n'est pas ok
				Thread.sleep(100);//On attend 100 millisecondes (Car on a le droit à 2 requetes/seconde)
			} else {//Sinon, on parse le json de l'api et on l'ajoute au json de retour
				jsonRes = parsingJsonApiDirectionAjoutRetour(jsonRes, lastPoints, jsonLine);
			}
		} while (!DistanceDAO.checkStatusJsonApi(jsonLine));//Tant que le statut n'est pas ok, on recontacte l'api google
		
		return jsonRes;
	}
	
	//Parsing du json retourné par l'api et ajout au json de retour
	public static String parsingJsonApiDirectionAjoutRetour(String jsonRes, boolean lastPoints, String jsonLine) throws JSONException {
		ArrayList<Double[]> listeCoordonneesLigne;
		int nbCoord, i;
		
		listeCoordonneesLigne = extractCoordinatesFromJsonApi(jsonLine);
		nbCoord = listeCoordonneesLigne.size();
		
		jsonRes+="[";
		for (i = 0 ; i < nbCoord ; i++) {
			jsonRes += "["
					+listeCoordonneesLigne.get(i)[1]+","
					+listeCoordonneesLigne.get(i)[0]+""
				+ "]";
			if (!(lastPoints && (i == nbCoord-1))) {//NOT(Si on est bien sur les derniers points à ajouter et qu'on atteint le tout dernier point des derniers points (Oh mon dieu ...))
				jsonRes+=",";
			} 
		}
		jsonRes+="]";
		if (!lastPoints) {
			jsonRes+=",";
		}
		
		return jsonRes;
	}
	
	//Génére la liste des coordonnées du json de retour à partir du json envoyé par l'API google
	public static ArrayList<Double[]> extractCoordinatesFromJsonApi(String jsonLine) throws JSONException {
		ArrayList<Double[]> listeCoordonneesLigne = new ArrayList<>();
		JSONObject jsonApi, jsonRoutes, jsonLegs, jsonStep, startLocation, endLocation;
		JSONArray routesArray, legsArray, stepsArray;
		int nbSteps, nbLegs, i, j;
		
		jsonApi = new JSONObject(jsonLine);
		routesArray = jsonApi.getJSONArray("routes");//Récupération array englobant le tout
		jsonRoutes = routesArray.getJSONObject(0);//Il n'y en a qu'un si l'objet est valide, on prend donc le premier
		legsArray = jsonRoutes.getJSONArray("legs");//Récup de l'array juste avant les étapes avec coordonnées
		nbLegs = legsArray.length();
		
		for (i = 0 ; i < nbLegs ; i++) {
			jsonLegs = legsArray.getJSONObject(i);//On va chercher le tableau des "legs" en cours
			stepsArray = jsonLegs.getJSONArray("steps");//Recuperation des steps du leg en courant, qui contient les coordonnées pour la ligne à dessiner
			nbSteps = stepsArray.length();
			
			for (j = 0 ; j < nbSteps ; j++) {
				jsonStep = stepsArray.getJSONObject(j);
				startLocation = jsonStep.getJSONObject("start_location");
				Double[] coordTmpStart = new Double[2];
				coordTmpStart[0] = startLocation.getDouble("lat"); coordTmpStart[1] = startLocation.getDouble("lng");//Ajout start location
				listeCoordonneesLigne.add(coordTmpStart);//On ajoute seulement la start location car le point suivant part de la "end location", on évite les doublons inutiles
//				if (i == nbSteps-1) {//Si on arrive sur le dernier élément, on ajoute la "end location" (Supprimer cette condition pour avoir les doublons)
					endLocation = jsonStep.getJSONObject("end_location");
					Double[] coordTmpEnd = new Double[2];
					coordTmpEnd[0] = endLocation.getDouble("lat"); coordTmpEnd[1] = endLocation.getDouble("lng");//Ajout end location
					listeCoordonneesLigne.add(coordTmpEnd);
//				} 
			}
		}
		
		return listeCoordonneesLigne;
	}
	
	//Construction de la chaine de caractère de l'url à appeller
	public static String constructUrlAppelApi(double[][] matriceCoord, ArrayList<Integer> resultRecuit, int debut, int fin) throws UnsupportedEncodingException {
		int i;
		String pipe = URLEncoder.encode("|", "UTF-8").toString();
		String urlRes="https://maps.googleapis.com/maps/api/directions/json?origin=";
		
		//Origine
		urlRes += matriceCoord[resultRecuit.get(debut)-1][0]+","+matriceCoord[resultRecuit.get(debut)-1][1];
		//Destination
		urlRes += "&destination="+matriceCoord[resultRecuit.get(fin)-1][0]+","+matriceCoord[resultRecuit.get(fin)-1][1];
		
		//Waypoints
		if ((fin - debut + 1) > 2) {//Seulement si on a plus de deux points
			urlRes += "&waypoints=";
			for (i = debut+1 ; i < fin ; i++) {
				urlRes += matriceCoord[resultRecuit.get(i)-1][0]+","+matriceCoord[resultRecuit.get(i)-1][1];
				if (i != (fin -1)) {
					urlRes+=pipe;
				}
			}
		}
		
		urlRes+="&mode=walking";		
		return urlRes;
	}
	
	
	//Affichages divers pour le debug #INUTILE
	public static void affDivers(double[][] matriceCoord, ArrayList<Integer> resultRecuit, String jsonRes) {
		System.out.println("Matrice coordonnées : ");
		for (int i = 0 ; i < matriceCoord.length ; i++) {
			for (int j = 0 ; j < matriceCoord[i].length ; j++) {
				System.out.print("| "+matriceCoord[i][j]);
			}
			System.out.println();
		}
		
		System.out.println("Chemin résultat :");
		for (int tmp = 0 ; tmp < resultRecuit.size() ; tmp++) {
			System.out.println(resultRecuit.get(tmp));
		}
		System.out.println("Json résultat :"+jsonRes);
	}
	
	//########################## Directions pour le barycentre
	//Appelle toutes les fonctions pourt tracer l'itineraire des marqeurs au bar le plus proche
	public static String generateJsonDirectionsMarqueursToClosestBar(String objectToReturn, double latBary, double longBary, double latClosest, double longClosest, double[][] matriceCoordMarqueurs) throws InterruptedException, JSONException {
		objectToReturn += "{ \"type\":\"Feature\","
				+ "\"geometry\":{\"type\":\"MultiLineString\","
				+ "\"coordinates\":[";
		
		objectToReturn=multiLineStringBarycentreFromApi(objectToReturn, latBary, longBary, latClosest, longClosest, matriceCoordMarqueurs);
		
		objectToReturn+="]},"
				+ "\"properties\":{\"nom\":\"Chemin jusqu'au bar le plus proche\"}"
				+ "}";
		
		return objectToReturn;
	}
	
	//Effectue l'appel de l'API pour chaque point et ajoute la ligne au json à retourner
	public static String multiLineStringBarycentreFromApi(String objectToReturn, double latBary, double longBary, double latClosest, double longClosest, double[][] matriceCoordMarqueurs) throws InterruptedException, JSONException {
		int i;
		int nbMarqueurs = matriceCoordMarqueurs.length;
		
		for (i = 0 ; i < nbMarqueurs ; i++) {
			double latitudeStart = matriceCoordMarqueurs[i][0];
			double longitudeStart = matriceCoordMarqueurs[i][1];
			
			String apiCall="https://maps.googleapis.com/maps/api/directions/json?origin="+latitudeStart+","+longitudeStart+"&destination="+latClosest+","+longClosest+"&mode=walking&key=AIzaSyDUkya_hOxVHDqHvpxQ366Acm2zXj5-vq0";
			objectToReturn= boucleAttenteAppelApiGoogle(apiCall, objectToReturn, false);
		}
		
		String apiCall = "https://maps.googleapis.com/maps/api/directions/json?origin="+latBary+","+longBary+"&destination="+latClosest+","+longClosest+"&mode=walking&key=AIzaSyDUkya_hOxVHDqHvpxQ366Acm2zXj5-vq0";
		objectToReturn= boucleAttenteAppelApiGoogle(apiCall, objectToReturn, true);
		
		return objectToReturn;
	}
	
}