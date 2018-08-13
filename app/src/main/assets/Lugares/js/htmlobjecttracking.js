// information about server communication. This sample webservice is provided by Wikitude and returns random dummy places near given location
var ServerInformation = {
	POIDATA_SERVER: "https://latacungaar-backend.herokuapp.com/",
	POIDATA_SERVER_ARG_LAT: "lat",
	POIDATA_SERVER_ARG_LON: "lon",
	POIDATA_SERVER_ARG_NR_POIS: "nrPois"
};
var World = {
    // you may request new data from server periodically, however: in this sample data is only requested once
    isRequestingData: false,
    objectInfo: null,
    // true once data was fetched
    initiallyLoadedData: false,
    loaded: false,
    drawables: [],
    init: function initFn(poiData) {
        this.targetCollectionResource = new AR.TargetCollectionResource("assets/Latacunga.wto", {
                });

                this.tracker = new AR.ObjectTracker(this.targetCollectionResource, {
                    onTargetsLoaded : this.loadingStep,
                    onError: function(errorMessage) {
                        alert(errorMessage);
                    }
                });
               // var areglo=["parque_vicente_leon","edificio_de_la_universidad_de_las_fuerzas_armadas","iglesia_de_santo_domingo","iglesia_de_nuestra_senora_del_salto","catedral_de_latacunga","iglesia_de_san_francisco","iglesia_de_san_agustin","iglesia_la_merced"];
                for (var i=0;i<=poiData.length;i++){

                var singlePoi = {
                				"id": poiData[i].id,
                				"latitude": parseFloat(poiData[i].latitude),
                				"longitude": parseFloat(poiData[i].longitude),
                				"description": poiData[i].description,
                				"name":poiData[i].name,
                				"targetName": poiData[i].targetName,
                				"permisos": poiData[i].permisos,
                				"usoActual": poiData[i].usoActual,
                				"impactoPositivo": poiData[i].impactoPositivo,
                				"impactoNegativo": poiData[i].impactoNegativo,
                				"horario":poiData[i].horario,
                				"direccion":poiData[i].direccion
                			};

//create a new html drawable and pass some setup parameters to it
var htmlDrawable = new AR.HtmlDrawable({
html:"<head><style> div {border: 1px solid gray; padding: 8px; } .col-md-6 {width: 200px; height: 100px; font-size: 10px; overflow-y: scroll;} h1 { text-align: center;text-transform: uppercase; color: #FDFEFE;} p {text-indent: 50px; text-align: justify;letter-spacing: 3px; color: #D0D3D4 ;}</style></head> <body> <div><h1>"
+singlePoi.name+"</h1><div class='col-md-6'><p>"+singlePoi.description+"</div></div></body>"
}, 3, {
 translate : { x: -2,
   y:0,
   z:0},
  horizontalAnchor : AR.CONST.HORIZONTAL_ANCHOR.LEFT,
  opacity : 0.9
});
                       //areglo de objetos visualizados


                                     drawables=[htmlDrawable];
                                this.objectTrackable = new AR.ObjectTrackable(this.tracker, singlePoi.targetName, {
                                    drawables: {
                                        cam: drawables
                                    },
                                    onObjectRecognized: this.objectRecognized,
                                    onObjectLost: this.objectLost,
                                    onError: function(errorMessage) {
                                        alert(errorMessage);
                                    }
                                });
                                AR.context.onScreenClick = this.screenClick;
                }

    },
  screenClick: function onScreenClickFn() {
            document.getElementById("info").setAttribute("class", "info");
   },
    objectRecognized: function objectRecognizedFn() {
        World.removeLoadingBar();
        World.setAugmentationsEnabled(true);
    },
    objectLost: function objectLostFn() {
        World.setAugmentationsEnabled(false);
    },
    setAugmentationsEnabled: function setAugmentationsEnabledFn(enabled) {
        for (var i = 0; i < World.drawables.length; i++) {
            World.drawables[i].enabled = enabled;
        }
    },

    removeLoadingBar: function removeLoadingBarFn() {
        if (!World.loaded ) {
            var e = document.getElementById('loadingMessage');
            e.parentElement.removeChild(e);
            World.loaded = true;
        }
    },

    loadingStep: function loadingStepFn() {
            var cssDivLeft = " style='display: table-cell;vertical-align: middle; text-align: right; width: 50%; padding-right: 15px;'";
            var cssDivRight = " style='display: table-cell;vertical-align: middle; text-align: left;'";
            document.getElementById('loadingMessage').innerHTML =
                        "<div" + cssDivLeft + ">Escanear Lugar:</div>" +
                        "<div" + cssDivRight + "><img src='assets/phoneAR.png' width='50px' height='50px'></img></div>";
    },
    //Web service
    locationChanged: function locationChangedFn(lat, lon, alt, acc) {

        		// request data if not already present
        		if (!World.initiallyLoadedData) {
        			World.requestDataFromServer(lat, lon);
        			World.initiallyLoadedData = true;
        		}
        	},

        // request POI data
        	requestDataFromServer: function requestDataFromServerFn(lat, lon) {

        		// set helper var to avoid requesting places while loading
        		World.isRequestingData = true;
        		//World.updateStatusMessage('Requesting places from web-service');

        		// server-url to JSON content provider
        		var serverUrl = ServerInformation.POIDATA_SERVER + "?" + ServerInformation.POIDATA_SERVER_ARG_LAT + "=" + lat + "&" + ServerInformation.POIDATA_SERVER_ARG_LON + "=" + lon + "&" + ServerInformation.POIDATA_SERVER_ARG_NR_POIS + "=20";

        		var jqxhr = $.getJSON(serverUrl, function(data) {
        				//World.loadPoisFromJsonData(data);
        				World.init(data);
        			})
        			.error(function(err) {
        				//World.updateStatusMessage("Invalid web-service response.", true);
        				World.isRequestingData = false;
        			})
        			.complete(function() {
        				World.isRequestingData = false;
        			});
        	}
};
/* forward locationChanges to custom function */
AR.context.onLocationChanged = World.locationChanged;
//World.init();
