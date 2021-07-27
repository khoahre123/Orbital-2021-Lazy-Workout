package com.example.lazyworkout;


import com.example.lazyworkout.api.AuthenticationHelper;
import com.google.firebase.database.FirebaseDatabase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

@PowerMockRunnerDelegate(JUnit4.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({FirebaseDatabase.class})
public class RegisterActivityTest {
    @Test
    public void validateUsername() {
        assertEquals(true, AuthenticationHelper.validateUsername(""));
        assertEquals(false, AuthenticationHelper.validateUsername("lmao123"));
    }

    @Test
    public void validateEmail() {
        assertEquals("Email is empty!","Email is required", AuthenticationHelper.validateEmail(""));
    }

    @Test
    public void validatePassword() {
        assertEquals("Password is required", AuthenticationHelper.validatePassword(""));
        assertEquals(null, AuthenticationHelper.validatePassword("abcxyz"));
    }
}
