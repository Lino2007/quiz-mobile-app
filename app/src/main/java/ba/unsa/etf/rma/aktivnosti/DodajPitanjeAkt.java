package ba.unsa.etf.rma.aktivnosti;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.OdgovoriListAdapter;

public class DodajPitanjeAkt extends AppCompatActivity {

    private Button dodaj, dodajTacan, dodajPitanje;
    private EditText nazivPitanja, odgovor;
    private ListView lvOdgovori;
    ArrayList<String> listaOdgovora = new ArrayList<>();
    String tacan = null;
    boolean valid = false;
    OdgovoriListAdapter odgovoriAdapter = null;
    int pozicijaTacnog=-1;
    public static int poz = -1;
    ArrayList<String> zaValidaciju= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_pitanje_akt);

        dodaj = (Button) findViewById(R.id.btnDodajOdgovor);
        dodajTacan = (Button) findViewById(R.id.btnDodajTacan);
        dodajPitanje = (Button) findViewById(R.id.btnDodajPitanje);
        nazivPitanja = (EditText) findViewById(R.id.etNaziv);
        odgovor = (EditText) findViewById(R.id.etOdgovor);
        lvOdgovori = (ListView) findViewById(R.id.lvOdgovori);
        zaValidaciju= getIntent().getStringArrayListExtra("zaValidaciju");
        odgovoriAdapter = new OdgovoriListAdapter(this, listaOdgovora, getResources());
        if (listaOdgovora == null) lvOdgovori.setAdapter(null);

        dodajTacan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Prosljedjuje se pozicija tacnog radi obojenja
                if (tacan == null && odgovor.getText().toString().length() > 0 && (listaOdgovora.indexOf(odgovor.getText().toString()) == -1 || listaOdgovora.isEmpty())) {
                    poz = listaOdgovora.size();
                    pozicijaTacnog= listaOdgovora.size();
                    tacan = odgovor.getText().toString();
                    listaOdgovora.add(tacan);
                    lvOdgovori.setAdapter(odgovoriAdapter);
                    lvOdgovori.setBackgroundColor(Color.WHITE);
                    odgovor.setBackgroundColor(Color.WHITE);
                }
                else {
                    odgovor.setBackgroundColor(Color.parseColor("#E85F41"));
                }
            }
        });

       odgovor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               odgovor.setBackgroundColor(Color.WHITE);
            }
            });

       nazivPitanja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nazivPitanja.setBackgroundColor(Color.WHITE);
            }
        });

        dodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (listaOdgovora.indexOf(odgovor.getText().toString())==-1 && odgovor.getText().toString().length() > 0) {
                listaOdgovora.add(odgovor.getText().toString());
                lvOdgovori.setAdapter(odgovoriAdapter);
                     lvOdgovori.setBackgroundColor(Color.WHITE);
                     odgovor.setBackgroundColor(Color.WHITE);
                 }
                 else {
                      odgovor.setBackgroundColor(Color.parseColor("#E85F41"));
                 }
            }
        });

        dodajPitanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validiraj();
                if (valid == true) {
                    Intent vratiPitanje = getIntent();
                    vratiPitanje.putExtra("pitanje", listaOdgovora);
                    vratiPitanje.putExtra("naziv", nazivPitanja.getText().toString());
                    vratiPitanje.putExtra("tacan", tacan);
                    setResult(-200, vratiPitanje);
                    poz=-1;
                    finish();
                }

            }
        });

        lvOdgovori.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!(listaOdgovora.isEmpty())) {
                    listaOdgovora.remove(position);
                    if (position==pozicijaTacnog) {
                        tacan=null;
                        poz=-1;
                    }
                  if(listaOdgovora.isEmpty())   {
                      lvOdgovori.setAdapter(null);
                      tacan=null;
                  }
                  else lvOdgovori.setAdapter(odgovoriAdapter);
                    lvOdgovori.setBackgroundColor(Color.WHITE);

                }
            }

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) setResult(Activity.CONTEXT_INCLUDE_CODE);
        return super.onKeyDown(keyCode, event);
    }

    void validiraj() {
        if (nazivPitanja.getText().toString().isEmpty()) {
            nazivPitanja.setBackgroundColor(Color.parseColor("#E85F41"));
        } else {
            nazivPitanja.setBackgroundColor(Color.WHITE);
        }
        if (  nazivPitanja.getText().toString().isEmpty() || zaValidaciju.indexOf(nazivPitanja.getText().toString())!=-1 ) {
            nazivPitanja.setBackgroundColor(Color.parseColor("#E85F41"));
        } else {
            nazivPitanja.setBackgroundColor(Color.WHITE);
        }
        if (listaOdgovora.isEmpty() || tacan == null) {
            lvOdgovori.setBackgroundColor(Color.parseColor("#E85F41"));
        } else {
            lvOdgovori.setBackgroundColor(Color.WHITE);
        }
        if (!(listaOdgovora.isEmpty()) && !(nazivPitanja.getText().toString().isEmpty()) && tacan != null && zaValidaciju.indexOf(nazivPitanja.getText().toString())==-1) {
            valid = true;
        }
    }
}
