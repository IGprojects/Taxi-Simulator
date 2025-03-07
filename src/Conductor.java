import java.util.List;

/**
 * @class Conductor
 * @brief Defineix la classe pare dels tipus de conductors
 *
 * @author Grup b9
 * @version 2025.03.04
 */
public abstract class Conductor implements IConductor {

    @Override
    public Vehicle obtenirVehicle();

    @Override
    public void assignarVehicle(Vehicle v);

    @Override
    public boolean estaDisponible();

    @Override
    public void assignarPeticio(Peticio p);

    @Override
    public void executarRuta(Ruta r);

    @Override
    public abstract void decidirMoviment(Mapa mapa, List<Peticio> peticions);

    @Override
    public void recollirPassatgers(int numPass);
}
