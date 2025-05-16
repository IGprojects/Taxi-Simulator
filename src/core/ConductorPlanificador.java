package core;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import events.CarregarBateriaEvent;
import events.DeixarPassatgersEvent;
import events.FiRutaEvent;
import events.IniciRutaEvent;
import events.MoureVehicleEvent;
import events.RecollirPassatgersEvent;

/**
 * @class ConductorPlanificador
 * @brief Conductor que planifica rutes entre càrregues completes.
 * @author Anouar El Barkouki Hitach
 * @version 2025.03.04
 */
public class ConductorPlanificador extends Conductor {

    private final Vehicle vehicle; /// < Vehicle que condueix el conductor.
    private Parquing parquingPrivat; /// < Pàrquing privat assignat al conductor.

    public ConductorPlanificador(int id, String nom, Vehicle vehicle, Parquing parquingPrivat) {
        super(id, nom, vehicle);
        this.id = id;
        this.nom = nom;
        this.vehicle = vehicle;
        this.parquingPrivat = parquingPrivat;
    }

    public Ruta planificarRuta(List<Peticio> peticions, Simulador s, LocalTime horaIniciSimulacio) {
        List<Peticio> recollides = new ArrayList<>(); /// < Peticions recollides durant la ruta.
        List<Lloc> rutaLlocs = new ArrayList<>(); /// < Llocs que formen la ruta.
        List<Lloc> ultimCami = new ArrayList<>(); /// < Últim camí recorregut.
        List<Pair<Integer, Integer>> rutaLlocsOrigenPeticions = new ArrayList<>(); /// < Llocs d'origen de les
                                                                                   /// peticions.
        List<Pair<Integer, Integer>> rutaLlocsDestiPeticions = new ArrayList<>(); /// < Llocs de destí de les peticions.
        Vehicle vehicle = getVehicle(); /// < Vehicle que condueix el conductor.
        Lloc ubicacio = vehicle.getUbicacioActual(); /// < Ubicació actual del vehicle.
        LocalTime horaActual = horaIniciSimulacio; /// < Hora actual de la simulació.
        Peticio primeraPeticio = null; /// < Primera petició a recollir.
        double tempsFinsPrimerOrigen = 0; /// < Temps fins a la primera petició.
        double autonomiaRestant = vehicle.obtenirBateria(); /// < Autonomia restant del vehicle.
        double distTotal = 0; /// < Distància total de la ruta.
        boolean rutaCarrega = false; /// < Indica si la ruta inclou càrrega.
        int passatgersActuals = vehicle.getPassatgersActuals(); /// < Passatgers actuals del vehicle.
        boolean finalitzar = false; /// < Indica si s'ha de finalitzar la ruta.
        rutaLlocs.add(ubicacio); /// < Afegim la ubicació inicial a la ruta.
        System.out.println("Inici planificació ruta del conductor " + this.getId() + " a les " + horaIniciSimulacio);
        System.out.println(
                "Ubicació inicial: " + ubicacio.obtenirId() + ", bateria: " + autonomiaRestant + " passatgers: "
                        + vehicle.getPassatgersActuals());

        while (!finalitzar) {
            // Variables per guardar la millor petició en aquesta iteració
            Peticio millorPeticio = null;
            List<Lloc> millorCami = null;
            double millorTemps = Double.MAX_VALUE;

            // Si ja hem agafat una primera petició i no és compartida, la ruta s’ha de
            // tancar aquí
            if (primeraPeticio != null && !primeraPeticio.esVehicleCompartit()) {
                finalitzar = true;
            } else {
                // Bucle per buscar la millor petició pendent que es pugui afegir
                for (Peticio p : peticions) {
                    if (p.estatActual() != EstatPeticio.PENDENT) {
                        System.out.println("Petició " + p.obtenirId() + " descartada: no està pendent.");
                        continue;
                    }

                    // Intentar trobar camí fins a l’origen i del destí fins a un carregador
                    List<Lloc> cami = s.getMapa().camiVoraç(ubicacio, p.obtenirOrigen());
                    Ruta rutaFinsCarregador = s.getMapa().rutaParquingPrivatMesProper(p.obtenirDesti(),
                            p.obtenirHoraMaximaArribada(), this);
                    if (cami == null || cami.isEmpty() || rutaFinsCarregador == null
                            || rutaFinsCarregador.getLlocs().isEmpty()) {
                        System.out.println("Petició " + p.obtenirId()
                                + " descartada: no hi ha camí a origen o no hi ha camí a carregador.");
                        continue;
                    }

                    // Calcular temps i distància fins a l’origen
                    double tempsOrigen = s.getMapa().calcularTempsRuta(cami);
                    double distOrigen = s.getMapa().calcularDistanciaRuta(cami);

                    LocalTime horaArribada = horaActual.plusMinutes((long) tempsOrigen);
                    // Verificació de temps límit per arribar a l’origen
                    if (horaArribada.isAfter(p.obtenirHoraMaximaArribada())) {
                        System.out.println("Petició " + p.obtenirId() + " descartada: arribada massa tard a origen.");
                        continue;
                    }

                    // Verificació d’autonomia per arribar a l’origen
                    if (autonomiaRestant < distOrigen) {
                        System.out.println(
                                "Petició " + p.obtenirId() + " descartada: no hi ha bateria per arribar a origen.");
                        continue;
                    }

                    // Intentar trobar camí des de l’origen fins al destí
                    ultimCami = s.getMapa().camiVoraç(p.obtenirOrigen(), p.obtenirDesti());
                    if (ultimCami == null || ultimCami.isEmpty()) {
                        System.out.println("Petició " + p.obtenirId() + " descartada: no hi ha camí a destí.");
                        continue;
                    }

                    // Calcular temps i distància total de l’origen al destí
                    double tempsDesti = tempsOrigen + s.getMapa().calcularTempsRuta(ultimCami);
                    double distDesti = distOrigen + s.getMapa().calcularDistanciaRuta(ultimCami);
                    LocalTime horaFinal = horaActual.plusMinutes((long) tempsDesti);

                    // Verificació de temps límit per arribar al destí
                    if (horaFinal.isAfter(p.obtenirHoraMaximaArribada())) {
                        System.out.println("Petició " + p.obtenirId() + " descartada: arribada massa tard a destí.");
                        continue;
                    }

                    // Verificació de capacitat de passatgers
                    if (passatgersActuals + p.obtenirNumPassatgers() <= vehicle.MAXPASSATGERS) {
                        // Selecció de la millor petició basada en el temps més curt
                        if (tempsDesti < millorTemps) {
                            millorTemps = tempsDesti;
                            millorPeticio = p;
                            millorCami = new ArrayList<>();
                            millorCami.addAll(cami);
                            millorCami.addAll(ultimCami.subList(1, ultimCami.size()));
                            distTotal = distOrigen + distDesti;

                            // Guardem la primera petició per poder calcular l’hora de sortida
                            if (primeraPeticio == null) {
                                primeraPeticio = p;
                                tempsFinsPrimerOrigen = s.getMapa().calcularTempsRuta(cami);
                            }
                        }
                    } else {
                        System.out.println("Petició " + p.obtenirId()
                                + " descartada: no compleix restriccions de passatgers.");
                    }
                }
            }

            // Si no s'ha trobat cap petició afegible, finalitzem la planificació
            if (millorPeticio == null) {
                System.out.println("No s'ha pogut afegir més peticions.");
                finalitzar = true;
            } else {
                // Comprovar si podrem arribar a carregar després de fer la ruta
                double distanciaRuta = s.getMapa().calcularDistanciaRuta(millorCami);
                int consumPercentatge = (int) Math.ceil((distanciaRuta / vehicle.AUTONOMIA) * 100);

                Ruta rutaFinsCarregador = s.getMapa().rutaParquingPrivatMesProper(millorPeticio.obtenirDesti(),
                        millorPeticio.obtenirHoraMaximaArribada(), this);
                double distanciaFinsCarregador = s.getMapa().calcularDistanciaRuta(rutaFinsCarregador.getLlocs());
                int consumFinsCarregador = (int) Math.ceil((distanciaFinsCarregador / vehicle.AUTONOMIA) * 100);

                // Si no hi ha autonomia per arribar al carregador després, desviem la ruta cap
                // a un carregador
                if ((autonomiaRestant - consumPercentatge) - consumFinsCarregador < 0) {
                    rutaCarrega = true;
                    rutaFinsCarregador.getLlocs().remove(0); // evitem repetir el punt actual
                    millorCami.removeAll(ultimCami); // eliminem la part del camí a destí
                    millorCami.addAll(rutaFinsCarregador.getLlocs()); // afegim el camí cap al carregador
                } else {
                    // Afegim la petició a la ruta
                    System.out.println("Afegida petició " + millorPeticio.obtenirId() + " a la ruta.");
                    rutaLlocsOrigenPeticions.add(new Pair<>(millorPeticio.obtenirOrigen().obtenirId(),
                            millorPeticio.obtenirNumPassatgers()));
                    rutaLlocsDestiPeticions.add(new Pair<>(millorPeticio.obtenirDesti().obtenirId(),
                            millorPeticio.obtenirNumPassatgers()));

                    recollides.add(millorPeticio);
                    millorPeticio.peticioEnProces();
                    passatgersActuals += millorPeticio.obtenirNumPassatgers();
                }

                // Afegim a la ruta tots els llocs del millor camí (menys el primer, ja afegit)
                for (Lloc l : millorCami.subList(1, millorCami.size())) {
                    rutaLlocs.add(l);
                }

                // Actualitzem valors per a la següent iteració
                autonomiaRestant -= consumPercentatge;
                horaActual = horaActual.plusMinutes((long) millorTemps);
                ubicacio = rutaLlocs.get(rutaLlocs.size() - 1);
            }

        }

        // Si només hi ha un lloc (el d'inici), vol dir que no s'ha afegit cap petició
        if (rutaLlocs.size() == 1) {
            System.out.println("Cap ruta planificada.");
            return null;
        }

        // Elimina del conjunt original de peticions totes les que s'han assignat en
        // aquesta ruta
        peticions.removeAll(recollides);

        // Calculem l'hora de sortida real, retrocedint segons el temps necessari fins
        // al primer origen
        LocalTime horaSortidaReal = primeraPeticio.obtenirHoraMinimaRecollida()
                .minusMinutes((long) tempsFinsPrimerOrigen);

        // Informació per consola de la ruta final
        System.out.println("Ruta final planificada amb " + recollides.size()
                + " peticions. Hora de sortida real: " + horaSortidaReal);

        // Creació de l'objecte Ruta amb tota la informació acumulada
        Ruta rutaCompleta = new Ruta(
                rutaLlocs,
                horaSortidaReal,
                distTotal,
                s.getMapa().calcularTempsRuta(rutaLlocs),
                this,
                rutaCarrega);

        // Assignem la informació de quants passatgers s'han recollit i deixat a cada
        // lloc
        rutaCompleta.assignarLlocsOrigenPeticions(rutaLlocsOrigenPeticions);
        rutaCompleta.assignarLlocsDestiPeticions(rutaLlocsDestiPeticions);

        return rutaCompleta;
    }

    public boolean teBateria(double distancia, Simulador simulador, Mapa mapa, LocalTime horaInici,
            LocalTime horaActual) {
        // Cas 1: el vehicle té bateria suficient → pot continuar
        if (vehicle.teBateria(distancia, false)) {
            return true;
        } else {
            // Cas 2: no té prou bateria → buscar ruta cap al pàrquing privat més proper
            Ruta r = mapa.rutaParquingPrivatMesProper(vehicle.getUbicacioActual(), horaInici, this);

            if (r != null) {
                // Si s'ha trobat una ruta cap al punt de càrrega, marquem el conductor com
                // ocupat
                ocupat = true;

                // Afegim l'esdeveniment per iniciar la ruta cap al parquing per carregar
                simulador.afegirEsdeveniment(new IniciRutaEvent(horaActual, this, vehicle, r));
            } else {
                // No hi ha cap pàrquing disponible per carregar
                System.out.println("No hi ha cap pàrquing disponible");
            }

            return false;
        }
    }

    @Override
    public boolean potServirPeticio(int nombrePassatgers) {
        return !isOcupat() && !vehicle.esCarregant() && nombrePassatgers <= vehicle.getMaxPassatgers();
    }

    /**
     * @brief Executa una ruta detallada amb múltiples punts d’origen i destí,
     *        gestionant passatgers i càrrega.
     *
     *        Aquest mètode recorre el camí de la ruta planificada, afegint
     *        esdeveniments al simulador com recollida
     *        o deixada de passatgers en cada lloc intermedi, així com el moviment
     *        entre punts i la càrrega final si escau.
     *
     * @param ruta      La ruta que conté el camí i la informació dels passatgers a
     *                  recollir i deixar.
     * @param vehicle   El vehicle que realitza la ruta.
     * @param simulador El simulador on s’afegeixen els esdeveniments de la ruta.
     *
     * @pre La ruta ha d’incloure almenys dos llocs. Vehicle i simulador han d’estar
     *      inicialitzats.
     * @post Es generen esdeveniments de moviment, recollida i/o deixada de
     *       passatgers, o càrrega si cal.
     */
    @Override
    public void executarRuta(Ruta ruta, Vehicle vehicle, Simulador simulador) {
        List<Lloc> llocs = ruta.getLlocs();
        LocalTime horaActual = ruta.obtenirHoraInici();

        for (int i = 0; i < llocs.size() - 1; i++) {
            Lloc origen = llocs.get(i);
            Lloc desti = llocs.get(i + 1);

            // Calcula distància i temps entre origen i destí
            double distancia = simulador.getMapa().calcularDistancia(origen, desti);
            double temps = simulador.getMapa().calcularTemps(origen, desti);

            horaActual = horaActual.plusMinutes((long) temps);

            // Si hi ha passatgers per recollir en aquest lloc
            int passatgersOrigen = ruta.trobarOrigenId(origen.obtenirId());
            if (passatgersOrigen != -1) {
                RecollirPassatgersEvent recollir = new RecollirPassatgersEvent(
                        horaActual, this, origen, passatgersOrigen);
                simulador.afegirEsdeveniment(recollir);
            }

            horaActual = horaActual.plusMinutes(1); // marge abans de moure
            MoureVehicleEvent moure = new MoureVehicleEvent(
                    horaActual, vehicle, origen, desti, distancia);
            simulador.afegirEsdeveniment(moure);

            // Si hi ha passatgers per deixar en aquest lloc
            int passatgersDesti = ruta.trobarDestiId(desti.obtenirId());
            if (passatgersDesti != -1) {
                DeixarPassatgersEvent deixar = new DeixarPassatgersEvent(
                        horaActual, this, origen, passatgersDesti);
                simulador.afegirEsdeveniment(deixar);
            }
        }

        // Si la ruta és de càrrega, intentar carregar al parquing
        if (ruta.isRutaCarrega()) {
            Lloc ultimLloc = ruta.getLlocs().get(ruta.getLlocs().size() - 1);
            if (ultimLloc instanceof Parquing parquing) {
                PuntCarrega pc = parquing.puntCarregaPublicDisponible();
                if (pc != null) {
                    System.out.println("El vehicle " + vehicle.getId() + " ha arribat al punt de càrrega del parquing "
                            + parquing.obtenirId() + ".");
                    double duracio = (pc.getTipusCarga() == TipusPuntCarrega.CARGA_LENTA)
                            ? vehicle.TEMPSCARGALENTA
                            : vehicle.TEMPSCARGARAPIDA;

                    LocalTime horaIniciCarrega = horaActual;
                    simulador.afegirEsdeveniment(new CarregarBateriaEvent(horaIniciCarrega, vehicle, duracio, this));
                } else {
                    System.out.println("No hi ha punts de càrrega disponibles.");
                }
            }
        } else {
            // Ruta normal (no de càrrega): marcar final de ruta
            simulador.afegirEsdeveniment(new FiRutaEvent(horaActual, this, ruta));
        }
    }

    /**
     * @pre Cert.
     * @post Retorna el parquing privat assignat al conductor.
     * @return Parquing privat assignat al conductor.
     */
    public Parquing getParquingPrivat() {
        return parquingPrivat;
    }

    /**
     * @pre Cert.
     * @post retorna l'ID del parquing privat assignat al conductor.
     * @return ID del parquing privat assignat al conductor.
     */
    public int getIdParquing() {
        return parquingPrivat.obtenirId();
    }
}