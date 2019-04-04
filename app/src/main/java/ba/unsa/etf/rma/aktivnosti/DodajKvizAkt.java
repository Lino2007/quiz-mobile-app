package ba.unsa.etf.rma.aktivnosti;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
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
     ArrayList<Pitanje> x=new ArrayList<>();
      private ArrayList<String> kategorije= new ArrayList<>();
     public  ArrayList<Pitanje> kopijaPitanjaKviza = new ArrayList<>();
     public  ArrayList<Pitanje> kopijaMogucihPitanja= new ArrayList<>();
      DodajKvizAkt dkaAkk=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz_akt);

         dkaAkk=this;
        pozicija= getIntent().getExtras().getInt("poz_kviza");
        poz_kat=getIntent().getExtras().getInt("poz_kategorije");
       //  pitanjaKviza.clear();
        dodaj= (Button)  findViewById(R.id.button);
        editText= (EditText) findViewById(R.id.editText);
        listaMogucih= (ListView) findViewById(R.id.lvMogucaPitanja);
        listaPitanja= (ListView)  findViewById(R.id.lvDodanaPitanja);
        dkaSpinner= (Spinner) findViewById(R.id.dkaSpinner);

        if (pozicija!=-1 && KvizoviAkt.odabraniKvizovi.get(pozicija).getPitanja()!=null) {
            pitanjaKviza=  KvizoviAkt.odabraniKvizovi.get(pozicija).getPitanja();
            naziv_kviza=getIntent().getExtras().getString("naziv_kviza");
            editText.setText(naziv_kviza);
            kopijaPitanjaKviza=  kopirajPitanja(kopijaPitanjaKviza, pitanjaKviza);

        }

        if ((pitanjaKviza.size()>0 && pitanjaKviza.get(pitanjaKviza.size()-1).getNaziv()!="dummy") || pitanjaKviza.size()==0) {
            kopijaPitanjaKviza.clear();
            System.out.println(kopijaPitanjaKviza.size() + "kopija pitanja kviza");
            pitanjaKviza.add(new Pitanje ("dummy", "dummy", "dummy", null));
            kopijaPitanjaKviza=  kopirajPitanja(kopijaPitanjaKviza, pitanjaKviza);
        }

       kopijaMogucihPitanja= kopirajPitanja(x, KvizoviAkt.mogPitanja);

          mogucaAdapter= new MogucaListAdapter(this, kopijaMogucihPitanja , getResources());
          pitanjaAdapter= new PitanjaListAdapter(this,kopijaPitanjaKviza, getResources());

        dkaSpinner.setOnItemSelectedListener(this);
        kategorije= modificirajSpiner(KvizoviAkt.getCategories());
       ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, kategorije );

        dkaSpinner.setAdapter(dataAdapter);
        dkaSpinner.setSelection(poz_kat); //postavka pozicije

         listaPitanja.setAdapter(pitanjaAdapter);
          if (kopijaMogucihPitanja.size()==0)  listaMogucih.setAdapter(null);
          else  listaMogucih.setAdapter(mogucaAdapter);


        dodaj.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                 validacija();
                System.out.println(valid);
           if(pozicija==-1 && valid==true) {
                Bundle b= new Bundle();
                Intent addKviz=getIntent();
                b.putSerializable("listaPitanja", (Serializable) kopijaPitanjaKviza);
                addKviz.putExtras (b);
                KvizoviAkt.mogPitanja=kopijaMogucihPitanja;
                addKviz.putExtra ("kategorija",  dkaSpinner.getSelectedItemPosition());
                addKviz.putExtra ("naziv", editText.getText().toString());
               setResult(Activity.RESULT_OK,addKviz);
               System.out.println("aaaa");
                finish();
            }
            else if (pozicija!=-1 && valid==true) {
               Bundle b= new Bundle();
               Intent addKviz=getIntent();
               b.putSerializable("listaPitanja", (Serializable) kopijaPitanjaKviza);
               addKviz.putExtras (b);
               KvizoviAkt.mogPitanja=kopijaMogucihPitanja;
               addKviz.putExtra ("kategorija",  dkaSpinner.getSelectedItemPosition());
               addKviz.putExtra ("naziv", editText.getText().toString());
               addKviz.putExtra ("pozicija", pozicija);
               setResult(Activity.RESULT_FIRST_USER,addKviz);
               pozicija=-1;
               finish();
           }


            }});

        listaMogucih.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                if (kopijaPitanjaKviza.size()>0) kopijaPitanjaKviza.remove(kopijaPitanjaKviza.size()-1);
                kopijaPitanjaKviza.add (kopijaMogucihPitanja.get(position));
                kopijaMogucihPitanja.remove(position);
                   refresh();
                mogucaAdapter= new MogucaListAdapter(dkaAkk, kopijaMogucihPitanja, getResources());
               if (kopijaMogucihPitanja.size()==0)  listaMogucih.setAdapter(null);
                else listaMogucih.setAdapter(mogucaAdapter);
                listaPitanja.setAdapter(pitanjaAdapter);

            }

        });

        listaPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
             if (position< kopijaPitanjaKviza.size()-1) {
                 kopijaMogucihPitanja.add(kopijaPitanjaKviza.get(position));
                 kopijaPitanjaKviza.remove(position);
                 listaPitanja.setAdapter(pitanjaAdapter);
                 listaMogucih.setAdapter(mogucaAdapter);
             }
             else if (position== kopijaPitanjaKviza.size()-1) {
                 Intent dodajPitanje = new Intent(DodajKvizAkt.this, DodajPitanjeAkt.class);
                 DodajKvizAkt.this.startActivityForResult(dodajPitanje, 0 );
             }
            }

        });
    }

    private  ArrayList<String> modificirajSpiner(ArrayList<String> mod) {
        ArrayList<String> x= new ArrayList<>();
       for (String a: mod) {
           x.add(a);
       }
       x.add("Dodaj kategoriju");
       return x;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

           kopijaMogucihPitanja=  KvizoviAkt.mogPitanja;

            kopijaPitanjaKviza=  pitanjaKviza;
        }
        return super.onKeyDown(keyCode, event);
    }

    void validacija () {

        if (kopijaPitanjaKviza.size()<=1) listaPitanja.setBackgroundColor(Color.RED);
        else {

            listaPitanja.setBackgroundColor(Color.WHITE);

        }
        if (editText.getText().length()==0) editText.setBackgroundColor(Color.RED);
        else {

            editText.setBackgroundColor(Color.WHITE);

        }
        if (dkaSpinner.getSelectedItemPosition()==0 || dkaSpinner.getSelectedItemPosition()==KvizoviAkt.getCategories().size()) dkaSpinner.setBackgroundColor(Color.RED);
        else {

            dkaSpinner.setBackgroundColor(Color.WHITE);

        }
        if ( !(kopijaPitanjaKviza.size()<=1) && !(editText.getText().length()==0) && !(dkaSpinner.getSelectedItemPosition()==0 || dkaSpinner.getSelectedItemPosition()==KvizoviAkt.getCategories().size())) valid=true;
    }

    void refresh() {
        kopijaPitanjaKviza.add(new Pitanje ("dummy", "dummy", "dummy", null));
    }

    void shut() {
        kopijaPitanjaKviza.remove(kopijaPitanjaKviza.size()-1);
    }

    @Override
    public void onItemSelected (AdapterView< ? > parent, View view, int position, long id){
        String item = parent.getItemAtPosition(position).toString();

        if (parent.getSelectedItemPosition()== kategorije.size()-1) {
            Intent katAkt= new Intent (DodajKvizAkt.this, DodajKategorijuAkt.class);
            DodajKvizAkt.this.startActivity(katAkt);
        }
    }

    @Override
    public void onNothingSelected (AdapterView < ? > parent){

    }

    public ArrayList<Pitanje> kopirajPitanja (ArrayList<Pitanje> a , ArrayList<Pitanje> b) {
        if (b==null) return null;
     for (Pitanje x: b) {
            a.add(x);
       }
        return a;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_CANCELED){
            kopijaPitanjaKviza.remove (kopijaPitanjaKviza.size()-1);
              kopijaPitanjaKviza.add(new Pitanje(data.getStringExtra("naziv"), data.getStringExtra("naziv"), data.getStringExtra("tacan"), data.getStringArrayListExtra("pitanje")));
              refresh();
            listaPitanja.setAdapter(pitanjaAdapter);

        }


    }
}
