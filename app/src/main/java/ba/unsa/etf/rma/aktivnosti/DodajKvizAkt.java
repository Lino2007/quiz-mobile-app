package ba.unsa.etf.rma.aktivnosti;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.SQLOutput;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.adapteri.MogucaListAdapter;
import ba.unsa.etf.rma.adapteri.PitanjaListAdapter;
import ba.unsa.etf.rma.klase.Pitanje;


public class DodajKvizAkt extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText editText;
    private ListView listaPitanja, listaMogucih;
    private Spinner dkaSpinner;
    private Button dodaj,importKviz;
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

        File file = new File (Environment.getExternalStorageDirectory(), "test.csv");


        dodaj = (Button) findViewById(R.id.btnDodajKviz);
        editText = (EditText) findViewById(R.id.etNaziv);
        listaMogucih = (ListView) findViewById(R.id.lvMogucaPitanja);
        listaPitanja = (ListView) findViewById(R.id.lvDodanaPitanja);
        dkaSpinner = (Spinner) findViewById(R.id.spKategorije);
        importKviz= (Button) findViewById(R.id.btnImportKviz);




        if (pozicija != -1 && KvizoviAkt.odabraniKvizovi.get(pozicija).getPitanja() != null) {
            pitanjaKviza = KvizoviAkt.odabraniKvizovi.get(pozicija).getPitanja();
            naziv_kviza = getIntent().getExtras().getString("naziv_kviza");
            editText.setText(naziv_kviza);
            kopijaPitanjaKviza = kopirajPitanja(kopijaPitanjaKviza, pitanjaKviza);
        }

        if ((pitanjaKviza.size() > 0 && pitanjaKviza.get(pitanjaKviza.size() - 1).getNaziv() != null) || pitanjaKviza.size() == 0) {
            kopijaPitanjaKviza.clear();
            pitanjaKviza.add(new Pitanje(null, null, null, null));
            kopijaPitanjaKviza = kopirajPitanja(kopijaPitanjaKviza, pitanjaKviza);
        }

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
                    addKviz.putExtra("kategorija", dkaSpinner.getSelectedItemPosition());
                    addKviz.putExtra("naziv", editText.getText().toString());
                    setResult(-32, addKviz);
                    finish();
                } else if (pozicija != -1 && valid == true) {
                    Bundle b = new Bundle();
                    Intent addKviz = getIntent();
                    b.putSerializable("listaPitanja", (Serializable) kopijaPitanjaKviza);
                    addKviz.putExtras(b);
                    addKviz.putExtra("kategorija", dkaSpinner.getSelectedItemPosition());
                    addKviz.putExtra("naziv", editText.getText().toString());
                    addKviz.putExtra("pozicija", pozicija);
                    setResult(-133, addKviz);
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

        importKviz.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent importIntent = new Intent (Intent.ACTION_OPEN_DOCUMENT);
                importIntent.addCategory(Intent.CATEGORY_OPENABLE);
                importIntent.setType("text/*");
                startActivityForResult(importIntent, 1999);
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
            if (editText.getText().length() == 0)
                editText.setBackgroundColor(Color.parseColor("#E85F41"));
            else {
                editText.setBackgroundColor(Color.WHITE);
            }

            if (editText.getText().length() == 0 || !(listaKvizova.indexOf(editText.getText().toString()) == -1))
                editText.setBackgroundColor(Color.parseColor("#E85F41"));
            else {
                editText.setBackgroundColor(Color.WHITE);
            }

            if (dkaSpinner.getSelectedItemPosition() == kategorije.size())
                dkaSpinner.setBackgroundColor(Color.parseColor("#E85F41"));
            else {
                dkaSpinner.setBackgroundColor(Color.WHITE);
            }

            if (!(editText.getText().length() == 0) && !(dkaSpinner.getSelectedItemPosition() == kategorije.size()) && (listaKvizova.indexOf(editText.getText().toString()) == -1))
                valid = true;
        } else {
            if (editText.getText().length() == 0)
                editText.setBackgroundColor(Color.parseColor("#E85F41"));
            else {
                editText.setBackgroundColor(Color.WHITE);
            }

            if (editText.getText().length() == 0 || ((!(naziv_kviza.equals(editText.getText().toString())) && listaKvizova.indexOf(editText.getText().toString()) != -1)))
                editText.setBackgroundColor(Color.parseColor("#E85F41"));
            else {
                editText.setBackgroundColor(Color.WHITE);
                aPoint = true;
            }

            if (dkaSpinner.getSelectedItemPosition() == kategorije.size())
                dkaSpinner.setBackgroundColor(Color.parseColor("#E85F41"));
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
        //-200: dodavanje novog pitanja , -300: Povratak back buttonom , -100: Dodavanje kategorije
        if (resultCode == (-200)) {
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
            dkaSpinner.setSelection(kategorije.size() - 2);
            refreshKat = true;
        } else if (resultCode == (-300)) {
            dkaSpinner.setSelection(0);
        }
        else if (requestCode==1999 && resultCode == Activity.RESULT_OK) {
            Uri uri= null;
          if (data!=null) {
                uri= data.getData();
                parsirajCSV(uri);
            }

        }
    }

    private String getTextFromUri (Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }
        inputStream.close();
        stringBuilder.setLength(stringBuilder.length()-1);
        return stringBuilder.toString();

    }

    private int dajBrojLinija (String str) {
        String[] brLinija= str.split("\r\n|\r|\n");
        return brLinija.length;
    }

    private void pozoviAlert (String a, String b) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle(a)
                .setMessage(b)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.show();

    }

    private void parsirajCSV (Uri uri) {
        if (uri==null) return ;
        final String DEFAULT_ICON= "1000";

         String parsedString= new String();
        try {
            parsedString = getTextFromUri(uri);
        }
        catch (Exception e) {
            System.out.println("Nesto nije uredu sa fajlom");
            System.out.println("Podaci o problemu: "  +  e);

        }


        if (parsedString.isEmpty())  return ;
        String testStr= new String (parsedString);
         int brojPitanja= dajBrojLinija(testStr)-1;



        String [] parsedList= parsedString.split(System.lineSeparator());



        //Parsiranje prvog reda
        if (parsedList==null || parsedList.length==0) return ;
        String [] prviRed= parsedList[0].split(",");
        if (prviRed.length!=3) {
            System.out.println("Nesto nije uredu");
            return ;
        }
        String nazivKviza, kategorija;
        nazivKviza = prviRed[0];    kategorija= prviRed[1];



        int brPitanja=-1;
        try {
            brPitanja=Integer.parseInt(prviRed[2]);
        }
        catch (Exception e){
            pozoviAlert("Greska pri importu", "Kviz kojeg imporujete ima neispravan broj pitanja!" );
            return ;
        }
        int index= listaKvizova.indexOf(prviRed[0]);
        if (index!=-1 || nazivKviza.length()<1) {
            pozoviAlert("Greska pri importu", "Kviz kojeg importujete već postoji!" );
            return ;
        }



        if (brPitanja!= parsedList.length-1) {
            pozoviAlert("Greska pri importu", "Kviz kojeg imporujete ima neispravan broj pitanja!" );
            return ;
        }
        int i=1;
        boolean isItOkay=true, tacanPostoji=true;
        ArrayList<String> zaOdgovore= new ArrayList<>();
        ArrayList<String> validPitanja= new ArrayList<>();
        ArrayList<Pitanje> zaPitanja= new ArrayList<>();
      //  String tacan= new String();
        int tacan=-1;
        int brojOdgovora=-1;
        String tacanOdgovor= new String();
        while (i<parsedList.length) {

            String[] ostali= parsedList[i].split(",");
            if (ostali.length<4) {
                pozoviAlert("Greska pri importu", "Kviz kojeg imporujete ima neispravan broj pitanja!" );
                isItOkay=false;
                break;
            }
            if (validPitanja!=null && validPitanja.indexOf(ostali[0])!=-1) {
                pozoviAlert("Greska pri importu", "Kviz nije ispravan postoje dva pitanja sa istim nazivom!" );
                isItOkay=false;
                break;
            }
            try {
                brojOdgovora= Integer.parseInt(ostali[1]);
            }
            catch (Exception e) {
                pozoviAlert("Greska pri importu", "Kviz kojeg importujete ima neispravan broj odgovora!");
                isItOkay=false;
                break;
            }
              if (brojOdgovora<1 || brojOdgovora!= ostali.length-3) {
                  pozoviAlert("Greska pri importu", "Kviz kojeg importujete ima neispravan broj odgovora!");
                  isItOkay=false;
                  break;
              }
              try {
                  tacan = Integer.parseInt(ostali[2]);
              }
              catch (Exception e) {
                  pozoviAlert("Greska pri importu", "Kviz kojeg importujete ima neispravan index tačnog odgovora!");
                  isItOkay=false;
                  break;
              }
              if (tacan<0 ||  tacan>brojOdgovora) {
                  pozoviAlert("Greska pri importu", "Kviz kojeg importujete ima neispravan index tačnog odgovora!");
                  break;
              }
              for (int j=3; j<brojOdgovora+3; j++ ) {
                  zaOdgovore.add (ostali[j]);
                  if (j-3==tacan) tacanOdgovor=ostali[j];
              }

              validPitanja.add (ostali[0]);
              zaPitanja.add (new Pitanje (ostali[0], ostali[0], tacanOdgovor, copy(zaOdgovore)));

              zaOdgovore.clear();
              tacan=-1;
              tacanOdgovor=null;
            i++;
        }

        int indKat= kategorije.indexOf(kategorija);
          if (indKat==-1) {
              KvizoviAkt.listaKategorija.add ( new Kategorija(kategorija,DEFAULT_ICON));
              kategorije.remove(kategorije.size() - 1);
              kategorije.add(KvizoviAkt.listaKategorija.get(KvizoviAkt.listaKategorija.size() - 1).getNaziv());
              kategorije.add("Dodaj kategoriju");
              indKat= kategorije.indexOf(kategorija);
              ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, kategorije);
              dkaSpinner.setAdapter(dataAdapter);
        }

        if (isItOkay) {
                dkaSpinner.setSelection(indKat);
                kopijaPitanjaKviza.clear();
               kopijaPitanjaKviza= kopirajPitanja(kopijaPitanjaKviza,zaPitanja);
               kopijaPitanjaKviza.add (null);
                pitanjaAdapter = new PitanjaListAdapter(this, kopijaPitanjaKviza, getResources());
                listaPitanja.setAdapter(pitanjaAdapter);
                listaMogucih.setAdapter(null);
                editText.setText(nazivKviza);
            }



    }

    private ArrayList<String> copy (ArrayList<String> base) {
        if (base.size()==0) return null;
        ArrayList<String> izlaz= new ArrayList<>();
        for (String x: base) {
            izlaz.add (x);
        }
        return izlaz;
    }
}