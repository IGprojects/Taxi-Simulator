import java.util.List;

/**
 * @class ConductorPlanificador
 * @brief Conductor que planifica rutes entre càrregues completes.
 */
public class ConductorPlanificador extends Conductor {

    @Override
    public void decidirMoviment(Mapa mapa, List<Peticio> peticions);

    /**
     * @pre peticions != null
     * @post Retorna la petició que millor s'ajusta al pla de càrrega.
     *
     * @param peticions Llista de peticions disponibles.
     * @param mapa El mapa de la simulació.
     * @return Petició òptima seleccionada.
     */
    private Peticio seleccionarMillorPeticio(List<Peticio> peticions, Mapa mapa);
}
