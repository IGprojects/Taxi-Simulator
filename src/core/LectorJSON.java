package core;

import events.CarregarBateriaEvent;
import events.DeixarPassatgersEvent;
import events.Event;
import events.FiCarregaEvent;
import events.FiRutaEvent;
import events.IniciRutaEvent;
import events.MoureVehicleEvent;
import events.RecollirPassatgersEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @class LectorJSon
 * @brief Classe per carregar i llegir simulacions en json.
 * @author Ignasi Ferrés Iglesias
 * @version 2025.05.13
 */
public class LectorJSON {
    /**
     * @brief Llegeix un fitxer JSON i retorna el contingut com a cadena de text.
     *
     * @param pathFitxer Ruta del fitxer JSON a llegir.
     * @return Contingut del fitxer com a cadena de text.
     */

    public static Map<Integer, Lloc> convertirLlistaAMap_Llocs(List<Lloc> llocs) {
        return llocs.stream()
                .collect(Collectors.toMap(
                        Lloc::obtenirId, // Función para extraer la clave (ID)
                        lloc -> lloc // Función para el valor (el objeto mismo)
                ));
    }

    public static Map<Integer, Vehicle> convertirLlistaAMap_Vehicles(List<Vehicle> vehicles) {
        return vehicles.stream()
                .collect(Collectors.toMap(
                        Vehicle::getId, // Función para extraer la clave (ID)
                        Vehicle -> Vehicle // Función para el valor (el objeto mismo)
                ));
    }
    public static Map<Integer, Conductor> convertirLlistaAMap_Conductors(List<Conductor> conductors) {
        return conductors.stream()
                .collect(Collectors.toMap(
                        Conductor::getId, // Función para extraer la clave (ID)
                        Conductor -> Conductor // Función para el valor (el objeto mismo)
                ));
    }

    public static List<Lloc> carregarLlocs(String pathFitxer) {
        List<Lloc> llocs = new ArrayList<>();
        String jsonContent = llegirFitxerComplet(pathFitxer);

        // Buscar el contingut del array "llocs"
        Pattern arrayPattern = Pattern.compile("\"llocs\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher arrayMatcher = arrayPattern.matcher(jsonContent);

        if (!arrayMatcher.find()) {
            System.out.println("No s'ha trobat l'array 'llocs' al JSON.");
            return llocs;
        }

        String llocsContent = arrayMatcher.group(1);

        // Patró que tolera ordre variable dels camps dins de cada objecte
        Pattern pattern = Pattern.compile(
                "\\{[^}]*?"
                + "\"ID\"\\s*:\\s*(\\d+)[^}]*?"
                + "\"TIPUS\"\\s*:\\s*\"([PL])\"[^}]*?"
                + "(?:\"MAX_VEHICLES\"\\s*:\\s*(\\d+)[^}]*?)?"
                + "(?:\"N_CARREGADORS\"\\s*:\\s*(\\d+)[^}]*?)?"
                + "(?:\"N_CARREGADORS_PRIVATS\"\\s*:\\s*(\\d+)[^}]*?)?"
                + "\\}",
                Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(llocsContent);

        while (matcher.find()) {
            int id = Integer.parseInt(matcher.group(1));
            String tipus = matcher.group(2);

            if (tipus.equals("P")) {
                int maxVehicles = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
                int nCarregadors = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : 0;
                int nCarregadorsPrivats = matcher.group(5) != null ? Integer.parseInt(matcher.group(5)) : 0;

                List<PuntCarrega> puntsPublics = new ArrayList<>();
                List<PuntCarrega> puntsPrivats = new ArrayList<>();

                for (int i = 0; i < nCarregadors; i++) {
                    puntsPublics.add(new PuntCarrega(
                            i % 2 == 0 ? TipusPuntCarrega.CARGA_RAPIDA : TipusPuntCarrega.CARGA_LENTA));
                }

                for (int i = 0; i < nCarregadorsPrivats; i++) {
                    puntsPrivats.add(new PuntCarrega(
                            i % 2 == 0 ? TipusPuntCarrega.CARGA_RAPIDA : TipusPuntCarrega.CARGA_LENTA));
                }

                llocs.add(new Parquing(id, maxVehicles, puntsPublics, puntsPrivats));
            } else {
                llocs.add(new Lloc(id));
            }
        }

        System.out.println("Llocs carregats: " + llocs.size());
        return llocs;
    }
    
    public static List<Cami> carregarCamins(String pathFitxer, Map<Integer, Lloc> llocsPerId) {
        List<Cami> camins = new ArrayList<>();
        String jsonContent = llegirFitxerComplet(pathFitxer);

        Pattern pattern = Pattern.compile(
                "\\{\\s*\"ORIGEN\"\\s*:\\s*\"?(\\d+)\"?\\s*,"
                + "\\s*\"DESTI\"\\s*:\\s*\"?(\\d+)\"?\\s*,"
                + "\\s*\"DISTANCIA_KM\"\\s*:\\s*(\\d+\\.?\\d*)\\s*,"
                + "\\s*\"TEMPS_MIN\"\\s*:\\s*(\\d+\\.?\\d*)\\s*\\}");

        Matcher matcher = pattern.matcher(jsonContent);

        while (matcher.find()) {
            int idOrigen = Integer.parseInt(matcher.group(1));
            int idDesti = Integer.parseInt(matcher.group(2));
            double distancia = Double.parseDouble(matcher.group(3));
            double temps = Double.parseDouble(matcher.group(4));

            Lloc origen = llocsPerId.get(idOrigen);
            Lloc desti = llocsPerId.get(idDesti);

            if (origen != null && desti != null) {
                camins.add(new Cami(origen, desti, distancia, temps));
            }
        }
        return camins;
    }

    public static List<Vehicle> carregarVehicles(String pathFitxer, Map<Integer, Lloc> llocsPerId) {
        List<Vehicle> vehicles = new ArrayList<>();
        String jsonContent = llegirFitxerComplet(pathFitxer);

        Pattern pattern = Pattern.compile(
                "\\{\\s*\"ID\"\\s*:\\s*\"?(\\d+)\"?\\s*,"
                + "\\s*\"ID_UBICACIO\"\\s*:\\s*\"?(\\d+)\"?\\s*,"
                + "\\s*\"AUTONOMIA_KM\"\\s*:\\s*(\\d+)\\s*,"
                + "\\s*\"MAX_PASSATGERS\"\\s*:\\s*(\\d+)\\s*,"
                + "\\s*\"TEMPS_CARGA_RAPIDA\"\\s*:\\s*(\\d+\\.?\\d*)\\s*,"
                + "\\s*\"TEMPS_CARGA_LENTA\"\\s*:\\s*(\\d+\\.?\\d*)\\s*\\}");

        Matcher matcher = pattern.matcher(jsonContent);

        while (matcher.find()) {
            int id = Integer.parseInt(matcher.group(1));
            int idUbicacio = Integer.parseInt(matcher.group(2));
            int autonomia = Integer.parseInt(matcher.group(3));
            int maxPassatgers = Integer.parseInt(matcher.group(4));
            double tempsCargaRapida = Double.parseDouble(matcher.group(5));
            double tempsCargaLenta = Double.parseDouble(matcher.group(6));

            Lloc ubicacio = llocsPerId.get(idUbicacio);
            if (ubicacio != null) {
                vehicles.add(new Vehicle(id, ubicacio, maxPassatgers, autonomia,
                        tempsCargaLenta, tempsCargaRapida));
            }
        }
        return vehicles;
    }

    public static List<Estadistiques> carregarEstadistiques(String pathFitxer) {
        List<Estadistiques> estadistiques = new ArrayList<>();
        String jsonContent = llegirFitxerComplet(pathFitxer);

        Pattern pattern = Pattern.compile(
                "\\{\\s*\"peticionesServidas\"\\s*:\\s*(\\d+)\\s*,"
                + "\\s*\"peticionesNoServidas\"\\s*:\\s*(\\d+)\\s*,"
                + "\\s*\"tiempoTotalEspera\"\\s*:\\s*(\\d+\\.?\\d*)\\s*,"
                + "\\s*\"tiempoMaximoEspera\"\\s*:\\s*(\\d+\\.?\\d*)\\s*,"
                + "\\s*\"ocupacionTotalVehiculos\"\\s*:\\s*(\\d+\\.?\\d*)\\s*,"
                + "\\s*\"muestrasOcupacion\"\\s*:\\s*(\\d+)\\s*,"
                + "\\s*\"porcentajeBateriaPromedio\"\\s*:\\s*(\\d+\\.?\\d*)\\s*,"
                + "\\s*\"muestrasBateria\"\\s*:\\s*(\\d+)\\s*,"
                + "\\s*\"tiempoTotalViaje\"\\s*:\\s*(\\d+\\.?\\d*)\\s*,"
                + "\\s*\"muestrasViaje\"\\s*:\\s*(\\d+)\\s*\\}");

        Matcher matcher = pattern.matcher(jsonContent);

        while (matcher.find()) {
            int peticionesServidas = Integer.parseInt(matcher.group(1));
            int peticionesNoServidas = Integer.parseInt(matcher.group(2));
            double tiempoTotalEspera = Double.parseDouble(matcher.group(3));
            double tiempoMaximoEspera = Double.parseDouble(matcher.group(4));
            double ocupacionTotalVehiculos = Double.parseDouble(matcher.group(5));
            int muestrasOcupacion = Integer.parseInt(matcher.group(6));
            double porcentajeBateriaPromedio = Double.parseDouble(matcher.group(7));
            int muestrasBateria = Integer.parseInt(matcher.group(8));
            double tiempoTotalViaje = Double.parseDouble(matcher.group(9));
            int muestrasViaje = Integer.parseInt(matcher.group(10));

            estadistiques.add(new Estadistiques(
                    peticionesServidas, peticionesNoServidas,
                    tiempoTotalEspera, tiempoMaximoEspera,
                    ocupacionTotalVehiculos, muestrasOcupacion,
                    porcentajeBateriaPromedio, muestrasBateria,
                    tiempoTotalViaje, muestrasViaje
            ));
        }

        return estadistiques;
    }

    private static String llegirFitxerComplet(String pathFitxer) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(pathFitxer))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error llegint fitxer: " + e.getMessage());
        }
        return content.toString();
    }

    public static List<Conductor> carregarConductors(String pathFitxer,
            Map<Integer, Vehicle> vehiclesPerId, Map<Integer, Lloc> llocsPerId) {
        List<Conductor> conductors = new ArrayList<>();
        String jsonContent = llegirFitxerComplet(pathFitxer);

        // Primero extraemos el array completo de conductors
        Pattern arrayPattern = Pattern.compile("\"conductors\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher arrayMatcher = arrayPattern.matcher(jsonContent);

        if (!arrayMatcher.find()) {
            System.out.println("No se encontró el array de conductors en el JSON");
            return conductors;
        }

        String conductorsContent = arrayMatcher.group(1);

        // Patrón mejorado que maneja ambos tipos de conductores
        Pattern pattern = Pattern.compile(
                "\\{\\s*\"ID\"\\s*:\\s*(\\d+)\\s*,"
                + "\\s*\"NOM\"\\s*:\\s*\"([^\"]+)\"\\s*,"
                + "\\s*\"TIPUS\"\\s*:\\s*\"([^\"]+)\"\\s*,"
                + "\\s*\"IDVEHICLE\"\\s*:\\s*(\\d+)"
                + "(?:\\s*,\\s*\"ID_PARQUING_PRIVAT\"\\s*:\\s*(\\d+))?"
                + "\\s*\\}");

        Matcher matcher = pattern.matcher(conductorsContent);

        while (matcher.find()) {
            int id = Integer.parseInt(matcher.group(1));
            String nom = matcher.group(2);
            String tipus = matcher.group(3).toLowerCase();
            int idVehicle = Integer.parseInt(matcher.group(4));

            Vehicle vehicle = vehiclesPerId.get(idVehicle);
            if (vehicle == null) {
                System.err.println("⚠️ Vehicle amb ID " + idVehicle + " no trobat. Conductor ID " + id + " omès.");
                continue;
            }

            try {
                Conductor conductor;
                switch (tipus) {
                    case "vorac":
                        conductor = new ConductorVorac(id, nom, vehicle);
                        break;
                    case "planificador":
                        if (matcher.group(5) == null) {
                            System.err.println("⚠️ Conductor planificador ID " + id + " sense ID_PARQUING_PRIVAT. Omès.");
                            continue;
                        }
                        int idParquing = Integer.parseInt(matcher.group(5));
                        Lloc lloc = llocsPerId.get(idParquing);
                        if (!(lloc instanceof Parquing)) {
                            System.err.println("⚠️ ID_PARQUING_PRIVAT " + idParquing + " no és un Parquing vàlid. Conductor ID " + id + " omès.");
                            continue;
                        }
                        conductor = new ConductorPlanificador(id, nom, vehicle, (Parquing) lloc);
                        break;
                    default:
                        System.err.println("⚠️ Tipus de conductor desconegut: " + tipus + ". Conductor ID " + id + " omès.");
                        continue;
                }
                conductors.add(conductor);
            } catch (Exception e) {
                System.err.println("⚠️ Error processant conductor ID " + id + ": " + e.getMessage());
            }
        }
        return conductors;
    }

    public static LocalTime[] carregarHorari(String pathFitxer) {
        String jsonContent = llegirFitxerComplet(pathFitxer);

        // Patró corregit per extreure horaInici i horaFinal
        Pattern pattern = Pattern.compile(
                "\"horaInici\"\\s*:\\s*\"([^\"]*)\"\\s*," // Nota: [^\"]* per capturar tot el temps
                + "\\s*\"horaFinal\"\\s*:\\s*\"([^\"]*)\"");

        Matcher matcher = pattern.matcher(jsonContent);

        if (matcher.find()) {
            try {
                LocalTime horaInici = LocalTime.parse(matcher.group(1));
                LocalTime horaFinal = LocalTime.parse(matcher.group(2));
                return new LocalTime[]{horaInici, horaFinal};
            } catch (Exception e) {
                System.err.println("Error parsejant hores: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("No s'ha trobat el patró d'horari al JSON");
            System.err.println("Contingut buscat: " + jsonContent);
        }
        return null;
    }

    public static List<Event> carregarEvents(String pathFitxer,
            Map<Integer, Vehicle> vehiclesPerId,
            Map<Integer, Conductor> conductorsPerId,
            Map<Integer, Lloc> llocsPerId) {
        List<Event> events = new ArrayList<>();
        String jsonContent = llegirFitxerComplet(pathFitxer);

        // Patrón mejorado para extraer el array de eventos completo
        Pattern arrayPattern = Pattern.compile("\"events\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher arrayMatcher = arrayPattern.matcher(jsonContent);

        if (!arrayMatcher.find()) {
            System.out.println("No se encontró el array de eventos en el JSON");
            return events;
        }

        String eventsContent = arrayMatcher.group(1);

        // Patrón para cada evento individual
        Pattern eventPattern = Pattern.compile(
                "\\{\\s*\"temps\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*"
                + "\"type\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*"
                + "(.*?)\\s*\\}(?=\\s*,\\s*\\{|\\s*\\]\\s*$)",
                Pattern.DOTALL);

        Matcher matcher = eventPattern.matcher(eventsContent);

        while (matcher.find()) {
            try {
                LocalTime temps = LocalTime.parse(matcher.group(1));
                String eventType = matcher.group(2);
                String eventData = matcher.group(3).trim();

                System.out.println("Procesando evento tipo: " + eventType + " con datos: " + eventData);

                Event event = crearEventFromData(eventType, temps, eventData,
                        vehiclesPerId, conductorsPerId, llocsPerId);
                if (event != null) {
                    events.add(event);
                }
            } catch (Exception e) {
                System.err.println("Error parsejant event: " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Total eventos leídos: " + events.size());
        return events;
    }

    private static Event crearEventFromData(String eventType, LocalTime temps, String eventData,
            Map<Integer, Vehicle> vehiclesPerId,
            Map<Integer, Conductor> conductorsPerId,
            Map<Integer, Lloc> llocsPerId) {
        try {
            switch (eventType) {
                case "MoureVehicle":
                    return parseMoureVehicleEvent(temps, eventData, vehiclesPerId, llocsPerId);
                case "DeixarPassatgers":
                    return parseDeixarPassatgersEvent(temps, eventData, conductorsPerId, llocsPerId);
                case "FiRuta":
                    return parseFiRutaEvent(temps, eventData, conductorsPerId);
                case "IniciRuta":
                    return parseIniciRutaEvent(temps, eventData, conductorsPerId, vehiclesPerId, llocsPerId);
                default:
                    System.err.println("Tipus d'event desconegut: " + eventType);
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Error creant event " + eventType + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

//METODES PEL PARSEIG D EVENTS------------------------------------------------------------------
    private static Event parseRecollirPassatgersEvent(LocalTime temps, String data,
            Map<Integer, Conductor> conductorsPerId,
            Map<Integer, Lloc> llocsPerId) {
        Pattern p = Pattern.compile(
                "\"conductorId\"\\s*:\\s*(\\d+)\\s*,\\s*"
                + "\"destiId\"\\s*:\\s*(\\d+)\\s*,\\s*"
                + "\"passatgersRecollits\"\\s*:\\s*(\\d+)");

        Matcher m = p.matcher(data);
        if (m.find()) {
            Conductor conductor = conductorsPerId.get(Integer.parseInt(m.group(1)));
            Lloc desti = llocsPerId.get(Integer.parseInt(m.group(2)));
            int passatgers = Integer.parseInt(m.group(3));

            if (conductor != null && desti != null) {
                return new RecollirPassatgersEvent(temps, conductor, desti, passatgers);
            } else {
                if (conductor == null) {
                    System.err.println("Conductor no trobat amb ID: " + m.group(1));
                }
                if (desti == null) {
                    System.err.println("Lloc destí no trobat amb ID: " + m.group(2));
                }
            }
        }
        return null;
    }

    private static Event parseDeixarPassatgersEvent(LocalTime temps, String data,
            Map<Integer, Conductor> conductorsPerId,
            Map<Integer, Lloc> llocsPerId) {
        Pattern p = Pattern.compile(
                "\"conductorId\"\\s*:\\s*(\\d+)\\s*,\\s*"
                + "\"destiId\"\\s*:\\s*(\\d+)\\s*,\\s*"
                + "\"passatgersDeixats\"\\s*:\\s*(\\d+)");

        Matcher m = p.matcher(data);
        if (m.find()) {
            Conductor conductor = conductorsPerId.get(Integer.parseInt(m.group(1)));
            Lloc desti = llocsPerId.get(Integer.parseInt(m.group(2)));
            int passatgers = Integer.parseInt(m.group(3));

            if (conductor != null && desti != null) {
                return new DeixarPassatgersEvent(temps, conductor, desti, passatgers);
            } else {
                System.err.println("Datos incompletos para DeixarPassatgersEvent");
            }
        }
        return null;
    }

// [Els mètodes existents parseMoureVehicleEvent, parseIniciRutaEvent, etc. es mantenen iguals]
    // Métodos de parseo para cada tipo de evento
    private static Event parseMoureVehicleEvent(LocalTime temps, String data,
            Map<Integer, Vehicle> vehiclesPerId,
            Map<Integer, Lloc> llocsPerId) {
        Pattern p = Pattern.compile(
                "\"vehicleId\"\\s*:\\s*(\\d+)\\s*,\\s*"
                + "\"origenId\"\\s*:\\s*(\\d+)\\s*,\\s*"
                + "\"destiId\"\\s*:\\s*(\\d+)\\s*,\\s*"
                + "\"distancia\"\\s*:\\s*(\\d+\\.?\\d*)");

        Matcher m = p.matcher(data);
        if (m.find()) {
            Vehicle vehicle = vehiclesPerId.get(Integer.parseInt(m.group(1)));
            Lloc origen = llocsPerId.get(Integer.parseInt(m.group(2)));
            Lloc desti = llocsPerId.get(Integer.parseInt(m.group(3)));
            double distancia = Double.parseDouble(m.group(4));

            if (vehicle != null && origen != null && desti != null) {
                return new MoureVehicleEvent(temps, vehicle, origen, desti, distancia);
            } else {
                System.err.println("Datos incompletos para MoureVehicleEvent");
            }
        }
        return null;
    }

    private static Event parseIniciRutaEvent(LocalTime temps, String data,
            Map<Integer, Conductor> conductorsPerId,
            Map<Integer, Vehicle> vehiclesPerId,
            Map<Integer, Lloc> llocsPerId) {
        Pattern p = Pattern.compile(
                "\"conductorId\"\\s*:\\s*(\\d+)\\s*,\\s*"
                + "\"vehicleId\"\\s*:\\s*(\\d+)\\s*,\\s*"
                + "\"ruta\"\\s*:\\s*\\{\\s*"
                + "\"llocs\"\\s*:\\s*\\[(.*?)\\]\\s*,\\s*"
                + "\"distanciaTotal\"\\s*:\\s*(\\d+\\.?\\d*)\\s*,\\s*"
                + "\"tempsTotal\"\\s*:\\s*(\\d+\\.?\\d*)\\s*,\\s*"
                + "\"horaInici\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*"
                + "\"esRutaCarrega\"\\s*:\\s*(true|false)\\s*\\}");

        Matcher m = p.matcher(data);
        if (m.find()) {
            try {
                Conductor conductor = conductorsPerId.get(Integer.parseInt(m.group(1)));
                Vehicle vehicle = vehiclesPerId.get(Integer.parseInt(m.group(2)));

                // Procesar lista de ubicaciones
                String[] llocsArray = m.group(3).split("\\s*,\\s*");
                List<Lloc> llocsRuta = new ArrayList<>();
                for (String idStr : llocsArray) {
                    try {
                        int id = Integer.parseInt(idStr.trim());
                        Lloc lloc = llocsPerId.get(id);
                        if (lloc != null) {
                            llocsRuta.add(lloc);
                        } else {
                            System.err.println("Lloc no encontrado con ID: " + id);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsejant ID de lloc: " + idStr);
                    }
                }

                double distanciaTotal = Double.parseDouble(m.group(4));
                double tempsTotal = Double.parseDouble(m.group(5));
                LocalTime horaInici = LocalTime.parse(m.group(6));
                boolean esRutaCarrega = Boolean.parseBoolean(m.group(7));

                if (conductor != null && vehicle != null && !llocsRuta.isEmpty()) {
                    Ruta ruta = new Ruta();
                    ruta.setLlocs(llocsRuta);
                    ruta.setDistanciaTotal(distanciaTotal);
                    ruta.setTempsTotal(tempsTotal);
                    ruta.setHoraInici(horaInici);
                    ruta.setEsRutaCarrega(esRutaCarrega);

                    return new IniciRutaEvent(temps, conductor, vehicle, ruta);
                } else {
                    System.err.println("Datos incompletos para IniciRutaEvent");
                }
            } catch (Exception e) {
                System.err.println("Error parsejant IniciRutaEvent: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    private static Event parseFiRutaEvent(LocalTime temps, String data,
            Map<Integer, Conductor> conductorsPerId) {
        Pattern p = Pattern.compile(
                "\"conductorId\"\\s*:\\s*(\\d+)");

        Matcher m = p.matcher(data);
        if (m.find()) {
            Conductor conductor = conductorsPerId.get(Integer.parseInt(m.group(1)));
            if (conductor != null) {
                return new FiRutaEvent(temps, conductor, null);
            } else {
                System.err.println("Conductor no encontrado para FiRutaEvent");
            }
        }
        return null;
    }

    private static Event parseFiCarregaEvent(LocalTime temps, String data,
            Map<Integer, Conductor> conductorsPerId) {
        Pattern p = Pattern.compile(
                "\"conductorId\"\\s*:\\s*(\\d+)");

        Matcher m = p.matcher(data);
        if (m.find()) {
            int conductorId = Integer.parseInt(m.group(1));
            Conductor conductor = conductorsPerId.get(conductorId);

            if (conductor != null) {
                return new FiCarregaEvent(temps, conductor);
            } else {
                System.err.println("Conductor no trobat amb ID: " + conductorId);
            }
        }
        return null;
    }

    private static Event parseCarregarBateriaEvent(LocalTime temps, String data,
            Map<Integer, Vehicle> vehiclesPerId,
            Map<Integer, Conductor> conductorsPerId) {
        Pattern p = Pattern.compile(
                "\"vehicleId\"\\s*:\\s*(\\d+)\\s*,"
                + "\\s*\"duracioCarregaMinuts\"\\s*:\\s*(\\d+\\.?\\d*)\\s*,"
                + "\\s*\"conductorId\"\\s*:\\s*(\\d+)");

        Matcher m = p.matcher(data);
        if (m.find()) {
            int vehicleId = Integer.parseInt(m.group(1));
            double duracio = Double.parseDouble(m.group(2));
            int conductorId = Integer.parseInt(m.group(3));

            Vehicle vehicle = vehiclesPerId.get(vehicleId);
            Conductor conductor = conductorsPerId.get(conductorId);

            if (vehicle != null && conductor != null) {
                return new CarregarBateriaEvent(temps, vehicle, duracio, conductor);
            } else {
                if (vehicle == null) {
                    System.err.println("Vehicle no trobat amb ID: " + vehicleId);
                }
                if (conductor == null) {
                    System.err.println("Conductor no trobat amb ID: " + conductorId);
                }
            }
        }
        return null;
    }

    // METODES D ESCRIPTURA
    public static void writeJsonFile(List<Conductor> conductors, List<Vehicle> vehicles, List<Lloc> llocs,
            List<Cami> connexions, List<Peticio> peticions,
            Estadistiques estadistiques, PriorityQueue<Event> events, String filePath) throws IOException {

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n");

        // 1. Escribir "llocs" (se mantiene igual)
        jsonBuilder.append("  \"llocs\": [\n");
        for (int i = 0; i < llocs.size(); i++) {
            Lloc lloc = llocs.get(i);
            jsonBuilder.append("    {\n");
            jsonBuilder.append("      \"ID\": ").append(lloc.obtenirId()).append(",\n");
            if (lloc instanceof Parquing) {
                Parquing p = (Parquing) lloc;
                jsonBuilder.append("      \"TIPUS\": \"").append("P").append("\",\n");
                jsonBuilder.append("      \"N_CARREGADORS\": ").append(p.obtenirPuntsCarregaPublics()).append(",\n");
                jsonBuilder.append("      \"N_CARREGADORS_PRIVATS\": ").append(p.obtenirPuntsCarregaPrivats())
                        .append(",\n");
                jsonBuilder.append("      \"MAX_VEHICLES\": ").append(p.obtenirCapacitatMaxima()).append("\n");
            } else {
                jsonBuilder.append("      \"TIPUS\": \"").append("L").append("\"\n");
            }
            jsonBuilder.append("    }").append(i < llocs.size() - 1 ? ",\n" : "\n");
        }
        jsonBuilder.append("  ],\n");

        // 2. Escribir "connexions" (se mantiene igual)
        jsonBuilder.append("  \"connexions\": [\n");
        boolean first = true;
        for (Object obj : connexions) {
            if (obj instanceof Cami con) {
                if (!first) {
                    jsonBuilder.append(",\n");
                }
                jsonBuilder.append("    {\n");
                jsonBuilder.append("      \"ORIGEN\": ").append(con.obtenirOrigen().obtenirId()).append(",\n");
                jsonBuilder.append("      \"DESTI\": ").append(con.obtenirDesti().obtenirId()).append(",\n");
                jsonBuilder.append("      \"DISTANCIA_KM\": ").append(con.obtenirDistancia()).append(",\n");
                jsonBuilder.append("      \"TEMPS_MIN\": ").append(con.obtenirTemps()).append("\n");
                jsonBuilder.append("    }");
                first = false;
            } else {
                System.err.println("⚠️ Objecte no vàlid dins la llista de connexions: " + obj.getClass());
            }
        }
        jsonBuilder.append("\n  ],\n");

        // 3. Escribir "vehicles" (se mantiene igual)
        jsonBuilder.append("  \"vehicles\": [\n");
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicle = vehicles.get(i);
            jsonBuilder.append("    {\n");
            jsonBuilder.append("      \"ID\": ").append(vehicle.getId()).append(",\n");
            jsonBuilder.append("      \"ID_UBICACIO\": ").append(vehicle.getUbicacioActual().obtenirId()).append(",\n");
            jsonBuilder.append("      \"AUTONOMIA_KM\": ").append(vehicle.AUTONOMIA).append(",\n");
            jsonBuilder.append("      \"MAX_PASSATGERS\": ").append(vehicle.getMaxPassatgers()).append(",\n");
            jsonBuilder.append("      \"TEMPS_CARGA_RAPIDA\": ").append(vehicle.TEMPSCARGARAPIDA).append(",\n");
            jsonBuilder.append("      \"TEMPS_CARGA_LENTA\": ").append(vehicle.TEMPSCARGALENTA).append("\n");
            jsonBuilder.append("    }").append(i < vehicles.size() - 1 ? ",\n" : "\n");
        }
        jsonBuilder.append("  ],\n");

        // 4. Escribir "conductors" (se mantiene igual)
        jsonBuilder.append("  \"conductors\": [\n");
        for (int i = 0; i < conductors.size(); i++) {
            Conductor conductor = conductors.get(i);
            jsonBuilder.append("    {\n");
            jsonBuilder.append("      \"ID\": ").append(conductor.getId()).append(",\n");
            jsonBuilder.append("      \"NOM\": \"").append(conductor.nom).append("\",\n");
            if (conductor instanceof ConductorPlanificador) {
                ConductorPlanificador conductorPlan = (ConductorPlanificador) conductor;
                jsonBuilder.append("      \"TIPUS\": \"").append("planificador").append("\",\n");
                jsonBuilder.append("      \"IDVEHICLE\": ").append(conductor.getVehicle().getId()).append(",\n");
                jsonBuilder.append("      \"ID_PARQUING_PRIVAT\": ")
                        .append(conductorPlan.getParquingPrivat().obtenirId()).append("\n");
            } else {
                jsonBuilder.append("      \"TIPUS\": \"").append("vorac").append("\",\n");
                jsonBuilder.append("      \"IDVEHICLE\": ").append(conductor.getVehicle().getId()).append("\n");
            }
            jsonBuilder.append("    }").append(i < conductors.size() - 1 ? ",\n" : "\n");
        }
        jsonBuilder.append("  ],\n");

        // 5. Escribir "peticions" (se mantiene igual)
        jsonBuilder.append("  \"peticions\": [\n");
        for (int i = 0; i < peticions.size(); i++) {
            Peticio peticio = peticions.get(i);
            jsonBuilder.append("    {\n");
            jsonBuilder.append("      \"ID\": ").append(peticio.obtenirId()).append(",\n");
            jsonBuilder.append("      \"ORIGEN\": ").append(peticio.obtenirOrigen().obtenirId()).append(",\n");
            jsonBuilder.append("      \"DESTI\": ").append(peticio.obtenirDesti().obtenirId()).append(",\n");
            jsonBuilder.append("      \"HORA_MIN_RECOLLIDA\": \"").append(peticio.obtenirHoraMinimaRecollida())
                    .append("\",\n");
            jsonBuilder.append("      \"HORA_MAX_ARRIBADA\": \"").append(peticio.obtenirHoraMaximaArribada())
                    .append("\",\n");
            jsonBuilder.append("      \"NUM_PASSATGERS\": ").append(peticio.obtenirNumPassatgers()).append(",\n");
            jsonBuilder.append("      \"VEHICLE_COMPARTIT\": ").append(peticio.esVehicleCompartit()).append("\n");
            jsonBuilder.append("    }").append(i < peticions.size() - 1 ? ",\n" : "\n");
        }
        jsonBuilder.append("  ],\n");

        // 6. Escribir "horaInici" y "horaFinal" (se mantiene igual)
        jsonBuilder.append("  \"horaInici\": \"08:00\",\n");
        jsonBuilder.append("  \"horaFinal\": \"20:00\",\n");

        // 8. Escribir "events"
        jsonBuilder.append("  \"events\": [\n");

// Convert PriorityQueue to a list while preserving order
        List<Event> eventsList = new ArrayList<>();
        while (!events.isEmpty()) {
            eventsList.add(events.poll());
        }

        for (int i = 0; i < eventsList.size(); i++) {
            Event event = eventsList.get(i);
            jsonBuilder.append("    {\n");
            jsonBuilder.append("      \"temps\": \"").append(event.getTemps()).append("\"");

            // Determinar el tipo de evento y sus campos específicos
            if (event instanceof MoureVehicleEvent) {
                MoureVehicleEvent mve = (MoureVehicleEvent) event;
                jsonBuilder.append(",\n      \"type\": \"MoureVehicle\"");
                jsonBuilder.append(",\n      \"vehicleId\": ").append(mve.getVehicle().getId());
                jsonBuilder.append(",\n      \"origenId\": ").append(mve.getOrigen().obtenirId());
                jsonBuilder.append(",\n      \"destiId\": ").append(mve.getDesti().obtenirId());
                jsonBuilder.append(",\n      \"distancia\": ").append(mve.getDistancia());
            } else if (event instanceof IniciRutaEvent) {
                IniciRutaEvent ire = (IniciRutaEvent) event;
                jsonBuilder.append(",\n      \"type\": \"IniciRuta\"");
                jsonBuilder.append(",\n      \"conductorId\": ").append(ire.getConductor().getId());
                jsonBuilder.append(",\n      \"vehicleId\": ").append(ire.getVehicle().getId());
                jsonBuilder.append(",\n      \"ruta\": {\n");
                jsonBuilder.append("        \"llocs\": [");
                List<Lloc> llocsRuta = ire.getRuta().getLlocs();
                for (int j = 0; j < llocsRuta.size(); j++) {
                    jsonBuilder.append(llocsRuta.get(j).obtenirId());
                    if (j < llocsRuta.size() - 1) {
                        jsonBuilder.append(", ");
                    }
                }
                jsonBuilder.append("],\n");
                jsonBuilder.append("        \"distanciaTotal\": ").append(ire.getRuta().obtenirDistanciaTotal()).append(",\n");
                jsonBuilder.append("        \"tempsTotal\": ").append(ire.getRuta().obtenirTempsTotal()).append(",\n");
                jsonBuilder.append("        \"horaInici\": \"").append(ire.getRuta().getHoraInici()).append("\",\n");
                jsonBuilder.append("        \"esRutaCarrega\": ").append(ire.getRuta().isRutaCarrega()).append("\n");
                jsonBuilder.append("      }");
            } else if (event instanceof FiRutaEvent) {
                FiRutaEvent fre = (FiRutaEvent) event;
                jsonBuilder.append(",\n      \"type\": \"FiRuta\"");
                jsonBuilder.append(",\n      \"conductorId\": ").append(fre.getConductor().getId());
            } else if (event instanceof FiCarregaEvent) {
                FiCarregaEvent fce = (FiCarregaEvent) event;
                jsonBuilder.append(",\n      \"type\": \"FiCarrega\"");
                jsonBuilder.append(",\n      \"conductorId\": ").append(fce.getConductor().getId());
            } else if (event instanceof CarregarBateriaEvent) {
                CarregarBateriaEvent cbe = (CarregarBateriaEvent) event;
                jsonBuilder.append(",\n      \"type\": \"CarregarBateria\"");
                jsonBuilder.append(",\n      \"vehicleId\": ").append(cbe.getVehicle().getId());
                jsonBuilder.append(",\n      \"duracioCarregaMinuts\": ").append(cbe.getDuracioCarregaMinuts());
                jsonBuilder.append(",\n      \"conductorId\": ").append(cbe.getConductor().getId());
            } else if (event instanceof RecollirPassatgersEvent) {
                RecollirPassatgersEvent rpe = (RecollirPassatgersEvent) event;
                jsonBuilder.append(",\n      \"type\": \"RecollirPassatgers\"");
                jsonBuilder.append(",\n      \"conductorId\": ").append(rpe.getConductor().getId());
                jsonBuilder.append(",\n      \"destiId\": ").append(rpe.getDesti().obtenirId());
                jsonBuilder.append(",\n      \"passatgersRecollits\": ").append(rpe.getPassatgersRecollits());
            } else if (event instanceof DeixarPassatgersEvent) {
                DeixarPassatgersEvent dpe = (DeixarPassatgersEvent) event;
                jsonBuilder.append(",\n      \"type\": \"DeixarPassatgers\"");
                jsonBuilder.append(",\n      \"conductorId\": ").append(dpe.getConductor().getId());
                jsonBuilder.append(",\n      \"destiId\": ").append(dpe.getDesti().obtenirId());
                jsonBuilder.append(",\n      \"passatgersDeixats\": ").append(dpe.getPassatgersDeixats());
            }

            jsonBuilder.append("\n    }").append(i < eventsList.size() - 1 ? ",\n" : "\n");
        }
        jsonBuilder.append("  ]\n");  // Cierre del array events
        jsonBuilder.append("}\n");    // Cierre del objeto JSON principal

// Escribir en el archivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(jsonBuilder.toString());
        }
    }

    public static List<Peticio> carregarPeticions(String pathFitxer, Map<Integer, Lloc> llocsPerId) {
        List<Peticio> peticions = new ArrayList<>();
        String jsonContent = llegirFitxerComplet(pathFitxer);

        Pattern pattern = Pattern.compile(
                "\\{\\s*\"ID\"\\s*:\\s*\"?(\\d+)\"?\\s*,"
                + "\\s*\"ORIGEN\"\\s*:\\s*\"?(\\d+)\"?\\s*,"
                + "\\s*\"DESTI\"\\s*:\\s*\"?(\\d+)\"?\\s*,"
                + "\\s*\"HORA_MIN_RECOLLIDA\"\\s*:\\s*\"([^\"]+)\"\\s*,"
                + "\\s*\"HORA_MAX_ARRIBADA\"\\s*:\\s*\"([^\"]+)\"\\s*,"
                + "\\s*\"NUM_PASSATGERS\"\\s*:\\s*(\\d+)\\s*,"
                + "\\s*\"VEHICLE_COMPARTIT\"\\s*:\\s*(true|false)\\s*\\}");

        Matcher matcher = pattern.matcher(jsonContent);

        while (matcher.find()) {
            int id = Integer.parseInt(matcher.group(1));
            int origenId = Integer.parseInt(matcher.group(2));
            int destiId = Integer.parseInt(matcher.group(3));
            LocalTime horaMinRecollida = LocalTime.parse(matcher.group(4));
            LocalTime horaMaxArribada = LocalTime.parse(matcher.group(5));
            int numPassatgers = Integer.parseInt(matcher.group(6));
            boolean vehicleCompartit = Boolean.parseBoolean(matcher.group(7));

            Lloc origen = llocsPerId.get(origenId);
            Lloc desti = llocsPerId.get(destiId);

            if (origen != null && desti != null) {
                peticions.add(new Peticio(id, origen, desti, horaMinRecollida,
                        horaMaxArribada, numPassatgers, vehicleCompartit));
            }
        }
        return peticions;
    }

    //ESCRIPTOR PER ESTADISTIQUES
    public static void writeEstadistiques(String absolutePath, Estadistiques estadistiques) {
        try {
            Path path;
            path = Paths.get(absolutePath);
            String jsonContent;

            // Comprovem si el fitxer existeix
            boolean fileExists = Files.exists(path);

            if (fileExists) {
                // Llegim el contingut existent
                jsonContent = new String(Files.readAllBytes(path));

                // Patró per trobar l'array d'estadístiques
                Pattern pattern = Pattern.compile("(\"estadisticas\"\\s*:\\s*\\[)(.*?)(\\])", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(jsonContent);

                if (matcher.find()) {
                    // Preparem el nou bloc d'estadístiques
                    String newStat = crearBloqueEstadisticas(estadistiques);

                    // Afegim la nova estadística a l'array existent
                    String updatedContent;
                    if (matcher.group(2).trim().isEmpty()) {
                        updatedContent = matcher.group(1) + newStat + matcher.group(3);
                    } else {
                        updatedContent = matcher.group(1) + matcher.group(2) + ",\n" + newStat + matcher.group(3);
                    }

                    jsonContent = matcher.replaceFirst(updatedContent);
                } else {
                    // Si no existeix la secció d'estadístiques, la creem
                    jsonContent = jsonContent.replaceFirst(
                            "\\}",
                            ",\n\"estadisticas\": [\n" + crearBloqueEstadisticas(estadistiques) + "\n]\n}"
                    );
                }
            } else {
                // Creem un nou fitxer JSON amb les estadístiques
                jsonContent = "{\n\"estadisticas\": [\n" + crearBloqueEstadisticas(estadistiques) + "\n]\n}";
            }

            // Escrivim el contingut actualitzat al fitxer
            Files.write(path, jsonContent.getBytes());

        } catch (IOException e) {
            System.err.println("Error en escriure les estadístiques: " + e.getMessage());
            e.printStackTrace();
        }
    }

// Mètode auxiliar per crear el bloc JSON d'estadístiques
    private static String crearBloqueEstadisticas(Estadistiques estadistiques) {
        return String.format(
                "  {\n"
                + "    \"peticionesServidas\": %d,\n"
                + "    \"peticionesNoServidas\": %d,\n"
                + "    \"tiempoTotalEspera\": %.1f,\n"
                + "    \"tiempoMaximoEspera\": %.1f,\n"
                + "    \"ocupacionTotalVehiculos\": %.1f,\n"
                + "    \"muestrasOcupacion\": %d,\n"
                + "    \"porcentajeBateriaPromedio\": %.1f,\n"
                + "    \"muestrasBateria\": %d,\n"
                + "    \"tiempoTotalViaje\": %.2f,\n"
                + "    \"muestrasViaje\": %d\n"
                + "  }",
                estadistiques.getPeticionesServidas(),
                estadistiques.getPeticionesNoServidas(),
                estadistiques.getTiempoEsperaPromedio(),
                estadistiques.getTiempoMaximoEspera(),
                estadistiques.getOcupacionPromedioVehiculos(),
                estadistiques.getMuestrasOcupacion(),
                estadistiques.getPorcentajeBateriaPromedio(),
                estadistiques.getMuestrasBateria(),
                estadistiques.getTiempoViajePromedio(),
                estadistiques.getMuestrasViaje()
        );
    }

}
