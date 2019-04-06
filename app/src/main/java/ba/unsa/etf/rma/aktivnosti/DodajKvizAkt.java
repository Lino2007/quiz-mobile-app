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
    private boolean valid = false;
    public PitanjaListAdapter pitanjaAdapter = null;
    public MogucaListAdapter mogucaAdapter = null;
    public String naziv_kviza = null;
    public int pozicija = -1;
    public int poz_kat = 0;
    private ArrayList<Pitanje> pitanjaKviza = new ArrayList<>();
    private ArrayList<String> listaKvizova = new ArrayList<>();
    ArrayList<Pitanje> x = new ArrayList<>();
    private ArrayList<String> kategorije = new ArrayList<>();
    public ArrayList<Pitanje> kopijaPitanjaKviza = new ArrayList<>();
    public ArrayList<Pitanje> kopijaMogucihPitanja = new ArrayList<>();
    DodajKvizAkt dkaAkk = null;
    private boolean refreshKat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz_akt);
        uloadujListuKvizova();
        dkaAkk = this;
        pozicija = getIntent().getExtras().getInt("poz_kviza");
        poz_kat = getIntent().getExtras().getInt("poz_kategorije");
        //  pitanjaKviza.clear();
        dodaj = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        listaMogucih = (ListView) findViewById(R.id.lvMogucaPitanja);
        listaPitanja = (ListView) findViewById(R.id.lvDodanaPitanja);
        dkaSpinner = (Spinner) findViewById(R.id.spKategorije);

        if (pozicija != -1 && KvizoviAkt.odabraniKvizovi.get(pozicija).getPitanja() != null) {
            pitanjaKviza = KvizoviAkt.odabraniKvizovi.get(pozicija).getPitanja();
            naziv_kviza = getIntent().getExtras().getString("naziv_kviza");
            editText.setText(naziv_kviza);
            kopijaPitanjaKviza = kopirajPitanja(kopijaPitanjaKviza, pitanjaKviza);

        }

        if ((pitanjaKviza.size() > 0 && /* pitanjaKviza.get(pitanjaKviza.size() - 1).getNaziv() != "dummy"*/   pitanjaKviza.get(pitanjaKviza.size() - 1).getNaziv() != null) || pitanjaKviza.size() == 0) {
            kopijaPitanjaKviza.clear();

            //    pitanjaKviza.add(new Pitanje("dummy", "dummy", "dummy", null));
            pitanjaKviza.add(new Pitanje(null, null, null, null));
            kopijaPitanjaKviza = kopirajPitanja(kopijaPitanjaKviza, pitanjaKviza);
        }

        //    kopijaMogucihPitanja= kopirajPitanja(x, null);

        mogucaAdapter = new MogucaListAdapter(this, kopijaMogucihPitanja, getResources());
        pitanjaAdapter = new PitanjaListAdapter(this, kopijaPitanjaKviza, getResources());

        dkaSpinner.setOnItemSelectedListener(this);
        kategorije = modificirajSpiner(KvizoviAkt.getCategories());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, kategorije);

        dkaSpinner.setAdapter(dataAdapter);
        dkaSpinner.setSelection(poz_kat); //postavka pozicije

        listaPitanja.setAdapter(pitanjaAdapter);
        if (kopijaMogucihPitanja.isEmpty()) listaMogucih.setAdapter(null);
        else listaMogucih.setAdapter(mogucaAdapter);


        dodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validacija();

                if (pozicija == -1 && valid == true) {
                    Bundle b = new Bundle();
                    Intent addKviz = getIntent();
                    b.putSerializable("listaPitanja", (Serializable) kopijaPitanjaKviza);
                    addKviz.putExtras(b);
                    //KvizoviAkt.mogPitanja=kopijaMogucihPitanja;
                    addKviz.putExtra("kategorija", dkaSpinner.getSelectedItemPosition());
                    addKviz.putExtra("naziv", editText.getText().toString());
                    setResult(Activity.RESULT_OK, addKviz);

                    finish();
                } else if (pozicija != -1 && valid == true) {
                    Bundle b = new Bundle();
                    Intent addKviz = getIntent();
                    b.putSerializable("listaPitanja", (Serializable) kopijaPitanjaKviza);
                    addKviz.putExtras(b);
                    // KvizoviAkt.mogPitanja=kopijaMogucihPitanja;
                    addKviz.putExtra("kategorija", dkaSpinner.getSelectedItemPosition());
                    addKviz.putExtra("naziv", editText.getText().toString());
                    addKviz.putExtra("pozicija", pozicija);
                    setResult(Activity.RESULT_FIRST_USER, addKviz);
                    pozicija = -1;
                    finish();
                }


            }
        });

        listaMogucih.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (kopijaPitanjaKviza.size() > 0)
                    kopijaPitanjaKviza.remove(kopijaPitanjaKviza.size() - 1);
                kopijaPitanjaKviza.add(kopijaMogucihPitanja.get(position));
                kopijaMogucihPitanja.remove(position);
                refresh();
                mogucaAdapter = new MogucaListAdapter(dkaAkk, kopijaMogucihPitanja, getResources());
                if (kopijaMogucihPitanja.size() == 0) listaMogucih.setAdapter(null);
                else listaMogucih.setAdapter(mogucaAdapter);
                listaPitanja.setAdapter(pitanjaAdapter);

            }

        });

        listaPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < kopijaPitanjaKviza.size() - 1) {
                    kopijaMogucihPitanja.add(kopijaPitanjaKviza.get(position));
                    kopijaPitanjaKviza.remove(position);
                    listaPitanja.setAdapter(pitanjaAdapter);
                    listaMogucih.setAdapter(mogucaAdapter);
                } else if (position == kopijaPitanjaKviza.size() - 1) {
                    ArrayList<String> zaValidaciju = new ArrayList<>();
                    Intent dodajPitanje = new Intent(DodajKvizAkt.this, DodajPitanjeAkt.class);
                    for (Pitanje x : kopijaMogucihPitanja) {
                        zaValidaciju.add(x.getNaziv());
                    }
                    for (Pitanje x : kopijaPitanjaKviza) {
                        zaValidaciju.add(x.getNaziv());
                    }
                    dodajPitanje.putStringArrayListExtra("zaValidaciju", zaValidaciju);
                    DodajKvizAkt.this.startActivityForResult(dodajPitanje, 0);
                }
            }

        });
    }

    private ArrayList<String> modificirajSpiner(ArrayList<String> mod) {
        ArrayList<String> x = new ArrayList<>();
        for (String a : mod) {
            x.add(a);
        }
        x.add("Dodaj kategoriju");
        return x;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent x = getIntent();
            //  kopijaMogucihPitanja=  KvizoviAkt.mogPitanja;
            setResult(10000, x);
            kopijaPitanjaKviza = pitanjaKviza;
            if (refreshKat) {
                setResult(9000, x);
                refreshKat = false;

            }

        }
        return super.onKeyDown(keyCode, event);
    }

    void uloadujListuKvizova() {
        for (Kviz a : KvizoviAkt.listaKvizova) {
            listaKvizova.add(a.getNaziv());
        }

    }

    void validacija() {
        boolean aPoint = false, bPoint = false, cPoint = false;
        if (naziv_kviza == null) {

            if (editText.getText().length() == 0) editText.setBackgroundColor(Color.RED);
            else {
                editText.setBackgroundColor(Color.WHITE);
            }

            if (editText.getText().length() == 0 || !(listaKvizova.indexOf(editText.getText().toString()) == -1))
                editText.setBackgroundColor(Color.RED);
            else {
                editText.setBackgroundColor(Color.WHITE);
            }

            if (dkaSpinner.getSelectedItemPosition() == kategorije.size())
                dkaSpinner.setBackgroundColor(Color.RED);
            else {
                dkaSpinner.setBackgroundColor(Color.WHITE);
            }

            if (!(editText.getText().length() == 0) && !(dkaSpinner.getSelectedItemPosition() == kategorije.size()) && (listaKvizova.indexOf(editText.getText().toString()) == -1))
                valid = true;
        } else {
            if (editText.getText().length() == 0) editText.setBackgroundColor(Color.RED);
            else {
                editText.setBackgroundColor(Color.WHITE);
            }

            if (editText.getText().length() == 0 || ((!(naziv_kviza.equals(editText.getText().toString())) && listaKvizova.indexOf(editText.getText().toString()) != -1)))
                editText.setBackgroundColor(Color.RED);
            else {
                editText.setBackgroundColor(Color.WHITE);
                aPoint = true;
            }

            if (dkaSpinner.getSelectedItemPosition() == kategorije.size())
                dkaSpinner.setBackgroundColor(Color.RED);
            else {
                dkaSpinner.setBackgroundColor(Color.WHITE);
            }
            if (!(editText.getText().length() == 0) && aPoint && !(dkaSpinner.getSelectedItemPosition() == kategorije.size())) {
                valid = true;
                aPoint = bPoint = cPoint = false;
            }


        }
    }

    void refresh() {
        // kopijaPitanjaKviza.add(new Pitanje("dummy", "dummy", "dummy", null)); ++++++++++++++++++++++++++++++++++++++++++++++++++++
        kopijaPitanjaKviza.add(new Pitanje(null, null, null, null));
    }

    void shut() {
        kopijaPitanjaKviza.remove(kopijaPitanjaKviza.size() - 1);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();

        if (parent.getSelectedItemPosition() == kategorije.size() - 1) {
            Intent katAkt = new Intent(DodajKvizAkt.this, DodajKategorijuAkt.class);
            DodajKvizAkt.this.startActivityForResult(katAkt, 12);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public ArrayList<Pitanje> kopirajPitanja(ArrayList<Pitanje> a, ArrayList<Pitanje> b) {
        if (b == null) return null;
        for (Pitanje x : b) {
            a.add(x);
        }
        return a;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_CANCELED) {
            kopijaPitanjaKviza.remove(kopijaPitanjaKviza.size() - 1);
            kopijaPitanjaKviza.add(new Pitanje(data.getStringExtra("naziv"), data.getStringExtra("naziv"), data.getStringExtra("tacan"), data.getStringArrayListExtra("pitanje")));
            refresh();
            listaPitanja.setAdapter(pitanjaAdapter);

        } else if (resultCode == (-100)) {
            kategorije.remove(kategorije.size() - 1);
            kategorije.add(KvizoviAkt.listaKategorija.get(KvizoviAkt.listaKategorija.size() - 1).getNaziv());
            kategorije.add("Dodaj kategoriju");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, kategorije);
            dkaSpinner.setAdapter(dataAdapter);
            dkaSpinner.setSelection(0);
            refreshKat = true;
        } else if (resultCode == (-300)) {
            //do nothing please
            dkaSpinner.setSelection(0);
        }


    }
}
