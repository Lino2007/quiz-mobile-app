package ba.unsa.etf.rma.aktivnosti;

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

import java.util.ArrayList;
import java.util.List;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.*;


public class KvizoviAkt extends AppCompatActivity  implements OnItemSelectedListener {

   public ArrayList<Kategorija> listaKategorija =new ArrayList<>();
    public ArrayList<String> categories= new ArrayList<>();
    public KvizoviAkt kvizoviAkt ;

    //Za listu
   private ListView mainList;
    public MainListAdapter mainListAdapter=null;
    public ArrayList<Kviz> listaKvizova= new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        popuni();
        kvizoviAkt=this;
        Resources res=getResources();
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
      // spinner.setOnItemSelectedListener(this);
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
     */
    /*
    TODO
     Skontati kako izvrsiti prenos kategorije za spiner u DodajKvizAkt
     */

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(),  item, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //
    public void popuni() {
        categories.add ("Sve");
        categories.add ("Kategorija");
        listaKategorija.add (new Kategorija ("Automobili","ID1"));
        listaKategorija.add (new Kategorija ("Motori","ID2"));
        listaKategorija.add (new Kategorija ("Avioni","ID3"));
        for (Kategorija k : listaKategorija) {
            categories.add(k.getNaziv());
        }
        listaKvizova.add (new Kviz ("Kviz-1", null, null));
        listaKvizova.add (new Kviz ("Kviz-1", null, null));
        listaKvizova.add (new Kviz ("Kviz-1", null, null));
        listaKvizova.add (new Kviz ("Kviz-1", null, null));
        listaKvizova.add (new Kviz ("Kviz-1", null, null));
        listaKvizova.add (new Kviz ("Kviz-1", null, null));

    }

    public void onItemClick(int mPosition) {
        if (mPosition==listaKvizova.size()-1) System.out.println("Dodaj aktivnost");
       else System.out.println("Item clicked");
    }

    public void dodajKviz (Kviz kviz) {
        listaKvizova.add(kviz);
    }
}
