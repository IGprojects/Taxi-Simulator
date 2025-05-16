package core;

import java.util.ArrayList;
import java.util.List;

/**
 * @class Estadistiques
 * @brief Classe per gestionar i calcular estadístiques de simulacions.
 * @author Ignasi Ferrés Iglesias
 * @version 2025.05.13
 */
public class Estadistiques {

    /**
     * @brief Nombre total de peticions servides.
     */
    private int peticionesServidas;

    /**
     * @brief Nombre total de peticions no servides.
     */
    private int peticionesNoServidas;

    /**
     * @brief Temps total d'espera acumulat de totes les peticions servides (en
     * minuts).
     */
    private double tiempoTotalEspera;

    /**
     * @brief Temps màxim d'espera registrat (en minuts).
     */
    private double tiempoMaximoEspera;

    /**
     * @brief Ocupació total acumulada dels vehicles (en percentatge).
     */
    private double ocupacionTotalVehiculos;

    /**
     * @brief Nombre de mostres registrades d'ocupació.
     */
    private int muestrasOcupacion;

    /**
     * @brief Suma dels percentatges de bateria observats.
     */
    private double porcentajeBateriaPromedio;

    /**
     * @brief Nombre de mostres de percentatge de bateria.
     */
    private int muestrasBateria;

    /**
     * @brief Temps total de viatge acumulat (en minuts).
     */
    private double tiempoTotalViaje;

    /**
     * @brief Nombre de mostres de temps de viatge.
     */
    private int muestrasViaje;

    /**
     * @brief Llista de totes les simulacions finalitzades per a càlculs
     * agregats.
     */
    private static List<Estadistiques> historialSimulaciones = new ArrayList<>();

    /**
     * @brief Constructor per defecte. Inicialitza totes les estadístiques a
     * zero.
     */
    public Estadistiques() {
        resetear();
    }

    /**
     * @brief Constructor amb paràmetres.
     * @param peticionesServidas Nombre de peticions servides
     * @param peticionesNoServidas Nombre de peticions no servides
     * @param tiempoTotalEspera Temps total d'espera
     * @param tiempoMaximoEspera Temps màxim d'espera
     * @param ocupacionTotalVehiculos Ocupació total dels vehicles
     * @param muestrasOcupacion Nombre de mostres d'ocupació
     * @param porcentajeBateriaPromedio Percentatge total de bateria
     * @param muestrasBateria Nombre de mostres de bateria
     * @param tiempoTotalViaje Temps total de viatge
     * @param muestrasViaje Nombre de mostres de viatge
     */
    public Estadistiques(int peticionesServidas, int peticionesNoServidas,
            double tiempoTotalEspera, double tiempoMaximoEspera,
            double ocupacionTotalVehiculos, int muestrasOcupacion,
            double porcentajeBateriaPromedio, int muestrasBateria,
            double tiempoTotalViaje, int muestrasViaje) {
        this.peticionesServidas = peticionesServidas;
        this.peticionesNoServidas = peticionesNoServidas;
        this.tiempoTotalEspera = tiempoTotalEspera;
        this.tiempoMaximoEspera = tiempoMaximoEspera;
        this.ocupacionTotalVehiculos = ocupacionTotalVehiculos;
        this.muestrasOcupacion = muestrasOcupacion;
        this.porcentajeBateriaPromedio = porcentajeBateriaPromedio;
        this.muestrasBateria = muestrasBateria;
        this.tiempoTotalViaje = tiempoTotalViaje;
        this.muestrasViaje = muestrasViaje;
    }

    /**
     * @brief Reinicia totes les estadístiques a zero.
     */
    public void resetear() {
        peticionesServidas = 0;
        peticionesNoServidas = 0;
        tiempoTotalEspera = 0;
        tiempoMaximoEspera = 0;
        ocupacionTotalVehiculos = 0;
        muestrasOcupacion = 0;
        porcentajeBateriaPromedio = 0;
        muestrasBateria = 0;
        tiempoTotalViaje = 0;
        muestrasViaje = 0;
    }

    /**
     * @brief Registra una petició servida i el seu temps d'espera.
     * @param tiempoEspera Temps d'espera de la petició
     */
    public void registrarPeticionServida(double tiempoEspera) {
        peticionesServidas++;
        tiempoTotalEspera += tiempoEspera;
        if (tiempoEspera > tiempoMaximoEspera) {
            tiempoMaximoEspera = tiempoEspera;
        }
    }

    /**
     * @brief Registra el nombre total de peticions no servides.
     * @param numPeticions Nombre de peticions no servides
     */
    public void registrarPeticionNoServida(int numPeticions) {
        this.peticionesNoServidas = numPeticions;
    }

    /**
     * @brief Registra una nova mostra d'ocupació de vehicle.
     * @param porcentajeOcupacion Percentatge d'ocupació
     */
    public void registrarOcupacionVehiculo(double porcentajeOcupacion) {
        ocupacionTotalVehiculos += porcentajeOcupacion;
        muestrasOcupacion++;
    }

    /**
     * @brief Registra una nova mostra d'estat de bateria.
     * @param porcentajeBateria Percentatge de bateria
     */
    public void registrarEstadoBateria(double porcentajeBateria) {
        porcentajeBateriaPromedio += porcentajeBateria;
        muestrasBateria++;
    }

    /**
     * @brief Registra una nova mostra de temps de viatge.
     * @param tiempoViaje Temps de viatge
     */
    public void registrarTiempoViaje(double tiempoViaje) {
        tiempoTotalViaje += tiempoViaje;
        muestrasViaje++;
    }

    /**
     * @brief Finalitza la simulació i desa les estadístiques a l'historial.
     */
    public void finalizarSimulacion() {
        historialSimulaciones.add(this);
    }

    /**
     * @brief Calcula el temps d'espera mitjà per petició.
     * @return Temps mitjà d'espera
     */
    public double getTiempoEsperaPromedio() {
        return peticionesServidas > 0 ? tiempoTotalEspera / peticionesServidas : 0;
    }

    /**
     * @brief Calcula l'ocupació mitjana dels vehicles.
     * @return Ocupació mitjana
     */
    public double getOcupacionPromedioVehiculos() {
        return muestrasOcupacion > 0 ? ocupacionTotalVehiculos / muestrasOcupacion : 0;
    }

    /**
     * @brief Calcula el percentatge mitjà de bateria.
     * @return Percentatge mitjà de bateria
     */
    public double getPorcentajeBateriaPromedio() {
        return muestrasBateria > 0 ? porcentajeBateriaPromedio / muestrasBateria : 0;
    }

    /**
     * @brief Calcula el temps mitjà de viatge.
     * @return Temps mitjà de viatge
     */
    public double getTiempoViajePromedio() {
        return muestrasViaje > 0 ? tiempoTotalViaje / muestrasViaje : 0;
    }

    /**
     * @brief Calcula el percentatge d'èxit de les peticions.
     * @return Percentatge d'èxit
     */
    public double getPorcentajeExito() {
        int total = peticionesServidas + peticionesNoServidas;
        return total > 0 ? (peticionesServidas * 100.0) / total : 0;
    }

    /// @return Nombre de mostres d'ocupació
    public int getMuestrasOcupacion() {
        return this.muestrasOcupacion;
    }

    /// @return Nombre de mostres de bateria
    public int getMuestrasBateria() {
        return this.muestrasBateria;
    }

    /// @return Nombre de mostres de viatge
    public int getMuestrasViaje() {
        return this.muestrasViaje;
    }

    /**
     * @brief Calcula el percentatge d'èxit global de totes les simulacions.
     * @return Percentatge d'èxit agregat
     */
    public static double getPorcentajeExitoAgregado() {
        if (historialSimulaciones.isEmpty()) {
            return 0;
        }
        int totalServidas = 0, totalNoServidas = 0;
        for (Estadistiques stats : historialSimulaciones) {
            totalServidas += stats.peticionesServidas;
            totalNoServidas += stats.peticionesNoServidas;
        }
        int total = totalServidas + totalNoServidas;
        return total > 0 ? (totalServidas * 100.0) / total : 0;
    }

    /**
     * @brief Calcula el temps d'espera mitjà agregat de totes les simulacions.
     * @return Temps d'espera mitjà global
     */
    public static double getTiempoEsperaPromedioAgregado() {
        if (historialSimulaciones.isEmpty()) {
            return 0;
        }
        double totalTiempo = 0;
        int totalPeticiones = 0;
        for (Estadistiques stats : historialSimulaciones) {
            totalTiempo += stats.tiempoTotalEspera;
            totalPeticiones += stats.peticionesServidas;
        }
        return totalPeticiones > 0 ? totalTiempo / totalPeticiones : 0;
    }

    /// @return Nombre de peticions servides
    public int getPeticionesServidas() {
        return peticionesServidas;
    }

    /// @return Nombre de peticions no servides
    public int getPeticionesNoServidas() {
        return peticionesNoServidas;
    }

    /// @return Temps màxim d'espera registrat
    public double getTiempoMaximoEspera() {
        return tiempoMaximoEspera;
    }

    /**
     * @brief Retorna les estadístiques de la simulació en format llegible.
     * @return String amb estadístiques formatades
     */
    @Override
    public String toString() {
        return String.format("""
                             Estad\u00edstiques de Simulaci\u00f3:
                              - Peticions servides: %d
                              - Peticions no servides: %d
                              - Percentatge d'\u00e8xit: %.2f%%
                              - Temps espera promig: %.2f min
                              - Temps espera m\u00e0xim: %.2f min
                              - Ocupaci\u00f3 promig vehicles: %.2f%%
                              - Bateria promig: %.2f%%
                              - Temps viatge promig: %.2f min""",
                peticionesServidas,
                peticionesNoServidas,
                getPorcentajeExito(),
                getTiempoEsperaPromedio(),
                tiempoMaximoEspera,
                getOcupacionPromedioVehiculos(),
                getPorcentajeBateriaPromedio(),
                getTiempoViajePromedio()
        );
    }

    /**
     * @brief Retorna les estadístiques agregades de totes les simulacions.
     * @return String amb estadístiques agregades formatades
     */
    public static String getEstadisticasAgregadas() {
        return String.format("""
                             Estad\u00edstiques Agregades (%d simulacions):
                              - Percentatge d'\u00e8xit: %.2f%%
                              - Temps espera promig: %.2f min""",
                historialSimulaciones.size(),
                getPorcentajeExitoAgregado(),
                getTiempoEsperaPromedioAgregado()
        );
    }

    // --- SETTERS ---
    /**
     * @brief Estableix el nombre de peticions servides.
     */
    public void setPeticionesServidas(int peticionesServidas) {
        this.peticionesServidas = peticionesServidas;
    }

    /**
     * @brief Estableix el nombre de peticions no servides.
     */
    public void setPeticionesNoServidas(int peticionesNoServidas) {
        this.peticionesNoServidas = peticionesNoServidas;
    }

    /**
     * @brief Estableix el temps total d'espera.
     */
    public void setTiempoTotalEspera(double tiempoTotalEspera) {
        this.tiempoTotalEspera = tiempoTotalEspera;
    }

    /**
     * @brief Estableix el temps màxim d'espera.
     */
    public void setTiempoMaximoEspera(double tiempoMaximoEspera) {
        this.tiempoMaximoEspera = tiempoMaximoEspera;
    }

    /**
     * @brief Estableix l'ocupació total dels vehicles.
     */
    public void setOcupacionTotalVehiculos(double ocupacionTotalVehiculos) {
        this.ocupacionTotalVehiculos = ocupacionTotalVehiculos;
    }

    /**
     * @brief Estableix el nombre de mostres d'ocupació.
     */
    public void setMuestrasOcupacion(int muestrasOcupacion) {
        this.muestrasOcupacion = muestrasOcupacion;
    }

    /**
     * @brief Estableix la suma dels percentatges de bateria.
     */
    public void setPorcentajeBateriaPromedio(double porcentajeBateriaPromedio) {
        this.porcentajeBateriaPromedio = porcentajeBateriaPromedio;
    }

    /**
     * @brief Estableix el nombre de mostres de bateria.
     */
    public void setMuestrasBateria(int muestrasBateria) {
        this.muestrasBateria = muestrasBateria;
    }

    /**
     * @brief Estableix el temps total de viatge.
     */
    public void setTiempoTotalViaje(double tiempoTotalViaje) {
        this.tiempoTotalViaje = tiempoTotalViaje;
    }

    /**
     * @brief Estableix el nombre de mostres de viatge.
     */
    public void setMuestrasViaje(int muestrasViaje) {
        this.muestrasViaje = muestrasViaje;
    }
}
