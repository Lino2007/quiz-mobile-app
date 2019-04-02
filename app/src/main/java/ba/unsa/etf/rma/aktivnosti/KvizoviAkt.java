package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.*;


public class KvizoviAkt extends AppCompatActivity  implements OnItemSelectedListener {

   public static ArrayList<Kategorija> listaKategorija =new ArrayList<>();
    public static ArrayList<String> categories= new ArrayList<>();
    public KvizoviAkt kvizoviAkt ;
    public static ArrayList<Pitanje> mogPitanja= new ArrayList<> ();


    private String trenutnaKategorija= new String ();

    //Za listu
    private ListView mainList;
    public MainListAdapter mainListAdapter=null;
    public static ArrayList<Kviz> listaKvizova= new ArrayList<>();
    private  Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        trenutnaKategorija="Sve";
        mogPitanja.add (new Pitanje ("moguce pitanje 1", "moguce pitanje 1?", null , null));
        mogPitanja.add (new Pitanje ("moguce pitanje 2", "moguce pitanje 2?", null , null));
        popuni();
        kvizoviAkt=this;
        Resources res=getResources();
        spinner = (Spinner) findViewById(R.id.spinner);
        mainList= (ListView) findViewById(R.id.lvKvizovi);
      spinner.setOnItemSelectedListener(this);

      ListView mainList= (ListView) findViewById(R.id.lvKvizovi);
        mainListAdapter= new MainListAdapter (kvizoviAkt,listaKvizova, res);
       ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        mainList.setAdapter(mainListAdapter);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

    }

    /*Ideja: Trebalo bi ucitati sve elemente liste i manualno dodati jos jedan sa drugom slikom koji pokrece add activity (Rijeseno)
     Skontati kako izvrsiti prenos kategorije za spiner u DodajKvizAkt (Zapravo treba odabrati kategoriju a ne prenositi)
     */
    /*
    TODO

*/


    public String getTrenutnaKategorija() {
        return trenutnaKategorija;
    }

    public void setTrenutnaKategorija(String trenutnaKategorija) {
        this.trenutnaKategorija = trenutnaKategorija;
    }


    @Override
    public void onItemSelected (AdapterView < ? > parent, View view,int position, long id){
        String item = parent.getItemAtPosition(position).toString();
        if (position>=0 && position< categories.size()) {
            dajKvizoveKategorije(item);

        }


        // Showing selected spinner item
        Toast.makeText(parent.getContext(), item, Toast.LENGTH_LONG).show();

    }



    @Override
    public void onNothingSelected (AdapterView < ? > parent){

    }


    private void dajKvizoveKategorije(String item) {
        ArrayList<Kviz>  odabraniKvizovi=new ArrayList<>();
        int i=0;
        if (item!="Sve") {
            for (Kviz x : listaKvizova) {
                if (x.getKategorija().getNaziv() == item) {
                    odabraniKvizovi.add(x);
                }

            }
            setTrenutnaKategorija(item);
            odabraniKvizovi.add(listaKvizova.get(listaKvizova.size()-1));
            mainListAdapter= new MainListAdapter(kvizoviAkt, odabraniKvizovi, getResources());
            mainList.setAdapter(mainListAdapter);
            return ;
        }
        mainListAdapter=new MainListAdapter(kvizoviAkt,listaKvizova,getResources());
      mainList.setAdapter(mainListAdapter);
    }


    //
    public void popuni() {
        categories.add ("Sve");
        Kategorija prva= new Kategorija("automobili", "1");
        Kategorija druga= new Kategorija("motori", "2");
        Kategorija treca= new Kategorija("avioni", "3");

        listaKategorija.add (prva);
        listaKategorija.add (druga);
        listaKategorija.add (treca);
        for (Kategorija k : listaKategorija) {
            categories.add(k.getNaziv());
        }

        ArrayList<Pitanje> a = new ArrayList(), b =new ArrayList<>();
        a.add(new Pitanje ("Pitanje 1", "Pitanje 1?", null, null));
        a.add(new Pitanje ("Pitanje 2", "Pitanje 2?", null, null));
        a.add(new Pitanje ("Pitanje 3", "Pitanje 3?", null, null));
        a.add(new Pitanje ("Pitanje 4", "Pitanje 4?", null, null));
        b.add(new Pitanje ("Pitanje 1", "BPitanje 1?", null, null));
        b.add(new Pitanje ("Pitanje 2", "BPitanje 2?", null, null));
        b.add(new Pitanje ("Pitanje 3", "BPitanje 3?", null, null));
        b.add(new Pitanje ("Pitanje 4", "BPitanje 4?", null, null));
        listaKvizova.add (new Kviz ("Poznavanje dijelova", a, prva));
        listaKvizova.add (new Kviz ("Najpoznatiji motori", b, druga));
        listaKvizova.add (new Kviz ("Toyota", b, prva));
        listaKvizova.add (new Kviz ("Kawasaki", a, druga));
        listaKvizova.add (new Kviz ("airbus", b, treca));
        listaKvizova.add (new Kviz ("krila", a, treca));
        listaKvizova.add (new Kviz (null, null , new Kategorija ("dummy", "dummy")));

    }

    public void onItemClick(int mPosition) {
        if (mPosition==listaKvizova.size()-1) {
            //TODO ukloniti
         //   Bundle b= new Bundle(), c=new Bundle();
        //    ArrayList<Pitanje> pitanjaKviza= new ArrayList<>();
           // b.putSerializable("mogucaPitanja", (Serializable) mogPitanja);
        //    ArrayList<Kviz>  pomListaKvizova=new ArrayList<>();
          //  pomListaKvizova=  kopiraj(listaKvizova,pomListaKvizova);
         //   c.putSerializable("sviKvizovi", (Serializable) pomListaKvizova);

            Intent dodajIntent= new Intent( KvizoviAkt.this, DodajKvizAkt.class);
         //   dodajIntent.putExtras(b);
            KvizoviAkt.this.startActivity(dodajIntent);

            System.out.println("Dodaj aktivnost"); }
       else System.out.println("Item clicked");
    }

     public ArrayList<Kviz> kopiraj (ArrayList<Kviz> a, ArrayList<Kviz> b) {
        for (Kviz x: a) {
            b.add(x);
        }
        b.remove(b.size()-1);
        return b;
     }
    public void dodajKviz (Kviz kviz) {
        listaKvizova.add(kviz);
    }

    public static ArrayList<String> getCategories() {
        return categories;
    }


}
