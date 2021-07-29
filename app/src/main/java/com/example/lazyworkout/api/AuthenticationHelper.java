package com.example.lazyworkout.api;

import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthenticationHelper {

    public AuthenticationHelper() {}

    public static String validateEmail(String email) {
        if (email.isEmpty()) {
            return "Email is required";
        } else if (!checkValidEmail(email)) {
            return "The email is invalid";
        } else {
            return null;
        }
    }

    public static String validatePassword(String password) {
        if (password.isEmpty()) {
            return "Password is required";
        } else if (password.length() < 6) {
            return "Password cannot shorter than six characters";
        } else {
            return null;
        }
    }

    public static boolean validateUsername(String username) {
        if (username.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
