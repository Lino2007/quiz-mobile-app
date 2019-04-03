package ba.unsa.etf.rma.aktivnosti;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.OdgovoriListAdapter;

public class DodajPitanjeAkt extends AppCompatActivity {

  private  Button dodaj, dodajTacan, dodajPitanje;
  private EditText nazivPitanja, odgovor;
  private ListView lvOdgovori;
  ArrayList<String> listaOdgovora= new ArrayList<> ();
  OdgovoriListAdapter odgovoriAdapter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_pitanje_akt);

        dodaj= (Button)  findViewById (R.id.btnDodajOdgovor);
        dodajTacan = (Button) findViewById(R.id.btnDodajTacan);
        dodajPitanje= (Button) findViewById(R.id.btnDodajPitanje);
        nazivPitanja= (EditText) findViewById(R.id.etNaziv);
        odgovor= (EditText) findViewById(R.id.etOdgovor);
        lvOdgovori= (ListView) findViewById(R.id.lvOdgovori);

        //  mainListAdapter= new MainListAdapter (kvizoviAkt,listaKvizova, res);
        odgovoriAdapter= new OdgovoriListAdapter(this,listaOdgovora, getResources());

        if (listaOdgovora==null)  lvOdgovori.setAdapter(odgovoriAdapter);



    }
}
