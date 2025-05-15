package core;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
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
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class Mapa {

    private Map<Lloc, List<Cami>> llocs;

    /// < Llista de llocs i les seves connexions

    /**
     * Constructor de la classe Mapa
     */
    public Mapa() {
        llocs = new HashMap<>();
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
     * @post Retorna el Cami si hi ha camí entre origen i desti, altrament null
     */
    public Cami hihaCami(Lloc origen, Lloc desti) {
        List<Cami> camins = llocs.get(origen);
        if (camins == null) {
            throw new IllegalArgumentException("Lloc origen no existeix");
        }

        for (Cami cami : camins) {
            if (cami.obtenirDesti().equals(desti)) {
                return cami;
            }
        }

        return null; // No s'ha trobat cap camí
    }

    /**
     * @pre Origen i desti existents
     *
     * @post Retorna la distància en km que hi ha entre dos llocs
     */
    public double calcularDistancia(Lloc origen, Lloc desti) {
        if (hihaCami(origen, desti) == null) {
            throw new IllegalArgumentException(
                    "No hi ha camí entre origen " + origen.obtenirId() + "i destí " + desti.obtenirId());
        }
        List<Cami> camins = llocs.get(origen);
        for (Cami cami : camins) {
            if (cami.obtenirDesti().obtenirId() == desti.obtenirId()) {
                return cami.obtenirDistancia();
            }
        }
        return -1;
    }

    public double calcularTempsRuta(List<Lloc> ruta) {
        double tempsTotal = 0.0;

        for (int i = 0; i < ruta.size() - 1; i++) {
            Lloc origen = ruta.get(i);
            Lloc desti = ruta.get(i + 1);

            Cami cami = hihaCami(origen, desti);
            if (cami != null) {
                tempsTotal += cami.obtenirTemps();
            } else {
                System.err.println("No s'ha trobat camí entre " + origen.obtenirId() + " i " + desti.obtenirId());
                return Double.MAX_VALUE; // considerem que la ruta no és vàlida
            }
        }

        return tempsTotal;
    }

    public double calcularDistanciaRuta(List<Lloc> ruta) {
        double distanciaTotal = 0.0;

        for (int i = 0; i < ruta.size() - 1; i++) {
            Lloc origen = ruta.get(i);
            Lloc desti = ruta.get(i + 1);

            Cami cami = hihaCami(origen, desti);
            if (cami != null) {
                distanciaTotal += cami.obtenirDistancia();
            } else {
                System.err.println("No s'ha trobat camí entre " + origen.obtenirId() + " i " + desti.obtenirId());
                return Double.MAX_VALUE; // considerem que la ruta no és vàlida
            }
        }

        return distanciaTotal;
    }

    /**
     * @pre Origen i desti existents
     *
     * @post Retorna el temps en min que hi ha entre dos llocs
     */
    public double calcularTemps(Lloc origen, Lloc desti) {
        if (hihaCami(origen, desti) == null) {
            throw new IllegalArgumentException("No hi ha camí entre origen i destí");
        }

        List<Cami> camins = llocs.get(origen);
        for (Cami cami : camins) {
            if (cami.obtenirDesti().equals(desti)) {
                return cami.obtenirTemps();
            }
        }

        return -1;
    }

    /**
     * @pre Origen i desti existents
     *
     * @post Retorna el camí més ràpid entre dos llocs
     */
    public List<Lloc> camiVoraç(Lloc origen, Lloc desti) {
        Set<Lloc> visitats = new HashSet<>();
        List<Lloc> millorCami = new ArrayList<>();
        if (trobarCamiVoraç(origen, desti, visitats, new ArrayList<>(), millorCami)) {
            return millorCami;
        } else {
            return null;
        }

    }

    private boolean trobarCamiVoraç(Lloc actual, Lloc desti, Set<Lloc> visitats,
            List<Lloc> camiActual, List<Lloc> millorCami) {
        visitats.add(actual);
        camiActual.add(actual);

        if (actual.equals(desti)) {
            millorCami.clear();
            millorCami.addAll(camiActual);
            return true;
        }

        List<Cami> camins = new ArrayList<>(llocs.getOrDefault(actual, List.of()));
        camins.sort(Comparator.comparingDouble(Cami::obtenirTemps)); // ordena per temps més curt

        for (Cami cami : camins) {
            Lloc seguent = cami.obtenirDesti();
            if (!visitats.contains(seguent)) {
                boolean trobat = trobarCamiVoraç(seguent, desti, visitats, camiActual, millorCami);
                if (trobat) {
                    return true; // sortim tan bon punt trobem el destí

                }
            }
        }

        camiActual.remove(camiActual.size() - 1); // backtrack
        return false;
    }

    public Ruta rutaParquingMesProper(Lloc origen, LocalTime horaInici, Conductor conductor) {
        Ruta millorRuta = null;
        double millorTemps = Double.MAX_VALUE;

        for (Lloc lloc : llocs.keySet()) {
            if (lloc instanceof Parquing) {
                Parquing parquing = (Parquing) lloc;
                if (!parquing.estaPle()) {
                    List<Lloc> cami = camiVoraç(origen, lloc);
                    if (cami != null) {
                        double tempsRuta = calcularTempsRuta(cami);
                        if (tempsRuta < millorTemps) {
                            millorTemps = tempsRuta;
                            millorRuta = new Ruta(cami, horaInici, -1, tempsRuta, conductor, true);
                        }
                    }
                }

            }
        }

        return millorRuta;
    }

    public Ruta rutaParquingPrivatMesProper(Lloc origen, LocalTime horaInici, ConductorPlanificador conductor) {
        Ruta millorRuta = null;
        double millorTemps = Double.MAX_VALUE;

        for (Lloc lloc : llocs.keySet()) {
            if (lloc instanceof Parquing) {
                Parquing parquing = (Parquing) lloc;
                if (!parquing.estaPle() && parquing.esCarregadorPrivat(conductor.getParquingPrivat().obtenirId())) {
                    List<Lloc> cami = camiVoraç(origen, lloc);
                    if (cami != null) {
                        double tempsRuta = calcularTempsRuta(cami);
                        if (tempsRuta < millorTemps) {
                            millorTemps = tempsRuta;
                            millorRuta = new Ruta(cami, horaInici, -1, tempsRuta, conductor, true);
                        }
                    }
                }

            }
        }

        return millorRuta;
    }

    /**
     * @pre Cert
     * @post Retorna la llista de llocs i les seves connexions
     * @return Llista de llocs i les seves connexions
     */
    public Map<Lloc, List<Cami>> getLlocs() {
        return llocs;
    }

    /**
     * @pre Cert
     * @post Retorna la llista de connexions
     * @return Llista de connexions
     */
    public List<Cami> obtenirTotsElsCamins() {
        List<Cami> totsElsCamins = new ArrayList<>();

        for (List<Cami> caminsPerLloc : llocs.values()) {
            totsElsCamins.addAll(caminsPerLloc);
        }

        return totsElsCamins;
    }

}
