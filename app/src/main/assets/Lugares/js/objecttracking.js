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

    // true once data was fetched
    initiallyLoadedData: false,
    loaded: false,
    drawables: [],
    init: function initFn(poiData) {
        this.targetCollectionResource = new AR.TargetCollectionResource("assets/rio.wto", {
                });

                this.tracker = new AR.ObjectTracker(this.targetCollectionResource, {
                    onTargetsLoaded : this.loadingStep,
                    onError: function(errorMessage) {
                        alert(errorMessage);
                    }
                });
                var label = new AR.Label("Hola prueba 1", 1, {
                                         offsetY: 0.1,
                                         verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP,
                                         opacity: 0.9,
                                         zOrder: 1,
                                         style: {
                                             textColor: '#FFFFFF',
                                             backgroundColor: '#00000005'
                                         },
                                         translate: {
                                           x: 0,
                                           y: 0,
                                           z: 0
                                         }
                                     });
                this.objectTrackable = new AR.ObjectTrackable(this.tracker, "iglesia", {
                    drawables: {
                        cam: label
                    },
                    onObjectRecognized: this.objectRecognized,
                    onObjectLost: this.objectLost,
                    onError: function(errorMessage) {
                        alert(errorMessage);
                    }
                });
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
