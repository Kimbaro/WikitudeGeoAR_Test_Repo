/* Implementation of AR-Experience (aka "World"). */
var World = {
    /* You may request new data from server periodically, however: in this sample data is only requested once. */
    isRequestingData: false,

    /* True once data was fetched. */
    initiallyLoadedData: false,

    /* Different POI-Marker assets. */
    markerDrawableIdle: null,
    markerDrawableSelected: null,
    markerDrawableDirectionIndicator: null,

    /* List of AR.GeoObjects that are currently shown in the scene / World. */
    markerList: [],

    /* the last selected marker. */
    currentMarker: null,

    /* Called to inject new POI data. */
    loadPoisFromJsonData: function loadPoisFromJsonDataFn(poiData) {

        /* Empty list of visible markers. */
        World.markerList = [];

        /* Start loading marker assets. */
        World.markerDrawableIdle = new AR.ImageResource("assets/marker.png", {
            onError: World.onError
        });
        World.markerDrawableSelected = new AR.ImageResource("assets/logo2.gif", {
            onError: World.onError
        });
        World.markerDrawableDirectionIndicator = new AR.ImageResource("assets/indi.png", {
            onError: World.onError
        });

        /* Loop through POI-information and create an AR.GeoObject (=Marker) per POI. */
        for (var currentPlaceNr = 0; currentPlaceNr < poiData.length; currentPlaceNr++) {
            var singlePoi = {
                "id": poiData[currentPlaceNr].id,
                "latitude": parseFloat(poiData[currentPlaceNr].latitude),
                "longitude": parseFloat(poiData[currentPlaceNr].longitude),
                "altitude": parseFloat(poiData[currentPlaceNr].altitude),
                "title": poiData[currentPlaceNr].name,
                "description": poiData[currentPlaceNr].description
            };


            World.markerList.push(new Marker(singlePoi));
        }

        World.updateStatusMessage(currentPlaceNr + ' places loaded');
    },

    /* Updates status message shown in small "i"-button aligned bottom center. */
    updateStatusMessage: function updateStatusMessageFn(message, isWarning) {

        var themeToUse = isWarning ? "e" : "c";
        var iconToUse = isWarning ? "alert" : "info";

        $("#status-message").html(message);
        $("#popupInfoButton").buttonMarkup({
            theme: themeToUse,
            icon: iconToUse
        });
    },

    /* Location updates, fired every time you call architectView.setLocation() in native environment. */
    locationChanged: function locationChangedFn(lat, lon, alt, acc) {

        /* Request data if not already present. */
        if (!World.initiallyLoadedData) {
            World.requestDataFromLocal(lat, lon);
            World.initiallyLoadedData = true;
        }
    },

    /* Fired when user pressed maker in cam. */
    onMarkerSelected: function onMarkerSelectedFn(marker) {
        World.currentMarker = marker;
//        /* Deselect previous marker. */
//        if (World.currentMarker) {
//            if (World.currentMarker.poiData.id === marker.poiData.id) {
//                return;
//            }
//            World.currentMarker.setDeselected(World.currentMarker);
//        }

        /* Show panel. */
        $("#panel-poidetail").panel("open", 123);
        $(".ui-panel-dismiss").unbind("mousedown");

        /* Update panel values. */
        $("#poi-detail-title").html(marker.poiData.title);
        $("#poi-detail-description").html(marker.poiData.description);

        /* Deselect AR-marker when user exits detail screen div. */
        $("#panel-poidetail").on("panelbeforeclose", function (event, ui) {
            World.currentMarker.setDeselected(World.currentMarker);
        });
        /* Highlight current one. */
        marker.setSelected(marker);
    },// user clicked "More" button in POI-detail panel -> fire event to open native screen
    onPoiDetailMoreButtonClicked: function onPoiDetailMoreButtonClickedFn() {
        var currentMarker = World.currentMarker;
        var markerSelectedJSON = {
            name: "markerselected",
            id: currentMarker.poiData.id,
            title: currentMarker.poiData.title,
            description: currentMarker.poiData.description
        };
        AR.platform.sendJSONObject(markerSelectedJSON);
    },

    /*
        In case the data of your ARchitect World is static the content should be stored within the application.
        Create a JavaScript file (e.g. myJsonData.js) where a globally accessible variable is defined.
        Include the JavaScript in the ARchitect Worlds HTML by adding <script src="js/myJsonData.js"/> to make POI
        information available anywhere in your JavaScript.
    */

    /* Request POI data. */
    requestDataFromLocal: function requestDataFromLocalFn(lat, lon) {
        //DEMO Helper 2019-05-15
        // var poisNearby = Helper.bringPlacesToUser(myJsonData, lat, lon);
        // World.loadPoisFromJsonData(poisNearby);

        /*
            For demo purpose they are relocated randomly around the user using a 'Helper'-function.
            Comment out previous 2 lines and use the following line > instead < to use static values 1:1.
        */
         World.loadPoisFromJsonData(myJsonData);
    },

    onError: function onErrorFn(error) {
        alert(error);
    }
};


//현재 위치로 임의의 마커생성... ##사용에주의
var Helper = {

    /*
        For demo purpose only, this method takes poi data and a center point (latitude, longitude) to relocate the
        given places randomly around the user
    */
    bringPlacesToUser: function bringPlacesToUserFn(poiData, latitude, longitude) {
        for (var i = 0; i < poiData.length; i++) {
            poiData[i].latitude = latitude + (Math.random() / 5 - 0.1);
            poiData[i].longitude = longitude + (Math.random() / 5 - 0.1);
            /*
                Note: setting altitude to '0' will cause places being shown below / above user, depending on the
                user 's GPS signal altitude. Using this contant will ignore any altitude information and always
                show the places on user-level altitude.
            */
            poiData[i].altitude = AR.CONST.UNKNOWN_ALTITUDE;
        }
        return poiData;
    }
};

/* Forward locationChanges to custom function. */
AR.context.onLocationChanged = World.locationChanged;

/* Forward clicks in empty area to World. */
AR.context.onScreenClick = World.onScreenClick;

