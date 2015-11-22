package fr.eisti.recuit;

/**
 * Users: Lucie Anglade, Cécile Riquart
 * Date: 12/5/14
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class AffichageMatriceCarreeDouble {
    /**
     * afficherMatriceDouble : affiche une matrice de double
     * @param matriceAffiche : la matrice à afficher
     * @param prompt : petit commentaire associé à l'affichage de la matrice
     */
    public static void afficherMatriceDouble(double[][] matriceAffiche, String prompt) {
        int i,j;
        int tailleI, tailleJ;

        try {
            tailleI = matriceAffiche.length;
            tailleJ = matriceAffiche[0].length; //À modifier pour matrices non carrées

            System.out.println("############ "+prompt+" ############");
            System.out.println();
            for (i=0 ; i<tailleI ; i++) {
                for (j=0 ; j<tailleJ ; j++) {
                    if (j==0) {
                        System.out.print("|  "+matriceAffiche[i][j]+"  |  ");
                    } else {
                        System.out.print(matriceAffiche[i][j]+"  |  ");
                    }
                }
                System.out.println();
            }
            System.out.println();
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Erreur lors de l'affichage : matrice vide ou dépassement : "+e.getMessage());
        }
    }
}
