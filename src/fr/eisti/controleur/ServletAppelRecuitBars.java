package fr.eisti.controleur;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONObject;

import fr.eisti.dao.DirectionsDAO;
import fr.eisti.dao.DistanceDAO;
import fr.eisti.recuit.RecuitSimule;

@SuppressWarnings("serial")
public class ServletAppelRecuitBars extends HttpServlet {
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		String jsonEntreeChaine = request.getParameter("bars");

		if (jsonEntreeChaine == null) {
			System.out.println("Pas de points à traiter");
			out.write("erreur");
		} else {
			try {
				double start, duree;
				start = System.nanoTime();
				
				JSONObject jsonEntree = new JSONObject(jsonEntreeChaine);
				//Récupération des coordonnées de chaque bar
				double[][] matriceCoord = DistanceDAO.parseJsonBars(jsonEntree);
				//Recuperation des noms des bars
				String[] matriceNoms = DistanceDAO.parseJsonBarsNoms(jsonEntree);
				//Récupération de la matrice des distances
				double[][] matriceDist = DistanceDAO.calculMatriceDistance(matriceCoord);
				//Calcul du chemin via recuit simulé
				ArrayList<Integer> resultRecuit = RecuitSimule.recuit(80, 0.9, matriceDist);
				//Génération du json pour le chemin de retour
				String jsonToReturn = DirectionsDAO.outputJsonFromRecuit(matriceCoord, resultRecuit, matriceNoms);	
				
				duree = (System.nanoTime() - start)/1000000000.0;
				System.out.println("TEMPS EXECUTION : "+duree+" secondes");
				out.write(jsonToReturn);
			} catch (Exception e) {
				System.out.println("Erreur calcul chemin recuit : "+e.getMessage());
//				e.printStackTrace();
				out.write("erreur");
			}
		}
		out.close();
	}	
}
