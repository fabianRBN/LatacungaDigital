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
    objectInfo: null,
    rotating: false,
    selectedObject: null,

    init: function initFn(poiData) {
     //for (var lugar = 0; lugar< 2; lugar++) {
        this.targetCollectionResource = new AR.TargetCollectionResource("assets/prueba.wtc", {
            onError: function(errorMessage) {
                alert(errorMessage);
            }
        });

        this.tracker = new AR.ImageTracker(this.targetCollectionResource, {
           maximumNumberOfConcurrentlyTrackableTargets: 5,
           extendedRangeRecognition: AR.CONST.IMAGE_RECOGNITION_RANGE_EXTENSION.OFF,
            onTargetsLoaded: this.trackerLoaded,
            onError: this.trackerError
        });

        var sizeFactor = 0.01;
        var distanceFactor = 0.01;

        this.ejeLocation = {
            x: 0,
            y: 0,
            z: 0
        };

        this.objectInfo = [{
                name: "Titulo",
                distance: 0,
                modelFile: "assets/tituloSanAgustin.wt3",
                realSize: 0.002,
                description: "lugar",
                x:-0.5,
                y:0.8
            },
            {
                name: "Musica",
                distance: 0,
                modelFile: "assets/tarjetamusica.wt3",
                realSize: 0.001,
                description: "Sonido",
                x:-1,
                y:0.8
            },
            {
                name: "Ubicación",
                distance: 40 * distanceFactor,
                modelFile: "assets/tarjetaubicacion.wt3",
                realSize: 0.001,
                description: "Entre las calles Hnas. Páez y Quito.",
                x:-1,
                y:0.3
            },

            {
                name: "Construcción",
                distance: 10 * distanceFactor,
                modelFile: "assets/tarjetaconstruccion.wt3",
                realSize: 0.001,
                description: "Arquitectura Neoclásica, exibe sus columnas circulares con japitel cónico, arquitraje terminado en cornisa",
                x:-1,
                y:-0.2
            },

            {
                name: "Descripción",
                distance: 10 * distanceFactor,
                modelFile: "assets/tarjetadescripcion.wt3",
                realSize: 0.001 ,
                description: "El convento de los agustinos ha sido noviciado, centro vocacional y centro de estudios superiores",
                x:1,
                y:0.8
            },

            {
                name: "Atractivos",
                distance: 10 * distanceFactor,
                modelFile: "assets/tarjetaatractivo.wt3",
                realSize: 0.001,
                description: "Réplica de la Virgen del Quinche <br/> Replica San Agustin",
                x:1,
                y:0.3
            },
            {
              name: "Historia",
              distance: 10 * distanceFactor,
              modelFile: "assets/tarjetahistoria.wt3",
              realSize: 0.001,
              description: "Historia del atractivo, en linea de tiempo",
              x:1,
              y:-0.3
              }
        ];

        var objects = [];
        this.objectAnimations = [];
        var contador=0;
        for (var i = 0; i < this.objectInfo.length; i++) {
            var info = this.objectInfo[i];

            info.size = Math.log(info.realSize * 1000) * 0.01;
            if (i > 1) {
                info.distance = this.objectInfo[i - 1].distance + this.objectInfo[i - 1].size + info.size + 0.5;
            }

            objects[i] = new AR.Model(info.modelFile, {
                scale: {
                    x: info.realSize,
                    y: info.realSize,
                    z: info.realSize
                },
                translate: {
                    x: info.x,
                    y: info.y,
                    z: this.ejeLocation.z
                }
            });

            info.objectModel = objects[i];
            info.selectedAnimation = this.createSelectedAnimation(info);
            info.select = this.selectObject;
            contador=i;
            objects[i].onClick = this.objectClicked(info);
            if (i > 0) {
                this.objectAnimations.push(this.createOrbitAnimation(objects[i], info));
            }
        }

        //sonido
        this.sirenSound = new AR.Sound("assets/himno.mp3", {
                    onError : function(){
                        alert(errorMessage);
                    },
                    onFinishedPlaying : function() {
                       // World.setLightsEnabled(false);
                    }
                });
                this.sirenSound.load();
         //Labels
         var label = new AR.Label("Horario: \n Lunes:", 0.1, {
                                  offsetY: 0.1,
                                  verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP,
                                  opacity: 0.9,
                                  zOrder: 1,
                                  style: {
                                      textColor: '#FFFFFF',
                                      backgroundColor: '#00000005'
                                  },
                                  translate: {
                                     x: 1,
                                     y: -0.8,
                                     z: 0
                                  }
                              });
         objects[contador]=label;

       // var backdropImg = new AR.ImageResource("assets/backdrop.png");
      //  var backdrop = [new AR.ImageDrawable(backdropImg, 2)];

        for (var i = 1; i <= 21; i++) {
        var overlay = new AR.ImageTrackable(this.tracker, "SanAgustin"+i, {
                    drawables: {
                        //cam: backdrop.concat(objects,this.modeltitle)
                        cam:  objects
                    },
                    onImageRecognized: this.trackerEnterFov,
                    onImageLost: this.trackerExitFov,
                    onError: function(errorMessage) {
                        alert(errorMessage);
                    }
                });

        }
       // }//cierra el for de recorrido del webservcice

           var imgOne = new AR.ImageResource("assets/HistoriaSanAgustin.png");
           		var overlayOne = new AR.ImageDrawable(imgOne, 1, {
           			translate: {
           				x:-0.15
           			}

           		});


           var overlay1 = new AR.ImageTrackable(this.tracker, "SanAgustin", {
                               drawables: {
                                   cam:  overlayOne
                               },
                               onImageRecognized: this.trackerEnterFov,
                               onImageLost: this.trackerExitFov,
                               onError: function(errorMessage) {
                                   alert(errorMessage);
                               }
                           });

        AR.context.onScreenClick = this.screenClick;
    },

    createOrbitAnimation: function createOrbitAnimationFn(object, info) {
        var distance = 0.1;//info.distance;
        var roundingTime = distance * 10000;

        var yAnimation1 = new AR.PropertyAnimation(object, 'translate.y', info.y + distance * 1, info.y + distance * 0, roundingTime / 4, {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_IN_SINE
        });
        var xAnimation1 = new AR.PropertyAnimation(object, 'translate.x', info.x + distance * 0, info.x + distance * 1, roundingTime /4 , {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_OUT_SINE
        });

        var yAnimation2 = new AR.PropertyAnimation(object, 'translate.y', info.y + distance * 0, info.y + distance * -1, roundingTime / 4, {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_OUT_SINE
        });
        var xAnimation2 = new AR.PropertyAnimation(object, 'translate.x', info.x + distance * 1, info.x + distance * 0, roundingTime / 4, {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_IN_SINE
        });

        var yAnimation3 = new AR.PropertyAnimation(object, 'translate.y', info.y + distance * -1, info.y + distance * 0, roundingTime / 4, {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_IN_SINE
        });
        var xAnimation3 = new AR.PropertyAnimation(object, 'translate.x', info.x + distance * 0, info.x + distance * -1, roundingTime / 4, {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_OUT_SINE
        });

        var yAnimation4 = new AR.PropertyAnimation(object, 'translate.y', info.y + distance * 0, info.y + distance * 1, roundingTime / 4, {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_OUT_SINE
        });
        var xAnimation4 = new AR.PropertyAnimation(object, 'translate.x', info.x + distance * -1, info.x + distance * 0, roundingTime / 4, {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_IN_SINE
        });

        var q1 = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [yAnimation1, xAnimation1]);
        var q2 = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [yAnimation2, xAnimation2]);
        var q3 = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [yAnimation3, xAnimation3]);
        var q4 = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [yAnimation4, xAnimation4]);

        return new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.SEQUENTIAL, [q2, q3, q4, q1]);
    },

    createSelectedAnimation: function createSelectedAnimationFn(object) {
        var scaleUp = new AR.PropertyAnimation(object.objectModel, "scale.x", object.size, object.size * 0, 500);
        var scaleDown = new AR.PropertyAnimation(object.objectModel, "scale.x", object.size * 0, object.size, 500);

        return new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.SEQUENTIAL, [scaleUp, scaleDown]);
    },

    selectObject: function selectObjectFn(select) {
        if (select) {
            if (World.selectedObject !== null) {
                World.selectedObject.select(false);
            }
            World.selectedObject = this;
            this.selectedAnimation.start(-1);
        } else {
            this.selectedAnimation.stop();
            this.objectModel.scale = {
                x: 0,
                y: 0,
                z: 0
            };
        }
    },

    objectClicked: function objectClickedFn(object) {
        return function() {
            object.select(true);
            if(object.name == "Musica"){
           World.sirenSound.play();
            }else if(object.name =="Titulo"){
            //World.AnimationGroup.destroy();
            //AnimationGroup=null;
            //Historia.init();
            }
            else{
            document.getElementById("info").setAttribute("class", "info");
            document.getElementById("name").innerHTML = object.name;
            document.getElementById("descripcion").innerHTML = object.description;
            document.getElementById("info").setAttribute("class", "infoVisible");
            }


            return true;
        };
    },

    screenClick: function onScreenClickFn() {
        if (World.selectedObject !== null) {
            World.selectedObject.select(false);
            World.selectedObject = null;
        }

        document.getElementById("info").setAttribute("class", "info");
        World.sirenSound.stop();
    },
/*
    toggleAnimateobjects: function toggleAnimateobjectsFn() {
        if (!this.objectAnimations[0].isRunning()) {
            if (!this.rotating) {
                this.objectAnimations.forEach(function startAnimFn(a) {
                    a.start(-1);
                });
                this.rotating = true;
            } else {
                this.objectAnimations.forEach(function resumeAnimFn(a) {
                    a.resume();
                });
            }
        } else {
            this.objectAnimations.forEach(function pauseAnimFn(a) {
                a.pause();
            });
        }

        return true;
    },*/

    removeLoadingBar: function() {
        if (!World.loaded) {
            var e = document.getElementById('loadingMessage');
            e.parentElement.removeChild(e);
            World.loaded = true;
        }
    },

    trackerLoaded: function trackerLoadedFn() {
        var cssDivLeft = " style='display: table-cell;vertical-align: middle; text-align: right; width: 50%; padding-right: 15px;'";
        var cssDivRight = " style='display: table-cell;vertical-align: middle; text-align: left;'";
        document.getElementById('loadingMessage').innerHTML =
            "<div" + cssDivLeft + ">Escanear Lugar:</div>" +
            "<div" + cssDivRight + "><img src='assets/phoneAR.png' width='50px' height='50px'></img></div>";
    },

    trackerError: function trackerErrorFn() {
        document.getElementById('loadingMessage').innerHTML = "ClientTracker loading failed, please reload World.";
    },

    trackerEnterFov: function trackerEnterFovFn() {
        World.removeLoadingBar();
       // document.getElementById('toggleAnimationBtn').style.display = "block";
        if (World.selectedObject !== null) {
            document.getElementById("info").setAttribute("class", "infoVisible");
        }
    },

    trackerExitFov: function trackerExitFovFn() {
       // document.getElementById('toggleAnimationBtn').style.display = "none";
        document.getElementById("info").setAttribute("class", "info");
    },

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
    				World.init();
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
//AR.context.onLocationChanged = World.locationChanged;
World.init();
/*
var Historia ={
     loaded: false,
     fechaInfo: null,
     selectedFecha: null,
     init: function initFn(){
      this.targetCollectionResource = new AR.TargetCollectionResource("assets/Lugares.wtc", {
                 onError: function(errorMessage) {
                     alert(errorMessage);
                 }
             });

             this.tracker = new AR.ImageTracker(this.targetCollectionResource, {
                maximumNumberOfConcurrentlyTrackableTargets: 5,
                extendedRangeRecognition: AR.CONST.IMAGE_RECOGNITION_RANGE_EXTENSION.OFF,
                 onTargetsLoaded: this.trackerLoaded,
                 onError: this.trackerError
             });
                  var sizeFactor = 0.01;
                  var distanceFactor = 0.01;
          this.fechaInfo=[{
          name:"linea",
          distance: 0,
          modelFile: "assets/linea.wt3",
          realSize: 0.001,
          description: "linea de tiempo del lugar",
          x:0,
          y:-0.8,
          z:0
          },
          {
          name:"1579",
          distance: 10 * distanceFactor,
          modelFile: "assets/1579.wt3",
          realSize: 0.001,
          description: "Los Agustinos construyeron un suntuoso templo llamado  'San Bernabé'",
          x:-0.8,
          y:-0.6,
          z:0
          },
          {
           name:"1650",
           distance: 10 * distanceFactor,
           modelFile: "assets/1650.wt3",
           realSize: 0.001,
           description: "Fue destruido y se lo volvió a edificar en 1650",
           x:-0.5,
           y:-0.6,
           z:0
          },
          {
           name:"1738",
           distance: 10 * distanceFactor,
           modelFile: "assets/1738.wt3",
           realSize: 0.001,
           description: "Fue destruido por el terremoto de 1738",
           x:-0.2,
           y:-0.6,
           z:0
           },
            {
            name:"1757",
            distance: 10 * distanceFactor,
            modelFile: "assets/1757.wt3",
            realSize: 0.001,
            description: "Fue reconstruido nuevamente, pero sufrió daños el 22 de febrero de 1757",
            x:0.1,
            y:-0.6,
            z:0
            },
           {
           name:"1797",
           distance: 10 * distanceFactor,
           modelFile: "assets/1797.wt3",
           realSize: 0.001,
           description: "El terremoto de 1797 lo destruyó totalmente",
           x:0.4,
           y:-0.6,
           z:0
           },
           {
           name:"1820",
           distance: 10 * distanceFactor,
           modelFile: "assets/1820.wt3",
           realSize: 0.001,
           description: "Ocupado por fuerzas militares españolas una fracción del Batallón de los Andes",
           x:0.7,
           y:-0.6,
           z:0
           },
           {
           name:"1850",
           distance: 10 * distanceFactor,
           modelFile: "assets/1850.wt3",
           realSize: 0.001,
           description: "Nueva Edificacion,copia de San Pedro de Roma concebido por el padre Nicolas Herrera",
           x:1,
           y:-0.6,
           z:0
           }
          ];
          var fechas=[];
          for (var i=0;i<this.fechaInfo.length; i++){
          var info=this.fechaInfo[i];
          fechas[i]=new AR.Model(info.modelFile,{
          scale:{
          x:info.realSize,
          y:info.realSize,
          z:info.realSize
          },
          translate:{
          x:info.x,
          y:info.y,
          z:info.z
          }
          });
          info.fechaModel=fechas[i];
          info.select=this.selectFecha;
          fechas[i].onClick=this.fechaClicked(info);
          }
          var overlay = new AR.ImageTrackable(this.tracker, "SanAgustin4", {
                      drawables: {
                          cam: fechas
                      },
                      onImageRecognized: this.trackerEnterFov,
                      onImageLost: this.trackerExitFov,
                      onError: function(errorMessage) {
                          alert(errorMessage);
                      }
                  });
                   AR.context.onScreenClick = this.screenClick;
     },
           selectFecha: function selectFechaFn(select) {
             if (select) {
                 if (Historia.selectedFecha!== null) {
                     Historia.selectedFecha.select(false);
                 }
                 Historia.selectedFecha = this;
             } else {
                 this.fechaModel.scale = {
                     x: 0,
                     y: 0,
                     z: 0
                 };
             }
         },

         fechaClicked: function fechaClickedFn(object) {
             return function() {
                 object.select(true);
                 document.getElementById("info").setAttribute("class", "info");
                 document.getElementById("name").innerHTML = object.name;
                 document.getElementById("descripcion").innerHTML = object.description;
                 document.getElementById("info").setAttribute("class", "infoVisible");
                 return true;
             };
         },

         screenClick: function onScreenClickFn() {
             if (Historia.selectedFecha !== null) {
                 Historia.selectedFecha.select(false);
                 Historia.selectedFecha = null;
             }
             document.getElementById("info").setAttribute("class", "info");

         }
};*/

