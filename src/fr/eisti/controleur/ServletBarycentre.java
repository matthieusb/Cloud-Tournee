package fr.eisti.controleur;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import fr.eisti.dao.DistanceDAO;

@SuppressWarnings("serial")
public class ServletBarycentre extends HttpServlet {
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		String jsonEntreeChaineMarqueurs = request.getParameter("lieux");
		String jsonEntreeChaineBar = request.getParameter("bars");
//		System.out.println("Chaine en entrée : "+jsonEntreeChaineBar);
		
		if (jsonEntreeChaineMarqueurs == null || jsonEntreeChaineBar == null) {
			System.out.println("Pas de points ou de bars à traiter");
			out.write("erreur");
		} else {
			try {
				JSONObject jsonEntreeMarqueurs = new JSONObject(jsonEntreeChaineMarqueurs);
				JSONObject jsonEntreeBars = new JSONObject(jsonEntreeChaineBar);
				double[][] matriceCoordMarqueurs = parseJsonBary(jsonEntreeMarqueurs);
				double[][] coordBarycentre = calculBarycentre(matriceCoordMarqueurs);
				double[][] matriceCoordBars = DistanceDAO.parseJsonBars(jsonEntreeBars);
				String[] matriceNomsBars = DistanceDAO.parseJsonBarsNoms(jsonEntreeBars);
				
				String objectToReturn = generateJsonBarycentre(coordBarycentre);
				objectToReturn = DistanceDAO.constructJsonCheminBarPlusProche(objectToReturn, matriceCoordMarqueurs, matriceCoordBars, coordBarycentre, matriceNomsBars);
				
				objectToReturn+="]";
				out.write(objectToReturn);
			} catch (Exception e) {
				System.out.println("Erreur parsing du JSON : "+e.getMessage());
				out.write("erreur");
			}
		}
		out.close();
	}
	
	
	//Génére le json relatif au barycentre et l'ajoute au json de retour
	public String generateJsonBarycentre(double[][] coordBarycentre) {
		String objectToReturn;
		
		objectToReturn="[{ \"type\":\"Feature\","
				+ "\"geometry\":{\"type\":\"Point\",\"coordinates\":["+coordBarycentre[0][1]+","+coordBarycentre[0][0]+"]},"
				+ "\"properties\":{\"nom\":\"Point de rendez-vous\""
			+ "}},";
		
		return objectToReturn;
	}
	
	//Parse le json des marqueurs choisis par l'utilisateur
	public double[][] parseJsonBary(JSONObject jsonEntree) throws JSONException, Exception {
		JSONArray points = jsonEntree.getJSONArray("Points");
		int nbPoints = points.length();
		double[][] matriceCoord = null;
		
		if (nbPoints > 0) {//On re vérifier s'il y a des points à ajouter
			matriceCoord = new double[nbPoints][2];
			
			for(int i = 0 ; i < nbPoints ; i++) {
				matriceCoord[i][0] = (double) points.getJSONObject(i).get("lat");
				matriceCoord[i][1] = (double) points.getJSONObject(i).get("long");
//				System.out.println("Point n° "+(i+1)+" Latitude : "+points.getJSONObject(i).get("lat")+" Longitude : "+points.getJSONObject(i).get("long"));
			}
		}
		return matriceCoord;
	}
	
	//Calcule les coordonnées du barycentre avant de le renvoyer sur la carte
	public double[][] calculBarycentre(double[][] matriceCoord) {
		int nbPoints = matriceCoord.length;
		double x,y;
		double[][] coordBarycentre = new double[1][2];
		x = 0.0;
		y = 0.0;
		
		for (int i = 0 ; i < nbPoints ; i++) {
			for (int j = 0 ; j < 2 ; j++) {
				if (j == 0) {//Si on est sur x
					x = x + matriceCoord[i][j];
				} else if (j == 1) {//Si on est sur y
					y = y + matriceCoord[i][j];
				}
			}
		}
		x = x/(1.0*nbPoints);
		y = y/(1.0*nbPoints);
		coordBarycentre[0][0] = x;
		coordBarycentre[0][1] = y;
		
		return coordBarycentre;
	}
	
	
//	for (int i = 0 ; i < matriceCoord.length ; i++) {
//		for (int j = 0 ; j < 2 ; j++) {
//			System.out.print(" "+matriceCoord[i][j]+" ");
//		}
//		System.out.println();
//	}

	
	
	
}
