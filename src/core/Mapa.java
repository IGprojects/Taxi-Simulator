package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @class Mapa
 * @brief Mapa dels diferents llocs i connexions
 * @details Definirà el mapa i les connexions entre llocs
 *
 * @author Grup b9
 * @version 2025.03.04
 */
public class Mapa {

    private Map<Lloc, List<Cami>> llocs; /// < Llista de llocs i les seves connexions
    private int nLlocs; /// < Nombre de llocs del mapa
    private int nConnexions; /// < Nombre de connexions del mapa

    /**
     * Constructor de la classe Mapa
     */
    public Mapa(int nLlocs, int nConnexions) {
        llocs = new HashMap<>();
        this.nLlocs = nLlocs;
        this.nConnexions = nConnexions;
    }

    /**
     * @pre id>0
     *
     * @post Afegeix un nou lloc a la llista de llocs
     */
    public void afegirLloc(Lloc lloc) {
        llocs.put(lloc, new ArrayList<>());
    }

    /**
     * @pre Origen i desti existents
     *
     * @post Afegeix una nova connexio a la llista de connexions
     */
    public void afegirCami(Cami cami) {
        llocs.get(cami.obtenirOrigen()).add(cami);
    }

    /**
     * @pre Origen i desti existents
     *
     * @post Retorna true si hi ha camí entre origen i desti, altrament false
     */
    public boolean hihaCami(Lloc origen, Lloc desti) {
        List<Cami> camins = llocs.get(origen);
        if (camins == null)
            throw new IllegalArgumentException("Lloc origen no existeix");

        for (Cami cami : camins)
            if (cami.obtenirDesti().equals(desti))
                return true;

        return false;
    }

    /**
     * @pre Origen i desti existents
     *
     * @post Retorna la distància en km que hi ha entre dos llocs
     */
    public double calcularDistancia(Lloc origen, Lloc desti) {
        if (!hihaCami(origen, desti))
            throw new IllegalArgumentException("No hi ha camí entre origen i destí");

        List<Cami> camins = llocs.get(origen);
        for (Cami cami : camins)
            if (cami.obtenirDesti().equals(desti))
                return cami.obtenirDistancia();

        return -1;
    }

    /**
     * @pre Origen i desti existents
     *
     * @post Retorna el temps en min que hi ha entre dos llocs
     */
    public double calcularTemps(Lloc origen, Lloc desti) {
        if (!hihaCami(origen, desti))
            throw new IllegalArgumentException("No hi ha camí entre origen i destí");

        List<Cami> camins = llocs.get(origen);
        for (Cami cami : camins)
            if (cami.obtenirDesti().equals(desti))
                return cami.obtenirTemps();

        return -1;
    }

    /**
     * @pre Origen i desti existents
     *
     * @post Retorna el camí més ràpid entre dos llocs
     */

    public List<Lloc> camiVoraç(Lloc origen, Lloc desti) {
        List<Lloc> cami = new ArrayList<>();
        Set<Lloc> visitats = new HashSet<>();
        Lloc actual = origen;

        cami.add(actual);

        while (!actual.equals(desti)) {
            visitats.add(actual);
            List<Cami> camins = llocs.get(actual);

            if (camins == null || camins.isEmpty())
                break;

            Cami millor = null;
            for (Cami c : camins) {
                if (!visitats.contains(c.obtenirDesti())) {
                    if (millor == null || c.obtenirTemps() < millor.obtenirTemps()) {
                        millor = c;
                    }
                }
            }

            if (millor == null)
                break;

            actual = millor.obtenirDesti();
            cami.add(actual);
        }

        if (!actual.equals(desti))
            return Collections.emptyList();
        return cami;
    }

    /**
     * 
     * 
     * 
     * @param origen
     * @param desti
     * @return
     */
    public Map<Lloc, List<Cami>> getLlocs() {
        return llocs;
    }

    /**
     * 
     * 
     * 
     * @param id
     * @return
     */
    public Lloc getLlocPerId(int id) {
        for (Lloc lloc : llocs.keySet()) {
            if (lloc.obtenirId() == id)
                return lloc;
        }
        return null;
    }


    public getCarregadorPrivatPredeterminat() {
        for (Lloc lloc : llocs.keySet()) {
            if (lloc.esCarregadorPrivat())
                return lloc;
        }
        return null;
    }

}
