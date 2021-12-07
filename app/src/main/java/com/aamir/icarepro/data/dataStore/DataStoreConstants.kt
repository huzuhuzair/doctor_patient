package com.aamir.icarepro.data.dataStore

import androidx.datastore.preferences.preferencesKey

object DataStoreConstants{

    const val DataStoreName = "prefs"

    val DB_SECRET = preferencesKey<String>("db_secret")
    val AGENT_DB_SECRET = preferencesKey<String>("agent_db_secret")
    val DB_INFORMATION = preferencesKey<String>("db_information")
    val CURRENCY_INF = preferencesKey<String>("currency_inf")
    val APP_UNIQUE_CODE = preferencesKey<String>("app_unique_code")
    val USER_LOGGED_IN = preferencesKey<Boolean>("user_logged_in")
    val USER_DOC_UPLOADED = preferencesKey<Boolean>("user_doc_uploaded")
    val USER_SELECTED_ADDRESS = preferencesKey<Int>("user_selected_address")
    val USER_SELECTED_ADDRESS_ID = preferencesKey<Int>("user_selected_address_id")
    val USER_SELECTED_CAR = preferencesKey<Int>("user_selected_car")
    val USER_SELECTED_CAR_ID = preferencesKey<Int>("user_selected_car_id")
    val USER_LOGIN_DATA = preferencesKey<String>("user_login_data")
    val USER_ADDRESS = preferencesKey<String>("user_address_data")
    val LAT_LANG = preferencesKey<String>("lat_lang")
    val USER_CARS = preferencesKey<String>("user_cars_data")
    val FEATURE_DATA = preferencesKey<String>("feature_data")
    val DIALOG_TOKEN = preferencesKey<String>("dialog_token")
    val APP_TYPE = preferencesKey<Int>("app_type")
    val BOOKING_FLOW = preferencesKey<String>("booking_flow")
    val SETTING_DATA = preferencesKey<String>("setting_data")
    val APP_TERMINOLOGY = preferencesKey<String>("app_terminology")
    val GENRIC_SUPPLIERID = preferencesKey<Int>("genric_supplierId")
    val TERMS_CONDITION = preferencesKey<String>("terms_conditions")
    val GENERIC_SPL_BRANCHID = preferencesKey<Int>("genric_spl_branchId")
    val RESTAURANT_INF = preferencesKey<String>("restaurant_inf")
    val SCREEN_FLOW = preferencesKey<String>("screen_flow")
    val SELF_PICKUP = preferencesKey<String>("self_pickup")
    val SELECTED_LANGUAGE = preferencesKey<Int>("selected_language")
    val USER_DATA = preferencesKey<String>("user_data")
    val DEFAULT_BRNACH_ID = preferencesKey<String>("default_branch_id")
    val SUPPLIER_STATUS = preferencesKey<Boolean>("supplier_status")


    val USER_ID = preferencesKey<String>("user_id")
    val CART_ID = preferencesKey<String>("cart_id")
    val ACCESS_TOKEN = preferencesKey<String>("access_token")



    const val TYPE_STRING = "string"
    const val TYPE_DOUBLE = "double"
    const val TYPE_BOOLEAN = "boolean"
    const val TYPE_FLOAT = "float"
    const val TYPE_INT = "int"
}