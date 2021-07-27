package com.example.lazyworkout;

import com.example.lazyworkout.api.ChatHelper;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

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
public class ChatListTest {
    @Test
    public void checkString() {
        String[] firstTest = new String[] {null, "a", "b"};
        String[] secondTest = new String[] {"a", "b", "c", "d"};
        String[] thirdTest = new String[] {"1"};
        assertEquals(true, ChatHelper.checkString(firstTest));
        assertEquals(false, ChatHelper.checkString(secondTest));
        assertEquals(false, ChatHelper.checkString(thirdTest));
    }
}
