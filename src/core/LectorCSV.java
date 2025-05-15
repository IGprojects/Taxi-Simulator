package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @class LectorCSV
 * @brief Classe per llegir fitxers CSV i carregar dades a la simulació.
 *
 * @author Dídac Gros Labrador
 * @version 2025.05.13
 */
public class LectorCSV {

    /**
     * @pre pathFitxer != null
     * @post Carrega una llista de llocs des d'un fitxer CSV.
     * @param pathFitxer
     * @return Llista de llocs carregats
     */
    public static List<Lloc> carregarLlocs(String pathFitxer) {
        List<Lloc> llocs = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(new FileReader(pathFitxer))) {
            String linia = lector.readLine(); // saltem la capçalera

            while ((linia = lector.readLine()) != null) {
                String[] camps = linia.split(",");

                int id = Integer.parseInt(camps[0].trim());
                char tipus = camps[1].trim().charAt(0);

                if (tipus == 'L') {
                    // Localització normal
                    llocs.add(new Lloc(id));
                } else if (tipus == 'P') {
                    int maxVehicles = Integer.parseInt(camps[4].trim());
                    int nCarregadors = Integer.parseInt(camps[2].trim());
                    int nCarregadorsPrivats = Integer.parseInt(camps[3].trim());
                    List<PuntCarrega> puntsCarregaPublics = new ArrayList<>();
                    List<PuntCarrega> puntsCarregaPrivats = new ArrayList<>();

                    for (int i = 0; i < nCarregadors; i++) {
                        if (i % 2 == 0)
                            puntsCarregaPublics.add(new PuntCarrega(TipusPuntCarrega.CARGA_RAPIDA));
                        else
                            puntsCarregaPublics.add(new PuntCarrega(TipusPuntCarrega.CARGA_LENTA));
                    }
                    for (int i = 0; i < nCarregadorsPrivats; i++) {
                        if (i % 2 == 0)
                            puntsCarregaPublics.add(new PuntCarrega(TipusPuntCarrega.CARGA_RAPIDA));
                        else
                            puntsCarregaPublics.add(new PuntCarrega(TipusPuntCarrega.CARGA_LENTA));
                    }
                    llocs.add(new Parquing(id, maxVehicles, puntsCarregaPublics, puntsCarregaPrivats));
                } else {
                    System.err.println("Tipus desconegut: " + tipus + " a la línia: " + linia);
                }
            }

        } catch (IOException | NumberFormatException e) {
            System.err.println("Error llegint llocs: " + e.getMessage());
        }

        return llocs;
    }

    /**
     * Llegeix un fitxer CSV i retorna una llista de camins entre llocs.
     * 
     * @param pathFitxer Ruta del fitxer CSV
     * @param llocsPerId Mapa amb els llocs ja carregats (id -> Lloc)
     * @return Llista de camins
     */
    public static List<Cami> carregarCamins(String pathFitxer, Map<Integer, Lloc> llocsPerId) {
        List<Cami> camins = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(new FileReader(pathFitxer))) {
            String linia = lector.readLine(); // salta capçalera

            while ((linia = lector.readLine()) != null) {
                String[] camps = linia.split(",");

                int idOrigen = Integer.parseInt(camps[0].trim());
                int idDesti = Integer.parseInt(camps[1].trim());
                double distancia = Double.parseDouble(camps[2].trim());
                double temps = Double.parseDouble(camps[3].trim());

                Lloc origen = llocsPerId.get(idOrigen);
                Lloc desti = llocsPerId.get(idDesti);

                if (origen != null && desti != null) {
                    camins.add(new Cami(origen, desti, distancia, temps));
                } else {
                    System.err.println("Origen o destí no trobat per línia: " + linia);
                }
            }

        } catch (IOException | NumberFormatException e) {
            System.err.println("Error llegint camins: " + e.getMessage());
        }

        return camins;
    }

    /**
     * @pre pathFitxer != null && llocsPerId != null
     * @post Carrega una llista de peticions des d'un fitxer CSV.
     * @param pathFitxer
     * @param llocsPerId
     * @return Llista de peticions carregades
     */
    public static List<Peticio> carregarPeticions(String pathFitxer, Map<Integer, Lloc> llocsPerId) {
        List<Peticio> peticions = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(new FileReader(pathFitxer))) {
            String linia = lector.readLine(); // saltem capçalera

            while ((linia = lector.readLine()) != null) {
                String[] camps = linia.split(",");

                int id = Integer.parseInt(camps[0].trim());
                int idOrigen = Integer.parseInt(camps[1].trim());
                int idDesti = Integer.parseInt(camps[2].trim());
                LocalTime horaMinRecollida = LocalTime.parse(camps[3].trim());
                LocalTime horaMaxArribada = LocalTime.parse(camps[4].trim());
                int numPassatgers = Integer.parseInt(camps[5].trim());
                boolean vehicleCompartit = Boolean.parseBoolean(camps[6].trim());

                Lloc origen = llocsPerId.get(idOrigen);
                Lloc desti = llocsPerId.get(idDesti);

                if (origen != null && desti != null) {
                    peticions.add(new Peticio(id, origen, desti, horaMinRecollida, horaMaxArribada,
                            numPassatgers, vehicleCompartit));
                } else {
                    System.err.println("Origen o destí no trobat per petició ID " + id);
                }
            }

        } catch (IOException | NumberFormatException e) {
            System.err.println("Error llegint peticions: " + e.getMessage());
        }

        return peticions;
    }

    /**
     * @pre pathFitxer != null && vehiclesPerId != null && llocsPerId != null
     * @post Llegeix un fitxer CSV i carrega una llista de conductors, associats avehicles ja existents.
     * @param pathFitxer    Ruta del fitxer conductors.csv
     * @param vehiclesPerId Mapa d’ID -> Vehicle (vehicles ja carregats)
     * @return Llista de conductors
     */
    public static List<Conductor> carregarConductors(String pathFitxer, Map<Integer, Vehicle> vehiclesPerId,
            Map<Integer, Lloc> llocsPerId) {
        List<Conductor> conductors = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(new FileReader(pathFitxer))) {
            String linia = lector.readLine(); // salta capçalera

            while ((linia = lector.readLine()) != null) {
                String[] camps = linia.split(",");

                int id = Integer.parseInt(camps[0].trim());
                String nom = camps[1].trim();
                String tipus = camps[2].trim().toLowerCase();
                int idVehicle = Integer.parseInt(camps[3].trim());

                Vehicle vehicle = vehiclesPerId.get(idVehicle);

                if (vehicle == null) {
                    System.err.println("Vehicle amb ID " + idVehicle + " no trobat. Conductor ID " + id + " omès.");
                    continue;
                }

                Conductor conductor;
                if (tipus.equals("voraç")) {
                    conductor = new ConductorVoraç(id, nom, vehicle);
                } else if (tipus.equals("planificador")) {
                    int idParquing = Integer.parseInt(camps[4].trim());
                    Parquing parquing = (Parquing) llocsPerId.get(idParquing);
                    conductor = new ConductorPlanificador(id, nom, vehicle, parquing);
                } else {
                    System.err.println("Tipus de conductor desconegut: " + tipus);
                    continue;
                }

                conductors.add(conductor);
            }

        } catch (IOException | NumberFormatException e) {
            System.err.println("Error llegint conductors: " + e.getMessage());
        }

        return conductors;
    }

    /**
     * Llegeix vehicles des d’un fitxer CSV i retorna una llista de vehicles,
     * associats als llocs corresponents.
     *
     * @param pathFitxer Ruta del CSV
     * @param llocsPerId Mapa ID -> Lloc (ja carregats)
     * @return Llista de vehicles
     */
    public static List<Vehicle> carregarVehicles(String pathFitxer, Map<Integer, Lloc> llocsPerId) {
        List<Vehicle> vehicles = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(new FileReader(pathFitxer))) {
            String linia = lector.readLine(); // saltem la capçalera

            while ((linia = lector.readLine()) != null) {
                String[] camps = linia.split(",");

                int id = Integer.parseInt(camps[0].trim());
                int idUbicacio = Integer.parseInt(camps[1].trim());
                int autonomia = Integer.parseInt(camps[2].trim());
                int maxPassatgers = Integer.parseInt(camps[3].trim());
                double tempsCargaRapida = Double.parseDouble(camps[4].trim());
                double tempsCargaLenta = Double.parseDouble(camps[5].trim());

                Lloc ubicacio = llocsPerId.get(idUbicacio);
                if (ubicacio == null) {
                    System.err.println("Ubicació amb ID " + idUbicacio + " no trobada per vehicle " + id);
                    continue;
                }

                Vehicle vehicle = new Vehicle(id, ubicacio, maxPassatgers, autonomia, tempsCargaLenta,
                        tempsCargaRapida);
                vehicles.add(vehicle);
            }

        } catch (IOException | NumberFormatException e) {
            System.err.println("Error llegint vehicles: " + e.getMessage());
        }

        return vehicles;
    }
}
