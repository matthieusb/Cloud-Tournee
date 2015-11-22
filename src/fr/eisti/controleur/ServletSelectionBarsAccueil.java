package fr.eisti.controleur;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Comparateur.Comparateur;

import com.google.appengine.api.datastore.Entity;

import fr.eisti.dao.DatastoreDAO;
import fr.eisti.modele.Bar;

@SuppressWarnings("serial")
public class ServletSelectionBarsAccueil extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		PrintWriter out = response.getWriter();
		request.setCharacterEncoding("UTF-8");
		
		try {
			List<Entity> allBars = DatastoreDAO.selection_bars();	
			ArrayList<Bar> listBars = DatastoreDAO.listEntity_to_listBar(allBars);
			
			Collections.sort(listBars, new Comparateur());
			
			if (!listBars.isEmpty()) {
				String toReturn = DatastoreDAO.generateTableBars(listBars);
				out.write(toReturn);
				
			} else {
				out.write("Aucun bar dans la base de donnees");
			}
		} catch (Exception e) {
			System.out.println("Erreur : "+e.getMessage());
			out.write("erreur");
		}
	}
	
	
}
