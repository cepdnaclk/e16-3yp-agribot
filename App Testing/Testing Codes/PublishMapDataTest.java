package com.example.agribot;

import org.junit.Test;

import static org.junit.Assert.*;




public class PublishMapDataTest {

    ValidatePublishMapData val = new ValidatePublishMapData();

    @Test
    public void publish_isCorrect() throws Exception{ // Correct Values


        assertEquals(1, val.publishMapDataToBroker("1000","20","60","100"));
    }
    @Test
    public void publish_isNotCorrectNullValue() throws Exception{ // NULL values

        assertEquals(0, val.publishMapDataToBroker("1000","","60","100"));
    }
    @Test
    public void publish_isNotCorrectValues() throws Exception{ // Negative values

        assertEquals(-1 ,val.publishMapDataToBroker("1000","20","-1","100"));
    }
}
