package ba.unsa.etf.rma.klase;

import java.io.Serializable;

public class Kategorija implements Serializable {
  public String naziv, id;

    public Kategorija(String naziv, String id) {
        this.naziv = naziv;
        this.id = id;
    }

    public Kategorija() {
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return  "Naziv: "+ getNaziv()+ "Id: " + getId();
    }
}
