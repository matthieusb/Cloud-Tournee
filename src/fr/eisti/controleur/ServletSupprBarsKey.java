package fr.eisti.controleur;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;

import fr.eisti.dao.DatastoreDAO;

public class ServletSupprBarsKey extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ServletSupprBarsKey() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String key;
		PrintWriter out = response.getWriter();
		request.setCharacterEncoding("UTF-8");
		key = request.getParameter("key");
		
		DatastoreDAO.supprimer_bar_cle (key);
		out.write("ok");
		
//		request.getRequestDispatcher("index.jsp").forward(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String key;
		PrintWriter out = response.getWriter();
		request.setCharacterEncoding("UTF-8");
		key = request.getParameter("key");
		
		System.out.println(key);
		
		DatastoreDAO.supprimer_bar_cle(key);
		out.write("ok");
	}

}
