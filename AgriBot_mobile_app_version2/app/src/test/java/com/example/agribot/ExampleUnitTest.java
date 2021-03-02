package com.example.agribot;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        boolean ans = ValidateMapData.validate("1500", "50", "46", "100");
        System.out.println(ans);
    }
}

