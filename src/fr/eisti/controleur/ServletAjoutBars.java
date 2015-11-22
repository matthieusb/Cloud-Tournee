package fr.eisti.controleur;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.eisti.dao.DatastoreDAO;
import fr.eisti.modele.Bar;

@SuppressWarnings("serial")
public class ServletAjoutBars extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");
		
		try {
			ArrayList<Bar> listeBars = parsing_file("data/data.csv", ";");
			if (listeBars.size() == 0) {
				request.setAttribute("message", "Problème lors de l'ajout des bars");
				request.getRequestDispatcher("index.jsp").forward(request,response);
			} else {
				DatastoreDAO.ajout_bars_datastore(listeBars);
				request.setAttribute("message", "Bar ajoutés !");
				request.getRequestDispatcher("index.jsp").forward(request,response);
			}
		} catch (Exception e) {
			System.out.println("Erreur lors du parsing du fichier : "+e.getMessage());
			request.setAttribute("message", "Problème lors de l'ajout des bars");
			request.getRequestDispatcher("index.jsp").forward(request,response);
		}
	}
	
	//Parse le fichier et fait directement l'ajout dans la base 
	public ArrayList<Bar> parsing_file(String nom_fichier, String separateur) throws ParseException {
		String line;
		String[] line_split;
		String[] line_split_coord;
		boolean firstLine = true;
		ArrayList<Bar> listeBars = new ArrayList<>();
		
		try (BufferedReader buff = new BufferedReader(new FileReader(nom_fichier))) {
			while ((line = ( buff).readLine()) != null) {
				if (firstLine) {
					firstLine = false;
				} else {
					line_split = line.split(separateur);//On découpe selon le délimiteur
					if (line_split.length == 3) {//Si le format est correct
						Bar barTmp = null;
						line_split_coord = line_split[2].split(",");
						if (line_split_coord.length == 2) {//Si les coordonnées sont bien séparées d'une virgule
							barTmp = new Bar(line_split[0],line_split[1],Double.parseDouble(line_split_coord[0]),Double.parseDouble(line_split_coord[1]));
							listeBars.add(barTmp);
						}
					}
				}
			}
		} catch (IOException | NumberFormatException e) {
            System.out.println("Erreur lors de la lecture du fichier : "+e.getMessage());
        }
		return listeBars;
	}	
}
