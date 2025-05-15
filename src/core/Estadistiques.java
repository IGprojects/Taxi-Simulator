package core;

import java.util.ArrayList;
import java.util.List;

public class Estadistiques {

    // Estadísticas básicas
    private int peticionesServidas;
    private int peticionesNoServidas;
    private double tiempoTotalEspera;
    private double tiempoMaximoEspera;

    // Estadísticas de ocupación
    private double ocupacionTotalVehiculos;
    private int muestrasOcupacion;

    // Estadísticas de batería
    private double porcentajeBateriaPromedio;
    private int muestrasBateria;

    // Tiempos de viaje
    private double tiempoTotalViaje;
    private int muestrasViaje;

    // Lista para almacenar múltiples simulaciones (para estadísticas agregadas)
    private static List<Estadistiques> historialSimulaciones = new ArrayList<>();

    public Estadistiques() {
        resetear();
    }

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

    // Métodos para registrar datos
    public void registrarPeticionServida(double tiempoEspera) {
        peticionesServidas++;
        tiempoTotalEspera += tiempoEspera;
        if (tiempoEspera > tiempoMaximoEspera) {
            tiempoMaximoEspera = tiempoEspera;
        }
    }

    public void registrarPeticionNoServida(int numPeticions) {
        this.peticionesNoServidas = numPeticions;
    }

    public void registrarOcupacionVehiculo(double porcentajeOcupacion) {
        ocupacionTotalVehiculos += porcentajeOcupacion;
        muestrasOcupacion++;
    }

    public void registrarEstadoBateria(double porcentajeBateria) {
        porcentajeBateriaPromedio += porcentajeBateria;
        muestrasBateria++;
    }

    public void registrarTiempoViaje(double tiempoViaje) {
        tiempoTotalViaje += tiempoViaje;
        muestrasViaje++;
    }

    public void finalizarSimulacion() {
        historialSimulaciones.add(this);
    }

    // Métodos para calcular estadísticas
    public double getTiempoEsperaPromedio() {
        return peticionesServidas > 0 ? tiempoTotalEspera / peticionesServidas : 0;
    }

    public double getOcupacionPromedioVehiculos() {
        return muestrasOcupacion > 0 ? ocupacionTotalVehiculos / muestrasOcupacion : 0;
    }

    public double getPorcentajeBateriaPromedio() {
        return muestrasBateria > 0 ? porcentajeBateriaPromedio / muestrasBateria : 0;
    }

    public double getTiempoViajePromedio() {
        return muestrasViaje > 0 ? tiempoTotalViaje / muestrasViaje : 0;
    }

    public double getPorcentajeExito() {
        int total = peticionesServidas + peticionesNoServidas;
        return total > 0 ? (peticionesServidas * 100.0) / total : 0;
    }

    public int getMuestrasOcupacion() {
        return this.muestrasOcupacion;
    }

    public int getMuestrasBateria() {
        return this.muestrasBateria;
    }

    public int getMuestrasViaje() {
        return this.muestrasViaje;
    }

    // Métodos estáticos para estadísticas agregadas
    public static double getPorcentajeExitoAgregado() {
        if (historialSimulaciones.isEmpty()) {
            return 0;
        }

        int totalServidas = 0;
        int totalNoServidas = 0;

        for (Estadistiques stats : historialSimulaciones) {
            totalServidas += stats.peticionesServidas;
            totalNoServidas += stats.peticionesNoServidas;
        }

        int total = totalServidas + totalNoServidas;
        return total > 0 ? (totalServidas * 100.0) / total : 0;
    }

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

    // Getters
    public int getPeticionesServidas() {
        return peticionesServidas;
    }

    public int getPeticionesNoServidas() {
        return peticionesNoServidas;
    }

    public double getTiempoMaximoEspera() {
        return tiempoMaximoEspera;
    }

    @Override
    public String toString() {
        return String.format("""
                             Estad\u00edsticas de Simulaci\u00f3n:
                              - Peticiones servidas: %d
                              - Peticiones no servidas: %d
                              - Porcentaje de \u00e9xito: %.2f%%
                              - Tiempo espera promedio: %.2f min
                              - Tiempo espera m\u00e1ximo: %.2f min
                              - Ocupaci\u00f3n promedio veh\u00edculos: %.2f%%
                              - Bater\u00eda promedio: %.2f%%
                              - Tiempo viaje promedio: %.2f min""",
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

    public static String getEstadisticasAgregadas() {
        return String.format("""
                             Estad\u00edsticas Agregadas (%d simulaciones):
                              - Porcentaje de \u00e9xito: %.2f%%
                              - Tiempo espera promedio: %.2f min""",
                historialSimulaciones.size(),
                getPorcentajeExitoAgregado(),
                getTiempoEsperaPromedioAgregado()
        );
    }

    //SETTERS
    public void setPeticionesServidas(int peticionesServidas) {
        this.peticionesServidas = peticionesServidas;
    }

    public void setPeticionesNoServidas(int peticionesNoServidas) {
        this.peticionesNoServidas = peticionesNoServidas;
    }

    public void setTiempoTotalEspera(double tiempoTotalEspera) {
        this.tiempoTotalEspera = tiempoTotalEspera;
    }

    public void setTiempoMaximoEspera(double tiempoMaximoEspera) {
        this.tiempoMaximoEspera = tiempoMaximoEspera;
    }

    public void setOcupacionTotalVehiculos(double ocupacionTotalVehiculos) {
        this.ocupacionTotalVehiculos = ocupacionTotalVehiculos;
    }

    public void setMuestrasOcupacion(int muestrasOcupacion) {
        this.muestrasOcupacion = muestrasOcupacion;
    }

    public void setPorcentajeBateriaPromedio(double porcentajeBateriaPromedio) {
        this.porcentajeBateriaPromedio = porcentajeBateriaPromedio;
    }

    public void setMuestrasBateria(int muestrasBateria) {
        this.muestrasBateria = muestrasBateria;
    }

    public void setTiempoTotalViaje(double tiempoTotalViaje) {
        this.tiempoTotalViaje = tiempoTotalViaje;
    }

    public void setMuestrasViaje(int muestrasViaje) {
        this.muestrasViaje = muestrasViaje;
    }

}
