import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Cami;
import core.Conductor;
import core.LectorCSV;
import core.Lloc;
import core.Mapa;
import core.Peticio;
import core.Simulador;
import core.Vehicle;

public class Main {

    public static Mapa carregarMapa(List<Lloc> llocs, List<Cami> camins) {
        Mapa mapa = new Mapa(llocs.size(), camins.size());

        for (Lloc lloc : llocs) {
            mapa.afegirLloc(lloc);
        }

        for (Cami cami : camins) {
            mapa.afegirCami(cami);
        }

        System.out.println("Mapa carregat correctament!");

        return mapa;
    }

    public static void main(String[] args) {

        if (args.length != 7) {
            System.err.println(
                    "Ús: java -jar av-simulador.jar <llocs.csv> <camins.csv> <vehicles.csv> <conductors.csv> <peticions.csv> <hora_inici> <hora_final>");
            System.exit(1);
        }

        // Llegim els noms dels fitxers i hores des dels arguments
        String fitxerLlocs = args[0];
        String fitxerCamins = args[1];
        String fitxerVehicles = args[2];
        String fitxerConductors = args[3];
        String fitxerPeticions = args[4];
        LocalTime horaInici = LocalTime.parse(args[5]);
        LocalTime horaFinal = LocalTime.parse(args[6]);

        // Carreguem els llocs
        List<Lloc> llocs = LectorCSV.carregarLlocs(fitxerLlocs);

        // Creem un map per accedir als llocs per id
        Map<Integer, Lloc> llocsPerId = new HashMap<>();
        for (Lloc l : llocs)
            llocsPerId.put(l.obtenirId(), l);

        // Carreguem els camins
        List<Cami> camins = LectorCSV.carregarCamins(fitxerCamins, llocsPerId);

        // Creem el mapa
        Mapa mapa = carregarMapa(llocs, camins);

        // Carreguem els vehicles
        List<Vehicle> vehicles = LectorCSV.carregarVehicles(fitxerVehicles, llocsPerId);

        // Creem un map per accedir als vehicles per id
        Map<Integer, Vehicle> vehiclesPerId = new HashMap<>();
        for (Vehicle v : vehicles)
            vehiclesPerId.put(v.getId(), v);

        // Carreguem els conductors
        List<Conductor> conductors = LectorCSV.carregarConductors(fitxerConductors, vehiclesPerId);

        // Carreguem les peticions
        List<Peticio> peticions = LectorCSV.carregarPeticions(fitxerPeticions, llocsPerId);

        System.out.println("Dades carregades correctament.");
        System.out.println("Hora d'inici: " + horaInici);
        System.out.println("Hora final: " + horaFinal);

        Simulador simulador = new Simulador(horaInici, horaFinal, mapa, vehicles, conductors, peticions);
        simulador.iniciar();

    }
}
