/* Implementation of AR-Experience (aka "World"). */
var World = {
    /* True once data was fetched. */
    initiallyLoadedData: false,

    /* pOI-Marker asset. */
    markerDrawableIdle: null,

    /* Called to inject new POI data. */
    //안드로이드 자바스크립트 함수 맵핑으로 데이터 전달 *******
    //json 형식의 마커 데이터 전달
    loadPoisFromJsonData: function loadPoisFromJsonDataFn(poiData) {

        /*
            The example Image Recognition already explained how images are loaded and displayed in the augmented
            reality view. This sample loads an AR.ImageResource when the World variable was defined. It will be
            reused for each marker that we will create afterwards.
        */
        //마커 이미지 경로
        World.markerDrawableIdle = new AR.ImageResource("assets/marker_idle.png", {
            onError: World.onError
        });

        /*
            For creating the marker a new object AR.GeoObject will be created at the specified geolocation. An
            AR.GeoObject connects one or more AR.GeoLocations with multiple AR.Drawables. The AR.Drawables can be
            defined for multiple targets. A target can be the camera, the radar or a direction indicator. Both the
            radar and direction indicators will be covered in more detail in later examples.
        */
        var markerLocation = new AR.GeoLocation(poiData.latitude, poiData.longitude, poiData.altitude);
        var markerImageDrawableIdle = new AR.ImageDrawable(World.markerDrawableIdle, 2.5, {
            zOrder: 0,
            opacity: 1.0
        });

        /* Create GeoObject. */

        //지도상 마커 등록
        var markerObject = new AR.GeoObject(markerLocation, {
            drawables: {
                cam: [markerImageDrawableIdle]
            }
        });

        /* Updates status message as a user feedback that everything was loaded properly. */
        World.updateStatusMessage('1 place loaded');
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
    //안드로이드 함수 맵핑,
    //자신의 위치를 1초마다 갱신한 locationChanged함수를 호출하며 자바스크립트 상에서 해당 함수 호출 후 갱신된
    //자신의 위치를 기준으로 랜덤하게 마커 생성
    locationChanged: function locationChangedFn(lat, lon, alt, acc) {
        alert("javascript call method : "+lon+"||"+lat+"||"+alt);
        /*
            The custom function World.onLocationChanged checks with the flag World.initiallyLoadedData if the
            function was already called. With the first call of World.onLocationChanged an object that contains geo
            information will be created which will be later used to create a marker using the
            World.loadPoisFromJsonData function.
        */
        if (!World.initiallyLoadedData) {
            /* Creates a poi object with a random location near the user's location. */
            var poiData = {
                "id": 1,
                "longitude": (lon + (Math.random() / 5 - 0.1)),
                "latitude": (lat + (Math.random() / 5 - 0.1)),
                "altitude": 100.0
            };

            World.loadPoisFromJsonData(poiData);
            World.initiallyLoadedData = true;
        }
    },

    onError: function onErrorFn(error) {
        alert(error);
    }
};

/* 
    Set a custom function where location changes are forwarded to. There is also a possibility to set
    AR.context.onLocationChanged to null. In this case the function will not be called anymore and no further
    location updates will be received.
*/
//위의 설정정보를 onLocationChanged에 전달
AR.context.onLocationChanged = World.locationChanged;