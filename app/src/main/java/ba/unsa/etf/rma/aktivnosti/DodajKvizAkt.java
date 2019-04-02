package ba.unsa.etf.rma.aktivnosti;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.MogucaListAdapter;
import ba.unsa.etf.rma.klase.PitanjaListAdapter;
import ba.unsa.etf.rma.klase.Pitanje;

public class DodajKvizAkt extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

     private EditText editText;
     private ListView listaPitanja;
     private ListView listaMogucih;
     private Spinner dkaSpinner;
     private Button dodaj;
     public PitanjaListAdapter pitanjaAdapter=null;
     public MogucaListAdapter mogucaAdapter=null;
     public String naziv_kviza;
     public int pozicija=-1;
     public int poz_kat=0;
     private ArrayList<Pitanje> pitanjaKviza= new ArrayList<> ();
     private ArrayList<Kviz>  listaKvizova= new ArrayList<>();
      public Kviz noviKviz;
     DodajKvizAkt dkaAkk=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz_akt);
         dkaAkk=this;
        pozicija= getIntent().getExtras().getInt("poz_kviza");
        poz_kat=getIntent().getExtras().getInt("poz_kategorije");
        System.out.println("------------"+ poz_kat);

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
        if (pitanjaKviza.size()>0 && pitanjaKviza.get(pitanjaKviza.size()-1).getNaziv()!="dummy") {
            pitanjaKviza.add(new Pitanje ("dummy", "dummy", "dummy", null));
        }
          mogucaAdapter= new MogucaListAdapter(this, KvizoviAkt.mogPitanja , getResources());
          pitanjaAdapter= new PitanjaListAdapter(this, pitanjaKviza, getResources());

        dkaSpinner.setOnItemSelectedListener(this);
       ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, KvizoviAkt.getCategories());

        dkaSpinner.setAdapter(dataAdapter);
        dkaSpinner.setSelection(poz_kat); //postavka pozicije

         listaPitanja.setAdapter(pitanjaAdapter);
        listaMogucih.setAdapter(mogucaAdapter);

        System.out.println("nesto");
        dodaj.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {


            }});

        listaMogucih.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id){

               /* String beforeName = itemListAdapter.getItem(position).getItemName().toString();

                String changedName = "Thomas";
                itemListAdapter.getItem(position).setItemName(changedName); */
               KvizoviAkt.mogPitanja.remove(0);
                mogucaAdapter= new MogucaListAdapter(dkaAkk, KvizoviAkt.mogPitanja, getResources());
                listaMogucih.setAdapter(mogucaAdapter);


            }

        });

    }





    @Override
    public void onItemSelected (AdapterView< ? > parent, View view, int position, long id){
        String item = parent.getItemAtPosition(position).toString();
     /*   if (position>=0 && position< categories.size()) {
            dajKvizoveKategorije(item) ;
        }

   */
        // Showing selected spinner item
        Toast.makeText(parent.getContext(), item, Toast.LENGTH_LONG).show();

    }



    @Override
    public void onNothingSelected (AdapterView < ? > parent){

    }


    public void onItemClick(int mPosition) {
    }
}
