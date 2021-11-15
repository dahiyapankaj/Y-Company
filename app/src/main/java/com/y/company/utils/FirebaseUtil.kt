package com.y.company.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.AuthUI
import com.y.company.BuildConfig
import com.y.company.utils.FirebaseUtil

/**
 * Utility class for initializing Firebase services and connecting them to the Firebase Emulator
 * Suite if necessary.
 */
object FirebaseUtil {
    /** Use emulators only in debug builds  */ //    private static final boolean sUseEmulators = false;
    private val sUseEmulators = false
//    private val sUseEmulators = BuildConfig.DEBUG
    private var FIRESTORE: FirebaseFirestore? = null
    private var AUTH: FirebaseAuth? = null
    private var AUTH_UI: AuthUI? = null

    // Connect to the Cloud Firestore emulator when appropriate. The host '10.0.2.2' is a
    // special IP address to let the Android emulator connect to 'localhost'.
    val firestore: FirebaseFirestore?
        get() {
            if (FIRESTORE == null) {
                FIRESTORE = FirebaseFirestore.getInstance()

                // Connect to the Cloud Firestore emulator when appropriate. The host '10.0.2.2' is a
                // special IP address to let the Android emulator connect to 'localhost'.
                if (sUseEmulators) {
                    FIRESTORE!!.useEmulator("10.0.2.2", 8082)
                }
            }
            return FIRESTORE
        }

    // Connect to the Firebase Auth emulator when appropriate. The host '10.0.2.2' is a
    // special IP address to let the Android emulator connect to 'localhost'.
    val auth: FirebaseAuth?
        get() {
            if (AUTH == null) {
                AUTH = FirebaseAuth.getInstance()

                // Connect to the Firebase Auth emulator when appropriate. The host '10.0.2.2' is a
                // special IP address to let the Android emulator connect to 'localhost'.
                if (sUseEmulators) {
                    AUTH!!.useEmulator("10.0.2.2", 9099)
                }
            }
            return AUTH
        }

    // Connect to the Firebase Auth emulator when appropriate. The host '10.0.2.2' is a
    // special IP address to let the Android emulator connect to 'localhost'.
    val authUI: AuthUI?
        get() {
            if (AUTH_UI == null) {
                AUTH_UI = AuthUI.getInstance()

                // Connect to the Firebase Auth emulator when appropriate. The host '10.0.2.2' is a
                // special IP address to let the Android emulator connect to 'localhost'.
                if (sUseEmulators) {
                    AUTH_UI!!.useEmulator("10.0.2.2", 9099)
                }
            }
            return AUTH_UI
        }
}