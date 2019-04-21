package ba.unsa.etf.rma.aktivnosti;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.Serializable;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.MainListAdapter;
import ba.unsa.etf.rma.fragmenti.DetailFrag;
import ba.unsa.etf.rma.fragmenti.ListaFrag;
import ba.unsa.etf.rma.klase.*;


public class KvizoviAkt extends AppCompatActivity implements OnItemSelectedListener, ListaFrag.ListUpdater, DetailFrag.ListFunction {

    public static ArrayList<Kategorija> listaKategorija = new ArrayList<>();
    public static ArrayList<String> categories = new ArrayList<>();
    public KvizoviAkt kvizoviAkt;
    public static ArrayList<Kviz> odabraniKvizovi = new ArrayList<>();
    private String trenutnaKategorija = new String();
    private ListView mainList;
    public MainListAdapter mainListAdapter = null;
    public static ArrayList<Kviz> listaKvizova = new ArrayList<>();
    private Spinner spinner;
    public static int jedinica=1;
    private FrameLayout detail, lista;
    Activity refreshActivity= null;
    TestRefresh instanca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        trenutnaKategorija = "Svi";
      if(jedinica==1)  popuni();
          jedinica--;


        kvizoviAkt = this;
        Resources res = getResources();
        spinner = (Spinner) findViewById(R.id.spPostojeceKategorije);
        mainList = (ListView) findViewById(R.id.lvKvizovi);
        Configuration config= getResources().getConfiguration();

        FragmentManager fragment=getSupportFragmentManager();
        detail= (FrameLayout) findViewById(R.id.detailPlace);
        lista= (FrameLayout) findViewById(R.id.listPlace);

        if (config.orientation== Configuration.ORIENTATION_PORTRAIT) {

            spinner.setOnItemSelectedListener(this);
            final ListView mainList = (ListView) findViewById(R.id.lvKvizovi);
            mainListAdapter = new MainListAdapter(kvizoviAkt, listaKvizova, res);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
            mainList.setAdapter(mainListAdapter);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);
            spinner.setSelection(0);

            mainList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                    dugiKlik(pos);
                    return true;
                }
            });

            mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (position != odabraniKvizovi.size() - 1) {
                        Intent newIntent = new Intent(KvizoviAkt.this, IgrajKvizAkt.class);
                        newIntent.putExtra("kviz", (Serializable) odabraniKvizovi.get(position));
                        KvizoviAkt.this.startActivityForResult(newIntent, 32000);
                        //Poziv igrajkvizakt
                    }
                }

            });

        }
        else {
     //       fragment.
            ListaFrag lf= new ListaFrag();
            DetailFrag df= new DetailFrag();

            fragment.beginTransaction().replace(R.id.listPlace, lf, lf.getTag()).commit();
           fragment.beginTransaction().replace(R.id.detailPlace , df , df.getTag()).commit();
        }

    }



    public void setTrenutnaKategorija(String trenutnaKategorija) {
        this.trenutnaKategorija = trenutnaKategorija;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        if (position >= 0 && position < categories.size()) {
            if (listaKvizova.size() > 1) {
                dajKvizoveKategorije(item);
            } else {
                odabraniKvizovi = kopiraj(listaKvizova, odabraniKvizovi);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void dajKvizoveKategorije(String item) {
        int i = 0;
        odabraniKvizovi.clear();
        Configuration config= getResources().getConfiguration();
        if (item != "Svi") {
            for (Kviz x : listaKvizova) {
                if (x.getKategorija() != null && x.getKategorija().getNaziv() == item) {
                    odabraniKvizovi.add(x);
                }
            }
            setTrenutnaKategorija(item);
            System.out.println(listaKvizova.size());
            odabraniKvizovi.add(listaKvizova.get(listaKvizova.size() - 1));
        if (config.orientation== Configuration.ORIENTATION_PORTRAIT) {
            mainListAdapter = new MainListAdapter(kvizoviAkt, odabraniKvizovi, getResources());
            mainList.setAdapter(mainListAdapter);
        }
            return;
        }
        odabraniKvizovi = kopiraj(listaKvizova, odabraniKvizovi);
        if (config.orientation== Configuration.ORIENTATION_PORTRAIT) {
            mainListAdapter = new MainListAdapter(kvizoviAkt, odabraniKvizovi, getResources());
            mainList.setAdapter(mainListAdapter);
        }
    }


    public void popuni() {
        categories.add("Svi");
  //    Ako zelite eksperimentirati sa vec unesenim podatcima
  /*   Kategorija prva= new Kategorija("automobili", "1");
    Kategorija druga= new Kategorija("motori", "2");
        Kategorija treca= new Kategorija("avioni", "3");

        listaKategorija.add (prva);
      listaKategorija.add (druga);
       listaKategorija.add (treca);

        for (Kategorija k : listaKategorija) {

            categories.add(k.getNaziv());
        }
 ArrayList<Pitanje> a = new ArrayList(), b =new ArrayList<>();
        ArrayList<String> odgov=new ArrayList<>();  odgov.add("Prvi odgovor"); odgov.add("Drugi odgovor"); odgov.add("Neki odgov");
        ArrayList<String> odgov2=new ArrayList<>();  odgov2.add("Garbage"); odgov2.add("Collector"); odgov2.add("Is the best");
        a.add(new Pitanje ("Pitanje 1", "Pitanje 1?",  odgov.get(0), odgov));
        a.add(new Pitanje ("Pitanje 2", "Pitanje 2?", odgov2.get(0), odgov2));
        a.add(new Pitanje ("Pitanje 3", "Pitanje 3?", odgov.get(0), odgov));
        a.add(new Pitanje ("Pitanje 4", "Pitanje 4?", odgov2.get(0), odgov2));
      b.add(new Pitanje ("Pitanje 1", "BPitanje 1?", odgov2.get(0), odgov2));
        b.add(new Pitanje ("Pitanje 2", "BPitanje 2?", odgov2.get(0), odgov2));
        b.add(new Pitanje ("Pitanje 3", "BPitanje 3?", odgov2.get(0), odgov2));
        b.add(new Pitanje ("Pitanje 4", "BPitanje 4?", odgov2.get(0), odgov2));
        listaKvizova.add (new Kviz ("Poznavanje dijelova", a, prva));
        listaKvizova.add (new Kviz ("Najpoznatiji motori", b, druga));
        listaKvizova.add (new Kviz ("Toyota", a, prva));
        listaKvizova.add (new Kviz ("Kawasaki", a, druga));
        listaKvizova.add (new Kviz ("airbus", b, treca));
        listaKvizova.add (new Kviz ("airbusss", b, druga)); */

    if (listaKvizova.isEmpty()) {
            listaKvizova.add(new Kviz(null, null, null));
      }
        odabraniKvizovi = kopiraj(listaKvizova, odabraniKvizovi);

    }

    private  void dugiKlik (int mPosition) {
        if (mPosition == odabraniKvizovi.size() - 1) {
            Intent dodajIntent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
            dodajIntent.putExtra("poz_kviza", -1);
            dodajIntent.putExtra("poz_kategorije", 0);
            KvizoviAkt.this.startActivityForResult(dodajIntent, 0);
        } else {
            Intent dodajIntent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
            dodajIntent.putExtra("poz_kviza", mPosition);
            int indexKategorije;
            try {
                if (odabraniKvizovi.get(mPosition).getKategorija() != null) {
                    indexKategorije = getCategoriesByName(odabraniKvizovi.get(mPosition).getKategorija().getNaziv());
                } else {
                    indexKategorije = -2;
                }
            } catch (Exception e) {
                System.out.println(e);
                return;
            }
            dodajIntent.putExtra("poz_kategorije", indexKategorije);
            dodajIntent.putExtra("naziv_kviza", odabraniKvizovi.get(mPosition).getNaziv());
            KvizoviAkt.this.startActivityForResult(dodajIntent, 0);
        }
    }

    public void onItemClick(int mPosition) {
        //Utvrdjivanje da li je kliknuto na dodaj ili na kviz
        if (mPosition == odabraniKvizovi.size() - 1) {
            Intent dodajIntent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
            dodajIntent.putExtra("poz_kviza", -1);
            dodajIntent.putExtra("poz_kategorije", 0);
            KvizoviAkt.this.startActivityForResult(dodajIntent, 0);
        } else {
            Intent dodajIntent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
            dodajIntent.putExtra("poz_kviza", mPosition);
            int indexKategorije;
            try {
                if (odabraniKvizovi.get(mPosition).getKategorija() != null) {
                    indexKategorije = getCategoriesByName(odabraniKvizovi.get(mPosition).getKategorija().getNaziv());
                } else {
                    indexKategorije = -2;
                }
            } catch (Exception e) {
                System.out.println(e);
                return;
            }
            dodajIntent.putExtra("poz_kategorije", indexKategorije);
            dodajIntent.putExtra("naziv_kviza", odabraniKvizovi.get(mPosition).getNaziv());
            KvizoviAkt.this.startActivityForResult(dodajIntent, 0);
        }
    }

    public ArrayList<Kviz> kopiraj(ArrayList<Kviz> a, ArrayList<Kviz> b) {
        b.clear();
        for (Kviz x : a) {
            b.add(x);
        }
        return b;
    }

    public void dodajKviz(Kviz kviz) {
        listaKvizova.add(kviz);
    }

    public static ArrayList<String> getCategories() {
        return categories;
    }

    private boolean isItPortrait () {
        Configuration config= getResources().getConfiguration();
        return config.orientation==Configuration.ORIENTATION_PORTRAIT;
    }

    public static int getCategoriesByName(String name) {
        int target = categories.indexOf(name);
        if (target == -1) throw new IllegalArgumentException("Ne postoji kategorija");
        return target;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Result_OK oznacava dodavanje novog kviza, dok FIRST_USER azuriranje
        //Result kod 9000 oznacava izlazak na back dugme
        if (resultCode == -32) {
            Bundle bundleOb = data.getExtras();
            ArrayList<Pitanje> novaPitanja = (ArrayList<Pitanje>) bundleOb.getSerializable("listaPitanja");

            novaPitanja.remove(novaPitanja.size() - 1);
            listaKvizova.remove(listaKvizova.size() - 1);
            Kviz noviKviz = null;
            if ((data.getExtras().getInt("kategorija") - 1) != -1) {
                noviKviz = new Kviz(data.getStringExtra("naziv"), novaPitanja, listaKategorija.get(data.getExtras().getInt("kategorija") - 1));
            } else noviKviz = new Kviz(data.getStringExtra("naziv"), novaPitanja, null);
            dodajKviz(noviKviz);
            refreshCategories();
            listaKvizova.add(new Kviz(null, null, null));
            if (isItPortrait()) {
                spinner.setSelection(0);
            mainListAdapter = new MainListAdapter(kvizoviAkt, listaKvizova, getResources());
            mainList.setAdapter(mainListAdapter); }

        } else if (resultCode == -133) {
            Bundle bundleOb = data.getExtras();
            ArrayList<Pitanje> novaPitanja = (ArrayList<Pitanje>) bundleOb.getSerializable("listaPitanja");
            novaPitanja.remove(novaPitanja.size() - 1);
            int pozicija = data.getExtras().getInt("pozicija");
            odabraniKvizovi.get(pozicija).setPitanja(novaPitanja);
            odabraniKvizovi.get(pozicija).setNaziv(data.getStringExtra("naziv"));
            if ((data.getExtras().getInt("kategorija") - 1) != -1) {
                odabraniKvizovi.get(pozicija).setKategorija(listaKategorija.get(data.getExtras().getInt("kategorija") - 1));
            } else {
                odabraniKvizovi.get(pozicija).setKategorija(null);
            }
            refreshCategories();

            if (isItPortrait()) {
                spinner.setSelection(0);
                mainListAdapter = new MainListAdapter(kvizoviAkt, listaKvizova, getResources());
                mainList.setAdapter(mainListAdapter);
            }

        }
       else if (resultCode == 9000) {

             refreshCategories();
        }
        else if (resultCode == 32000) {
            return ;
        }
        else if (resultCode== 10000) {
            refreshCategories();
        }

        if (isItPortrait()) {
            if (categories.size() > 1) {
                spinner.setSelection(1);
                spinner.setSelection(0);
            }
        }


    }

    void refreshCategories() {
        categories.clear();
        categories.add("Svi");
        for (Kategorija k : listaKategorija) {
            if (k == null) continue;
            categories.add(k.getNaziv());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
if (isItPortrait())     spinner.setAdapter(dataAdapter);
    }

    @Override
    public void filterList(int i) {
        if (i<0) return ;
        dajKvizoveKategorije(categories.get(i));
        ListaFrag lf= new ListaFrag();
        DetailFrag df= new DetailFrag();
       FragmentManager fragment = getSupportFragmentManager();

        fragment.beginTransaction().replace(R.id.listPlace, lf, lf.getTag()).commit();
        fragment.beginTransaction().replace(R.id.detailPlace , df , df.getTag()).commit();

    }

    @Override
    public void addKviz(int i) {
         dugiKlik(i);
    }


    void recallFrags () {
        ListaFrag lf= new ListaFrag();
        DetailFrag df= new DetailFrag();


        FragmentManager fragment = getSupportFragmentManager();
        fragment.beginTransaction().replace(R.id.listPlace, lf, lf.getTag()).commit();
        fragment.beginTransaction().replace(R.id.detailPlace , df , df.getTag()).commit();
    }

    @Override
    public void editKviz() {
          filterList(0);
    }

    @Override
    public void playKviz(int i) {
        if (i != odabraniKvizovi.size() - 1) {
            Intent newIntent = new Intent(KvizoviAkt.this, IgrajKvizAkt.class);
            newIntent.putExtra("kviz", (Serializable) odabraniKvizovi.get(i));
            KvizoviAkt.this.startActivityForResult(newIntent, 32000);

            //Poziv igrajkvizakt
        }

    }

    public interface TestRefresh {
        void refreshKat ();
        void refreshKviz ();
    }
}
