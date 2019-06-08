package ba.unsa.etf.rma.klase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Pitanje implements Serializable  {
    String naziv, tekstPitanja, tacan;
    ArrayList<String> odgovori=  new ArrayList<>();

    public Pitanje(String naziv, String tekstPitanja, String tacan, ArrayList<String> odgovori) {
        this.naziv = naziv;
        this.tekstPitanja = tekstPitanja;
        this.tacan = tacan;
        this.odgovori = odgovori;
    }

    public Pitanje() {
    }

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
        if (getOdgovori()==null) return null;
        if (getOdgovori().size()==0) return null;
        else if (getOdgovori().size()==1) return odgovori;
        Collections.shuffle(odgovori);
        return odgovori;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pitanje pitanje = (Pitanje) o;
        return Objects.equals(naziv, pitanje.naziv) &&
                Objects.equals(tekstPitanja, pitanje.tekstPitanja) &&
                Objects.equals(tacan, pitanje.tacan) &&
                Objects.equals(odgovori, pitanje.odgovori);
    }

    @Override
    public int hashCode() {
        return Objects.hash(naziv, tekstPitanja, tacan, odgovori);
    }

    @Override
    public String toString() {
        return "Pitanje{" +
                "naziv='" + naziv + '\'' +
                ", tekstPitanja='" + tekstPitanja + '\'' +
                ", tacan='" + tacan + '\'' +
                '}';
    }
}
