package fr.eisti.dao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class DistanceDAO {
	
	//############################ CALCUL DISTANCE MATRIX CHEMINS BARS ############################
	public static double[][] calculMatriceDistance(double[][] matriceCoord) throws UnsupportedEncodingException {
//		System.out.println("Calcul matrice de distance");
		int nbBars = matriceCoord.length;
		double[][] matriceDist;

		//Fabrication de la chaine à envoyer à l'API Google
		String apiCall = fabriqueChaineEnvoiApi(matriceCoord,nbBars);
		//Calcul matrice des distances selon le cas
		matriceDist = createDistanceMatrixAccordingToApiResult(matriceCoord, nbBars, apiCall);
		
		return matriceDist;
	}
	
	//Appelle la bonne fonction de création de matrice des distances selon la réponse de l'API
	public static double[][] createDistanceMatrixAccordingToApiResult(double[][] matriceCoord, int nbBars, String apiCall) {
		double[][] matriceDist = null;
		String jsonLine;
		
		if (nbBars > 10) {
			matriceDist = distanceMatrixVolOiseau(matriceCoord, nbBars);
		} else {
			jsonLine = DatastoreDAO.getCode(apiCall);
			
			try {
				if (checkStatusJsonApi(jsonLine)) {//Si le json de l'api google est retourné correctement
					//Appel parsing json API google
					matriceDist = distanceMatrixFromJsonApiGoogle(jsonLine, nbBars);
				} else {
					//Appeller calcul vol d'oiseau
					matriceDist = distanceMatrixVolOiseau(matriceCoord, nbBars);
				}
			} catch (Exception e) {
				System.out.println("Exception calcul matrice distance : "+e.getMessage());
				matriceDist = distanceMatrixVolOiseau(matriceCoord, nbBars);
			}
		}
		
//		for (int i = 0 ; i < nbBars ; i++) {
//		for (int j = 0 ; j < nbBars ; j++) {
//			System.out.print("| "+matriceDist[i][j]+" ");
//		}
//		System.out.println();
//	}
		return matriceDist;
	}
	
	//##################### GENERATION MATRICE DE DISTANCE RECUIT SIMULE
	//Matrice de distance à vol d'oiseau avec prise en compte de la courbure de la terre
	public static double[][] distanceMatrixVolOiseau(double[][] matriceCoord, int nbBars) {
		int i,j;
		double latA, latB, longA, longB;
		double[][] matriceDist = new double[nbBars][nbBars];
		double pi = Math.PI;
		
		for (i = 0 ; i < nbBars ; i++) {
			for (j = 0 ; j < nbBars ; j++) {
				latA = matriceCoord[i][0]*pi / 180;
				longA = matriceCoord[i][1]*pi / 180;
				latB = matriceCoord[j][0]*pi / 180;
				longB = matriceCoord[j][1]*pi / 180;
				
				matriceDist[i][j] = 6378 * Math.acos(Math.cos(latA) * Math.cos(latB) * Math.cos(longB - longA) + Math.sin(latA) * Math.sin(latB));
			}
		}
		return matriceDist;
	}
	
	//Calcul  de la matrice de distance en utilisant la matrice retournée en json par l'api google
	public static double[][] distanceMatrixFromJsonApiGoogle(String jsonLine, int nbBars) throws JSONException {
		int i,j, nbElements;
		JSONObject jsonApi, elementsObj, distanceObj, distanceValue;
		JSONArray rowsArray, elementsArray;
		double[][] matriceDist = new double[nbBars][nbBars];
		
		jsonApi = new JSONObject(jsonLine);
		rowsArray = jsonApi.getJSONArray("rows");
		int nbRows = rowsArray.length();
		
		for (i = 0 ; i < nbRows ; i++) {
			elementsObj = rowsArray.getJSONObject(i);
			elementsArray = elementsObj.getJSONArray("elements");
			nbElements = elementsArray.length();
			for (j = 0 ; j < nbElements ; j++) {
				distanceObj = elementsArray.getJSONObject(j);
				distanceValue = distanceObj.getJSONObject("distance");
				matriceDist[i][j] = ((Integer) distanceValue.get("value")).doubleValue()/1000.0;
			}
		}
		return matriceDist;
	}
	
	//####################### VERIF STATUT APIs
	//Va chercher le statut du json retourné par l'API Google (Vrai si statut ok, faux sinon)
	public static boolean checkStatusJsonApi(String jsonLine) throws JSONException {
		boolean statusOk = false;
		
		if (jsonLine != null && (!jsonLine.trim().equals(""))) {
			JSONObject jsonApi = new JSONObject(jsonLine);
			String status = (String) jsonApi.get("status");
			statusOk = (status.equals("OK")); 
		}

		return statusOk;
	}
	
	//###################### PREPARATION REQUETE APIs
	//Fabrique la chaine à envoyer à l'api pour la matrice des distances GOOGLE
	public static String fabriqueChaineEnvoiApi(double[][] matriceCoord, int nbBars) throws UnsupportedEncodingException {
		int i;
		String pipe = URLEncoder.encode("|", "UTF-8").toString();
		String apiCall = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
		
		//Remplissage des origines
		for (i = 0 ; i < nbBars-1 ; i++) {
			apiCall += matriceCoord[i][0]+","+matriceCoord[i][1];
			apiCall+=pipe;
		}
		apiCall += matriceCoord[i][0]+","+matriceCoord[i][1]+"&destinations=";
		
		//Remplissage des destinations
		for (i = 0 ; i < nbBars-1 ; i++) {
			apiCall += matriceCoord[i][0]+","+matriceCoord[i][1];
			apiCall+=pipe;
		}
		apiCall += matriceCoord[i][0]+","+matriceCoord[i][1]+"&mode=walking&language=en-GB&key=AIzaSyDUkya_hOxVHDqHvpxQ366Acm2zXj5-vq0";
		return apiCall;	
	}
	
	//####################### PARSING JSON BARS EN ENTREE
	//Parsing du json pour récupérer les bars
	public static double[][] parseJsonBars(JSONObject jsonEntree) throws JSONException, Exception {
//		System.out.println("Extraction coordonnées des bars");
		JSONArray points = jsonEntree.getJSONArray("Points");
		int nbPoints = points.length();
		double[][] matriceCoord = null;
		
		if (nbPoints > 0) {//On re vérifie s'il y a des points à ajouter
			matriceCoord = new double[nbPoints][2];
			
			for(int i = 0 ; i < nbPoints ; i++) {
				matriceCoord[i][0] = (double) points.getJSONObject(i).get("lat");
				matriceCoord[i][1] = (double) points.getJSONObject(i).get("long");
//				System.out.println("Point n° "+(i+1)+" Latitude : "+points.getJSONObject(i).get("lat")+" Longitude : "+points.getJSONObject(i).get("long"));
			}
		}
		return matriceCoord;
	}
	
	public static String[] parseJsonBarsNoms(JSONObject jsonEntree) throws JSONException, Exception {
//		System.out.println("Extraction noms des bars");
		JSONArray points = jsonEntree.getJSONArray("Points");
		int nbPoints = points.length();
		String[] matriceNoms = null;
		
		if (nbPoints > 0) {//On re vérifie s'il y a des points à ajouter
			matriceNoms = new String[nbPoints];
			
			for(int i = 0 ; i < nbPoints ; i++) {
				matriceNoms[i] = (String) points.getJSONObject(i).get("nom");
//				System.out.println("Point n° "+(i+1)+" Nom : "+points.getJSONObject(i).get("nom")+" Latitude : "+points.getJSONObject(i).get("lat")+" Longitude : "+points.getJSONObject(i).get("long"));
			}
		}
		return matriceNoms;
	}
	
	//############################ DISTANCE POUR BARYCENTRE ET BAR LE PLUS PROCHE ############################
	//Fait tous les appels pour ajouter l'itinéraire des marqueurs au bar le plus proche
	public static String constructJsonCheminBarPlusProche(String objectToReturn, double[][] matriceCoordMarqueurs, double[][] matriceCoordBars, double[][] coordBarycentre, String[]  matriceNomsBars) throws UnsupportedEncodingException, InterruptedException, JSONException {
		double latBary, longBary;
		int indiceBarPlusProche;
		int nbBars = matriceCoordBars.length;

		//Recup coordonnées barycentre
		latBary = coordBarycentre[0][0];
		longBary = coordBarycentre[0][1];

		//Calcul distance bars et barycentre + Recupération bar distance minimale (Coordonnées + nom)
		indiceBarPlusProche = findBarLePlusProche(latBary, longBary, matriceCoordBars, nbBars);

		//Ajout point bar distance minimale au json
		objectToReturn += "{ \"type\":\"Feature\","
				+ "\"geometry\":{\"type\":\"Point\",\"coordinates\":["+matriceCoordBars[indiceBarPlusProche][1]+","+matriceCoordBars[indiceBarPlusProche][0]+"]},"
				+ "\"properties\":{\"type\" : \"Bar le plus proche du point de rendez-vous\",\"nom\":\""+matriceNomsBars[indiceBarPlusProche]+"\""
			+ "}},";
		
		//Ajout itinéraire entre barycentre
		objectToReturn=DirectionsDAO.generateJsonDirectionsMarqueursToClosestBar(objectToReturn, latBary, longBary, matriceCoordBars[indiceBarPlusProche][0], matriceCoordBars[indiceBarPlusProche][1], matriceCoordMarqueurs);

		return objectToReturn;
	}

	//Retourne l'indice du bar le plus proche
	public static int findBarLePlusProche(double latBary, double longBary, double[][] matriceCoordBars, int nbBars) throws UnsupportedEncodingException {
		String jsonLine;
		double[] matriceDist=null;
		int indiceRes=0;
		
		String apiCall = fabriqueChaineEnvoiApiBarycentre(matriceCoordBars, nbBars, latBary, longBary);//génération url d'appel de l'api google pour la matrice de distance
		jsonLine = DatastoreDAO.getCode(apiCall);
		try {
			if (checkStatusJsonApi(jsonLine)) {//Si le json de l'api google est retourné correctement
				matriceDist = matriceDistanceBarycentreFromJsonApi(jsonLine, nbBars);	
				if (matriceDist == null) {
					matriceDist = matriceDistanceBarycentreVolDoiseau(latBary, longBary, matriceCoordBars, nbBars);
				}
			} else {
				matriceDist = matriceDistanceBarycentreVolDoiseau(latBary, longBary, matriceCoordBars, nbBars);
			}
		} catch (Exception e) {
			System.out.println("Exception calcul matrice distance : "+e.getMessage());
			matriceDist = matriceDistanceBarycentreVolDoiseau(latBary, longBary, matriceCoordBars, nbBars);
		}
		
		if (matriceDist != null) {
			indiceRes = trouveIndiceDistanceLaPlusFaible(matriceDist, nbBars);
		}

		return indiceRes;
	}
	
	public static int trouveIndiceDistanceLaPlusFaible(double[] matriceDist, int nbBars) {
		int i,indiceRes;
		double min, tmp;
		
		min = matriceDist[0];
		indiceRes = 0;
		
		for (i = 1 ; i < nbBars ; i++) {
			tmp = matriceDist[i];
			if (tmp < min) {
				min = tmp;
				indiceRes = i;
			}
		}
		return indiceRes;
	}
	
	//Calcule la matrice de distance à vol d'oiseau pour barycentre 
	public static double[] matriceDistanceBarycentreVolDoiseau(double latBary, double longBary, double[][] matriceCoordBars, int nbBars) {
		double[] matriceDist = new double[nbBars];
		double latBaryPi, latB, longBaryPi, longB;
		double pi = Math.PI;
		int i;
		
		latBaryPi = latBary*pi / 180;
		longBaryPi = longBary*pi / 180;
		
		for (i = 0 ; i < nbBars ; i++) {
			latB = matriceCoordBars[i][0]*pi / 180;
			longB = matriceCoordBars[i][1]*pi / 180;
			
			matriceDist[i] = 6378 * Math.acos(Math.cos(latBaryPi) * Math.cos(latB) * Math.cos(longB - longBaryPi) + Math.sin(latBaryPi) * Math.sin(latB));
		}
		
		return matriceDist;
	}
	
	//Parse le json renvoyé par l'api et génère la matrice de distance, renvoie null si quelque chose cloche (Pas le même nombre de bars, probleme de json ...)
	public static double[] matriceDistanceBarycentreFromJsonApi(String jsonLine, int nbBars) throws JSONException {
		double[] matriceDist = new double[nbBars];
		int nbRows, nbElements, i;
		JSONObject jsonApi, elementsObj, distanceObj, distanceValue;
		JSONArray rowsArray, elementsArray;
		
		jsonApi = new JSONObject(jsonLine);
		rowsArray = jsonApi.getJSONArray("rows");
		nbRows = rowsArray.length();
		
		if (nbRows == 1) {
			elementsObj = rowsArray.getJSONObject(0);
			elementsArray = elementsObj.getJSONArray("elements");
			nbElements = elementsArray.length();
			
			if (nbElements != nbBars) {
				matriceDist = null;
			} else {
				for (i = 0 ; i < nbElements ; i++) {
					distanceObj = elementsArray.getJSONObject(i);
					distanceValue = distanceObj.getJSONObject("distance");
					matriceDist[i] = ((Integer) distanceValue.get("value")).doubleValue()/1000.0;
				}
			}
		} else {
			matriceDist = null;
		}
		
		return matriceDist;
	}
	
	public static String fabriqueChaineEnvoiApiBarycentre(double[][] matriceCoordBars, int nbBars, double latBary, double longBary) throws UnsupportedEncodingException {
		int i;
		String pipe = URLEncoder.encode("|", "UTF-8").toString();
		String apiCall="https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
		
		
		apiCall += latBary+","+longBary;//Origin = barycentre
		apiCall+="&destinations=";
		//Ajouts des bars (Destinations)
		for (i = 0 ; i < nbBars-1 ; i++) {
			apiCall += matriceCoordBars[i][0]+","+matriceCoordBars[i][1];
			apiCall += pipe;
		}
		
		apiCall += matriceCoordBars[i][0]+","+matriceCoordBars[i][1]+"&mode=walking&language=en-GB&key=AIzaSyDUkya_hOxVHDqHvpxQ366Acm2zXj5-vq0";
		
		return apiCall;
	}	
}
