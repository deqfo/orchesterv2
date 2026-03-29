package orchester.models;

import orchester.models.Nastroj;

public class SlacikovyNastroj extends Nastroj {
    private String sekcia;

    public SlacikovyNastroj(String druh, double cena, String zvuk, int pocet, String sekcia) {
        super(druh, cena, zvuk, pocet);
        setSekcia(sekcia);
    }

    public String getSekcia() {
        return sekcia;
    }

    public void setSekcia(String sekcia) {
        if (sekcia.equals("")) {
            System.out.println("Sekcia nemoze byt prazdna");
        } else {
            this.sekcia = sekcia;
        }
    }

    @Override
    public String toString() {
        return "SlacikovyNastroj [" + super.toString() + ", sekcia=" + sekcia + "]";
    }

}
