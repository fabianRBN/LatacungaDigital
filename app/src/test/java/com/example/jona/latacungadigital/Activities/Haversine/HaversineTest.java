package com.example.jona.latacungadigital.Activities.Haversine;

import com.example.jona.latacungadigital.Activities.modelos.Coordenada;

import org.junit.Test;

import java.text.DecimalFormat;

import static org.junit.Assert.*;

/**
 * Created by fabian on 29/07/2018.
 */
public class HaversineTest {
    @Test
    public void distance() throws Exception {
        Haversine haversine = new Haversine();
        double distanciGeografica = haversine.distance(
                new Coordenada(-0.737376,-78.669212),
                new Coordenada(-0.836831,-78.669183));
        assertEquals(11 , (int) distanciGeografica);
    }

}