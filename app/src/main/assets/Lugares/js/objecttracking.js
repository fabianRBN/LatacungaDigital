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
    //limpiar el entorno AR
                   AR.context.destroyAll () ;
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

                			var Titulo = new AR.Model("assets/"+singlePoi.targetName+".wt3", {
                            			onLoaded: this.loadingStep,
                            			scale: {
                            				x: 0.005,
                            				y: 0.005,
                            				z: 0.005
                            			},
                            			translate: {
                            				x: 0.0,
                            				y: 1,
                            				z: 0.0
                            			},
                            			rotate: {
                            				z: 0
                            			}
                            		});
               /*
                 direccion
                var titulodireccion = new AR.Label("Dirección:", 0.1, {
                                                                zOrder: 1,
                                                                style: {
                                                                textColor: '#48C9B0'
                                                                },
                                                                translate: {
                                                                x: -0.4,
                                                                y: 0.7,
                                                                z: 0
                                                                }
                                                                });*/
                var Direccion = new AR.Label(singlePoi.direccion, 0.08, {
                                                zOrder: 1,
                                                style: {
                                                textColor: '#48C9B0',
                                                backgroundColor: '#FFFFFF'
                                                },
                                                translate: {
                                                x: -0.6,
                                                y: 0.5,
                                                z: 0
                                                }
                                                });
         var imgOne = new AR.ImageResource("assets/direccion.png");
         var overlaydireccion = new AR.ImageDrawable(imgOne, 0.3, {
         			translate: {
         				x:-0.5,
         				y:0.7,
         				z:0
         			}

         });

         //permisos

               /* var titulopermiso = new AR.Label("Permisos:", 0.1, {
                                                                zOrder: 1,
                                                                style: {
                                                                textColor: '#8E44AD'
                                                                },
                                                                translate: {
                                                                x: -0.4,
                                                                y: 0.3,
                                                                z: 0
                                                                }
                                                                });*/
                var Permiso = new AR.Label(singlePoi.permisos, 0.08, {
                                                zOrder: 1,
                                                style: {
                                                textColor: '#8E44AD',
                                                backgroundColor: '#FFFFFF'
                                                },
                                                translate: {
                                                x: -0.6,
                                                y: 0.1,
                                                z: 0
                                                }
                                                });
         var imgOne = new AR.ImageResource("assets/permiso.png");
         var overlaypermiso = new AR.ImageDrawable(imgOne, 0.3, {
         			translate: {
         				x:-0.5,
         				y:0.3,
         				z:0
         			}

         });
         //Uso Actual

                        /* var titulouso = new AR.Label("Uso Actual:", 0.1, {
                                                                         zOrder: 1,
                                                                         style: {
                                                                         textColor: '#229954'
                                                                         },
                                                                         translate: {
                                                                         x: -0.4,
                                                                         y: -0.1,
                                                                         z: 0
                                                                         }
                                                                         });*/
                         var Uso = new AR.Label(singlePoi.usoActual, 0.08, {
                                                         zOrder: 1,
                                                         style: {
                                                         textColor: '#229954',
                                                         backgroundColor: '#FFFFFF'
                                                         },
                                                         translate: {
                                                         x: -0.5,
                                                         y: -0.4,
                                                         z: 0
                                                         }
                                                         });
                  var imgOne = new AR.ImageResource("assets/uso.png");
                  var overlayuso = new AR.ImageDrawable(imgOne, 0.3, {
                  			translate: {
                  				x:-0.5,
                  				y:-0.1,
                  				z:0
                  			}

                  });
                  //impacto positivo
        /*  var titulopositivo = new AR.Label("Impacto Positivo:", 0.08, {
                              zOrder: 1,
                              style: {
                              textColor: '#5DADE2'
                             },
                             translate: {
                              x: 0.7,
                              y: 0.7,
                             z: 0
                              }
                             });*/
                             var Positivo = new AR.Label(singlePoi.impactoPositivo, 0.08, {
                                                             zOrder: 1,
                                                             style: {
                                                             textColor: '#5DADE2',
                                                             backgroundColor: '#FFFFFF'
                                                             },
                                                             translate: {
                                                             x: 0.7,
                                                             y: 0.5,
                                                             z: 0
                                                             }
                                                             });
                      var imgOne = new AR.ImageResource("assets/positivo.png");
                      var overlaypositivo = new AR.ImageDrawable(imgOne, 0.3, {
                      			translate: {
                      				x:0.5,
                      				y:0.7,
                      				z:0
                      			}

                      });
                      //impacto negativo
                      /*var titulonegativo = new AR.Label("Impacto Negativo:", 0.08, {
                                                    zOrder: 1,
                                                    style: {
                                                    textColor: '#EC7063'
                                                   },
                                                   translate: {
                                                    x: 0.7,
                                                    y: 0.3,
                                                   z: 0
                                                    }
                                                   });*/
                    var Negativo = new AR.Label(singlePoi.impactoNegativo, 0.08, {
                                                   zOrder: 1,
                                                     style: {
                                                       textColor: '#EC7063',
                                                       backgroundColor: '#FFFFFF'
                                                    },
                                               translate: {
                                               x: 0.7,
                                                y: 0.1,
                                              z: 0
                                             }
                                          });
                                            var imgOne = new AR.ImageResource("assets/negativo.png");
                                            var overlaynegativo = new AR.ImageDrawable(imgOne, 0.3, {
                                            			translate: {
                                            				x:0.5,
                                            				y:0.3,
                                            				z:0
                                            			}

                                            });
 //Horario
                      var titulohorario = new AR.Label("Horario:", 0.1, {
                                                     zOrder: 1,
                                                     style: {
                                                     textColor: '#17202A',
                                                     backgroundColor: '#FFFFFF'
                                                    },
                                                    translate: {
                                                     x: 0.7,
                                                     y: -0.1,
                                                    z: 0
                                                     }
                                                    });

                            var Domingo = new AR.Label("Domingo: "+singlePoi.horario.Domingo.horaInicio+"-"+singlePoi.horario.Domingo.horaSalida
                            , 0.05, { zOrder: 1, style: { textColor: '#17202A',backgroundColor: '#FFFFFF'},translate: {x: 0.5, y: -0.2, z: 0}});
                            var Lunes = new AR.Label("Lunes: "+singlePoi.horario.Lunes.horaInicio+"-"+singlePoi.horario.Lunes.horaSalida
                            , 0.05, {zOrder: 1,style: {textColor: '#17202A',backgroundColor: '#FFFFFF'}, translate: {x: 1,y: -0.2,z: 0} });
                            var Martes = new AR.Label("Martes: "+singlePoi.horario.Martes.horaInicio+"-"+singlePoi.horario.Martes.horaSalida
                             , 0.05, {zOrder: 1,style: {textColor: '#17202A',backgroundColor: '#FFFFFF'}, translate: {x: 0.5,y: -0.3,z: 0} });
                             var Miercoles = new AR.Label("Miercoles: "+singlePoi.horario.Miercoles.horaInicio+"-"+singlePoi.horario.Miercoles.horaSalida
                             , 0.05, {zOrder: 1,style: {textColor: '#17202A',backgroundColor: '#FFFFFF'}, translate: {x: 1,y: -0.3,z: 0} });
                             var Jueves = new AR.Label("Jueves: "+singlePoi.horario.Jueves.horaInicio+"-"+singlePoi.horario.Jueves.horaSalida
                             , 0.05, {zOrder: 1,style: {textColor: '#17202A',backgroundColor: '#FFFFFF'}, translate: {x: 0.5,y: -0.4,z: 0} });
                             var Viernes = new AR.Label("Viernes: "+singlePoi.horario.Lunes.horaInicio+"-"+singlePoi.horario.Viernes.horaSalida
                             , 0.05, {zOrder: 1,style: {textColor: '#17202A',backgroundColor: '#FFFFFF'}, translate: {x: 1,y: -0.4,z: 0} });
                             var Sabado = new AR.Label("Sabado: "+singlePoi.horario.Sabado.horaInicio+"-"+singlePoi.horario.Sabado.horaSalida
                             , 0.05, {zOrder: 1,style: {textColor: '#17202A',backgroundColor: '#FFFFFF'}, translate: {x: 0.5,y: -0.5,z: 0} });

                              var imgDescripcion= new AR.ImageResource("assets/descripcion.png");

                              //sonido
                                      this.sirenSound = new AR.Sound("assets/intro.mp3", {
                                                  onError : function(){
                                                      alert(errorMessage);
                                                  },
                                                  onFinishedPlaying : function() {
                                                     // World.setLightsEnabled(false);
                                                  }
                                              });
                                              this.sirenSound.load();
                             var overlaydescripcion = new AR.ImageDrawable(imgDescripcion, 0.1, {
                                              translate: {
                                              x:0,
                                              y:0.8,
                                              z:0
                                              }

                               });



//create a new html drawable and pass some setup parameters to it
/*var htmlDrawable = new AR.HtmlDrawable({
html:"<head><style> div {border: 1px solid gray; padding: 8px; width: 500px; font-size: 10px; overflow-y: scroll; background: white; } h1 { text-align: center;text-transform: uppercase; color: #FDFEFE;} p {text-indent: 50px; text-align: justify;letter-spacing: 3px; color: #000000 ;}</style></head> <body> <div><p>"+singlePoi.description+"</div></body>"
}, 5, {
  translate : { x: -2,
   y:-3,
   z:0},
  horizontalAnchor : AR.CONST.HORIZONTAL_ANCHOR.LEFT,
  opacity : 0.9
});*/
                       //areglo de objetos visualizados


                                     drawables=[overlaydescripcion, Titulo,Direccion,overlaydireccion,
                                                        Permiso,overlaypermiso,
                                                       Uso,overlayuso,
                                                        Positivo, overlaypositivo,
                                                        Negativo, overlaynegativo, titulohorario,
                                                       Domingo,Lunes, Martes,Miercoles,Jueves,Viernes,Sabado]
                                this.objectTrackable = new AR.ObjectTrackable(this.tracker, singlePoi.targetName, {
                                    drawables: {
                                        cam: drawables
                                    },
                                    onObjectRecognized: this.objectRecognized,
                                    onObjectLost: this.objectLost,
                                    onError: function(errorMessage) {
                                        alert(errorMessage);
                                    },
                                    onClick : function () {
                                    document.getElementById("info").setAttribute("class", "info");
                                    document.getElementById("name").innerHTML = "Hola";
                                    //document.getElementById("descripcion").innerHTML = singlePoi.description;
                                    document.getElementById("info").setAttribute("class", "infoVisible");
                                                                  }
                                });
                                AR.context.onScreenClick = this.screenClick;
                }

    },
  screenClick: function onScreenClickFn() {
            document.getElementById("info").setAttribute("class", "info");
   },
    objectRecognized: function objectRecognizedFn() {
        World.sirenSound.play();
        World.removeLoadingBar();
        World.setAugmentationsEnabled(true);
    },
    objectLost: function objectLostFn() {
        document.getElementById("info").setAttribute("class", "info");
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
