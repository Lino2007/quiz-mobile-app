package ba.unsa.etf.rma.aktivnosti;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.MainListAdapter;
import ba.unsa.etf.rma.klase.MogucaListAdapter;
import ba.unsa.etf.rma.klase.PitanjaListAdapter;
import ba.unsa.etf.rma.klase.Pitanje;



public class DodajKvizAkt extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

     private EditText editText;
     private ListView listaPitanja, listaMogucih;
     private Spinner dkaSpinner;
     private Button dodaj;
     private boolean valid=false;
     public PitanjaListAdapter pitanjaAdapter=null;
     public MogucaListAdapter mogucaAdapter=null;
     public String naziv_kviza;
     public int pozicija=-1;
     public int poz_kat=0;
     private ArrayList<Pitanje> pitanjaKviza= new ArrayList<> ();
     private ArrayList<Kviz>  listaKvizova= new ArrayList<>();
     DodajKvizAkt dkaAkk=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz_akt);
         dkaAkk=this;
        pozicija= getIntent().getExtras().getInt("poz_kviza");
        poz_kat=getIntent().getExtras().getInt("poz_kategorije");
         pitanjaKviza.clear();
        dodaj= (Button)  findViewById(R.id.button);
        editText= (EditText) findViewById(R.id.editText);
        listaMogucih= (ListView) findViewById(R.id.lvMogucaPitanja);
        listaPitanja= (ListView)  findViewById(R.id.lvDodanaPitanja);
        dkaSpinner= (Spinner) findViewById(R.id.dkaSpinner);

        if (pozicija!=-1 && KvizoviAkt.odabraniKvizovi.get(pozicija).getPitanja()!=null) {
            pitanjaKviza= KvizoviAkt.odabraniKvizovi.get(pozicija).getPitanja();
            naziv_kviza=getIntent().getExtras().getString("naziv_kviza");
            editText.setText(naziv_kviza);
        }
        if ((pitanjaKviza.size()>0 && pitanjaKviza.get(pitanjaKviza.size()-1).getNaziv()!="dummy") || pitanjaKviza.size()==0) {
            pitanjaKviza.add(new Pitanje ("dummy", "dummy", "dummy", null));
        }
          mogucaAdapter= new MogucaListAdapter(this, KvizoviAkt.mogPitanja , getResources());
          pitanjaAdapter= new PitanjaListAdapter(this, pitanjaKviza, getResources());

        dkaSpinner.setOnItemSelectedListener(this);
       ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, KvizoviAkt.getCategories());

        dkaSpinner.setAdapter(dataAdapter);
        dkaSpinner.setSelection(poz_kat); //postavka pozicije

         listaPitanja.setAdapter(pitanjaAdapter);
          if (KvizoviAkt.mogPitanja.size()==0)  listaMogucih.setAdapter(null);
          else  listaMogucih.setAdapter(mogucaAdapter);


        dodaj.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                 validacija();
            /*  if(pozicija==-1 && valid==true) {
                  shut();
                  Kviz noviKviz= new Kviz (editText.getText().toString(), pitanjaKviza, KvizoviAkt.listaKategorija.get(dkaSpinner.getSelectedItemPosition()));
                KvizoviAkt.listaKvizova.remove(listaKvizova.size()-1);
                  KvizoviAkt.listaKvizova.add (noviKviz);
                 listaKvizova.add (new Kviz (null, null , new Kategorija("dummy", "dummy")));

                  finish();

              } */


            }});

        listaMogucih.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                if (pitanjaKviza.size()>0) pitanjaKviza.remove(pitanjaKviza.size()-1);
                  pitanjaKviza.add (KvizoviAkt.mogPitanja.get(position));
                  KvizoviAkt.mogPitanja.remove(position);
                   refresh();
                mogucaAdapter= new MogucaListAdapter(dkaAkk, KvizoviAkt.mogPitanja, getResources());
               if (KvizoviAkt.mogPitanja.size()==0)  listaMogucih.setAdapter(null);
                else listaMogucih.setAdapter(mogucaAdapter);
                listaPitanja.setAdapter(pitanjaAdapter);

            }

        });

        listaPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
             if (position< pitanjaKviza.size()-1) {
                 KvizoviAkt.mogPitanja.add(pitanjaKviza.get(position));
                 pitanjaKviza.remove(position);
                 listaPitanja.setAdapter(pitanjaAdapter);
                 listaMogucih.setAdapter(mogucaAdapter);
             }
            }

        });
    }

    void validacija () {
        System.out.println(pitanjaKviza.size()+ "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        if (pitanjaKviza.size()==1) listaPitanja.setBackgroundColor(Color.RED);
        else {
            valid = true;
            listaPitanja.setBackgroundColor(Color.WHITE);
        }
        if (editText.getText().length()==0) editText.setBackgroundColor(Color.RED);
        else {
            valid = true;
            editText.setBackgroundColor(Color.WHITE);

        }
        if (dkaSpinner.getSelectedItemPosition()==0) dkaSpinner.setBackgroundColor(Color.RED);
        else {
            valid = true;
            dkaSpinner.setBackgroundColor(Color.WHITE);
        }
    }

    void refresh() {
        pitanjaKviza.add(new Pitanje ("dummy", "dummy", "dummy", null));
    }

    void shut() {
        pitanjaKviza.remove(pitanjaKviza.size()-1);
    }

    @Override
    public void onItemSelected (AdapterView< ? > parent, View view, int position, long id){
        String item = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected (AdapterView < ? > parent){

    }
}
