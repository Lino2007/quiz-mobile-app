package ba.unsa.etf.rma.klase;

import java.io.Serializable;
import java.util.ArrayList;

public class Kviz implements Serializable {
    String naziv=new String();
    ArrayList<Pitanje> pitanja= new ArrayList<>();
    Kategorija kategorija;

    public Kviz(String naziv, ArrayList<Pitanje> pitanja, Kategorija kategorija) {
        this.naziv = naziv;
        this.pitanja = pitanja;
        this.kategorija = kategorija;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public ArrayList<Pitanje> getPitanja() {
        return pitanja;
    }

    public void setPitanja(ArrayList<Pitanje> pitanja) {
        this.pitanja = pitanja;
    }

    public Kategorija getKategorija() {
        return kategorija;
    }

    public void setKategorija(Kategorija kategorija) {
        this.kategorija = kategorija;
    }

    public void dodajPitanje (Pitanje novoPitanje) {
        pitanja.add(novoPitanje);
    }


    @Override
    public String toString() {
        return  "Naziv: "+ getNaziv()+ "Id: " + getKategorija().getNaziv();
    }
}
