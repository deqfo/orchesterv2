package orchester.models;

public class Hrac{
    private String meno;
    private String priezvisko;
    private Nastroj nastroje;
    private double hodinovaSadzba;

    public Hrac(String meno, String priezvisko, Nastroj nastroje, double hodinovaSadzba) {
        setMeno(meno);
        setPriezvisko(priezvisko);
        setNastroje(nastroje);
        setHodinovaSadzba(hodinovaSadzba);
    }

    public Hrac(String[] data) {
        if (data.length < 5) {
            throw new IllegalArgumentException("Chybaju data hraca.");
        }

        setMeno(data[1]);
        setPriezvisko(data[2]);
        setNastroje(null);
        setHodinovaSadzba(Double.parseDouble(data[4]));
    }

    public String getMeno() {
        return meno;
    }

    public String getPriezvisko() {
        return priezvisko;
    }

    public Nastroj getNastroje() {
        return nastroje;
    }

    public double getHodinovaSadzba() {
        return hodinovaSadzba;
    }

    public void setMeno(String meno) {
        if (meno.equals("")) {
            System.out.println("Meno nemoze byt prazdne");
        } else {
            this.meno = meno;
        }
    }

    public void setPriezvisko(String priezvisko) {
        if (priezvisko.equals("")) {
            System.out.println("Priezvisko nemoze byt prazdne");
        } else {
            this.priezvisko = priezvisko;
        }
    }

    public void setNastroje(Nastroj nastroje) {
        this.nastroje = nastroje;
    }

    public void setHodinovaSadzba(double hodinovaSadzba) {
        if (hodinovaSadzba < 0) {
            System.out.println("Hodinova sadzba nemoze byt zaporna");
        } else {
            this.hodinovaSadzba = hodinovaSadzba;
        }
    }

    @Override
    public String toString() {
        return "Hrac [meno=" + meno + ", priezvisko=" + priezvisko + ", nastroje="
                + (nastroje != null ? nastroje.toString() : "null")
                + ", hodinovaSadzba=" + hodinovaSadzba + "]";
    }
}
