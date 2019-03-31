package ba.unsa.etf.rma.aktivnosti;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    // private ArrayList<String> test= (ArrayList<String>) getIntent().getSerializableExtra("key");
  //     private ArrayList<Pitanje> mogucaPitanja= new ArrayList<>();

     // private ArrayList<Pitanje> mogucaPitanja= (ArrayList<Pitanje>) getIntent().getSerializableExtra("mogucaPitanja");

 //    private KvizoviAkt kvAkt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz_akt);

        Bundle b= getIntent().getExtras();
        ArrayList<Pitanje> mogucaPitanja= (ArrayList<Pitanje>)b.getSerializable("mogucaPitanja");
        System.out.println(mogucaPitanja.size());
        editText= (EditText) findViewById(R.id.editText);
        listaMogucih= (ListView) findViewById(R.id.lvMogucaPitanja);
        listaPitanja= (ListView)  findViewById(R.id.lvDodanaPitanja);
        dkaSpinner= (Spinner) findViewById(R.id.dkaSpinner);

        dkaSpinner.setOnItemSelectedListener(this);
       ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, KvizoviAkt.getCategories());
        dkaSpinner.setAdapter(dataAdapter);
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





}
