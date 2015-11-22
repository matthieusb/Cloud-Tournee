package fr.eisti.recuit;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: eisti
 * Date: 12/9/14
 * Time: 9:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class AffichageDesChemins {
    /**
     * afficherChemin : affiche un chemin qui est stocké dans un array list
     * @param res_final : l'array list à afficher
     * @param prompt : petit commentaire associé à l'affichage de la matrice
     */
    public static void afficherChemin(ArrayList<Integer> res_final, String prompt) {
        int i;
        int taille;

        try {
            taille = res_final.size();

            System.out.println("############ "+prompt+" ############");
            System.out.println();
            for (i=0 ; i<taille ; i++) {

                System.out.print("| "+res_final.get(i));
            }
            System.out.println();
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Erreur lors de l'affichage : matrice vide ou dépassement : "+e.getMessage());
        }
    }
}
