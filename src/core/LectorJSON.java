package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import events.CarregarBateriaEvent;
import events.Event;
import events.FiCarregaEvent;
import events.FiRutaEvent;
import events.IniciRutaEvent;
import events.MoureVehicleEvent;

public class LectorJSON {

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

        // Patró per extreure cada objecte lloc
        Pattern pattern = Pattern.compile(
                "\\{\\s*\"ID\"\\s*:\\s*\"?(\\d+)\"?\\s*,"
                        + "\\s*\"TIPUS\"\\s*:\\s*(\\d+)\\s*,"
                        + "\\s*\"N_CARREGADORS\"\\s*:\\s*\"([^\"]+)\"\\s*,"
                        + "\\s*\"N_CARREGADORS_PRIVATS\"\\s*:\\s*(\\d+)\\s*,"
                        + "\\s*\"MAX_VEHICLES\"\\s*:\\s*(\\d+)\\s*\\}");

        Matcher matcher = pattern.matcher(jsonContent);

        while (matcher.find()) {
            int id = Integer.parseInt(matcher.group(1));
            String tipus = matcher.group(2);

            if (tipus.equals("L")) {
                llocs.add(new Lloc(id));
            } else if (tipus.equals("P")) {

                int maxVehicles = Integer.parseInt(matcher.group(5));
                int nCarregadors = Integer.parseInt(matcher.group(3));
                int nCarregadorsPrivats = Integer.parseInt(matcher.group(4));

                List<PuntCarrega> puntsCarregaPublics = new ArrayList<>();
                List<PuntCarrega> puntsCarregaPrivats = new ArrayList<>();

                for (int j = 0; j < nCarregadors; j++) {
                    puntsCarregaPublics.add(new PuntCarrega(
                            j % 2 == 0 ? TipusPuntCarrega.CARGA_RAPIDA : TipusPuntCarrega.CARGA_LENTA));
                }
                for (int j = 0; j < nCarregadorsPrivats; j++) {
                    puntsCarregaPrivats.add(new PuntCarrega(
                            j % 2 == 0 ? TipusPuntCarrega.CARGA_RAPIDA : TipusPuntCarrega.CARGA_LENTA));
                }
                llocs.add(new Parquing(id, maxVehicles, puntsCarregaPublics, puntsCarregaPrivats));
            }
        }
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

    public static List<Conductor> carregarConductors(String pathFitxer, Map<Integer, Vehicle> vehiclesPerId,
            Map<Integer, Lloc> llocsPerId) {
        List<Conductor> conductors = new ArrayList<>();
        String jsonContent = llegirFitxerComplet(pathFitxer);

        Pattern pattern = Pattern.compile(
                "\\{\\s*\"ID\"\\s*:\\s*\"?(\\d+)\"?\\s*,\\s*"
                        + "\"NOM\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*"
                        + "\"TIPUS\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*"
                        + "\"IDVEHICLE\"\\s*:\\s*\"?(\\d+)\"?\\s*,\\s*"
                        + "\"ID_PARQUING_PRIVAT\"\\s*:\\s*\"?(\\d+)\"?\\s*\\}");

        Matcher matcher = pattern.matcher(jsonContent);

        while (matcher.find()) {
            int id = Integer.parseInt(matcher.group(1));
            String nom = matcher.group(2);
            String tipus = matcher.group(3).toLowerCase();
            int idVehicle = Integer.parseInt(matcher.group(4));
            int idParquing = Integer.parseInt(matcher.group(5));

            Vehicle vehicle = vehiclesPerId.get(idVehicle);
            if (vehicle == null) {
                System.err.println("Vehicle amb ID " + idVehicle + " no trobat. Conductor ID " + id + " omès.");
                continue;
            }

            Conductor conductor;
            switch (tipus) {
                case "voraç":
                    conductor = new ConductorVoraç(id, nom, vehicle);
                    break;
                case "planificador":
                    Parquing parquing = (Parquing) llocsPerId.get(idParquing);

                    conductor = new ConductorPlanificador(id, nom, vehicle, parquing);
                    break;
                default:
                    System.err.println("Tipus de conductor desconegut: " + tipus);
                    continue;
            }
            conductors.add(conductor);
        }
        return conductors;
    }

    public static LocalTime[] carregarHorari(String pathFitxer) {
        String jsonContent = llegirFitxerComplet(pathFitxer);

        // Patró per extreure horaInici i horaFinal
        Pattern pattern = Pattern.compile(
                "\"horaInici\"\\s*:\\s*\"([^\"])\"\\s,"
                        + "\\s*\"horaFinal\"\\s*:\\s*\"([^\"]*)\"");

        Matcher matcher = pattern.matcher(jsonContent);

        if (matcher.find()) {
            try {
                LocalTime horaInici = LocalTime.parse(matcher.group(1));
                LocalTime horaFinal = LocalTime.parse(matcher.group(2));
                return new LocalTime[] { horaInici, horaFinal };
            } catch (Exception e) {
                System.err.println("Error parsejant hores: " + e.getMessage());
            }
        }
        return null;
    }

    public static List<Event> carregarEvents(String pathFitxer,
            Map<Integer, Vehicle> vehiclesPerId,
            Map<Integer, Conductor> conductorsPerId,
            Map<Integer, Lloc> llocsPerId) {
        List<Event> events = new ArrayList<>();
        String jsonContent = llegirFitxerComplet(pathFitxer);

        // Patró general per extreure events
        Pattern eventPattern = Pattern.compile(
                "\\{\\s*\"type\"\\s*:\\s*\"([^\"]+)\"\\s*,"
                        + "\\s*\"temps\"\\s*:\\s*\"([^\"]+)\"\\s*,"
                        + "(.?)\\s\\}(?=\\s*,\\s*\\{|\\s*\\]\\s*$)");

        Matcher matcher = eventPattern.matcher(jsonContent);

        while (matcher.find()) {
            try {
                String eventType = matcher.group(1);
                LocalTime temps = LocalTime.parse(matcher.group(2));
                String eventData = matcher.group(3);

                Event event = crearEventFromData(eventType, temps, eventData,
                        vehiclesPerId, conductorsPerId, llocsPerId);
                if (event != null) {
                    events.add(event);
                }
            } catch (Exception e) {
                System.err.println("Error parsejant event: " + e.getMessage());
            }
        }

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
                case "IniciRuta":
                    return parseIniciRutaEvent(temps, eventData, conductorsPerId, vehiclesPerId, llocsPerId);
                case "FiRuta":
                    return parseFiRutaEvent(temps, eventData, conductorsPerId);
                case "FiCarrega":
                    return parseFiCarregaEvent(temps, eventData, conductorsPerId);
                case "CarregarBateria":
                    return parseCarregarBateriaEvent(temps, eventData, vehiclesPerId, conductorsPerId);
                default:
                    System.err.println("Tipus d'event desconegut: " + eventType);
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Error creant event " + eventType + ": " + e.getMessage());
            return null;
        }
    }

    // Métodos de parseo para cada tipo de evento
    private static Event parseMoureVehicleEvent(LocalTime temps, String data,
            Map<Integer, Vehicle> vehiclesPerId,
            Map<Integer, Lloc> llocsPerId) {
        Pattern p = Pattern.compile(
                "\"vehicleId\"\\s*:\\s*(\\d+)\\s*,"
                        + "\\s*\"origenId\"\\s*:\\s*(\\d+)\\s*,"
                        + "\\s*\"destiId\"\\s*:\\s*(\\d+)\\s*,"
                        + "\\s*\"distancia\"\\s*:\\s*(\\d+\\.?\\d*)");

        Matcher m = p.matcher(data);
        if (m.find()) {
            Vehicle vehicle = vehiclesPerId.get(Integer.parseInt(m.group(1)));
            Lloc origen = llocsPerId.get(Integer.parseInt(m.group(2)));
            Lloc desti = llocsPerId.get(Integer.parseInt(m.group(3)));
            double distancia = Double.parseDouble(m.group(4));

            if (vehicle != null && origen != null && desti != null) {
                return new MoureVehicleEvent(temps, vehicle, origen, desti, distancia);
            }
        }
        return null;
    }

    private static Event parseIniciRutaEvent(LocalTime temps, String data,
            Map<Integer, Conductor> conductorsPerId,
            Map<Integer, Vehicle> vehiclesPerId,
            Map<Integer, Lloc> llocsPerId) {

        // Patrón regex mejorado para capturar todos los campos
        Pattern p = Pattern.compile(
                "\"conductorId\"\\s*:\\s*(\\d+)\\s*,"
                        + "\\s*\"vehicleId\"\\s*:\\s*(\\d+)\\s*,"
                        + "\\s*\"ruta\"\\s*:\\s*\\{\\s*"
                        + "\"llocs\"\\s*:\\s*\\[(.?)\\]\\s,"
                        + "\\s*\"distanciaTotal\"\\s*:\\s*(\\d+\\.?\\d*)\\s*,"
                        + "\\s*\"tempsTotal\"\\s*:\\s*(\\d+\\.?\\d*)\\s*,"
                        + "\\s*\"horaInici\"\\s*:\\s*\"([^\"])\"\\s,"
                        + "\\s*\"esRutaCarrega\"\\s*:\\s*(true|false)\\s*\\}");

        Matcher m = p.matcher(data);
        if (m.find()) {
            try {
                // Parsear datos básicos
                Conductor conductor = conductorsPerId.get(Integer.parseInt(m.group(1)));
                Vehicle vehicle = vehiclesPerId.get(Integer.parseInt(m.group(2)));

                // Parsear lista de ubicaciones
                List<Lloc> llocsRuta = Arrays.stream(m.group(3).split("\\s*,\\s*"))
                        .map(Integer::parseInt)
                        .map(llocsPerId::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                // Validar que todos los puntos existen
                if (llocsRuta.size() != m.group(3).split(",").length) {
                    System.err.println("Algunos llocs de la ruta no existen");
                    return null;
                }

                // Parsear resto de campos de la ruta
                double distanciaTotal = Double.parseDouble(m.group(4));
                double tempsTotal = Double.parseDouble(m.group(5));
                LocalTime horaInici = LocalTime.parse(m.group(6));
                boolean esRutaCarrega = Boolean.parseBoolean(m.group(7));

                if (conductor != null && vehicle != null && !llocsRuta.isEmpty()) {
                    // Crear la ruta completa
                    Ruta ruta = new Ruta();
                    ruta.setLlocs(llocsRuta);
                    ruta.setDistanciaTotal(distanciaTotal);
                    ruta.setTempsTotal(tempsTotal);
                    ruta.setHoraInici(horaInici);
                    ruta.setConductor(conductor);
                    ruta.setEsRutaCarrega(esRutaCarrega);

                    return new IniciRutaEvent(temps, conductor, vehicle, ruta);
                }
            } catch (Exception e) {
                System.err.println("Error parsejant IniciRutaEvent: " + e.getMessage());
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
            int conductorId = Integer.parseInt(m.group(1));
            Conductor conductor = conductorsPerId.get(conductorId);

            if (conductor != null) {
                return new FiRutaEvent(temps, conductor, null);
            } else {
                System.err.println("Conductor no trobat amb ID: " + conductorId);
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
            List<Cami> connexions, List<Peticio> peticions, String filePath) throws IOException {

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n");

        // 1. Escribir "llocs"
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

        // 2. Escribir "connexions"
        jsonBuilder.append("  \"connexions\": [\n");
        for (int i = 0; i < connexions.size(); i++) {
            Cami con = connexions.get(i);
            jsonBuilder.append("    {\n");
            jsonBuilder.append("      \"ORIGEN\": ").append(con.obtenirOrigen().obtenirId()).append(",\n");
            jsonBuilder.append("      \"DESTI\": ").append(con.obtenirDesti().obtenirId()).append(",\n");
            jsonBuilder.append("      \"DISTANCIA_KM\": ").append(con.obtenirDistancia()).append(",\n");
            jsonBuilder.append("      \"TEMPS_MIN\": ").append(con.obtenirTemps()).append("\n");
            jsonBuilder.append("    }").append(i < connexions.size() - 1 ? ",\n" : "\n");
        }
        jsonBuilder.append("  ],\n");

        // 3. Escribir "vehicles"
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

        // 4. Escribir "conductors"
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
                jsonBuilder.append("      \"TIPUS\": \"").append("voraç").append("\",\n");
                jsonBuilder.append("      \"IDVEHICLE\": ").append(conductor.getVehicle().getId()).append("\n");
            }
            jsonBuilder.append("    }").append(i < conductors.size() - 1 ? ",\n" : "\n");
        }
        jsonBuilder.append("  ],\n");

        // 5. Escribir "peticions" en el format especificat
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

        // 6. Escribir "horaInici" y "horaFinal" (valores fijos o personalizables)
        jsonBuilder.append("  \"horaInici\": \"08:00\",\n");
        jsonBuilder.append("  \"horaFinal\": \"20:00\",\n");

        // 7. Escribir "events" (ejemplo básico, puedes adaptarlo)
        jsonBuilder.append("  \"events\": [\n");
        jsonBuilder.append("    {\n");
        jsonBuilder.append("      \"type\": \"MoureVehicle\",\n");
        jsonBuilder.append("      \"temps\": \"08:00\",\n");
        jsonBuilder.append("      \"vehicleId\": 1,\n");
        jsonBuilder.append("      \"origenId\": 101,\n");
        jsonBuilder.append("      \"destiId\": 102,\n");
        jsonBuilder.append("      \"distancia\": 5.2\n");
        jsonBuilder.append("    }\n");
        jsonBuilder.append("  ]\n");

        jsonBuilder.append("}");

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

}