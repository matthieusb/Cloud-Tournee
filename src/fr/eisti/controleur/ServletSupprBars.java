package fr.eisti.controleur;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;

import fr.eisti.dao.DatastoreDAO;

@SuppressWarnings("serial")
public class ServletSupprBars extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		req.setCharacterEncoding("UTF-8");
		List<Entity> allBars = DatastoreDAO.selection_bars();
		DatastoreDAO.supprimer_bars(allBars);
		
		req.setAttribute("message", "Bars supprimés");
		req.getRequestDispatcher("index.jsp").forward(req,resp);
	}
}
