package com.example.lazyworkout;

import android.os.Looper;

import androidx.test.core.app.ActivityScenario;

import com.example.lazyworkout.api.AuthenticationHelper;
import com.example.lazyworkout.view.LoginActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@PowerMockRunnerDelegate(JUnit4.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({FirebaseDatabase.class})
public class LoginActivityTest {

    private DatabaseReference mockedDatabaseReference;


    @Before
    public void before() {
        mockedDatabaseReference = Mockito.mock(DatabaseReference.class);

        FirebaseDatabase mockedFirebaseDatabase = Mockito.mock(FirebaseDatabase.class);
        when(mockedFirebaseDatabase.getReference()).thenReturn(mockedDatabaseReference);


    }

    @Test
    public void validateEmail() {
        Pattern pattern = Mockito.mock(Pattern.class);
        when(pattern.matcher("abbyy#@z~~").matches()).thenReturn(false);
        when(pattern.matcher("lmao@gmail.com").matches()).thenReturn(true);
        assertEquals("Email is empty!","Email is required", AuthenticationHelper.validateEmail(""));
        assertEquals("Email is invalid!", "The email is invalid", AuthenticationHelper.validateEmail("abbyy#@z~~"));
        assertEquals("Email is valid!", null, AuthenticationHelper.validateEmail("lmao@gmail.com"));
    }
}
