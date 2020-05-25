package com.neverim.ubergarage;

public class Constants {
    public static final int PICK_IMAGE = 1;
    public static final int READ_EXTERNAL_REQUEST_CODE = 2;
    public static final int OFFER_RECYCLER = 0;
    public static final int PROFILE_RECYCLER = 1;
    public static final int WITH_IMAGE = 1;
    public static final int NO_IMAGE = 0;
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static int CURRENT_USER_ID = -1;
    public static final int BACK_TIME_INTERVAL = 2000;

    public static int getUserId() {
        return CURRENT_USER_ID;
    }

    public static void setUserId(int id) {
        CURRENT_USER_ID = id;
    }
}
