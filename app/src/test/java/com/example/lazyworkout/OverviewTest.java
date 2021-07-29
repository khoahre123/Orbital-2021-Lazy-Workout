package com.example.lazyworkout;

import com.example.lazyworkout.view.OverviewActivity;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
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
public class OverviewTest {
    @Test
    public void isAllPermissionGranted() {
        OverviewActivity overviewActivity1 = Mockito.mock(OverviewActivity.class);
        when(overviewActivity1.permissionOverlayWindowGranted()).thenReturn(false);
        when(overviewActivity1.permissionUsageStatsGranted()).thenReturn(true);
        assertEquals(false, overviewActivity1.isAllPermissionGranted());

        OverviewActivity overviewActivity2 = Mockito.mock(OverviewActivity.class);
        when(overviewActivity2.permissionOverlayWindowGranted()).thenReturn(true);
        when(overviewActivity2.permissionUsageStatsGranted()).thenReturn(false);
        assertEquals(false, overviewActivity2.isAllPermissionGranted());
    }
}
