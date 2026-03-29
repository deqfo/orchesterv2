package orchester.service;

import orchester.models.Nastroj;
import orchester.models.SlacikovyNastroj;
import orchester.models.StrunovyNastroj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Service {

    private List<Nastroj> nastroje = new ArrayList<>();

    public List<Nastroj> getNastroje() {
        return nastroje;
    }

    public void addNastroj(Nastroj nastroj) {
        nastroje.add(nastroj);
    }

    public void updateNastroj(int index, Nastroj nastroj) {
        nastroje.set(index, nastroj);
    }

    public void removeNastroj(Nastroj nastroj) {
        nastroje.remove(nastroj);
    }

    public void loadFromResource(String resourcePath) throws IOException {
        InputStream inputStream = Service.class.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("Subor sa nenasiel: " + resourcePath);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        loadFromReader(reader);
        reader.close();
    }

    public void loadFromFile(Path path) throws IOException {
        BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
        loadFromReader(reader);
        reader.close();
    }

    public void saveToFile(Path path) throws IOException {
        PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8));

        for (Nastroj nastroj : nastroje) {
            writer.println(serialize(nastroj));
        }

        writer.close();
    }

    public String getTyp(Nastroj nastroj) {
        if (nastroj instanceof SlacikovyNastroj) {
            return "Slacikovy";
        } else {
            return "Strunovy";
        }
    }

    public String getDetail(Nastroj nastroj) {
        if (nastroj instanceof SlacikovyNastroj) {
            SlacikovyNastroj s = (SlacikovyNastroj) nastroj;
            return "Sekcia: " + s.getSekcia();
        } else {
            StrunovyNastroj s = (StrunovyNastroj) nastroj;
            return "Struny: " + s.getPocetStrun() + ", ladenie: " + s.getLadenie();
        }
    }

    private void loadFromReader(BufferedReader reader) throws IOException {
        nastroje.clear();

        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.isBlank()) {
                nastroje.add(parseLine(line));
            }
        }
    }

    private Nastroj parseLine(String line) {
        String[] parts = line.split(";");

        if (parts[0].equals("Slacikovy")) {
            if (parts.length < 6) {
                throw new IllegalArgumentException("Chyba sekcia pre riadok: " + line);
            }

            String druh = parts[1];
            double cena = Double.parseDouble(parts[2]);
            String zvuk = parts[3];
            int pocet = Integer.parseInt(parts[4]);
            String sekcia = parts[5];

            return new SlacikovyNastroj(druh, cena, zvuk, pocet, sekcia);
        }

        if (parts[0].equals("Strunovy")) {
            if (parts.length < 7) {
                throw new IllegalArgumentException("Chybaju data pre riadok: " + line);
            }

            String druh = parts[1];
            double cena = Double.parseDouble(parts[2]);
            String zvuk = parts[3];
            int pocet = Integer.parseInt(parts[4]);
            int pocetStrun = Integer.parseInt(parts[5]);
            String ladenie = parts[6];

            return new StrunovyNastroj(druh, cena, zvuk, pocet, pocetStrun, ladenie);
        }

        throw new IllegalArgumentException("Neznamy typ nastroja: " + line);
    }

    private String serialize(Nastroj nastroj) {
        if (nastroj instanceof SlacikovyNastroj) {
            SlacikovyNastroj s = (SlacikovyNastroj) nastroj;
            return "Slacikovy;" + s.getDruh() + ";" + s.getCena() + ";" + s.getZvuk() + ";" + s.getPocet() + ";" + s.getSekcia();
        } else {
            StrunovyNastroj s = (StrunovyNastroj) nastroj;
            return "Strunovy;" + s.getDruh() + ";" + s.getCena() + ";" + s.getZvuk() + ";" + s.getPocet() + ";" + s.getPocetStrun() + ";" + s.getLadenie();
        }
    }
}
