<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="fr.eisti.dao.DatastoreDAO" %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script src="./leaflet/js/script.js"></script>
	<title>Mini Projet Cloud - Accueil</title>
	
	<link rel="stylesheet" type="text/css" href="./css/style.css"/>
	<link rel="stylesheet" type="text/css" href="./bootstrap/css/bootstrap.css"/>
</head>
<body onload="getBarsAccueil()" role="document">

	<nav class="navbar navbar-inverse navbar-static-top" role="navigation" id="nav">
		<div class="container">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target="#bs-example-navbar-collapse-1">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<h3>Une tournée SVP !</h3>
			</div>
			<!-- Collect the nav links, forms, and other content for toggling -->
			<div class="collapse navbar-collapse"
				id="bs-example-navbar-collapse-1">
				<ul class="nav navbar-nav">
					<li><a href="index.jsp"><img src="./imgs/guinness.png" alt="Image guinness" height="30"/></a></li>
					<li><a href="ajoutBars">Ajout de tous les bars</a></li>
					<li><a href="supprBars">Suppression de tous les bars</a></li>
					<li><a href="/leaflet/geo.jsp">Accéder à la carte</a></li>
					<li><a href="#tutoriel">Accéder au tutoriel</a></li>
<!-- 					<li><a href="#" onclick="javascript:window.location.reload();"><img height="30"  src="./imgs/refresh.png" alt="refreshButton" title="Raraîchir la page"/></a></li> -->
				</ul>
			</div>
		</div>
	</nav>
	
<!-- 	<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12"> -->
<!-- 		<br/><br/><<br/><br/><br/><br/><br/> -->
<!-- 	</div> -->

	<!-- Affichage des notifications  -->
	<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
		<h2>Notifications</h2>
		<c:choose>
			<c:when test="${empty message}">Aucune notification actuellement</c:when>
			<c:otherwise>${message}</c:otherwise>
		</c:choose>
	</div>
	
	<!-- 	Affichage de la liste des bars -->
	<div id="blankAfterNav" class="col-xs-12 col-sm-12 col-md-6 col-lg-6">
		<h2>Liste des bars</h2>
		
		<div id="listeBars"></div>
	</div>

<!-- 	Affichage du formulaire d'ajout -->
	<div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">
		<div class="container-fluid">

			<form id="form_add" name="form_add" method="POST" action="/ajoutBarForm">
				<h2>Ajouter un bar</h2>
				<div class="form-group">
					<label for="nom"> * Nom </label> <input id="nom" name="nom"
						class="form-control" required />
				</div>
				<div class="form-group">
					<label for="ad"> * Adresse </label> <input id="ad" name="ad"
						class="form-control" required /> <br />
				</div>
				<div class="form-group">
					<label for="lat"> * Latitude (nombre) </label> <input id="lat"
						name="lat" type="number" class="form-control" value="0" required
						step="0.0001" /> <br />
				</div>
				<div class="form-group">
					<label for="long"> * Longitude (nombre) </label> <input id="long"
						name="long" type="number" class="form-control" value="0" required
						step="0.0001" /> <br />

					<p class="help-block">* : champs obligatoires</p>
				</div>
				<button type="submit" class="btn btn-success">Ajouter ce
					bar</button>
			</form>

			<div id="res"></div>
		</div>
	</div>
	
<!-- 	Affichage du tutoriel d'utilisation -->
	<div id="tutoriel" class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
	<h2>Tutoriel d'utilisation</h2>
	
	<font color="green">Information : </font>D'une manière générale, les actions sur le Datastore, qui contient les données, mettent un certain temps à être réellement effectuées. Elles sont donc visibles sur l'application avec un petit décalage.
	
		<h3>Ajout/Suppression de bars</h3>
		Pour ajouter des bars, vous pouvez utiliser le <em>formulaire</em> mis à votre disposition. 
		Il est également possible d'ajouter un total de 46 bars d'un seul coup en utilisant le bouton <em>"Ajout de tous les bars"</em> du menu. <br/>
		<font color="red">Attention : </font> Après un ajout de tous les bars, évitez de recharger la page, l'ajout se refera. En effet, la collision n'est pas gérée par la BDD, un bar avec les mêmes infos sera ajouté plusieurs fois. Cliquez plutôt sur la pinte de guinness du menu, elle rechargera l'accueil<br/> 
		<font color="green">Remarque importante : </font>Les bars que vous ajoutez à la BDD seront automatiquement ajoutés à la carte et seront utilisés pour le calcul du chemin le plus court.<br/><br/>
		Pour effectuer une suppression, vous pouvez soit : 
			<ul>
				<li>Supprimer tous les bars en cliquant sur le bouton <em>"Suppression de tous les bars"</em></li>
				<li>Supprimer seulement les bars que vous ne voulez pas utiliser lors du calcul chemin le plus proche, en cliquant sur la croix du bar souhaité dans le tableau</li>
			</ul>
		<h3>Utilisation de la carte</h3>
		Pour accéder à la carte, il suffit de cliquer sur le lien <em>"Accéder à la carte"</em>. Une fois dans cette partie du site, vous pouvez : 
			<ul>
				<li>Placer des points puis calculer le point des rendez-vous entre ces derniers. Par la suite, l'application calculera le point de rendez-vous possible (Barycentre) ainsi que l'itinéraire entre chaque point entré et le bar le plus proche. L'itinéraire entre le barycentre et le bar le plus proche sera également disponible </li>
				<li>Calculer le chemin le plus court entre tous les bars de la base de données</li>
			</ul> 
		Pour abaisser le <font color="green">menu de la carte</font>, il vous suffit de passer votre souris sur la zone grise en haut de la page.<br/>
		<font color="red">Attention : </font> les deux fonctionnalités suivantes font apparaître des pop-up. Tant que la pop-up n'est pas apparue, cela signifie que le programme tourne toujours. Vous pouvez rafficher une pop-up d'une action précédente à tout moment via le menu. 
			<h4>Point de rendez-vous et bar le plus proche</h4>
			Commencez d'abord par placer les points souhaités sur la carte : 
			<img alt="ajoutPoints" src="./imgs/addMarqueur.png" class="center-block"/>
			Puis, il suffit d'ouvrir le menu et de cliquer sur le bouton suivant : 
			<img alt="ajoutPoints" src="./imgs/arrowBary.png" class="center-block"/>
			
			<h4>Calcul du chemin le plus court et visualisation de l'itinéraire</h4>
			Ajustez d'abord les bars dans la base de données selon ceux que souhaitez garder pour le calcul du chemin puis cliquez sur le bouton suivant : 
			<img alt="ajoutPoints" src="./imgs/arrowRecuit.png" class="center-block"/>
			Il faut ensuite attendre que le calcul et les appels d'API se fassent, cela peut prendre du temps (Un dizaine de secondes en moyenne avec 46 bars). Une fois terminé, survolez l'icone gris en haut à droite de la page. Cochez la case "Itinéraire" pour voir le résultat.
			<font color="green">Remarque : </font> Si vous voulez simplement situer les bars ajoutés à votre bdd, cochez le case "Bars".
			<img alt="ajoutPoints" src="./imgs/itineraire.png" class="center-block"/>
			Bonne utilisation ! <br/>
	</div>
	
	<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12"><a href="#nav">Revenir en début de page</a></div>
	
	<script src="./bootstrap/js/jquery.js"></script>
    <script src="./bootstrap/js/bootstrap.min.js"></script>
</body>
</html>