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
import ba.unsa.etf.rma.klase.Pitanje;

public class DodajKvizAkt extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

     private EditText editText;
     private ListView listaPitanja;
     private ListView listaMogucih;
     private Spinner dkaSpinner;
     private Button dodaj;
     public PitanjaListAdapter pitanjaAdapter=null;
     public MogucaListAdapter mogucaAdapter=null;
     public boolean t=true;
     private ArrayList<Pitanje> pitanjaKviza= new ArrayList<> ();
     private ArrayList<Kviz>  listaKvizova= new ArrayList<>();
      public Kviz noviKviz;
     DodajKvizAkt dkaAkk=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz_akt);
         dkaAkk=this;
        Bundle b= getIntent().getExtras();
        final ArrayList<Pitanje> mogucaPitanja= (ArrayList<Pitanje>)b.getSerializable("mogucaPitanja");
        Bundle c= getIntent().getExtras();
        ArrayList<Kviz> listaKvizova= (ArrayList<Kviz>)b.getSerializable("sviKvizovi");

     if (t) {
          pitanjaKviza.add(new Pitanje ("dummy", "dummy", "dummy", null));
           t=false;
       }
        dodaj= (Button)  findViewById(R.id.button);
        editText= (EditText) findViewById(R.id.editText);
        listaMogucih= (ListView) findViewById(R.id.lvMogucaPitanja);
        listaPitanja= (ListView)  findViewById(R.id.lvDodanaPitanja);
        dkaSpinner= (Spinner) findViewById(R.id.dkaSpinner);

          mogucaAdapter= new MogucaListAdapter(this, mogucaPitanja, getResources());

        pitanjaAdapter= new PitanjaListAdapter(this,pitanjaKviza, getResources());
        dkaSpinner.setOnItemSelectedListener(this);
       ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, KvizoviAkt.getCategories());
        dkaSpinner.setAdapter(dataAdapter);
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
               mogucaPitanja.remove(0);
                mogucaAdapter= new MogucaListAdapter(dkaAkk,mogucaPitanja, getResources());
                listaMogucih.setAdapter(mogucaAdapter);
                System.out.println(position);

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
