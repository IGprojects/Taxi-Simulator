/**
 * @class PuntCarga
 * @brief Defineix la localitzacio amb punt de carfa
 *
 * @author Grup b9
 * @version 2025.03.04
 */
public class PuntCarga extends Lloc {
    /**
     * @pre Cert.
     *
     * @post Retorna cert si el punt és de càrrega rapida, altrament fals.
     */
    public boolean esCarregaRapida();

    /**
     * @pre v != null
     * @post La bateria del vehicle s'ha incrementat fins al màxim permès.
     * 
     * @param v Vehicle a carregar.
     */
    public void carregarVehicle(Vehicle v);
}
