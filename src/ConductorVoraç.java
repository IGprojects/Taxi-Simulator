import java.util.List;

/**
 * @class ConductorVoraç
 * @brief Defineix un tipus de conductor (conductor voraç)
 *
 * @author Grup b9
 * @version 2025.03.04
 */
public class ConductorVoraç extends Conductor {
    @Override
    public void decidirMoviment(Mapa mapa, List<Peticio> peticions);
}
