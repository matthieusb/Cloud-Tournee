package fr.eisti.controleur;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;

import fr.eisti.dao.DatastoreDAO;

@SuppressWarnings("serial")
public class ServletSelectionBarsJson extends HttpServlet {
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		int i, nbBars;
		String objectToReturn = "[";
		
		//On va d'abord chercher les bars dans la BDD
		List<Entity> allBars = DatastoreDAO.selection_bars();
		nbBars = allBars.size();
		
		//On crée le JSON contenant tous les bars
		for (i=0 ; i < nbBars ; i++) {
			Entity bar = allBars.get(i);
			
			if (i == (nbBars -1)) {//Si on arrive au dernier bar
				objectToReturn = objectToReturn+"{ \"type\":\"Feature\","
						+ "\"geometry\":{\"type\":\"Point\",\"coordinates\":["+bar.getProperty("longitude")+","+bar.getProperty("latitude")+"]},"
						+ "\"properties\":{\"nom\":\""+bar.getProperty("nom")+"\","
								+ "\"adresse\":\""+bar.getProperty("adresse")+"\"}"
					+ "}]";
			} else {
				objectToReturn = objectToReturn+"{ \"type\":\"Feature\","
						+ "\"geometry\":{\"type\":\"Point\",\"coordinates\":["+bar.getProperty("longitude")+","+bar.getProperty("latitude")+"]},"
						+ "\"properties\":{\"nom\":\""+bar.getProperty("nom")+"\","
								+ "\"adresse\":\""+bar.getProperty("adresse")+"\"}"
					+ "},";
			}
		}
		out.write(objectToReturn);
		out.close();
	}
}
