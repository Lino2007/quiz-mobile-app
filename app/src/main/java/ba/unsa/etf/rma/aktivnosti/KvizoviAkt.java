package ba.unsa.etf.rma.aktivnosti;

import android.app.Activity;
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
  //  public static ArrayList<Pitanje> mogPitanja= new ArrayList<> ();
    public static ArrayList<Kviz>  odabraniKvizovi= new ArrayList<> ();



    private String trenutnaKategorija= new String ();

    //Za listu
    private  ListView mainList;
    public  MainListAdapter mainListAdapter=null;
    public static ArrayList<Kviz> listaKvizova= new ArrayList<>();
    private  Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        trenutnaKategorija="Sve";
     //   mogPitanja.add (new Pitanje ("moguce pitanje 1", "moguce pitanje 1?", null , null));
   //     mogPitanja.add (new Pitanje ("moguce pitanje 2", "moguce pitanje 2?", null , null));
        popuni();
        kvizoviAkt=this;
        Resources res=getResources();
        spinner = (Spinner) findViewById(R.id.spinner);
        mainList= (ListView) findViewById(R.id.lvKvizovi);
      spinner.setOnItemSelectedListener(this);

      ListView mainList= (ListView) findViewById(R.id.lvKvizovi);
        mainListAdapter= new MainListAdapter (kvizoviAkt,listaKvizova, res);
       // System.out.println(categories.size()+ "!!!!!!!!!!!!!!!!!!!!!"+ categories.get(1));
       ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);

        mainList.setAdapter(mainListAdapter);
     //   mainList.setAdapter(mainListAdapter);
     //   mainList.setAdapter(mainListAdapter);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
         spinner.setSelection(0);
    }

    public void setTrenutnaKategorija(String trenutnaKategorija) {
        this.trenutnaKategorija = trenutnaKategorija;
    }


    @Override
    public void onItemSelected (AdapterView < ? > parent, View view,int position, long id){
        String item = parent.getItemAtPosition(position).toString();
        if (position>=0 && position< categories.size()) {
            if (listaKvizova.size()>1) {
                dajKvizoveKategorije(item);
            }
            else {
               odabraniKvizovi= kopiraj(listaKvizova,odabraniKvizovi);
            }

        }
       // Toast.makeText(parent.getContext(), item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected (AdapterView < ? > parent){

    }

    private void dajKvizoveKategorije(String item) {
        int i=0;
        odabraniKvizovi.clear();
        if (item!="Sve") {
            for (Kviz x : listaKvizova) {
                if ( x.getKategorija()!=null && x.getKategorija().getNaziv() == item) {
                    odabraniKvizovi.add(x);
                }

            }
            setTrenutnaKategorija(item);
            System.out.println(listaKvizova.size());
            odabraniKvizovi.add(listaKvizova.get(listaKvizova.size()-1));
            mainListAdapter= new MainListAdapter(kvizoviAkt, odabraniKvizovi, getResources());
            mainList.setAdapter(mainListAdapter);
            return ;
        }
       odabraniKvizovi=kopiraj(listaKvizova,odabraniKvizovi);
        mainListAdapter=new MainListAdapter(kvizoviAkt,odabraniKvizovi,getResources());
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
     //   System.out.println(listaKategorija.size());
        for (Kategorija k : listaKategorija) {

            categories.add(k.getNaziv());
        }
      //  System.out.println(listaKategorija.size() + " NAKON");

      ArrayList<Pitanje> a = new ArrayList(), b =new ArrayList<>();
        ArrayList<String> odgov=new ArrayList<>();  odgov.add("Prvi odgovor"); odgov.add("Drugi odgovor"); odgov.add("Neki odgov");
        ArrayList<String> odgov2=new ArrayList<>();  odgov2.add("Garbage"); odgov2.add("Collector"); odgov2.add("Is the best");
        a.add(new Pitanje ("Pitanje 1", "Pitanje 1?", null, odgov));
        a.add(new Pitanje ("Pitanje 2", "Pitanje 2?", null, odgov2));
        a.add(new Pitanje ("Pitanje 3", "Pitanje 3?", odgov.get(0), odgov));
        a.add(new Pitanje ("Pitanje 4", "Pitanje 4?", odgov.get(1), odgov2));
      b.add(new Pitanje ("Pitanje 1", "BPitanje 1?", null, null));
        b.add(new Pitanje ("Pitanje 2", "BPitanje 2?", null, null));
        b.add(new Pitanje ("Pitanje 3", "BPitanje 3?", null, null));
        b.add(new Pitanje ("Pitanje 4", "BPitanje 4?", null, null));
        listaKvizova.add (new Kviz ("Poznavanje dijelova", a, prva));
        listaKvizova.add (new Kviz ("Najpoznatiji motori", b, druga));
        listaKvizova.add (new Kviz ("Toyota", b, prva));
        listaKvizova.add (new Kviz ("Kawasaki", a, druga));
        listaKvizova.add (new Kviz ("airbus", b, treca));


        //provjeri zasto kad su pitanja null zeza
  //     listaKvizova.add (new Kviz ("krila", null, treca));
    //    listaKvizova.add (new Kviz ("kril2a", null, treca));
        listaKvizova.add (new Kviz (null, null , new Kategorija ("dummy", "dummy")));
         odabraniKvizovi= kopiraj(listaKvizova,odabraniKvizovi);

    }

    public void onItemClick(int mPosition) {
    //   System.out.println(odabraniKvizovi.get(0) +" "+ odabraniKvizovi.get(1) );
        System.out.println(mPosition);
        if (mPosition==odabraniKvizovi.size()-1) {
            Intent dodajIntent= new Intent( KvizoviAkt.this, DodajKvizAkt.class);
            dodajIntent.putExtra ("poz_kviza",-1);
            dodajIntent.putExtra ("poz_kategorije", 0);
            KvizoviAkt.this.startActivityForResult(dodajIntent, 0 );


   }
       else {
             Intent dodajIntent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
             dodajIntent.putExtra ("poz_kviza", mPosition);
             int indexKategorije;
             try {
                 if (odabraniKvizovi.get(mPosition).getKategorija()!=null) {
                indexKategorije=getCategoriesByName(odabraniKvizovi.get(mPosition).getKategorija().getNaziv()); }
                 else {
                     indexKategorije=-2;
                 }
             }
             catch (Exception e) {
                 System.out.println(e + "!!!!!!!!!!!!");
                 return ;
            }
//            indexKategorije=getCategoriesByName(odabraniKvizovi.get(mPosition).getKategorija().getNaziv());
            dodajIntent.putExtra ("poz_kategorije", indexKategorije);
            dodajIntent.putExtra ("naziv_kviza", odabraniKvizovi.get(mPosition).getNaziv());
            KvizoviAkt.this.startActivityForResult(dodajIntent, 0 );



        }
    }

     public  ArrayList<Kviz> kopiraj (ArrayList<Kviz> a, ArrayList<Kviz> b) {
        b.clear();
        for (Kviz x: a) {
            b.add(x);
        }

        return b;
     }
    public void dodajKviz (Kviz kviz) {
        listaKvizova.add(kviz);
    }

    public static ArrayList<String> getCategories() {
        return categories;
    }

    public static int getCategoriesByName (String name) {
        System.out.println(name + "NAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        int target= categories.indexOf(name);
        if (target==-1) throw new IllegalArgumentException("Ne postoji kategorija");
        return target;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("USAO......****************...******************************");
            if(resultCode == Activity.RESULT_OK){
                Bundle bundleOb= data.getExtras();
                  ArrayList<Pitanje> novaPitanja=  (ArrayList<Pitanje>) bundleOb.getSerializable("listaPitanja");
                  novaPitanja.remove (novaPitanja.size()-1);
               listaKvizova.remove(listaKvizova.size()-1);
               Kviz noviKviz=null;
               if ((data.getExtras().getInt ("kategorija")-1) != -1) {
               noviKviz=new Kviz (data.getStringExtra("naziv"), novaPitanja, listaKategorija.get(data.getExtras().getInt ("kategorija")-1)); }
               else  noviKviz=new Kviz (data.getStringExtra("naziv"), novaPitanja, null);
                dodajKviz(noviKviz);
               refreshCategories();
                listaKvizova.add (new Kviz (null, null , new Kategorija ("dummy", "dummy")));
                spinner.setSelection(0);
                mainListAdapter=new MainListAdapter(kvizoviAkt,listaKvizova,getResources());
                mainList.setAdapter(mainListAdapter);
            }
            else if (resultCode == Activity.RESULT_FIRST_USER) {
                Bundle bundleOb= data.getExtras();
                ArrayList<Pitanje> novaPitanja=  (ArrayList<Pitanje>) bundleOb.getSerializable("listaPitanja");
                System.out.println(novaPitanja.size());
               novaPitanja.remove (novaPitanja.size()-1);
                int pozicija=data.getExtras().getInt("pozicija");
                odabraniKvizovi.get(pozicija).setPitanja(novaPitanja);
                odabraniKvizovi.get(pozicija).setNaziv (data.getStringExtra("naziv"));
                if ((data.getExtras().getInt ("kategorija")-1) != -1){
                    odabraniKvizovi.get(pozicija).setKategorija(listaKategorija.get(data.getExtras().getInt("kategorija") - 1));
                }
                else {
                    odabraniKvizovi.get(pozicija).setKategorija(null);
                }
                refreshCategories();
                spinner.setSelection(0);
                mainListAdapter=new MainListAdapter(kvizoviAkt,listaKvizova,getResources());
                mainList.setAdapter(mainListAdapter);
            }
             if (resultCode== 9000) {
                 System.out.println("REFREEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                 refreshCategories();
             }

    }

    void refreshCategories () {
        categories.clear();
        categories.add ("Sve");
        for (Kategorija k : listaKategorija) {

            categories.add(k.getNaziv());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinner.setAdapter(dataAdapter);
    }
}
