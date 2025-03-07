import java.util.ArrayList;
import java.util.List;

/**
 * @class Lloc
 * @brief Característiques de cada lloc
 * @details Definirà els atributs de cada lloc
 *
 * @author Grup b9
 * @version 2025.03.04
 */
public class Lloc implements ILloc {
    @Override
    public int obtenirCapacitatMaxima();

    @Override
    public boolean entrarVehicle();

    @Override
    public void sortirVehicle();

    @Override
    public int obtenirVehiclesActuals();

    @Override
    public boolean estaPle();

}
