package core;
import java.util.List;

/**
 * @class CalculEstadistics
 * @brief Classe que calcula estadístiques sobre la simulació.
 * @details Processa informació sobre peticions i vehicles per extreure
 *          mètriques clau.
 * 
 * @author Grup b9
 * @version 2025.03.04
 */
public class CalculEstadistic {

    /**
     * @pre s != null
     * @post Actualitza les estadístiques basant-se en les dades de la simulació.
     *
     * @param s Simulador amb les dades de la simulació.
     */
    public void calcularEstadistiques(Simulador s);

    /**
     * @brief Retorna el nombre de peticions no servides.
     * 
     * @pre Cert.
     * @post Retorna el nombre de peticions que no han pogut ser servides.
     * 
     * @return Nombre de peticions no servides.
     */
    public int obtenirPeticionsNoServides();

    /**
     * @pre Cert.
     * @post Retorna el temps mig d'espera en minuts.
     * 
     * @return Temps mitjà d'espera en minuts.
     */
    public double obtenirTempsMigEspera();

    /**
     * @pre Cert.
     * @post Retorna el temps d'espera més llarg registrat.
     * 
     * @return Temps màxim d'espera en minuts.
     */
    public double obtenirTempsMaximEspera();

    /**
     * @pre p != null
     * @post Calcula el temps d'espera d'una petició.
     * 
     * @param p Petició per calcular l'espera.
     * @return Temps d'espera en minuts.
     */
    private int calcularTempsEspera(Peticio p);
}
