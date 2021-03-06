package ba.unsa.etf.rma.klase;

import java.io.Serializable;
import java.util.Objects;

public class Kategorija implements Serializable {
  public String naziv, id;

    public Kategorija(String naziv, String id) {
        this.naziv = naziv;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kategorija that = (Kategorija) o;
        return Objects.equals(naziv, that.naziv) &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(naziv, id);
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

