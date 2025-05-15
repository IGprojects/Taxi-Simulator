package core;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @class Ruta
 * @brief Representa la ruta que segueix un vehicle per completar una petició.
 * @details Conté una seqüència de llocs per on passa la ruta, així com la
 *          distància i el temps total del recorregut.
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class Ruta {
    private List<Lloc> llocs; /// < Llista de llocs que formen la ruta.
    private List<Cami> camins = new ArrayList<>();
    private double distanciaTotal; /// < Distància total de la ruta.
    private double tempsTotal; /// < Temps total de la ruta.
    private LocalTime horaInici; /// < Hora d'inici de la ruta.
    private Conductor conductor; /// < Conductor que realitza la ruta.
    private boolean esRutaCarrega; /// < Indica si la ruta és per una petició.
    private List<Pair<Integer, Integer>> llocsOrigenPeticioId; /// < Llista de llocs dels orígens de les peticions.
    private List<Pair<Integer, Integer>> llocsDestiPeticioId; /// < Llista de llocs dels destins de les peticions.
    private int passatgersPeticio; /// < Nombre de passatgers de la petició.

    public Ruta(List<Lloc> llocs, LocalTime horaInici, double distanciaTotal, double tempsTotal, Conductor conductor,
            boolean esRutaCarrega) {
        this.llocs = llocs;
        this.distanciaTotal = distanciaTotal;
        this.tempsTotal = tempsTotal;
        this.horaInici = horaInici;
        this.conductor = conductor;
        this.esRutaCarrega = esRutaCarrega;
        llocsOrigenPeticioId = new ArrayList<Pair<Integer, Integer>>();
        llocsDestiPeticioId = new ArrayList<Pair<Integer, Integer>>();
        passatgersPeticio = 0;
    }

    public Ruta() {
        this.llocs = null; // Llista buida en lloc de null
        this.distanciaTotal = 0.0;
        this.tempsTotal = 0.0;
        this.horaInici = null; // O LocalTime.MIN si prefereixes un valor per defecte
        this.conductor = null;
        this.esRutaCarrega = false; // Valor per defecte més lògic

    }

    /**
     * @pre llocs != null
     * @post retorna la llista del llocs per on te que passar per fer la ruta.
     * 
     * @return llista de llocs que es passa per realitzar la ruta
     */
    public List<Lloc> getLlocs() {
        return this.llocs;
    }

    /**
     * @pre cami != null
     * @post Afegeix el destí del camí a la ruta i actualitza la distància i el
     *       temps total.
     *
     * @param cami Camí a afegir a la ruta.
     */
    public void afegirCami(Cami cami) {
        llocs.add(cami.obtenirOrigen());
        llocs.add(cami.obtenirDesti());
        distanciaTotal += cami.obtenirDistancia();
        tempsTotal += cami.obtenirTemps();
    }

    /**
     * @brief Afegeix una seqüència de trams a partir d’una llista de llocs
     *        consecutius.
     * @pre cami.size() >= 2 && mapa != null
     * @post Actualitza la ruta amb els camins entre llocs consecutius, i també
     *       actualitza distància i temps totals.
     *
     * @param cami Llista de llocs consecutius que defineixen el recorregut.
     * @param mapa Mapa que permet calcular la distància i temps entre els llocs.
     */
    public void afegirTramDesDeLlocs(List<Lloc> cami, Mapa mapa) {
        if (cami == null || cami.size() < 2)
            return;

        for (int i = 0; i < cami.size() - 1; i++) {
            Lloc origen = cami.get(i);
            Lloc desti = cami.get(i + 1);
            double dist = mapa.calcularDistancia(origen, desti);
            double temps = mapa.calcularTemps(origen, desti);

            camins.add(new Cami(origen, desti, dist, temps));

            if (llocs.isEmpty() || !llocs.get(llocs.size() - 1).equals(origen)) {
                llocs.add(origen);
            }
            llocs.add(desti);

            distanciaTotal += dist;
            tempsTotal += temps;
        }
    }

    public void assignarPassatgersPeticio(int passatgers) {
        this.passatgersPeticio = passatgers;
    }

    public int obtenirPassatgersPeticio() {
        return passatgersPeticio;
    }

    public void assignarLlocsOrigenPeticions(List<Pair<Integer, Integer>> llocs) {
        llocsOrigenPeticioId = llocs;
    }

    public void assignarLlocsDestiPeticions(List<Pair<Integer, Integer>> llocs) {
        llocsDestiPeticioId = llocs;
    }

    /**
     * @pre La llista llocsOrigenPeticioId ha d’estar inicialitzada (pot estar
     *      buida).
     * @post No es modifica la llista. Només es retorna el primer element que
     *       compleixi la condició.
     * @param valor El valor que es vol cercar com a clau (first) dins la llista de
     *              parells.
     * @return El primer Pair que té la clau igual al valor donat, o null si no es
     *         troba cap coincidència.
     */
    public int trobarOrigenId(int valor) {
        for (Pair<Integer, Integer> pair : llocsOrigenPeticioId) {
            if (pair.getKey().equals(valor)) {
                llocsOrigenPeticioId.remove(pair);
                return pair.getValue();
            }
        }

        return -1; // No s'ha trobat cap coincidència
    }

    public int trobarDestiId(int valor) {
        for (Pair<Integer, Integer> pair : llocsDestiPeticioId) {
            if (pair.getKey().equals(valor)) {
                llocsDestiPeticioId.remove(pair);
                return pair.getValue();
            }
        }
        return -1; // No s'ha trobat cap coincidència
    }

    /**
     * @pre Cert.
     * @post Retorna la distància acumulada de la ruta.
     *
     * @return Distància total en quilòmetres.
     */
    public double obtenirDistanciaTotal() {
        return distanciaTotal;
    }

    /**
     * @pre Cert.
     * @post Retorna el temps acumulat de la ruta.
     *
     * @return Temps total en minuts.
     */
    public double obtenirTempsTotal() {
        return tempsTotal;
    }

    /**
     * @pre Cert.
     * @post Retorna si la ruta és per fer una càrrega o no.
     *
     * @return true si és una ruta per fer una càrrega, false en cas contrari.
     */
    public boolean isRutaCarrega() {
        return esRutaCarrega;
    }

    /**
     * @pre Cert.
     * @post Retorna el conductor que realitza la ruta.
     *
     * @return Conductor que realitza la ruta.
     */
    public Conductor obtenirConductor() {
        return conductor;
    }

    /**
     * @pre Cert.
     * @post Retorna la llista de camins de la ruta.
     *
     * @return Llista de camins de la ruta.
     */
    public List<Cami> obtenirTrams() {
        return camins;
    }

    /**
     * @pre Cert.
     * @post Retorna l'hora inicial.
     *
     * @return Hora inicial.
     */
    public LocalTime obtenirHoraInici() {
        return horaInici;
    }

    /**
     * @pre Cert.
     * @post Retorna true si la ruta conté llocs, false si està buida.
     *
     */
    public boolean esBuida() {
        return llocs.isEmpty();
    }

    // SETTERS

    public void setLlocs(List<Lloc> llocs) {

        this.llocs = llocs; // Defensa contra modificacions externes
    }

    public void setDistanciaTotal(double distanciaTotal) {
        if (distanciaTotal <= 0) {
            throw new IllegalArgumentException("La distància total ha de ser positiva");
        }
        this.distanciaTotal = distanciaTotal;
    }

    public void setTempsTotal(double tempsTotal) {
        if (tempsTotal <= 0) {
            throw new IllegalArgumentException("El temps total ha de ser positiu");
        }
        this.tempsTotal = tempsTotal;
    }

    public void setHoraInici(LocalTime horaInici) {
        if (horaInici == null) {
            throw new IllegalArgumentException("L'hora d'inici no pot ser nul·la");
        }
        this.horaInici = horaInici;
    }

    public void setConductor(Conductor conductor) {
        if (conductor == null) {
            throw new IllegalArgumentException("El conductor no pot ser nul");
        }
        this.conductor = conductor;
    }

    public void setEsRutaCarrega(boolean esRutaCarrega) {
        this.esRutaCarrega = esRutaCarrega;
    }

    public double getDistanciaTotal() {
        return distanciaTotal;
    }

    public double getTempsTotal() {
        return tempsTotal;
    }

    public LocalTime getHoraInici() {
        return horaInici;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public boolean isEsRutaCarrega() {
        return esRutaCarrega;
    }
}
