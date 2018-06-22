var Solar = {
    planetsInfo: null,
    marcadorseleccionado: null,
    init: function(marcador) {

        var distanceFactor = 580.2;

        /* null means: use relative to user, sun is NORTH to the user */
        var locationSun = new AR.RelativeLocation(null, 25000, 0, 5000);

        /* sizes & distances are far away from real values! used these scalings to be able to show within user range */
        var sizeFactor = 0.5;
        var sizeEarth = 12.8 * 25;

        /* every object in space has a name, location and a circle (drawable) */
        var sunImg = new AR.ImageResource("assets/agente.jpg");
        var mercuryImg = new AR.ImageResource("assets/label.png");
        var venusImg = new AR.ImageResource("assets/label.png");
        var earthImg = new AR.ImageResource("assets/label.png");
        var marsImg = new AR.ImageResource("assets/label.png");
        var indicatorImg = new AR.ImageResource("assets/indi.png");

        var sunSize = (((109 * sizeEarth) / sizeEarth) * 0.3) * sizeFactor;
        var mercurySize = (10 * sizeEarth / sizeEarth) * sizeFactor;
        var venusSize = (10* sizeEarth / sizeEarth) * sizeFactor;
        var earthSize = (10* sizeEarth / sizeEarth) * sizeFactor;
        var marsSize = (10 * sizeEarth / sizeEarth) * sizeFactor;

        var parque = {
            name: "Parque",
            distance: 0,
            location: locationSun,
            imgDrawable: new AR.ImageDrawable(sunImg, sunSize),
            size: sunSize,
            description: "The Sun is the star at the center of the Solar System. It is almost perfectly spherical and consists of hot plasma interwoven with magnetic fields.",
            mass: "2&nbsp;10<sup>30</sup>&nbsp;kg",
            diameter: "1,392,684&nbsp;km"
        };

        var mercury = {
            name: "DATO 1",
            distance: 7 * distanceFactor,
            location: new AR.RelativeLocation(locationSun, 0, -7 * distanceFactor , 2000),
            imgDrawable: new AR.ImageDrawable(mercuryImg, mercurySize),
            size: mercurySize,
            description: "Is the innermost planet in the Solar System. It is also the smallest, and its orbit is the most eccentric (that is, the least perfectly circular).",
            mass: "3.3&nbsp;10<sup>23</sup>&nbsp;kg",
            diameter: "4,880&nbsp;km"
        };

        var venus = {
            name: "DATO 2",
            distance: 8 * distanceFactor,
            location: new AR.RelativeLocation(locationSun, 0, 8 * distanceFactor, 0),
            imgDrawable: new AR.ImageDrawable(venusImg, venusSize),
            size: venusSize,
            description: "Is named after the Roman goddess of love and beauty. After the Moon, it is the brightest natural object in the night sky, bright enough to cast shadows.",
            mass: "4.9&nbsp;10<sup>24</sup>",
            diameter: "12,092&nbsp;km"
        };

        var earth = {
            name: "DATO 3",
            distance: 9 * distanceFactor,
            location: new AR.RelativeLocation(locationSun, 0, 9 * distanceFactor, -2000),
            imgDrawable: new AR.ImageDrawable(earthImg, earthSize),
            size: earthSize,
            description: "Is the third planet from the Sun, and the densest and fifth-largest of the eight planets in the Solar System. It is sometimes referred to as the world, the Blue Planet, Terra.",
            mass: " 6&nbsp;10<sup>24</sup>&nbsp;kg",
            diameter: "12,742&nbsp;km"
        };

        var mars = {
            name: "DATO 4",
            distance: 10 * distanceFactor,
            location: new AR.RelativeLocation(locationSun, 0, -10 * distanceFactor, -3000),
            imgDrawable: new AR.ImageDrawable(marsImg, marsSize),
            size: marsSize,
            description: "Is named after the Roman god of war, it is often described as the &quot;Red Planet&quot;, as the iron oxide prevalent on its surface gives it a reddish appearance.",
            mass: "6.4&nbsp;10<sup>23</sup>&nbsp;kg",
            diameter: "6794&nbsp;km"
        };


        /* put sun, planets (and pluto) in an array */
        this.planetsInfo = [parque, mercury, venus, earth, mars];

         //objeto recibido
         this.marcadorseleccionado=marcador;
         //if(this.marcadorseleccionado!=null){
         //alert("Dato "+this.marcadorseleccionado.name)
         //}

        /* create helper array to create goeObjects out of given information */
        var planetsGeoObjects = [];
        for (var i = 0; i < this.planetsInfo.length; i++) {

            /* show name of object below*/
            var label = new AR.Label(this.planetsInfo[i].name, 3, {
                offsetY: -this.planetsInfo[i].size / 2,
                verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP,
                opacity: 0.9,
                zOrder: 1,
                style: {
                    textColor: '#FFFFFF',
                    backgroundColor: '#00000005'
                }
            });

            /* drawable in cam of object -> image and label */
            var drawables = [];
            drawables[0] = this.planetsInfo[i].imgDrawable;
            drawables[1] = label;

            /* Create objects in AR*/
            planetsGeoObjects[i] = new AR.GeoObject(this.planetsInfo[i].location, {
                drawables: {
                    cam: drawables
                },
                //animacion desabilitada
                //enabled: false
                onClick: this.planetClicked(this.planetsInfo[i],this.marcadorseleccionado)
            });
            if (i > 0) {
                this.animate(this.planetsInfo[i]);
            } else {
                var sunHackAnim = new AR.PropertyAnimation(this.planetsInfo[i].location, 'northing', 10000, 10000, 1000, {
                    type: AR.CONST.EASING_CURVE_TYPE.EASE_IN_SINE
                });
                sunHackAnim.start(-1);
            }
        }

        // Add indicator to sun
        var imageDrawable = new AR.ImageDrawable(indicatorImg, 0.1, {
            verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP
        });
        planetsGeoObjects[0].drawables.addIndicatorDrawable(imageDrawable);
    },

    animate: function(planet) {
        var relLocation = planet.location;
        var distance = planet.distance;
        var roundingTime = distance * 2 * 2;

        var northSouthAnimation1 = new AR.PropertyAnimation(relLocation, 'northing', distance * 1, distance * 0, roundingTime / 4, {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_IN_OUT_QUINT
        });
        var eastWestAnimation1 = new AR.PropertyAnimation(relLocation, 'easting', distance * 0, distance * 1, roundingTime / 4, {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_OUT_IN_QUINT
        });

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

        var northSouthAnimation4 = new AR.PropertyAnimation(relLocation, 'northing', distance * 0, distance * 1, roundingTime / 4, {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_OUT_IN_QUINT
        });
        var eastWestAnimation4 = new AR.PropertyAnimation(relLocation, 'easting', distance * -1, distance * 0, roundingTime / 4, {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_IN_OUT_QUINT
        });

        var q1 = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [northSouthAnimation1, eastWestAnimation1]);
        var q2 = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [northSouthAnimation2, eastWestAnimation2]);
        var q3 = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [northSouthAnimation3, eastWestAnimation3]);
        var q4 = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [northSouthAnimation4, eastWestAnimation4]);

        var cicularAnimationGroup = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.SEQUENTIAL, [q2,q3]);

        cicularAnimationGroup.start(-1);
    },



    planetClicked: function(marker, objeto) {

        return function() {
        if (marker.name == "Parque"){
            // update panel values
            		$("#poi-detail-title").html(objeto.name);
            		$("#poi-detail-description").html(objeto.description);
            		$("#poi-detail-distance").html(objeto.longitude);
            		// show panel
            		$("#panel-poidetail").panel("open", 123);
            		$(".ui-panel-dismiss").unbind("mousedown");
            		$("#panel-poidetail").on("panelbeforeclose", function(event, ui) {
            			World.currentMarker.setDeselected(World.currentMarker);
            		});
        }else{
               // alert("Datos click");
                document.getElementById("info").setAttribute("class", "info");
                document.getElementById("name").innerHTML = marker.name;
                document.getElementById("dato1").innerHTML = objeto.latitude;
                document.getElementById("dato2").innerHTML =objeto.longitude;
                document.getElementById("info").setAttribute("class", "infoVisible");
        }

        };
    }
};

//Solar.init();