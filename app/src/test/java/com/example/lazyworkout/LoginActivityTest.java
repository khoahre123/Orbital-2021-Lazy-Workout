package com.example.lazyworkout;

import com.example.lazyworkout.view.LoginActivity;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginActivityTest {

    @Test
    public void email() {
        LoginActivity login = new LoginActivity();
        boolean isValid = login.validateEmail(null);
        assertFalse(isValid);
    }
}
