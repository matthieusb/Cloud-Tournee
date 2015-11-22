var map;	// carte
var control; // bouton de controle haut droit
var baseLayers;  // liste de fonds de carte
var overlays = []; // liste de layer geoJSON(liste de points à afficher) 
var drawnItems = new L.FeatureGroup(); // objets créés par le menu leafletdraw

// Si vous souhaitez faire de l'Ajax, retourne l'objet XMLHTTPRequest
function getXHR() {
	var xhr = null;
	if (window.XMLHttpRequest)
		xhr = new XMLHttpRequest(); 
	if (window.ActiveXObject)  
		xhr = new ActiveXObject("Microsoft.XMLHTTP");
	return xhr;
}

// transformation d'une donnée en layer geoJSON, voir API leaflet geoJSON
// params : liste de points au format geoJSON; attributs de style au format json, vide /defaut
function getGeoJson(liste, styl) {
 return L.geoJson(liste,  {
		    style: styl,
		    // boucle pour chacun des marqueurs
		    onEachFeature: function (feature, layer) {
		    	var str="";
		    	JSON.parse(JSON.stringify(feature.properties), function(k,v) {
		    		if (k != "") {
		    			str += "<br/> "+k+" : "+v;
		    		}
		    	});
		    	// permet d'associer une popup contenant la description de l'élément (l'attribut description est supposée connu )
		    	layer.bindPopup(str); 
		    }
		});
}

//Transformation du json en chaine avec seulement latitude et longitude de chaque point (Simplifie le travail du servlet)
function drawnItemsCoordToString(jsonToConvert) {
	nbPoints = Object.keys(jsonToConvert['_layers']).length;
	var j = 0;
	
	if (nbPoints == 0) {
		str=null;
	} else {
		str="{\"Points\" : [";
		for (var i in jsonToConvert['_layers']) {
			j++;
			str+="{\"lat\":"+jsonToConvert['_layers'][i]['_latlng']['lat']+", \"long\":"+jsonToConvert['_layers'][i]['_latlng']['lng']+"}";
			if (j != nbPoints) {
				str+=","
			} 
		}
		str+="]}";
	}
	return str;
}

function barsItemsCoordToString(jsonToConvert) {
	nbPoints = Object.keys(jsonToConvert['_layers']).length;
	var j = 0;
	
//	console.log(jsonToConvert);
	if (nbPoints == 0) {
		str=null;
	} else {
		str="{\"Points\" : [";
		for (var i in jsonToConvert['_layers']) {
			j++;
			str+="{\"lat\":"+jsonToConvert['_layers'][i]['_latlng']['lat']+", \"long\":"+jsonToConvert['_layers'][i]['_latlng']['lng']+",\"nom\" :\""+jsonToConvert['_layers'][i]['feature']['properties']['nom']+"\"}";
			if (j != nbPoints) {
				str+=","
			} 
		}
		str+="]}";
	}
	return str;
}

//Fonction pour parser le json de retour et écrire des infos dans la pop dans le cas du barycentre
function outputInfosBarycentreFromJson(rep) {
	var infosPopUp = document.getElementById('infosBarycentre');
	nbElements = Object.keys(rep).length;
	
	infosPopUp.innerHTML="";//Nettoyage des données d'avant
	
	if (nbElements == 3) {//Si on a bien trois éléments retournés comme prévu
		infosPopUp.innerHTML+="<h4>Point de rendez-vous</h4> Latitude : "+rep[0]['geometry']['coordinates'][1]+"<br/>Longitude : "+rep[0]['geometry']['coordinates'][0];
		infosPopUp.innerHTML+="<h4>Bar le plus proche</h4> Nom : "+rep[1]['properties']['nom']+"<br/> Latitude : "+rep[1]['geometry']['coordinates'][1]+"<br/>Longitude : "+rep[1]['geometry']['coordinates'][0];
	} else {
		infosPopUp.innerHTML="<b>Erreur dans le retour, veuillez vous référer à la carte ou au tutoriel</b>";
	}
	infosPopUp.innerHTML+="<br/><br/><button type=\"button\" class=\"btn btn-danger\" name=\"buttonPopUpBarcyentreClose\" onclick=\"hideDiv('Barycentre')\">Fermer et afficher la carte</button>";	
}

//Fonction pour envoyer les drawn items à une servlet (Et retourner le barycentre par la suite)
function getDrawnItemsDrawBarycentre() {
	var xhr = getXHR();
	xhr.onreadystatechange = function() {
		if (xhr.readyState == 4 && xhr.status == 200) {
			// réception de la liste des bars au format geoJSON
			var rep = xhr.responseText;
			// transformation en layer leaflet
			if(rep != "erreur") {
				//console.log(rep);
				var layer = getGeoJson(eval(rep)); 
				layer.addTo(map);
				outputInfosBarycentreFromJson(eval(rep));
				showDiv('Barycentre');
			} else {
				alert("Aucun point ajouté ! ");
			}
		} 
	}

	xhr.ontimeout = function(){
  		alert("request timed out");
	}
	
	//Transformation en chaine et filtrage infos
	strSentDrawnItems = drawnItemsCoordToString(drawnItems);
	strSentBars = barsItemsCoordToString(overlays['Bars']);
	//Envoi de la chaine
	xhr.open('POST', "/barycentre", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.send("lieux="+strSentDrawnItems+"&bars="+strSentBars);
}

//Parse le json retourné par l'appel du recuit simulé et le retourne correctement formaté dans la popup
function outputInfosRecuitFromJson(rep) {
	var infosPopUp = document.getElementById('infosRecuit');
	nbElements = Object.keys(rep).length;
	
	infosPopUp.innerHTML="";//Nettoyage des données d'avant
	
	if (nbElements > 1) {//Si on a plusieurs points retournés comme prévu
		for (var i = 1 ; i < nbElements ; i++) {
			infosPopUp.innerHTML+="<h4>Bar n° "+i+"</h4> Nom : "+rep[i]['properties']['nom']+"<br/> " +
					"Latitude : "+rep[i]['geometry']['coordinates'][1]+"" +
					"<br/>Longitude : "+rep[1]['geometry']['coordinates'][0];
		}
	} else {
		infosPopUp.innerHTML="<b>Erreur dans le retour, veuillez vous référer à la carte ou au tutoriel</b>";
	}
	infosPopUp.innerHTML+="<br/><br/><button type=\"button\" class=\"btn btn-danger\" name=\"buttonPopUpRecuitClose\" onclick=\"hideDiv('Recuit')\">Fermer et afficher la carte</button>";
	
}

//Envoie les bars et retourne le chemin le plus court
function getBarsCalculCheminRecuit() {
	var xhr = getXHR();
	xhr.onreadystatechange = function() {
		if (xhr.readyState == 4 && xhr.status == 200) {
			// réception de la liste des bars au format geoJSON
			var rep = xhr.responseText;
//			console.log(rep);
			// transformation en layer leaflet
			if(rep == "erreur") {
				alert("Erreur ! ");
			} else {
				var layer = getGeoJson(eval(rep)); 
				overlays['Itineraire']=layer;
				control.addOverlay(layer, 'Itineraire');
				outputInfosRecuitFromJson(eval(rep));
				showDiv('Recuit');
			}
		} 
	}

	xhr.ontimeout = function(){
  		alert("request timed out");
	}
	//Transformation en chaine et filtrage infos
	strSent = barsItemsCoordToString(overlays['Bars']);	
	//Envoi de la chaine
//	xhr.open('POST', "/appelRecuit?bars="+strSent, true);
	xhr.open('POST', "/appelRecuit", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.send("bars="+strSent);
}

// Si vous souhaitez récupérer la position de tous les bars en bases pour les inclure dans la carte
// ici on suppose que les données sont au format JSON, à modifier pour le XML
function getAllData() {
	var xhr = getXHR();
	xhr.onreadystatechange = function() {
		if (xhr.readyState == 4 && xhr.status == 200) {
			// réception de la liste des bars au format geoJSON
			var rep = xhr.responseText;
			// transformation en layer leaflet
			var layer = getGeoJson(eval(rep)); 
			// ajout à la carte
			overlays['Bars']=layer;
			control.addOverlay(layer, 'Bars');
		} 
	}

	xhr.ontimeout = function(){
  		alert("request timed out");
	}
	xhr.open('POST', "/selectBarsJson", true);
	xhr.send(null);
}


// fonction d'initialisation de la carte (postn gps, zoom, menus control & draw)
function initMap() {
	/*  COORDS GPS DE PAU  */
	map = L.map('mamap', {
	    			center: [43.3, -0.3667],
	    			zoom: 14
				});
	

	/*  COORDS GPS DE CERGY, centrées sur l'EISTI 
	map = L.map('mamap', {
	    			center: [49.0305947, 2.0754885],
	    			zoom: 14
				});
	*/
	// cree layer openstreetmap
	var osmUrl='http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
	var osmAttrib='Map data © <a href="http://openstreetmap.org">OpenStreetMap</a> contributors';
	var osm = new L.TileLayer(osmUrl, {minZoom: 8, maxZoom: 18, attribution: osmAttrib});	
	//cree layer google
	var ggl = new L.Google('ROADMAP');
	var gglsat = new L.Google();

	// ajout layer/defaut
	map.addLayer(osm);

	//creation control menu
	baseLayers = {"OpenStreetMap": osm, "Google": ggl, "Google Sat": gglsat};
	control = L.control.layers(baseLayers, overlays).addTo(map);

	// Récupération des données et affichage ds la carte
	getAllData();

	/************************************** leafletdraw ****************************/
	map.addLayer(drawnItems);

	// menu de controle de dessin leaflet draw
	var drawControl = new L.Control.Draw({
		position: 'topleft',
		draw: {
			polyline: false,
			polygon: false,
			rectangle: false,
			circle: false,
			marker: true	// on n'a beoin que de pouvoir dessiner des markers
		},
		edit: {	// menu permettant la modification des markers (déplacement ou suppression après dessin)
			featureGroup: drawnItems,	
			remove: true  // activation de la suppression
		}
	});

	// ajout du menu à la carte
	map.addControl(drawControl);

	// evenement lorsque l'on clique dans le menu pour dessiner qqch
	map.on('draw:created', function (e) {
		var type = e.layerType,
			layer = e.layer;
	
		//if (type === 'marker') 
			//layer.bindPopup( <ici vous pouvez customiser le contenu de votre popup> );   // ajout d'une popup au marqueur

		drawnItems.addLayer(layer);
		oldlayerdraw = layer;
	});

	// evenement édition, lorsque l'on veut déplacer ou supprimer un marqueur
	map.on('draw:edited', function (e) {
		var layers = e.layers;
		var countOfEditedLayers = 0;
		layers.eachLayer(function(layer) {
			countOfEditedLayers++;
		});
	});
}

//################################## FEATURES ACCUEIL ###########################
function getBarsAccueil() {
	var xhr = getXHR();
	xhr.onreadystatechange = function() {
		if (xhr.readyState == 4 && xhr.status == 200) {
			// réception de la liste des bars au format geoJSON
			var rep = xhr.responseText;
			var divBars = document.getElementById('listeBars');
			divBars.innerHTML = rep;
		} 
	}

	xhr.ontimeout = function(){
  		alert("request timed out");
	}
	xhr.open('GET', "/selectionBarsAccueil", true);
	xhr.send(null);
}

function supprBarAccueil(key) {
	var xhr = getXHR();
	xhr.onreadystatechange = function() {
		if (xhr.readyState == 4 && xhr.status == 200) {
			// réception de la liste des bars au format geoJSON
			var rep = xhr.responseText;
			location.reload();
		} 
	}

	xhr.ontimeout = function(){
  		alert("request timed out");
	}
	xhr.open('POST', "/servletSupprBarsKey", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.send("key="+key);
}

//####################################### POP UP 
//################################ POPUP ################################
//Cache la pop up 
function hideDiv(endroit) { 
  if (document.getElementById) { // DOM3 = IE5, NS6 
	   $(function() {
		$('#hideshow'+endroit).fadeOut("slow",function() {
		$('#hideshow'+endroit).css('visibility','hidden');  
//		document.getElementById("hideshow"+endroit).style.visibility = 'hidden';
		});
	   });
  } else { 
      if (document.layers) { // Netscape 4 
          document.hideshow.visibility = 'hidden'; 
      } else { // IE 4 
          document.all.hideshow.style.visibility = 'hidden'; 
      } 
  } 
}

//Fait apparaitre la pop up  
function showDiv(endroit) {
  if (document.getElementById) { // DOM3 = IE5, NS6 
	   $(function() {
		$('#hideshow'+endroit).fadeIn("slow");   
		$('#hideshow'+endroit).css('visibility','visible');  
	   });
//       document.getElementById("hideshow"+endroit).style.visibility = 'visible'; 
  } else { 
      if (document.layers) { // Netscape 4 
          document.hideshow.visibility = 'visible'; 
      } else { // IE 4 
          document.all.hideshow.style.visibility = 'visible'; 
      } 
  } 
}


