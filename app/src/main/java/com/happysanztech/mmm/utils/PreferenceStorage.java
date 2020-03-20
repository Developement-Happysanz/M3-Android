package com.happysanztech.mmm.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Admin on 02-01-2018.
 */

public class PreferenceStorage {

    /*To save FCM key locally*/
    public static void saveGCM(Context context, String gcmId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_FCM_ID, gcmId);
        editor.apply();
    }

    public static String getGCM(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getString(MobilizerConstants.KEY_FCM_ID, "");
    }
    /*End*/

    // UserId
    public static void saveUserId(Context context, String userId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_USER_ID, userId);
        editor.apply();
    }

    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String  userId;
        userId = sharedPreferences.getString(MobilizerConstants.KEY_USER_ID, "");
        return userId;
    }
    /*End*/

    // Name
    public static void saveName(Context context, String name) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_NAME, name);
        editor.apply();
    }

    public static String getName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String name;
        name = sharedPreferences.getString(MobilizerConstants.KEY_NAME, "");
        return name;
    }
    /*End*/

    // User Name
    public static void saveUserName(Context context, String userName) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_USER_NAME, userName);
        editor.apply();
    }

    public static String getUserName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userName;
        userName = sharedPreferences.getString(MobilizerConstants.KEY_USER_NAME, "");
        return userName;
    }
    /*End*/

    // User Image
    public static void saveUserPicture(Context context, String userPicture) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_USER_IMAGE, userPicture);
        editor.apply();
    }

    public static String getUserPicture(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userPicture;
        userPicture = sharedPreferences.getString(MobilizerConstants.KEY_USER_IMAGE, "");
        return userPicture;
    }
    /*End*/

    // User Type Name
    public static void saveUserTypeName(Context context, String userTypeName) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_USER_TYPE_NAME, userTypeName);
        editor.apply();
    }

    public static String getUserTypeName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userTypeName;
        userTypeName = sharedPreferences.getString(MobilizerConstants.KEY_USER_TYPE_NAME, "");
        return userTypeName;
    }
    /*End*/

    // User Type
    public static void saveUserType(Context context, String userType) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_USER_TYPE, userType);
        editor.apply();
    }

    public static String getUserType(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(MobilizerConstants.KEY_USER_TYPE, "");
        return userType;
    }
    /*End*/

    // Staff Id
    public static void saveStaffId(Context context, String staffId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_STAFF_ID, staffId);
        editor.apply();
    }

    public static String getStaffId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(MobilizerConstants.KEY_STAFF_ID, "");
        return userType;
    }
    /*End*/

    // Admission Id
    public static void saveAdmissionId(Context context, String staffId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.PARAMS_ADMISSION_ID, staffId);
        editor.apply();
    }

    public static String getAdmissionId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(MobilizerConstants.PARAMS_ADMISSION_ID, "");
        return userType;
    }
    /*End*/

    // Admission Id
    public static void saveCaste(Context context, String staffId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_COMMUNITY_CLASS, staffId);
        editor.apply();
    }

    public static String getCaste(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(MobilizerConstants.KEY_COMMUNITY_CLASS, "");
        return userType;
    }
    /*End*/

    // Admission Id
    public static void saveDisability(Context context, String staffId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.PARAMS_DISABILITY, staffId);
        editor.apply();
    }

    public static String getDisability(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(MobilizerConstants.PARAMS_DISABILITY, "");
        return userType;
    }
    /*End*/

    // PIA Id
    public static void savePIAId(Context context, String staffId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_PIA_ID, staffId);
        editor.apply();
    }

    public static String getPIAId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(MobilizerConstants.KEY_PIA_ID, "");
        return userType;
    }
    /*End*/

    // Staff Full Name
    public static void saveStaffFullName(Context context, String staffFullName) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_NAME, staffFullName);
        editor.apply();
    }

    public static String getStaffFullName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(MobilizerConstants.KEY_NAME, "");
        return userType;
    }
    /*End*/

    // Staff Sex
    public static void saveStaffSex(Context context, String staffSex) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_SEX, staffSex);
        editor.apply();
    }

    public static String getStaffSex(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(MobilizerConstants.KEY_SEX, "");
        return userType;
    }
    /*End*/

    // Staff Sex
    public static void saveStaffAge(Context context, String staffAge) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_AGE, staffAge);
        editor.apply();
    }

    public static String getStaffAge(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(MobilizerConstants.KEY_AGE, "");
        return userType;
    }
    /*End*/

    // Staff Nationality
    public static void saveStaffNationality(Context context, String staffNationality) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_NATIONALITY, staffNationality);
        editor.apply();
    }

    public static String getStaffNationality(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(MobilizerConstants.KEY_NATIONALITY, "");
        return userType;
    }
    /*End*/

    // Staff Religion
    public static void saveStaffReligion(Context context, String staffReligion) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_RELIGION, staffReligion);
        editor.apply();
    }

    public static String getStaffReligion(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String staffReligion;
        staffReligion = sharedPreferences.getString(MobilizerConstants.KEY_RELIGION, "");
        return staffReligion;
    }
    /*End*/

    // Staff Community Class
    public static void saveStaffCommunityClass(Context context, String staffCommunityClass) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_COMMUNITY_CLASS, staffCommunityClass);
        editor.apply();
    }

    public static String getStaffCommunityClass(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String staffCommunityClass;
        staffCommunityClass = sharedPreferences.getString(MobilizerConstants.KEY_COMMUNITY_CLASS, "");
        return staffCommunityClass;
    }
    /*End*/

    // Staff Community
    public static void saveStaffCommunity(Context context, String staffCommunity) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_COMMUNITY, staffCommunity);
        editor.apply();
    }

    public static String getStaffCommunity(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String staffCommunity;
        staffCommunity = sharedPreferences.getString(MobilizerConstants.KEY_COMMUNITY, "");
        return staffCommunity;
    }
    /*End*/

    // Staff Address
    public static void saveStaffAddress(Context context, String staffAddress) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_ADDRESS, staffAddress);
        editor.apply();
    }

    public static String getStaffAddress(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String staffAddress;
        staffAddress = sharedPreferences.getString(MobilizerConstants.KEY_ADDRESS, "");
        return staffAddress;
    }
    /*End*/

    // Staff Email
    public static void saveStaffEmail(Context context, String staffEmail) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_EMAIL, staffEmail);
        editor.apply();
    }

    public static String getStaffEmail(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String staffEmail;
        staffEmail = sharedPreferences.getString(MobilizerConstants.KEY_EMAIL, "");
        return staffEmail;
    }
    /*End*/

    // Staff Phone
    public static void saveStaffPhone(Context context, String staffPhone) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_PHONE, staffPhone);
        editor.apply();
    }

    public static String getStaffPhone(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String staffPhone;
        staffPhone = sharedPreferences.getString(MobilizerConstants.KEY_PHONE, "");
        return staffPhone;
    }
    /*End*/

    // Staff Qualification
    public static void saveStaffQualification(Context context, String staffQualification) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_QUALIFICATION, staffQualification);
        editor.apply();
    }

    public static String getStaffQualification(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String staffQualification;
        staffQualification = sharedPreferences.getString(MobilizerConstants.KEY_QUALIFICATION, "");
        return staffQualification;
    }
    /*End*/

    // Staff Qualification
    public static void saveAadhaarAction(Context context, String staffQualification) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_AADHAAR, staffQualification);
        editor.apply();
    }

    public static String getAadhaarAction(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String staffQualification;
        staffQualification = sharedPreferences.getString(MobilizerConstants.KEY_AADHAAR, "");
        return staffQualification;
    }
    /*End*/

    // Center Id
    public static void saveCenterId(Context context, String staffQualification) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MobilizerConstants.KEY_CENTER_ID, staffQualification);
        editor.apply();
    }

    public static String getCenterId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String staffQualification;
        staffQualification = sharedPreferences.getString(MobilizerConstants.KEY_CENTER_ID, "");
        return staffQualification;
    }
    /*End*/

    // Center Id
    public static void saveLocationCheck(Context context, Boolean check) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("tnsrl_check", check);
        editor.apply();
    }

    public static Boolean getLocationCheck(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Boolean check = true;
        check = sharedPreferences.getBoolean("tnsrl_check",check);
        return check;
    }
    /*End*/


}
