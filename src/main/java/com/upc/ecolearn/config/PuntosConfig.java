package com.upc.ecolearn.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PuntosConfig {

    @Value("${puntos.clasificacion.base}")
    private int clasificacionBase;

    @Value("${puntos.clasificacion.bonus-confianza}")
    private int bonusConfianza;

    @Value("${puntos.reto.completado}")
    private int retoCompletado;

    @Value("${puntos.logro.bonus}")
    private int logroBonus;

    @Value("${puntos.nivel.umbral}")
    private int nivelUmbral;

    @Value("${puntos.confianza.umbral}")
    private double confianzaUmbral;

    public int calcularPuntos(double confianza) {
        return confianza >= confianzaUmbral
                ? clasificacionBase + bonusConfianza
                : clasificacionBase;
    }

    public boolean esClasificacionCorrecta(double confianza) {
        return confianza >= confianzaUmbral;
    }

    public int calcularNivel(int puntos) {
        return (puntos / nivelUmbral) + 1;
    }

    public int getPuntosRetoCompletado() { return retoCompletado; }
    public int getPuntosLogroBonus() { return logroBonus; }
    public int getNivelUmbral() { return nivelUmbral; }
}