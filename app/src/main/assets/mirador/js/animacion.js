var Menu = {
    objetosInfo: null,
    marcadorseleccionado: null,
    init: function(marcador) {
    //limpiar el entorno AR
           AR.context.destroyAll () ;
    // show radar & set click-listener
     	PoiRadar.show();
       		$('#radarContainer').unbind('click');
       		$("#radarContainer").click(PoiRadar.clickedRadar);

        //objeto recibido
        this.marcadorseleccionado=marcador;
        var distanceFactor = 580.2;
       var geoLoc = new AR.GeoLocation(this.marcadorseleccionado.latitude, this.marcadorseleccionado.longitude,this.marcadorseleccionado.altitude);
       /* null means: use relative to user, Eje is NORTH to the user */
        var locationEje = new AR.RelativeLocation(geoLoc, 0, 0, 0);
        /* sizes & distances are far away from real values! used these scalings to be able to show within user range */
        var sizeFactor = 0.5;
        var sizeEscala = 12.8 * 25;

       var imagenesLugares=[];
       var contador=0;
       $.each(this.marcadorseleccionado.imagenes, function(i, item) {
              // console.log("indice "+i+"-"+item.imagenURL);
               imagenesLugares[contador]=item.imagenURL;
               contador++;
           });
        //console.log("imagen "+imagenesLugares[0]);
        /* every object has a name, location and a circle (drawable) */
        var ejeImg = new AR.ImageResource(imagenesLugares[0]);
        var objeto1Img = new AR.ImageResource("assets/direccion.png");
        var objeto2Img= new AR.ImageResource("assets/permisos.png");
        var objeto3Img= new AR.ImageResource("assets/impacto.png");
        var objeto4Img = new AR.ImageResource("assets/uso.png");
        var objeto5Img = new AR.ImageResource("assets/close.png");
       // var indicatorImg = new AR.ImageResource("assets/indicador.png");

        var Lugar = {
            name: this.marcadorseleccionado.name,
            distance: 0,
            location: locationEje,
            imgDrawable: new AR.ImageDrawable(ejeImg,7),
            size: 5
        };

        var objeto1 = {
            name: this.marcadorseleccionado.direccion,
            distance: 7 * distanceFactor,
            location: new AR.RelativeLocation(locationEje, 0,0,10),
            imgDrawable: new AR.ImageDrawable(objeto1Img, 2),
            size: 2
        };

        var objeto2 = {
            name: this.marcadorseleccionado.permisos,
            distance: 8 * distanceFactor,
            location: new AR.RelativeLocation(locationEje, 0, 0, 20),
            imgDrawable: new AR.ImageDrawable(objeto2Img, 2),
            size: 2
        };

        var objeto3 = {
            name: this.marcadorseleccionado.usoActual,
            distance: 9 * distanceFactor,
            location: new AR.RelativeLocation(locationEje, 0, 0 , -12),
            imgDrawable: new AR.ImageDrawable(objeto3Img, 2),
            size: 2
        };

        var objeto4 = {
            name: this.marcadorseleccionado.impactoPositivo,
            distance: 10 * distanceFactor,
            location: new AR.RelativeLocation(locationEje, 0, 0, -20),
            imgDrawable: new AR.ImageDrawable(objeto4Img, 2),
            size: 2
        };
       var btncerrar= {
            name: "Cerrar",
            distance: 10 * distanceFactor,
            location: new AR.RelativeLocation(locationEje, 0, 0, -7),
            imgDrawable: new AR.ImageDrawable(objeto5Img, 2),
            size: 1
        };


/*
        The representation of an AR.GeoObject in the radar is defined in its drawables set (second argument of AR.GeoObject constructor).
        Once drawables.radar is set the object is also shown on the radar e.g. as an AR.Circle
    */
    this.radarCircle = new AR.Circle(0.1, {
        horizontalAnchor: AR.CONST.HORIZONTAL_ANCHOR.CENTER,
        opacity: 0.8,
        style: {
            fillColor: "#17202A"
        }
    });
    this.radardrawables = [];
    this.radardrawables.push(this.radarCircle);
         /* put eje, objetos 1234 an array */
         this.objetosInfo = [Lugar, objeto1, objeto2, objeto3, objeto4, btncerrar];


         //if(this.marcadorseleccionado!=null){
         //alert("Dato "+this.marcadorseleccionado.name)
         //}

        /* create helper array to create goeObjects out of given information */
        var objetosGeoObjects = [];
        for (var i = 0; i < this.objetosInfo.length; i++) {

            /* show name of object below*/
            var label = new AR.Label(this.objetosInfo[i].name, 1, {
                offsetY: -this.objetosInfo[i].size / 2,
                verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP,
                opacity: 0.9,
                zOrder: 1,
                style: {
                    textColor: '#FFFFFF',
                    backgroundColor: '#00000005',
                    fontStyle: AR.CONST.FONT_STYLE.BOLD
                }
            });

            /* drawable in cam of object -> image and label */
            var drawables = [];
            drawables[0] = this.objetosInfo[i].imgDrawable;
            drawables[1] = label;

            /* Create objects in AR*/
            objetosGeoObjects[i] = new AR.GeoObject(this.objetosInfo[i].location, {
                drawables: {
                    cam: drawables,
                     radar: this.radardrawables
                },
                //animacion desabilitada
                //enabled: false
                onClick: this.objetoClicked(this.objetosInfo[i],this.marcadorseleccionado)
            });
           /* if (i > 0) {
                this.animate(this.objetosInfo[i]);
            } else {
                var ejeHackAnim = new AR.PropertyAnimation(this.objetosInfo[i].location, 'northing', 10000, 10000, 1000, {
                    type: AR.CONST.EASING_CURVE_TYPE.EASE_IN_SINE
                });
                ejeHackAnim.start(-1);
            }*/
        }

        // Add indicator to Eje
       /* var imageDrawable = new AR.ImageDrawable(indicatorImg, 0.2, {
            verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP
        });
        objetosGeoObjects[0].drawables.addIndicatorDrawable(imageDrawable);*/
    },

    animate: function(objeto) {
        var relLocation = objeto.location;
        var distance = objeto.distance;
        var roundingTime = distance * 2 * 2;


        var northSouthAnimation2 = new AR.PropertyAnimation(relLocation, 'northing', distance * 0, distance * -1, roundingTime / 4, {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_OUT_IN_QUINT
        });
        var eastWestAnimation2 = new AR.PropertyAnimation(relLocation, 'easting', distance * 1, distance * 0, roundingTime / 4, {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_IN_OUT_QUINT
        });

        var northSouthAnimation3 = new AR.PropertyAnimation(relLocation, 'northing', distance * -1, distance * 0, roundingTime / 4, {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_IN_OUT_QUINT
        });
        var eastWestAnimation3 = new AR.PropertyAnimation(relLocation, 'easting', distance * 0, distance * -1, roundingTime / 4, {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_OUT_IN_QUINT
        });


        var q1 = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [northSouthAnimation2, eastWestAnimation2]);
        var q2 = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [northSouthAnimation3, eastWestAnimation3]);

        var cicularAnimationGroup = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.SEQUENTIAL, [q1,q2]);

        cicularAnimationGroup.start(-1);
    },



    objetoClicked: function(marker, objetoseleccionado) {

        return function() {
        if (marker.name == objetoseleccionado.name){
            // update panel values
            		$("#poi-detail-title").html(objetoseleccionado.name);
            		$("#poi-detail-description").html(objetoseleccionado.description);
            		$("#poi-detail-distance").html(objetoseleccionado.distance);
            		// show panel
            		$("#panel-poidetail").panel("open", 123);
            		$(".ui-panel-dismiss").unbind("mousedown");
            		//$("#panel-poidetail").on("panelbeforeclose", function(event, ui) {
            			//World.currentMarker.setDeselected(World.currentMarker);
            		//});
        }else if(marker.name=="Cerrar"){
              World.reloadPlaces();
        }

        };
    },
    //close div info
    onclosedivButtonClicked: function onclosedivButtonClickedFn(){
           $('#info').css('display', 'block');
         $("#info").css('visibility', 'hidden');
    	}
};