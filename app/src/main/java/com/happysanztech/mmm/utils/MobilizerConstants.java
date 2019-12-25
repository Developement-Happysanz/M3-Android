package com.happysanztech.mmm.utils;

/**
 * Created by Admin on 02-01-2018.
 */

public class MobilizerConstants {

    // URLS
    // BASE URL
//    private static final String BASE_URL = "https://iyerandrao.com/";
    private static final String BASE_URL = "https://m3tnsrlm/happysanz.net/";

    // BUILD URL
//    public static final String BUILD_URL = BASE_URL + "uitensyfi/";
//    public static final String BUILD_URL = BASE_URL + "M3TNSRLM/";
//    public static final String BUILD_URL = BASE_URL + "m3test/";
    public static final String BUILD_URL = BASE_URL + "application/";

    // GENERAL URL
    // USERS URL
    public static final String USER_LOGIN_API = "apimain/login/";

    //Mobilizer URL
    private static final String MOBILIZER_API = "apimobilizer/";

    //    SignIn params
    public static final String PARAMS_USERNAME = "user_name";
    public static final String PARAMS_PASSWORD = "password";
    public static final String PARAMS_GCM_KEY = "device_id";
    public static final String PARAMS_MOBILE_TYPE = "mobile_type";

    // User Login Preferences
    // User data
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_IMAGE = "user_pic";
    public static final String KEY_USER_TYPE = "user_type";
    public static final String KEY_USER_TYPE_NAME = "user_type_name";
    // Staff profile
    public static final String KEY_STAFF_ID = "staff_id";
    public static final String KEY_PIA_ID = "pia_id";
    public static final String KEY_SEX = "sex";
    public static final String KEY_AGE = "age";
    public static final String KEY_NATIONALITY = "nationality";
    public static final String KEY_RELIGION = "religion";
    public static final String KEY_COMMUNITY_CLASS = "community_class";
    public static final String KEY_COMMUNITY = "community";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_QUALIFICATION = "qualification";

    // Aadhaar action
    public static final String KEY_AADHAAR = "aadhaar_action";

    // Aadhaar action
    public static final String KEY_CENTER_ID = "center_id";


    // PARAMETERS
    //Service Params
    public static String PARAM_MESSAGE = "msg";

    //    Shared FCM ID
    public static final String KEY_FCM_ID = "fcm_id";

    // Alert Dialog Constants
    public static String ALERT_DIALOG_TITLE = "alertDialogTitle";
    public static String ALERT_DIALOG_MESSAGE = "alertDialogMessage";
    public static String ALERT_DIALOG_TAG = "alertDialogTag";
    public static String ALERT_DIALOG_POS_BUTTON = "alert_dialog_pos_button";
    public static String ALERT_DIALOG_NEG_BUTTON = "alert_dialog_neg_button";

    //    Trade list
    public static final String BLOOD_GROUP_LIST = MOBILIZER_API + "select_bloodgroup/";

    //    Trade list
    public static final String TRADE_LIST = MOBILIZER_API + "select_trade/";

    //    Trade list
    public static final String TIMINGS_LIST = MOBILIZER_API + "select_timings/";

    //    Upload candidate pic
    public static final String UPLOAD_CANDIDATE_PIC = MOBILIZER_API + "student_picupload/";

    //    Upload candidate pic
    public static final String UPLOAD_USER_PIC = MOBILIZER_API + "user_profilepic/";

    //    Upload candidate pic
    public static final String GET_USER_PROFILE = MOBILIZER_API + "user_profile/";

    //    Upload candidate pic
    public static final String UPDATE_USER_PROFILE = MOBILIZER_API + "user_profile_update/";

    //    Add candidate
    public static final String ADD_CANDIDATE = MOBILIZER_API + "add_student/";

    //    PROSPECT LIST
    public static final String ALL_STUDENTS = MOBILIZER_API + "list_students";

    //    PROSPECT STATUS
    public static final String STUDENTS_LIST_STATUS = MOBILIZER_API + "list_students_status";

    //    PROSPECT DETAIL
    public static final String STUDENT_DETAIL = MOBILIZER_API + "view_student";

    //    UPDATE CANDIDATE
    public static final String UPDATE_CANDIDATE = MOBILIZER_API + "update_student";

    //    Add candidate params
    public static final String KEY_STUDENT_ID = "student_id";
    public static final String PARAMS_HAVE_AADHAAR_CARD = "have_aadhaar_card";
    public static final String PARAMS_AADHAAR_CARD_NUMBER = "aadhaar_card_number";
    public static final String PARAMS_NAME = "name";
    public static final String PARAMS_ADMISSION_ID = "admission_id";
    public static final String PARAMS_SEX = "sex";
    public static final String PARAMS_DOB = "dob";
    public static final String PARAMS_AGE = "age";
    public static final String PARAMS_NATIONALITY = "nationality";
    public static final String PARAMS_RELIGION = "religion";
    public static final String PARAMS_COMMUNITY_CLASS = "community_class";
    public static final String PARAMS_COMMUNITY = "community";
    public static final String PARAMS_FATHER_NAME = "father_name";
    public static final String PARAMS_MOTHER_NAME = "mother_name";
    public static final String PARAMS_MOBILE = "mobile";
    public static final String PARAMS_SEC_MOBILE = "sec_mobile";
    public static final String PARAMS_EMAIL = "email";
    public static final String PARAMS_STATE = "state";
    public static final String PARAMS_CITY = "city";
    public static final String PARAMS_ADDRESS = "address";
    public static final String PARAMS_MOTHER_TONGUE = "mother_tongue";
    public static final String PARAMS_DISABILITY = "disability";
    public static final String PARAMS_BLOOD_GROUP = "blood_group";
    public static final String PARAMS_ADMISSION_DATE = "admission_date";
    public static final String PARAMS_ADMISSION_LOCATION = "admission_location";
    public static final String PARAMS_ADMISSION_LATITUDE = "admission_latitude ";
    public static final String PARAMS_ADMISSION_LONGITUDE = "admission_longitude";
    public static final String PARAMS_PREFERRED_TRADE = "preferred_trade";
    public static final String PARAMS_PREFERRED_TIMING = "preferred_timing";
    public static final String PARAMS_LAST_INSTITUTE = "last_institute";
    public static final String PARAMS_LAST_STUDIED = "last_studied";
    public static final String PARAMS_QUALIFIED_PROMOTION = "qualified_promotion";
    public static final String PARAMS_TRANSFER_CERTIFICATE = "transfer_certificate";
    public static final String PARAMS_STATUS = "status";
    public static final String PARAMS_CREATED_BY = "created_by";
    public static final String PARAMS_CREATED_AT = "created_at";
    public static final String PARAMS_PIA_ID = "pia_id";

    //    Center details
    public static final String CENTER_INFO = MOBILIZER_API + "view_centerdetails/";

    //    Trade details
    public static final String TRADES = MOBILIZER_API + "select_trade/";

    //    Batch details
    public static final String BATCH = MOBILIZER_API + "select_batch/";

    //    Batch details params
    public static final String PARAMS_TRADE_ID = "trade_id";

    //    MTS
    public static final String MTS = MOBILIZER_API + "add_mobilocation/";

    //    MTS params
    public static final String PARAMS_LATITUDE = "latitude";
    public static final String PARAMS_LONGITUDE = "longitude";
    public static final String PARAMS_LOCATION = "location";
    public static final String PARAMS_DISTANCE = "miles";
    public static final String PARAMS_DATETIME = "location_datetime";

    //Forgot password URL
    public static final String USER_FORGOT_PASSWORD = "apimain/forgot_password/";

    //    Change password
    public static final String CHANGE_PASSWORD = "apimain/change_password/";

    //    Change password params
    public static final String PARAMS_OLD_PASSWORD = "old_password";
    public static final String PARAMS_NEW_PASSWORD = "new_password";

    //    Task list
    public static final String TASK_LIST = MOBILIZER_API + "list_task/";

    //    Task add
    public static final String TASK_ADD = MOBILIZER_API + "add_task/";

    //    Task add params
    public static final String PARAMS_TASK_TITLE = "task_title";
    public static final String PARAMS_TASK_DESCRIPTION = "task_description";
    public static final String PARAMS_TASK_DATE = "task_date";
    public static final String PARAMS_TASK_STATUS = "status";

    //    Task photo upload
    public static final String TASK_PHOTOS = MOBILIZER_API + "task_picupload/";

    //    Task photo params
    public static final String PARAMS_TASK_ID = "task_id";

    //    Task update
    public static final String TASK_UPDATE = MOBILIZER_API + "update_task/";

    //    Task image list
    public static final String TASK_IMAGE_LIST = MOBILIZER_API + "list_taskpic/";

    //    Center image list
    public static final String CENTER_IMAGE_LIST = MOBILIZER_API + "view_centerimages/";

    //    Center photo params
    public static final String PARAMS_CENTER_ID = "center_id";

    //    Center image list
    public static final String CENTER_VIDEO_LIST = MOBILIZER_API + "view_centervideos/";

    //    Center image list
    public static final String NOTIFICATION_LIST = MOBILIZER_API + "disp_circular/";

    //    Center image list
    public static final String USER_DAILY_ACTIVITY = "apimain/user_activity";
    public static final String DAILY_ACTIVITY = "activity_detail";

}
