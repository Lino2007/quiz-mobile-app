package ba.unsa.etf.rma.aktivnosti;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.OdgovoriListAdapter;

public class DodajPitanjeAkt extends AppCompatActivity {

  private  Button dodaj, dodajTacan, dodajPitanje;
  private EditText nazivPitanja, odgovor;
  private ListView lvOdgovori;
  ArrayList<String> listaOdgovora= new ArrayList<> ();
  String tacan=null;
  boolean valid=false;
  OdgovoriListAdapter odgovoriAdapter=null;
  public static int poz=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_pitanje_akt);
       // poz=-1;
        dodaj= (Button)  findViewById (R.id.btnDodajOdgovor);
        dodajTacan = (Button) findViewById(R.id.btnDodajTacan);
        dodajPitanje= (Button) findViewById(R.id.btnDodajPitanje);
        nazivPitanja= (EditText) findViewById(R.id.etNaziv);
        odgovor= (EditText) findViewById(R.id.etOdgovor);
        lvOdgovori= (ListView) findViewById(R.id.lvOdgovori);

        odgovoriAdapter= new OdgovoriListAdapter(this,listaOdgovora,getResources());

        if (listaOdgovora==null)  lvOdgovori.setAdapter(null);


        dodajTacan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (tacan==null &&  odgovor.getText().toString().length()>0 && listaOdgovora.indexOf(odgovor.getText().toString())==-1) {
                    poz=listaOdgovora.size();
                    tacan= odgovor.getText().toString();
                    listaOdgovora.add(tacan);
                    lvOdgovori.setAdapter(odgovoriAdapter);
                    //poz=-1;
                }
            }});


        dodaj.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
              //  if (listaOdgovora.indexOf(odgovor.getText().toString())==-1) { // U SLUCAJU AKO JE TRAZENO DA ODGOVORI MORAJU BITI RAZLICITI
                    listaOdgovora.add(odgovor.getText().toString());
                    lvOdgovori.setAdapter(odgovoriAdapter);
               // }
            }});

   /*     dodaj.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (listaOdgovora.indexOf(odgovor.getText().toString())==-1) {
                    listaOdgovora.add(odgovor.getText().toString());
                    lvOdgovori.setAdapter(odgovoriAdapter);
                }
            }});
 */
        dodajPitanje.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                 validiraj();
                 if(valid==true) {
                     Intent vratiPitanje = getIntent();
                     vratiPitanje.putExtra ("pitanje", listaOdgovora);
                     vratiPitanje.putExtra("naziv", nazivPitanja.getText().toString());
                     vratiPitanje.putExtra ("tacan", tacan);
                     setResult(Activity.RESULT_CANCELED, vratiPitanje);
                     finish();
                 }

            }});


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

          setResult(Activity.CONTEXT_INCLUDE_CODE);
        }
        return super.onKeyDown(keyCode, event);
    }

     void validiraj () {
         if (nazivPitanja.getText().toString().isEmpty()) {
             nazivPitanja.setBackgroundColor(Color.RED);
         }
         else  {
             nazivPitanja.setBackgroundColor(Color.WHITE);

         }

         if (listaOdgovora.isEmpty() || tacan==null) {
             lvOdgovori.setBackgroundColor(Color.RED);
         }
         else  {
            lvOdgovori.setBackgroundColor(Color.WHITE);
         }

         if(!(listaOdgovora.isEmpty()) && !(nazivPitanja.getText().toString().isEmpty()) && tacan!=null) valid=true;

     }
}
