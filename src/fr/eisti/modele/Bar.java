package fr.eisti.modele;

import java.io.Serializable;

public class Bar implements Serializable{

	private static final long serialVersionUID = 1L;
	private String key;
	private String nom;
	private String adresse;
	private Double latitude;
	private Double longitude;
	
	public Bar() {
	}
	
	public Bar(String key, String nom, String adresse, Double latitude, Double longitude) {
		this.key=key;
		this.nom = nom;
		this.adresse = adresse;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Bar(String nom, String adresse, Double latitude, Double longitude) {
		this.nom = nom;
		this.adresse = adresse;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getAdresse() {
		return adresse;
	}
	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return this.nom+";"+this.adresse+";"+this.latitude+","+this.longitude;
	}
	
	
	
	

}
