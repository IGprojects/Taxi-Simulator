package core;

/**
 * @class ConductorVoraç
 * @brief Defineix un tipus de conductor (conductor voraç)
 *
 * @author Grup b9
 * @version 2025.03.04
 */
public class ConductorVoraç extends Conductor {

    public ConductorVoraç(int id, String nom, Vehicle vehicle) {
        super(id, nom, vehicle);
    }

    @Override
    public void executarRuta(Ruta r, Vehicle v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /*public void executarRuta(Mapa mapa, Ruta r, List<Peticio> peticions, Vehicle v) {
        for (Lloc lloc : r.getLlocs()) {
            Lloc desti = r.getLlocDesti();
            if (v.consumirBateria(mapa.calcularDistancia(v.getUbicacioActual(), desti))) {
                v.moure(desti, mapa.calcularDistancia(v.getUbicacioActual(), lloc));// falta passar per
            } else {
                v.carregarBateria(100);
            }

        }
    }
*/
    /**
     * @pre Cert.
     * @post Decideix el moviment del conductor basant-se en el mapa i les
     *       peticions.
     */
    // s hauria de fer aquest metodes per cada fill
    /*
     * public abstract void decidirMoviment(Mapa mapa, List<Peticio> peticions) {
     * // 1. Identificar el destino final (primera petición)
     * //tambe carregar si no pot arribar al desti
     * if (peticions.isEmpty()) {
     * // Si no hay peticiones, buscar la estación de carga más cercana si la
     * batería es baja
     * if (this.bateria < 30 && !this.carregant) {
     * buscarEstacionCarga(mapa);
     * }
     * return;
     * }
     * 
     * // Ordenar peticiones por prioridad (las que llevan más tiempo esperando
     * primero)
     * //s ordenaran de diferent manera segons el tipus de conductor
     * peticions.sort((p1, p2) -> calcularTempsEspera(p2) -
     * calcularTempsEspera(p1));
     * 
     * 
     * // Obtener el destino más prioritario
     * Lloc destinoPrincipal = peticions.get(0).getDesti();
     * 
     * // Si ya estamos en el destino, verificar si tenemos más peticiones
     * if (this.ubicacio.equals(destinoPrincipal)) {
     * manejarLlegadaDestino(peticions);
     * return;
     * }
     * 
     * // Calcular ruta óptima considerando batería
     * List<Lloc> rutaOptima = calcularRutaOptima(mapa, destinoPrincipal);
     * 
     * if (rutaOptima.isEmpty()) {
     * // No se encontró ruta viable, intentar cargar batería
     * //50 es pot cambiar numero orientatiu pero es caregggara al 100 aixins que no
     * tenir enc ompte
     * if (!this.carregant && this.bateria < 50) {
     * buscarEstacionCarga(mapa);
     * }
     * return;
     * }
     * 
     * // Mover al siguiente punto en la ruta
     * Lloc siguienteDestino = rutaOptima.get(0);
     * Cami camino = obtenerCamino(this.ubicacio, siguienteDestino, mapa);
     * 
     * if (camino != null) {
     * // Actualizar estado del vehículo
     * double consumo = (camino.getDistancia() / this.AUTONOMIA) * 100;
     * this.bateria -= consumo;
     * this.ubicacio = siguienteDestino;
     * 
     * // Verificar si necesitamos cargar en el nuevo lugar
     * if (this.bateria < 20 && siguienteDestino.tieneEstacionCarga()) {
     * this.carregant = true;
     * }
     * }
     * 
     * }
     */
}
