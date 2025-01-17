package com.example.myapplication.util;

import com.example.myapplication.GeoArActivity;
import com.wikitude.common.devicesupport.Feature;

import java.util.EnumSet;

import static com.wikitude.common.camera.CameraSettings.CameraFocusMode;
import static com.wikitude.common.camera.CameraSettings.CameraPosition;
import static com.wikitude.common.camera.CameraSettings.CameraResolution;

public class DefaultConfigs {

    private DefaultConfigs() {
    }

    // Defaults configuration for samples
    public static final Class DEFAULT_ACTIVITY = GeoArActivity.class;
    public static final CameraPosition DEFAULT_CAMERA_POSITION = CameraPosition.DEFAULT;
    public static final CameraResolution DEFAULT_CAMERA_RESOLUTION = CameraResolution.SD_640x480;
    public static final CameraFocusMode DEFAULT_CAMERA_FOCUS_MODE = CameraFocusMode.CONTINUOUS;
    public static final boolean DEFAULT_CAMERA_2_ENABLED = true;
    public static final EnumSet<Feature> DEFAULT_AR_FEATURES = EnumSet.allOf(Feature.class);

}
