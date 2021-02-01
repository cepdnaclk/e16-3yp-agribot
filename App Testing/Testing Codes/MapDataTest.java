package com.example.agribot;

import org.junit.Test;

import static org.junit.Assert.*;


public class MapDataTest {
    @Test
    public void inputData_isCorrect() throws Exception { // Correct Values

        assertTrue(ValidateMapData.validate("1500", "50", "46", "100"));
    }

    @Test
    public void inputData_isNotCorrect() throws Exception { // Incorrect Values

        assertFalse(ValidateMapData.validate("1500", "50", "-1", "100"));
    }
    @Test
    public void inputData_isNotCorrect2() throws Exception { // Incorrect Values

        assertFalse(ValidateMapData.validate("1500000", "50", "45", "100"));
    }

}
