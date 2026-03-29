package orchester.models;

public class Nastroj{
    private String druh;
    private double cena;
    private String zvuk;
    private int pocet;

    public Nastroj(String druh, double cena, String zvuk, int pocet) {
        setDruh(druh);
        setCena(cena);
        setZvuk(zvuk);
        setPocet(pocet);
    }


    public String getDruh() {
        return druh;
    }

    public double getCena() {
        return cena;
    }

    public String getZvuk() {
        return zvuk;
    }

    public int getPocet() {
        return pocet;
    }

    public void setDruh(String druh) {
        if (druh.equals("")) {
            System.out.println("Druh nemoze byt prazdny");
        } else {
            this.druh = druh;
        }
    }

    public void setCena(double cena) {
        if (cena < 0) {
            System.out.println("Cena nemoze byt zaporna");
        } else {
            this.cena = cena;
        }
    }

    public void setZvuk(String zvuk) {
        if (zvuk.equals("")) {
            System.out.println("Zvuk nemoze byt prazdny");
        } else {
            this.zvuk = zvuk;
        }
    }

    public void setPocet(int pocet) {
        if (pocet < 0) {
            System.out.println("Pocet nemoze byt zaporny");
        } else {
            this.pocet = pocet;
        }
    }

    @Override
    public String toString() {
        return "Nastroje [druh=" + druh + ", cena=" + cena + ", zvuk=" + zvuk + ", pocet=" + pocet + "]";
    }
}