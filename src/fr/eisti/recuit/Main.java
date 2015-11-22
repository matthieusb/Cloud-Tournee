package fr.eisti.recuit;

import java.util.ArrayList;

/**
 * Created by Lucie Anglade and Cécile Riquart on 12/16/14
 * RecuitSimule
 */
public class Main {
 	public static void main(String[] args) {
		double[][] matriceTest = new double[3][3];
		//Premiere ligne avec les numeros des bars
		matriceTest[0][0] = 0;
		matriceTest[0][1] = 1;
		matriceTest[0][2] = 2;
		//Deuxieme ligne avec les latitudes
		matriceTest[1][0] = 1;
		matriceTest[1][1] = 0;
		matriceTest[1][2] = 3;
		//Troisieme ligne avec les longitudes
		matriceTest[2][0] = 2;
		matriceTest[2][1] = 3;
		matriceTest[2][2] = 0;

        ArrayList<Integer> solutionS = new ArrayList<>();
        solutionS.add(1);solutionS.add(2);solutionS.add(3);

		ArrayList<Integer> res = RecuitSimule.recuit(80, 0.9, matriceTest);
        AffichageMatriceCarreeDouble.afficherMatriceDouble(matriceTest, "Matrice des distances");
        AffichageDesChemins.afficherChemin(res, "Approximation du meilleur chemin");
	}
}
