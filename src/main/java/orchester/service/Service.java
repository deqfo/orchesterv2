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
        } else if (nastroj instanceof StrunovyNastroj) {
            return "Strunovy";
        } else {
            return "Zakladny";
        }
    }

    public String getDetail(Nastroj nastroj) {
        if (nastroj instanceof SlacikovyNastroj) {
            SlacikovyNastroj s = (SlacikovyNastroj) nastroj;
            return "Sekcia: " + s.getSekcia();
        } else if (nastroj instanceof StrunovyNastroj) {
            StrunovyNastroj s = (StrunovyNastroj) nastroj;
            return "Struny: " + s.getPocetStrun() + ", ladenie: " + s.getLadenie();
        } else {
            return "Bez extra detailov";
        }
    }

    public String vytvorVypisSkladu() {
        if (nastroje.isEmpty()) {
            return "Sklad je prazdny.";
        }

        StringBuilder builder = new StringBuilder("---Databaza nastrojov---\n");
        double celkovaCena = 0;

        for (Nastroj nastroj : nastroje) {
            builder.append("Druh: ")
                    .append(nastroj.getDruh())
                    .append(", Pocet: ")
                    .append(nastroj.getPocet())
                    .append(", Cena: ")
                    .append(nastroj.getCena())
                    .append('\n');
            celkovaCena += nastroj.getPocet() * nastroj.getCena();
        }

        builder.append("Celkova cena skladu: ").append(celkovaCena);
        return builder.toString();
    }

    public String vytvorCenuVystupenia() {
        if (nastroje.isEmpty()) {
            return "Nie su zadane ziadne nastroje pre vypocet ceny vystupenia.";
        }

        double cenaVystupenia = 0;
        for (Nastroj nastroj : nastroje) {
            cenaVystupenia += nastroj.getPocet() * nastroj.getCena();
        }

        return "---Cena vystupenia---\n"
                + cenaVystupenia
                + " (sucet cien vsetkych nastrojov v sklade)";
    }

    public String vytvorSkladHraj() {
        if (nastroje.isEmpty()) {
            return "Sklad je prazdny.";
        }

        StringBuilder builder = new StringBuilder("---Sklad hraj---\n");

        for (Nastroj nastroj : nastroje) {
            for (int i = 0; i < nastroj.getPocet(); i++) {
                builder.append(nastroj.getZvuk()).append(' ');
            }
        }

        return builder.toString().trim();
    }

    private void loadFromReader(BufferedReader reader) throws IOException {
        nastroje.clear();

        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.isBlank()) {
                parseLine(line);
            }
        }
    }

    private void parseLine(String line) {
        String[] parts = line.split(";");

        if (parts[0].equals("Zakladny")) {
            if (parts.length < 5) {
                throw new IllegalArgumentException("Chybaju data pre riadok: " + line);
            }

            String druh = parts[1];
            double cena = Double.parseDouble(parts[2]);
            String zvuk = parts[3];
            int pocet = Integer.parseInt(parts[4]);

            nastroje.add(new Nastroj(druh, cena, zvuk, pocet));
            return;
        }

        if (parts[0].equals("Slacikovy")) {
            if (parts.length < 6) {
                throw new IllegalArgumentException("Chyba sekcia pre riadok: " + line);
            }

            String druh = parts[1];
            double cena = Double.parseDouble(parts[2]);
            String zvuk = parts[3];
            int pocet = Integer.parseInt(parts[4]);
            String sekcia = parts[5];

            nastroje.add(new SlacikovyNastroj(druh, cena, zvuk, pocet, sekcia));
            return;
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

            nastroje.add(new StrunovyNastroj(druh, cena, zvuk, pocet, pocetStrun, ladenie));
            return;
        }

        throw new IllegalArgumentException("Neznamy typ nastroja: " + line);
    }
    private String serialize(Nastroj nastroj) {
        if (nastroj instanceof SlacikovyNastroj) {
            SlacikovyNastroj s = (SlacikovyNastroj) nastroj;
            return "Slacikovy;" + s.getDruh() + ";" + s.getCena() + ";" + s.getZvuk() + ";" + s.getPocet() + ";" + s.getSekcia();
        } else if (nastroj instanceof StrunovyNastroj) {
            StrunovyNastroj s = (StrunovyNastroj) nastroj;
            return "Strunovy;" + s.getDruh() + ";" + s.getCena() + ";" + s.getZvuk() + ";" + s.getPocet() + ";" + s.getPocetStrun() + ";" + s.getLadenie();
        } else {
            return "Zakladny;" + nastroj.getDruh() + ";" + nastroj.getCena() + ";" + nastroj.getZvuk() + ";" + nastroj.getPocet();
        }
    }
}
