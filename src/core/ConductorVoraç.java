package core;

import java.time.LocalTime;
import java.util.List;

import events.CarregarBateriaEvent;
import events.DeixarPassatgersEvent;
import events.FiRutaEvent;
import events.IniciRutaEvent;
import events.MoureVehicleEvent;
import events.RecollirPassatgersEvent;

/**
 * @class ConductorVoraç
 * @brief Defineix un tipus de conductor (conductor voraç)
 *
 * @author Anouar El Barkouki Hitach
 * @version 2025.03.04
 */
public class ConductorVoraç extends Conductor {

    public ConductorVoraç(int id, String nom, Vehicle vehicle) {
        super(id, nom, vehicle);
    }

    /**
     * @brief Executa una ruta generant els esdeveniments corresponents segons el
     *        tipus de ruta (passatgers o càrrega).
     *
     *        Aquest mètode recorre els trams de la ruta planificada i afegeix
     *        esdeveniments de moviment, recollida,
     *        deixada de passatgers o càrrega de bateria, segons el tipus de ruta.
     *
     * @param ruta      La ruta que s'ha d'executar.
     * @param vehicle   El vehicle que realitzarà la ruta.
     * @param simulador El simulador que gestiona els esdeveniments.
     *
     * @pre La ruta ha d’estar inicialitzada amb almenys dos llocs i una hora
     *      d’inici vàlida.
     * @pre El vehicle ha d’estar preparat per executar la ruta (bateria suficient,
     *      ubicació correcta, etc.).
     * @post Es generen i registren esdeveniments al simulador per recollida,
     *       moviment, deixada o càrrega de bateria.
     */
    @Override
    public void executarRuta(Ruta ruta, Vehicle vehicle, Simulador simulador) {
        List<Lloc> llocs = ruta.getLlocs();
        LocalTime horaActual = ruta.obtenirHoraInici();

        // Recorre la ruta i afegeix esdeveniments per cada tram entre llocs
        for (int i = 0; i < llocs.size() - 1; i++) {
            Lloc origen = llocs.get(i);
            Lloc desti = llocs.get(i + 1);

            // Calcular distància i temps entre el tram actual
            double distancia = simulador.getMapa().calcularDistancia(origen, desti);
            double temps = simulador.getMapa().calcularTemps(origen, desti);

            horaActual = horaActual.plusMinutes((long) temps);

            // Si és una ruta de passatgers i és el primer tram → recollida
            if (!ruta.isRutaCarrega() && i == 0) {
                RecollirPassatgersEvent recollir = new RecollirPassatgersEvent(
                        horaActual, this, origen, ruta.obtenirPassatgersPeticio());
                simulador.afegirEsdeveniment(recollir);
            }

            // Afegim un petit marge de temps abans de moure el vehicle
            horaActual = horaActual.plusMinutes(1);
            MoureVehicleEvent moure = new MoureVehicleEvent(
                    horaActual, vehicle, origen, desti, distancia);
            simulador.afegirEsdeveniment(moure);

            // Si és una ruta de passatgers i és l'últim tram → deixar passatgers
            if (!ruta.isRutaCarrega() && i == llocs.size() - 2) {
                DeixarPassatgersEvent deixar = new DeixarPassatgersEvent(
                        horaActual, this, desti, ruta.obtenirPassatgersPeticio());
                simulador.afegirEsdeveniment(deixar);
            }
        }

        // Si és una ruta de càrrega, intentem iniciar la càrrega si hi ha un punt disponible
        if (ruta.isRutaCarrega()) {
            Lloc ultimLloc = ruta.getLlocs().get(ruta.getLlocs().size() - 1);
            if (ultimLloc instanceof Parquing parquing) {
                PuntCarrega pc = parquing.puntCarregaPublicDisponible();
                if (pc != null) {
                    double duracio = (pc.getTipusCarga() == TipusPuntCarrega.CARGA_LENTA)
                            ? vehicle.TEMPSCARGALENTA
                            : vehicle.TEMPSCARGARAPIDA;
                    simulador.afegirEsdeveniment(new CarregarBateriaEvent(horaActual, vehicle, duracio, this));
                } else {
                    System.out.println("No hi ha punts de càrrega disponibles.");
                }
            }
        } else {
            // Ruta normal (passatgers): marcar el final de la ruta
            simulador.afegirEsdeveniment(new FiRutaEvent(horaActual, this, ruta));
        }
    }

    /**
     * @brief Planifica una ruta directa des de l'origen fins al destí de la petició
     *        utilitzant un camí voraç.
     *
     *        Es calcula la ruta més ràpida (segons l'algorisme voraç del mapa)
     *        entre l’origen i el destí de la petició.
     *        A partir del camí, es calcula la distància i temps totals, i es
     *        construeix l’objecte Ruta associat al conductor.
     *
     * @param peticio La petició que s'ha de servir, amb origen, destí i número de
     *                passatgers.
     * @param mapa    El mapa que conté la informació dels camins i permet calcular
     *                rutes i distàncies.
     * @return Ruta objecte que representa la ruta planificada, o null si no es pot
     *         planificar cap camí vàlid.
     *
     * @pre La petició i el mapa han d’estar inicialitzats. El mapa ha de poder
     *      retornar un camí vàlid entre els llocs.
     * @post Es retorna una ruta (Ruta) amb el camí, hora d'inici, distància i temps
     *       totals, i nombre de passatgers assignat.
     */
    public Ruta planificarRuta(Peticio peticio, Mapa mapa) {
        // Obtenim el camí voraç entre l'origen i el destí de la petició
        List<Lloc> cami = mapa.camiVoraç(peticio.obtenirOrigen(), peticio.obtenirDesti());

        double distanciaTotal = 0;
        double tempsTotal = 0;

        // Recorrem el camí per calcular la distància i temps totals
        for (int i = 0; i < cami.size() - 1; i++) {
            Lloc origen = cami.get(i);
            Lloc desti = cami.get(i + 1);

            distanciaTotal += mapa.calcularDistancia(origen, desti); // distància entre cada parell de llocs
            tempsTotal += mapa.calcularTemps(origen, desti); // temps estimat de cada tram
        }

        // Creem la ruta amb la informació calculada
        Ruta ruta = new Ruta(
                cami,
                peticio.obtenirHoraMinimaRecollida(),
                distanciaTotal,
                tempsTotal,
                this,
                false // no és una ruta de càrrega
        );

        // Assignem els passatgers a la ruta (segons la petició)
        ruta.assignarPassatgersPeticio(peticio.obtenirNumPassatgers());

        return ruta;
    }

    @Override
    public boolean teBateria(double distancia, Simulador simulador, Mapa mapa, LocalTime horaInici,
            LocalTime horaActual) {
        // Comprovar si el vehicle pot fer la petició
        if (vehicle.teBateria(distancia, true))
            return true;

        // Si no pot fer la petició, buscar un parquing proper
        Ruta r = mapa.rutaParquingMesProper(vehicle.getUbicacioActual(), horaActual, this);

        if (r != null) {
            ocupat = true;
            // Afegir esdeveniment de moure el vehicle al parquing
            simulador.afegirEsdeveniment(new IniciRutaEvent(horaActual, this,
                    vehicle, r));
        } else
            System.out.println("No hi ha cap pàrquing disponible");

        return false;

    }

    @Override
    public boolean potServirPeticio(int nombrePassatgers) {
        return !isOcupat() && !vehicle.esCarregant() && nombrePassatgers <= vehicle.getMaxPassatgers();
    }

}
