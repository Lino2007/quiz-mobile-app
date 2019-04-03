package ba.unsa.etf.rma.klase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Pitanje implements Serializable ,Cloneable  {
    String naziv, tekstPitanja, tacan;
    ArrayList<String> odgovori=  new ArrayList<>();

    public Pitanje(String naziv, String tekstPitanja, String tacan, ArrayList<String> odgovori) {
        this.naziv = naziv;
        this.tekstPitanja = tekstPitanja;
        this.tacan = tacan;
        this.odgovori = odgovori;
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
        if (getOdgovori().size()==0) return null;
        Collections.shuffle(odgovori);
        return odgovori;
    }
    @Override
    public Object clone() throws CloneNotSupportedException {
        return (Pitanje) super.clone();
    }
    /*
    package ba.unsa.etf.rpr.predavanje09;

import java.util.Date;

class Student implements Cloneable {
    public String ime, prezime;
    public Date datumRodjenja;

    @Override
    public Object clone() throws CloneNotSupportedException {
        Student novi = (Student) super.clone();
        novi.datumRodjenja = (Date) this.datumRodjenja.clone();
        return novi;
    }
}

     */
}
