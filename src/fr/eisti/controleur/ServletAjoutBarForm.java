package fr.eisti.controleur;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.eisti.dao.DatastoreDAO;
import fr.eisti.modele.Bar;

@SuppressWarnings("serial")
public class ServletAjoutBarForm extends HttpServlet {
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");
		String nom = request.getParameter("nom");
		String adresse = request.getParameter("ad");
		String latitude = request.getParameter("lat");
		String longitude = request.getParameter("long");
		
		try {
			Bar bar = new Bar(nom, adresse, Double.parseDouble(latitude), Double.parseDouble(longitude));
			DatastoreDAO.ajout_bar_datastore(bar);
			
			request.setAttribute("message", "Bar ajouté !");
			request.getRequestDispatcher("index.jsp").forward(request,response);
		} catch (Exception e) {
			System.out.println("Erreur lors de l'ajout du bar : "+e.getMessage());
			request.setAttribute("message", "Problème lors de l'ajout du bar");
			request.getRequestDispatcher("index.jsp").forward(request,response);
		}
	}
	
	
}
