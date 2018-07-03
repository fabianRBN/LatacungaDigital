var World = {
    loaded: false,
    objectInfo: null,
    rotating: false,
    selectedObject: null,

    init: function initFn() {
        this.targetCollectionResource = new AR.TargetCollectionResource("assets/Lugares.wtc", {
            onError: function(errorMessage) {
                alert(errorMessage);
            }
        });

        this.tracker = new AR.ImageTracker(this.targetCollectionResource, {
            onTargetsLoaded: this.trackerLoaded,
            onError: this.trackerError
        });

        var sizeFactor = 0.01;
        var distanceFactor = 0.01;

        this.ejeLocation = {
            x: 0,
            y: 0,
            z: 0.5
        };

        this.objectInfo = [{
                name: "Eje",
                distance: 0,
                modelFile: "assets/eje.wt3",
                realSize: 0.2,
                description: "Sonido",
                x:0,
                y:0
            },
            {
                name: "Ubicación",
                distance: 40 * distanceFactor,
                modelFile: "assets/ubicacion.wt3",
                realSize: 0.003,
                description: "Entre las calles Hnas. Páez y Quito.",
                x:-0.8,
                y:0.3
            },

            {
                name: "Construcción",
                distance: 10 * distanceFactor,
                modelFile: "assets/construccion.wt3",
                realSize: 0.003,
                description: "La iglesia provincial fue edificada en 1650, pero el terremoto de 1797 la destruyó por completo y un nuevo templo surgió a partir de 1850.",
                x:-0.8,
                y:-0.3
            },

            {
                name: "Descripción",
                distance: 10 * distanceFactor,
                modelFile: "assets/descripcion.wt3",
                realSize: 0.003 ,
                description: "El convento de los agustinos ha sido noviciado, centro vocacional y centro de estudios superiores, la parte frontal del templo presenta una puerta de madera tallada con ábside circular a cuyos lados se alzan dos columnas que se interrumpen a media altura y luego termina en otro par de dimensiones reducidas que sirven para el arco final del frontispicio. A su vez dos torres aguzadas y un solo nivel culminan esta área frontal.",
                x:0.8,
                y:0.3
            },

            {
                name: "Atractivos",
                distance: 10 * distanceFactor,
                modelFile: "assets/atractivos.wt3",
                realSize: 0.003,
                description: "-Réplica de la Virgen del Quinche (en el lado izquierdo) y San Agustín-El altar está construido en madera y pan de oro.",
                x:0.8,
                y:-0.3
            }


        ];

        var objects = [];
        this.objectAnimations = [];

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
        //Titulo del nombre del lugar escaneado
           this.modeltitle = new AR.Model("assets/tituloSanAgustin.wt3", {
                    onLoaded: this.loadingStep,
                    scale: {
                        x: 0.005,
                        y: 0.005,
                        z: 0.005
        			},
        			translate: {
        				x: -0.7,
        				y: 0.8,
        				z: 0.5
        			}
        		});
        var backdropImg = new AR.ImageResource("assets/backdrop.png");
        var backdrop = [new AR.ImageDrawable(backdropImg, 2)];

        var overlay = new AR.ImageTrackable(this.tracker, "SanAgustin", {
            drawables: {
                cam: backdrop.concat(objects,this.modeltitle)
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
            if(object.name == "Eje"){
           World.sirenSound.play();
            }else{
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
    },

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
        document.getElementById('toggleAnimationBtn').style.display = "block";
        if (World.selectedObject !== null) {
            document.getElementById("info").setAttribute("class", "infoVisible");
        }
    },

    trackerExitFov: function trackerExitFovFn() {
        document.getElementById('toggleAnimationBtn').style.display = "none";
        document.getElementById("info").setAttribute("class", "info");
    }
};

World.init();
