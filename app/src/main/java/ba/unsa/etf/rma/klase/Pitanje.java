package ba.unsa.etf.rma.klase;

import java.util.ArrayList;
import java.util.Collections;

class Pitanje {
    String naziv, tekstPitanja, tacan;
    ArrayList<String> odgovori;

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getTekstPitanja() {
        return tekstPitanja;
    }

    public void setTekstPitanja(String tekstPitanja) {
        this.tekstPitanja = tekstPitanja;
    }

    public String getTacan() {
        return tacan;
    }

    public void setTacan(String tacan) {
        this.tacan = tacan;
    }

    public ArrayList<String> getOdgovori() {
        return odgovori;
    }

    public void setOdgovori(ArrayList<String> odgovori) {
        this.odgovori = odgovori;
    }

    public ArrayList<String> dajRandomOdgovore() {
        if (getOdgovori().size()==0) return null;
        Collections.shuffle(odgovori);
        return odgovori;
    }
}
