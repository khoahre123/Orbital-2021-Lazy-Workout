package com.example.lazyworkout.api;

import android.util.Patterns;

public class AuthenticationHelper {

    public AuthenticationHelper() {}

    public static String validateEmail(String email) {
        if (email.isEmpty()) {
            return "Email is required";
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "The email is invalid";
        } else {
            return null;
        }
    }

    public static String validatePassword(String password) {
        if (password.isEmpty()) {
            return "Password is required";
        } else {
            return null;
        }
    }
}
