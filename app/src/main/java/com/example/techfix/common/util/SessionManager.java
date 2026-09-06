package com.example.techfix.common.util;

import android.content.Context;
import android.content.SharedPreferences;

// Manages user login sessions using SharedPreferences
public class SessionManager {
    private static final String PREF_NAME = "TechFixSession";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    public static final String ROLE_CUSTOMER = "customer";
    public static final String ROLE_ADMIN = "admin";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Starts a new session for a user
    public void createSession(String email, String role) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_ROLE, role);
        editor.apply();
    }

    // Checks if a user is currently logged in
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Retrieves the logged-in user's email
    public String getEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    // Retrieves the logged-in user's role
    public String getRole() {
        return pref.getString(KEY_USER_ROLE, "");
    }

    // Clears the session data (Logout)
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
