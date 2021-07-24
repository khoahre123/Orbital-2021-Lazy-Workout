package com.example.lazyworkout;

import com.example.lazyworkout.util.Util;

import org.junit.Test;
import static org.junit.Assert.*;

public class UtilTest {
    @Test
    public void getGoal() {
        assertEquals("Util get goal: 4km",4, Util.getGoal("4.0 km"),0);
        assertEquals("Util get goal: 0km", 0, Util.getGoal("0.0 km"),0);
        assertEquals("Util get goal: 100km", 100, Util.getGoal("100.0 km"), 0);
    }

    @Test
    public void getStepSize() {
        assertEquals("Util get step size: 50cm", 0.0005, Util.getStepsize("50 cm"), 0.000001);
        assertEquals("Util get step size: 0cm", 0, Util.getStepsize("0 cm"), 0.000001);
        assertEquals("Util get step size: 120cm", 0.0012, Util.getStepsize("120 cm"), 0.000001);
    }

    @Test
    public void computeStepcount() {
        assertEquals(1, Util.computeStepcount(50,40), 0);
        assertEquals(200, Util.computeStepcount(2000, 10), 0);
        assertEquals(3, Util.computeStepcount(123,31), 0);
    }
}
