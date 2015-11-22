package fr.eisti.recuit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by Lucie Anglade and Cécile Riquart on 12/16/14
 * RecuitSimule
 */
public class RecuitSimule {
	//Pour stocker les chemins deja testés
	static ArrayList<ArrayList<Integer>> cheminsDejaTest = new ArrayList<>();

    /**
     * metropolis : Calcul de métropolis
     * @param fS : cout de la 1ere solution
     * @param fV : cout e la solution testée
     * @param t : température
     * @return metropolis
     */
	private static double metropolis(double fS, double fV, double t){
		return Math.exp((-Math.abs(fS-fV))/t);
	}

    /**
     * factorielle : calcule la factorielle d'un entier (méthode récursive terminale)
     * @param n : entier dont on veut connaitre la factorielle
     * @param acc : accumulateur qui contiendra le resultat attendu
     * @return acc : le resultat de n!
     */
	private static int factorielle(int n, int acc){
		if(n==0){
			return acc;
		} else {
			return factorielle(n - 1, n*acc);
		}
	}

    /**
     * cheminsPossibles : Calcul du nombre de chemins possible, n = nombre de bars
     * @param n : nombre de bars
     * @return res : nombre de chemins possibles pour parcourir tous les bars
     */
	private static int cheminsPossibles(int n){
		return factorielle(n - 1,1);
	}

    /**
     * calculCout : Calcul du cout d'un chemin stocké dans tab : somme des distances entre chaque bar
     * @param tab_chemin : chemin dont on veut connaitre le cout
     * @param mat_distance : matrice des distances (chaque cellule correspond à la distance pour aller d'un point A à nu point B)
     * @return res : le cout du chemin en paremetre
     */
	private static double calculCout(ArrayList<Integer> tab_chemin, double[][] mat_distance){
		double res = 0;
		int longueur = tab_chemin.size();
		for (int i = 0; i < longueur-1; i++){
			res = res + mat_distance[tab_chemin.get(i)-1][tab_chemin.get(i+1)-1];
		}
		return res;
	}

    /**
     * randomBornes : Fonction random entre deux bornes
     * @param val_min : le random généré doit etre supérieur à val_min
     * @param val_max : le random généré doit etre inférieur à val_max
     * @return un entier aléatoire compris entre val_min et val_max
     */
    private static int randomBornes(int val_min, int val_max) {
        Random r = new Random();
        return (val_min + r.nextInt(val_max - val_min));
    }

    /**
     * echange : Fonction echange de deux points dans un chemin
     * @param chemin_init : chemin initial qui sert de base pour la création d'un nouveau chemin
     * @param point1 : point à échanger
     * @param point2 : point à échanger
     * @return res_chemin : le nouveau chemin après l'échange de point1 et point2
     */
    private static ArrayList<Integer> echange(ArrayList<Integer> chemin_init, int point1, int point2) {
        ArrayList<Integer> res_chemin;
        res_chemin = chemin_init;
        int temp;

        temp = res_chemin.get(point1);
        res_chemin.set(point1, res_chemin.get(point2));
        res_chemin.set(point2, temp);

        return res_chemin;
    }

    //Tester si le chemin généré a déjà été traité

    /**
     * verifDejaTest : Vérifie si un chemin a déjà été testé ou pas
     * @param chemin_test : chemin que l'on veut tester
     * @return pas_trouve : vrai si le chemin a déjà été testé, faux sinon
     */
    private static boolean verifDejaTest(ArrayList<Integer> chemin_test) {
        Iterator<ArrayList<Integer>> iterateur = cheminsDejaTest.iterator();
        boolean pas_trouve = true;

        while ((iterateur.hasNext())&&(pas_trouve)) {
            ArrayList<Integer> new_Array = iterateur.next();

            pas_trouve = !(new_Array.equals(chemin_test));
        }
        return pas_trouve;
    }

    /**
     * selectNouveauChemin : sélectionne un nouveau chemin à tester
     * @param tab_chemin : chemin de base / de référence (cocher la bonne réponse =D)
     * @return res_chemin : un nouveau chemin (tavu)
     */
	private static ArrayList<Integer> selectNouveauChemin(ArrayList<Integer> tab_chemin){
		ArrayList<Integer> res_chemin = new ArrayList<>();
        int point_echange1=0, point_echange2=0;
        int taille = tab_chemin.size();
        boolean deja_test = false;
        boolean meme_point = true;

        while (!deja_test) {
            point_echange1 = randomBornes(0,taille-1);

            while (meme_point) {
                point_echange2 = randomBornes(0,taille-1);
                meme_point = (point_echange1 == point_echange2);
            }
            //Echange
            res_chemin = echange(tab_chemin, point_echange1, point_echange2);
            deja_test = !verifDejaTest(res_chemin);
        }

        cheminsDejaTest.add(res_chemin);


		return res_chemin;
	}

    /**
     * creationSolutionInit : création de la solution initiale
     * @param mat_distance : matrice des distances, permet de récupérer le nombre de bars
     * @return res : la solution initiale
     */
    private static ArrayList<Integer> creationSolutionInit(double[][] mat_distance){
        ArrayList<Integer> res = new ArrayList<>();
        int longueur = mat_distance.length;
        for(int i=0; i<longueur;i++){
            res.add(i,i+1);
        }
        return res;
    }

    /**
     * recuit : fonction qui agence tout le déroulement de l'algorithme du recuit
     * @param t : température
     * @param lambda : coefficient directeur de la fonction température
     * @param mat_distance : matrice des distances entre les différents bars
     * @return solutionSetoile : le chemin résultant de l'algorithme du recuit simulé, potentiel meilleur résultat, approché =D
     */
    /* Algo du recuit simulé
	  solutionS : une premiere solution random au probleme, donc l'ordre dans lequel les bars sont envoyés à la fonction ca marche
	  t : température, à fixer arbitrairement assez haute, 80 !!!!!!
	  lambda : à fixer. Pour la baisse de la temperature. doit etre compris entre 0 et 1

	  Structure des solutions : autant de colonne que de bars. 1e ligne : numero du bar, 2e : latitude, 3e : longitude
	
	  ********************** Appel : recuit(80, 0.9, matrice_distance) *************************
	*/
	public static ArrayList<Integer> recuit(double t, double lambda, double[][] mat_distance){
        ArrayList<Integer> solutionS = creationSolutionInit(mat_distance);
		ArrayList<Integer> solutionSetoile = solutionS;
		ArrayList<Integer> voisinV;
		int nbChemins = cheminsPossibles(solutionS.size());
		int i = 1;
		double coutV;
		double coutS;
        double limiteTemp;
        t = calculCout(solutionS, mat_distance) / solutionS.size();
        limiteTemp=5;
        cheminsDejaTest.add(solutionS);

		while((i<nbChemins) && (t > limiteTemp)){ //5 est arbitraire
			coutS = calculCout(solutionS, mat_distance);
			voisinV = selectNouveauChemin(solutionS);
			coutV = calculCout(voisinV, mat_distance);
			if((coutV < coutS) || (Math.random() < metropolis(coutS, coutV, t))) {
				solutionS = voisinV;
			}
			if(calculCout(solutionS, mat_distance) < calculCout(solutionSetoile, mat_distance)){
				solutionSetoile = solutionS;
			}
			t = lambda*t;
			i = i++;
		}

		return solutionSetoile;
	}

}
