package com.example.agribot;

import org.junit.Test;

import static org.junit.Assert.*;

public class SubBrokerTest {
    ValidateSubBroker val = new ValidateSubBroker();

    @Test
    public void subscription_isCorrect() throws Exception{ // Correct IP and the Correct Topic

        assertTrue(val.subscribeToBroker("52.201.221.111", "23xyz53j8/Data"));
    }

    @Test
    public void subscription_isNotCorrect() throws Exception{ //Incorrect IP and the InCorrect Topic

        assertFalse(val.subscribeToBroker("192.168.43.228", "InvalidTopic/Data"));
    }

    @Test
    public void subscription_isNotCorrectInvalidIP() throws Exception{ //Incorrect IP and the Correct Topic

        assertFalse(val.subscribeToBroker("192.168.43.228", "23xyz53j8/Data"));
    }

    @Test
    public void subscription_isNotCorrectInvalidTopic() throws Exception{ //Correct IP and the Incorrect Topic

        assertTrue(val.subscribeToBroker("52.201.221.111", "InvalidTopic/Data"));
    }

}

