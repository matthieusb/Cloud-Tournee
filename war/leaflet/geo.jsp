<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
	<title>MiniProjet Cloud - Carte </title>
	<link rel="stylesheet" href="./css/style.css" />
	<link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.css" />
	<link rel="stylesheet" type="text/css" href="../css/style.css"/>
	
	<link rel="stylesheet" type="text/css" href="../bootstrap/css/bootstrap.css"/>
	
	<script src="http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.js"></script>
	<script src="http://maps.google.com/maps/api/js?v=3&sensor=false"></script>
	<script src="./layer/tile/Google.js"></script>
	<script src="./layer/vector/KML.js"></script>
	<script src="./js/script.js"></script>
	<script src="./js/ActiveLayers.js"></script>
	<!--
	leaflet draw
	-->
	<link rel="stylesheet" href="./leaflet.draw/dist/leaflet.draw.css" />

	<script src="./leaflet.draw/src/Leaflet.draw.js"></script>

	<script src="./leaflet.draw/src/edit/handler/Edit.Poly.js"></script>
	<script src="./leaflet.draw/src/edit/handler/Edit.SimpleShape.js"></script>
	<script src="./leaflet.draw/src/edit/handler/Edit.Circle.js"></script>
	<script src="./leaflet.draw/src/edit/handler/Edit.Rectangle.js"></script>

	<script src="./leaflet.draw/src/draw/handler/Draw.Feature.js"></script>
	<script src="./leaflet.draw/src/draw/handler/Draw.Polyline.js"></script>
	<script src="./leaflet.draw/src/draw/handler/Draw.Polygon.js"></script>
	<script src="./leaflet.draw/src/draw/handler/Draw.SimpleShape.js"></script>
	<script src="./leaflet.draw/src/draw/handler/Draw.Rectangle.js"></script>
	<script src="./leaflet.draw/src/draw/handler/Draw.Circle.js"></script>
	<script src="./leaflet.draw/src/draw/handler/Draw.Marker.js"></script>

	<script src="./leaflet.draw/src/ext/LatLngUtil.js"></script>
	<script src="./leaflet.draw/src/ext/GeometryUtil.js"></script>
	<script src="./leaflet.draw/src/ext/LineUtil.Intersect.js"></script>
	<script src="./leaflet.draw/src/ext/Polyline.Intersect.js"></script>
	<script src="./leaflet.draw/src/ext/Polygon.Intersect.js"></script>

	<script src="./leaflet.draw/src/Control.Draw.js"></script>
	<script src="./leaflet.draw/src/Tooltip.js"></script>
	<script src="./leaflet.draw/src/Toolbar.js"></script>

	<script src="./leaflet.draw/src/draw/DrawToolbar.js"></script>
	<script src="./leaflet.draw/src/edit/EditToolbar.js"></script>
	<script src="./leaflet.draw/src/edit/handler/EditToolbar.Edit.js"></script>
	<script src="./leaflet.draw/src/edit/handler/EditToolbar.Delete.js"></script>

	</head>
	<body>
		<!--Si vous souhaitez ajouter un menu on vous fournit une petite zone -->
		<div id="menu">
			<div id="menu-contenu">
				<div style="text-align: center">
					<h2>Mini Projet Cloud - Menu</h2><br/>
<!-- 					<a href="/index.jsp">Retour à l'accueil</a><br/><br/> -->
					<button type="button" class="btn btn-default" name="buttonAccueil" onclick="self.location.href='/index.jsp'">Retour à l'accueil</button><br/><br/>
					<button type="button" name="buttonBary" class="btn btn-primary" onclick="getDrawnItemsDrawBarycentre()">Générer le point de rendez-vous et trouver le bar le plus proche</button><button type="button" class="btn btn-success" name="buttonPopUpBarcyentre" onclick="showDiv('Barycentre')">Afficher pop-up</button><br/><br/>
					<button type="button" name="buttonRecuit" class="btn btn-primary" onclick="getBarsCalculCheminRecuit()">Calcul du chemin le plus court entre les bars avec recuit simulé </button><button type="button" class="btn btn-success" name="buttonPopUpRecuit" onclick="showDiv('Recuit')">Afficher pop-up</button>
				</div>
			</div>
		</div>
		
		<!-- zone d'affichage de la carte -->
		<div id="mamap"></div>
		<script type="text/javascript">initMap();</script>
		
	<!-- POP UP BARYCENTRE -->
	<div id="hideshowBarycentre" style="visibility: hidden;">
		<div id="fade"></div>
		<div class="popup_block">
			<div class="popup">
				<a href="javascript:hideDiv('Barycentre')"> <img
					src="../imgs/icon_close.png" class="cntrl" alt="" />
				</a>
				<h3>Calcul du point de rendez-vous et du bar le plus proche</h3>
				<div id="infosBarycentre">Vous n'avez pas encore lancé de calcul</div>
			</div>
		</div>
	</div>
	<!-- FIN POP UP Barycentre-->
     
     <!-- POP UP RECUIT -->
	<div id="hideshowRecuit" style="visibility: hidden;">
		<div id="fade"></div>
		<div class="popup_block">
			<div class="popup">
				<a href="javascript:hideDiv('Recuit')"> <img
					src="../imgs/icon_close.png" class="cntrl" alt="" />
				</a>
				<h3>Chemin le plus court entre les bars</h3>
				<div id="infosRecuit">Vous n'avez pas encore lancé de calcul</div>
			</div>
		</div>
	</div>
	<!-- FIN POP UP RECUIT-->
		
		<script src="../bootstrap/js/jquery.js"></script>
    	<script src="../bootstrap/js/bootstrap.min.js"></script>
</body>
</html>