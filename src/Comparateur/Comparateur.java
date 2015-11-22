package Comparateur;

import java.util.Comparator;

import fr.eisti.modele.Bar;

public class Comparateur implements Comparator<Bar> {
	@Override
	public int compare(Bar b1, Bar b2) {
		return b1.getNom().compareTo(b2.getNom());
	}
}
