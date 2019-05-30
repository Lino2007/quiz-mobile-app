package ba.unsa.etf.rma.klase;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Kviz implements Serializable {
    String naziv=new String();
    ArrayList<Pitanje> pitanja= new ArrayList<>();
    Kategorija kategorija;

    public Kviz(String naziv, ArrayList<Pitanje> pitanja, Kategorija kategorija) {
        this.naziv = naziv;
        this.pitanja = pitanja;
        this.kategorija = kategorija;
    }

    public Kviz() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kviz kviz = (Kviz) o;
        return Objects.equals(naziv, kviz.naziv) &&
                Objects.equals(pitanja, kviz.pitanja) &&
                Objects.equals(kategorija, kviz.kategorija);
    }

    @Override
    public int hashCode() {
        return Objects.hash(naziv, pitanja, kategorija);
    }
}
