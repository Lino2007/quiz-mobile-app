package ba.unsa.etf.rma.aktivnosti;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


//import com.google.api.services.sqladmin.SQLAdminScopes;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.MainListAdapter;
import ba.unsa.etf.rma.baza.KategorijaDB;
import ba.unsa.etf.rma.baza.KvizDB;
import ba.unsa.etf.rma.baza.OdgovorDB;
import ba.unsa.etf.rma.baza.PitanjeDB;
import ba.unsa.etf.rma.baza.RanglistaDB;
import ba.unsa.etf.rma.fragmenti.DetailFrag;
import ba.unsa.etf.rma.fragmenti.ListaFrag;
import ba.unsa.etf.rma.klase.CalendarProvider;
import ba.unsa.etf.rma.klase.Firebase;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.NetworkChangeReceiver;
import ba.unsa.etf.rma.klase.NetworkUtil;
import ba.unsa.etf.rma.klase.Pitanje;

import static ba.unsa.etf.rma.baza.KategorijaDB.IKONICA_ID;
import static ba.unsa.etf.rma.baza.KvizDB.KATEGORIJA_ID;
import static ba.unsa.etf.rma.baza.KvizDB.KOLONA_ID;
import static ba.unsa.etf.rma.baza.KvizDB.KVIZ_ID;
import static ba.unsa.etf.rma.baza.OdgovorDB.ODGOVOR_ID;
import static ba.unsa.etf.rma.baza.PitanjeDB.KVIZ_FK;
import static ba.unsa.etf.rma.baza.PitanjeDB.PITANJE_ID;
import static ba.unsa.etf.rma.baza.PitanjeDB.TACAN_ODGOVOR;


public class KvizoviAkt extends AppCompatActivity implements OnItemSelectedListener, ListaFrag.ListUpdater, DetailFrag.ListFunction, Firebase.ProvjeriStatus, Firebase.Rangliste {


    //Statusni signali za poziv metoda u firebase-u
    public enum OCstatus {
        UNDEFINED, ADD_PITANJE, ADD_KVIZ, EDIT_KVIZ, GET_MOGUCA, GET_KATEGORIJE, ADD_KAT, GET_DB_CONTENT, GET_SPINNER_CONTENT, V_GET_KATEGORIJE, GET_RL, ADD_RL, IMPORT_PITANJA_CHECK, IMPORT_PITANJA_ADD, GET_ALL_RL, UPDATE_RL
    }

    //instanca baze podataka
    KvizDB kvizDB;
    OdgovorDB odgovorDB;
    PitanjeDB pitanjeDB;
    KategorijaDB kategorijaDB;
    public static ArrayList<Kategorija> listaKategorija = new ArrayList<>();
    public static ArrayList<String> categories = new ArrayList<>();
    public KvizoviAkt kvizoviAkt;
    public static ArrayList<Kviz> odabraniKvizovi = new ArrayList<>();  //Odabrani kvizovi vise nemaju nikakvu funkciju (po dodavanju baze), ali bi oduzelo dosta vremena da se uklone
    private String trenutnaKategorija = new String();
    private ListView mainList;
    public MainListAdapter mainListAdapter = null;
    public static ArrayList<Kviz> listaKvizova = new ArrayList<>();
    private Spinner spinner;
    public static int jedinica = 1;
    private FrameLayout detail, lista;
    public static String TOKEN = null;
    Configuration config;
    FragmentManager fragmentm = null;
    ListaFrag lfm = null;
    DetailFrag dfm = null;
    boolean isConnected = false;
    public Intent alarmClock = null;
    final String datePattern = "yyyy-MM-dd";

    ArrayListMultimap<String, Pair<Double, String>> sveRangliste = ArrayListMultimap.create();
    RanglistaDB ranglistaDB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();


     /*   Intent bcReceiver = new Intent(KvizoviAkt.this, NetworkChangeReceiver.class);
        KvizoviAkt.this.sendBroadcast(bcReceiver); */
        ranglistaDB = new RanglistaDB(this, "Rangliste", null, 1);
        kvizDB = new KvizDB(this, "Kvizovi", null, 1);
        odgovorDB = new OdgovorDB(this, "Odgovori", null, 1);
        pitanjeDB = new PitanjeDB(this, "Pitanja", null, 1);
        kategorijaDB = new KategorijaDB(this, "Kategorije", null, 1);


        if (isConnected) {
            Toast toast = Toast.makeText(getApplicationContext(), "Ima internet konekcije!", Toast.LENGTH_SHORT);
            toast.show();
            try {
                new Firebase(OCstatus.GET_DB_CONTENT, this, (Firebase.ProvjeriStatus) KvizoviAkt.this).execute(OCstatus.GET_DB_CONTENT, "Svi");


            } catch (Exception e) {
                System.out.println("Nesto nije uredu sa pristupom tokenu!");
            }
        } else {
            clearAll();
            dobaviPodatkeIzSQL();
            popuni();
            Toast toast = Toast.makeText(getApplicationContext(), "Nema internet konekcije!", Toast.LENGTH_SHORT);
            toast.show();
        }
        //Inicijalno citanje iz baze

        trenutnaKategorija = "Svi";
        kvizoviAkt = this;
        Resources res = getResources();
        spinner = (Spinner) findViewById(R.id.spPostojeceKategorije);
        mainList = (ListView) findViewById(R.id.lvKvizovi);
        config = getResources().getConfiguration();
        fragmentm = getSupportFragmentManager();
        detail = (FrameLayout) findViewById(R.id.detailPlace);
        lista = (FrameLayout) findViewById(R.id.listPlace);

        //Postavka ponasanja pri razlicitim konfiguracijama (portrait ili landscape)
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {

            spinner.setOnItemSelectedListener(this);
            final ListView mainList = (ListView) findViewById(R.id.lvKvizovi);
            mainListAdapter = new MainListAdapter(kvizoviAkt, listaKvizova, res);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
            mainList.setAdapter(mainListAdapter);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);

            if (isConnected) {
                blokirajElemente();
                new Firebase(this).execute(OCstatus.GET_MOGUCA);
            }
            //Dugi klik
            mainList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                    if (isConnected) dugiKlik(pos);
                    return true;
                }
            });
            //Kratki klik
            mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (position != odabraniKvizovi.size() - 1) {
                        int brojPitanja = 0;
                        Intent newIntent = new Intent(KvizoviAkt.this, IgrajKvizAkt.class);
                        if (odabraniKvizovi.get(position).getPitanja() != null) {
                            brojPitanja = odabraniKvizovi.get(position).getPitanja().size();
                        }
                        SimpleDateFormat date = new SimpleDateFormat(datePattern);
                        int qDuration = (int) Math.round((double) brojPitanja / 2);
                        new CalendarProvider(KvizoviAkt.this).ucitajDogadjaje();
                        System.out.println(CalendarProvider.listaDogadjaja.size());
                        System.out.println("++++++++++++++++++++++++++++++++++");
                        if (CalendarProvider.listaDogadjaja.size() > 0) {
                            Date date2 = new Date();
                            for (Map.Entry<String, String> e : CalendarProvider.listaDogadjaja.entrySet()) {
                                long vrijemeDogadjaja = Long.parseLong((e.getKey()));
                                long krajDogadjaja = Long.parseLong((e.getValue()));
                                Date date1 = new Date(vrijemeDogadjaja);
                                Date krajDate = new Date(krajDogadjaja);

                                    long qDurInMs = TimeUnit.MINUTES.toMillis(qDuration);
                                    long tNow = date2.getTime();
                                    long tNxt = date1.getTime();
                                    long krajD = krajDate.getTime();


                               // System.out.println(tNow + "_---" + tNxt  + "---_" + alrm);
                                    //Ukoliko suma trenutnog vremena i trajanja kviza je vece od vremena kad pocinje event
                                    if (tNow + qDurInMs > tNxt && tNow < tNxt) {
                                        int alrm = (int) (TimeUnit.MILLISECONDS.toMinutes((tNow - tNxt)) );
                                        if (alrm<0) alrm*=(-1);
                                        new AlertDialog.Builder(KvizoviAkt.this)
                                                .setTitle("Pokusaj igranja")
                                                .setMessage("Imate događaj u kalendaru za " + alrm + " minuta!")
                                                .setNegativeButton(android.R.string.ok, null)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                        return;
                                    }
                                    //Ukoliko kviz zapocinje u vremenu trajanja eventa
                                    else if (tNow > tNxt && tNow < krajD) {
                                        new AlertDialog.Builder(KvizoviAkt.this)
                                                .setTitle("Pokusaj igranja")
                                                .setMessage("Trenutno je aktivan događaj u kalendaru!")
                                                .setNegativeButton(android.R.string.ok, null)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                        return;

                                    }

                            }


                        }
                        newIntent.putExtra("kviz", (Serializable) odabraniKvizovi.get(position));
                        KvizoviAkt.this.startActivityForResult(newIntent, 32000);
                    }
                }

            });

        } else {
            //Inicijalizacija fragmenata u landscape modu
            lfm = new ListaFrag();
            dfm = new DetailFrag();
            fragmentm.beginTransaction().replace(R.id.listPlace, lfm, lfm.getTag()).commit();
            fragmentm.beginTransaction().replace(R.id.detailPlace, dfm, dfm.getTag()).commit();
        }


    }

    private BroadcastReceiver internetRisiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = NetworkUtil.getConnectivityStatusString(context);
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                    onDisconnected();
                } else {
                    if (isConnected == false)
                        onConnected();
                }
            }

        }
    };


    private void clearAll() {
        listaKvizova.clear();
        odabraniKvizovi.clear();
        categories.clear();
        listaKategorija.clear();
    }

    private void ucitajRanglisteSQL() {
        //Ucitavanje rang listi u sql bazu
        try {
            SQLiteDatabase db = ranglistaDB.getWritableDatabase();
            db.execSQL("delete from Rangliste");
            ContentValues dodajRangliste = new ContentValues();
            for (Map.Entry<String, Pair<Double, String>> entry : sveRangliste.entries()) {
                dodajRangliste.put(RanglistaDB.IME_IGRACA, entry.getValue().second);
                dodajRangliste.put(RanglistaDB.PROCENAT, entry.getValue().first);
                dodajRangliste.put(RanglistaDB.KVIZ_FK, entry.getKey());
                db.insert(RanglistaDB.DATABASE_TABLE, null, dodajRangliste);
                dodajRangliste.clear();
            }

        } catch (Exception e) {
            System.out.println("##############Greska prilikom dodavanja ranglisti u lokalnu bazu: " + e);
        }


    }

    @Override
    public void getRangliste(ArrayList<String> rl) {
        //ucitavanje ranglisti, rl nema nikakvu funkciju
        ucitajRanglisteSQL();
        odblokirajElemente();
    }

    @Override
    public void ugrabiSve(ArrayListMultimap<String, Pair<Double, String>> rl) {
        sveRangliste = rl;
        ArrayListMultimap<String, Pair<Double, String>> ranglisteSQL = ugrabiSveRLizSQL();


        //Porede se rang liste iz sql-a i firebase-a, i u skladu s tim salje se lista ranglisti koje nisu ucitane u firebase-u
        for (Map.Entry<String, Pair<Double, String>> entry : sveRangliste.entries()) {
            if (ranglisteSQL.containsEntry(entry.getKey(), entry.getValue())) {
                ranglisteSQL.remove(entry.getKey(), entry.getValue());
            }
        }
        //Posto je moguce sveRangliste(firebase) ne sadrze rangliste iz sql-a, onda se one dodaju
        for (Map.Entry<String, Pair<Double, String>> entry : ranglisteSQL.entries()) {
            sveRangliste.put(entry.getKey(), new Pair<Double, String>(entry.getValue().first, entry.getValue().second));
        }
        new Firebase(OCstatus.UPDATE_RL, this, (Firebase.Rangliste) KvizoviAkt.this).execute(OCstatus.UPDATE_RL, ranglisteSQL);



    }

    private ArrayListMultimap<String, Pair<Double, String>> ugrabiSveRLizSQL() {
        //Grabimo rangliste iz sql-a
        ArrayListMultimap<String, Pair<Double, String>> rangliste = ArrayListMultimap.create();
        try {
            SQLiteDatabase db = ranglistaDB.getWritableDatabase();
            String[] koloneRezultat = new String[]{RanglistaDB.KOLONA_ID, RanglistaDB.IME_IGRACA, RanglistaDB.KVIZ_FK, RanglistaDB.PROCENAT};
            String where = null;
            String whereArgs[] = null;
            String groupBy = null;
            String having = null;
            String order = null;
            Cursor cursor = db.query(RanglistaDB.DATABASE_TABLE, koloneRezultat, where, whereArgs, groupBy, having, order);
            Log.d("SQLite Ranglista ", "Povucene rangliste iz baze , get count je : " + cursor.getCount());
            while (cursor.moveToNext()) {
                rangliste.put(cursor.getString(2), new Pair<Double, String>(Double.parseDouble(cursor.getString(3)), cursor.getString(1)));
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("Nesto nije uredu sa ucitavanjem ranglisti: " + e);
        }
        return rangliste;
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internetRisiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(internetRisiver);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Izmjena liste prilikom odabira kategorije iz spinnera
        String item = parent.getItemAtPosition(position).toString();
        config = getResources().getConfiguration();
        Toast toast = Toast.makeText(getApplicationContext(), "Ucitavaju se kvizovi, molimo sacekajte!", Toast.LENGTH_SHORT);
        toast.show();
        if (position >= 0 && position < categories.size()) {
            if (isConnected) {
                if (listaKvizova.size() > 1) {
                    blokirajElemente();
                    new Firebase(OCstatus.GET_SPINNER_CONTENT, this, (Firebase.ProvjeriStatus) KvizoviAkt.this).execute(OCstatus.GET_SPINNER_CONTENT, item);
                } else {
                    blokirajElemente();
                    new Firebase(OCstatus.GET_SPINNER_CONTENT, this, (Firebase.ProvjeriStatus) KvizoviAkt.this).execute(OCstatus.GET_SPINNER_CONTENT, item);
                }
            } else {
                if (listaKvizova.size() > 1) {
                    dajKvizoveKategorije(item);
                } else {
                    odabraniKvizovi = kopiraj(listaKvizova, odabraniKvizovi);
                }
            }
        }
    }

    private void dajKvizoveKategorije(String item) {
        //Metoda za filtraciju kvizova po kategoriji
        int i = 0;
        odabraniKvizovi.clear();
        Configuration config = getResources().getConfiguration();
        if (item != "Svi") {
            for (Kviz x : listaKvizova) {
                if (x.getKategorija() != null && x.getKategorija().getNaziv() == item) {
                    odabraniKvizovi.add(x);
                }
            }
            setTrenutnaKategorija(item);
            System.out.println(listaKvizova.size());
            odabraniKvizovi.add(listaKvizova.get(listaKvizova.size() - 1));

            if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
                mainListAdapter = new MainListAdapter(kvizoviAkt, odabraniKvizovi, getResources());
                mainList.setAdapter(mainListAdapter);
            }
            return;
        }
        odabraniKvizovi = kopiraj(listaKvizova, odabraniKvizovi);
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mainListAdapter = new MainListAdapter(kvizoviAkt, odabraniKvizovi, getResources());
            mainList.setAdapter(mainListAdapter);
        }
    }


    public void setTrenutnaKategorija(String trenutnaKategorija) {
        this.trenutnaKategorija = trenutnaKategorija;
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void blokirajElemente() {
        //Sigurnosna blokacija elemenata prilikom rada async taska
        config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mainList.setEnabled(false);
            spinner.setEnabled(false);
        } else {
            if (dfm.grid != null)
                dfm.grid.setEnabled(false);
        }
    }

    void odblokirajElemente() {
        config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mainList.setEnabled(true);
            spinner.setEnabled(true);
        } else {
            if (dfm.grid != null) {
                dfm.grid.setEnabled(true);
            }
        }

    }

    @Override
    public void dobaviSpinerPodatke(ArrayList<Kviz> oKv, ArrayList<Kviz> sKv) {
        //Pomocna metoda iz intefejsa za dobavljanje kvizova prema kategoriji
        listaKvizova = sKv;
        odabraniKvizovi = oKv;
        popuni();
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mainListAdapter = new MainListAdapter(kvizoviAkt, listaKvizova, getResources());
            mainList.setAdapter(mainListAdapter);

        } else {
            lfm = new ListaFrag();
            dfm = new DetailFrag();
            fragmentm.beginTransaction().replace(R.id.listPlace, lfm, lfm.getTag()).commitAllowingStateLoss();
            fragmentm.beginTransaction().replace(R.id.detailPlace, dfm, dfm.getTag()).commitAllowingStateLoss();
        }

        odblokirajElemente();
    }

    @Override
    public void dobaviKategorije(ArrayList<Kategorija> kat) {
        listaKategorija = kat;
    }

    @Override
    public void dobaviPodatke(ArrayList<Kviz> oKv, ArrayList<Kviz> sKv, ArrayList<Kategorija> kat) {
        //Dobavljanje svih podataka, koristi se najcesce pri prvom paljenju aplikacije
        if (isConnected) {
            listaKvizova = sKv;
            odabraniKvizovi = oKv;
            listaKategorija = kat;
            config = getResources().getConfiguration();

            //Popunjavanje lokalne baze
            popuniLokalnuBazu();
            //Popunjavanje GUI elemenata na osnovu konfiguracije
            if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
                popuni();
                mainListAdapter = new MainListAdapter(kvizoviAkt, listaKvizova, getResources());
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
                mainList.setAdapter(mainListAdapter);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);
                spinner.setSelection(0);

            } else {
                popuni();
                fragmentm = getSupportFragmentManager();
                lfm = new ListaFrag();
                dfm = new DetailFrag();
                fragmentm.beginTransaction().replace(R.id.listPlace, lfm, lfm.getTag()).commitAllowingStateLoss();
                fragmentm.beginTransaction().replace(R.id.detailPlace, dfm, dfm.getTag()).commitAllowingStateLoss();
            }
            new Firebase(OCstatus.GET_ALL_RL, this, (Firebase.Rangliste) KvizoviAkt.this).execute(OCstatus.GET_ALL_RL);
        } else odblokirajElemente();

    }

    public void popuniLokalnuBazu() {
        SQLiteDatabase db = kvizDB.getWritableDatabase();
        db.execSQL("delete from Kvizovi");
        db = kategorijaDB.getWritableDatabase();
        db.execSQL("delete from Kategorije");
        db = pitanjeDB.getWritableDatabase();
        db.execSQL("delete from Pitanja");
        db = odgovorDB.getWritableDatabase();
        db.execSQL("delete from Odgovori");
        try {
            dodajKategorijeSQL(listaKategorija);
            for (Kviz a : listaKvizova) {
                if (a.getNaziv() != null)
                    dodajKvizSQL(a);
            }
        } catch (Exception e) {
            System.out.println("Nesto nije uredu sa lokalnom bazom " + e);
        }
    }



    public void dodajKvizSQL(Kviz kviz) {
         //Dodavanje kviza u SQL, analogno vrijedi za sve ostale metode <ime>SQL (ispod)
        try {
            SQLiteDatabase db = kvizDB.getWritableDatabase();
            ContentValues dodajKviz = new ContentValues();
            dodajKviz.put(KVIZ_ID, kviz.getNaziv());
            if (kviz.getKategorija() != null) {
                dodajKviz.put(KATEGORIJA_ID, kviz.getKategorija().getNaziv());
            } else {
                dodajKviz.put(KATEGORIJA_ID, "Svi");
            }
            if (kviz.getPitanja() != null && kviz.getPitanja().size() != 0) {

                dodajPitanjaSQL(kviz.getPitanja(), kviz.getNaziv());
            }
            db.insert(KvizDB.DATABASE_TABLE, null, dodajKviz);
        } catch (Exception e) {
            System.out.println("##############Greska prilikom dodavanja kviza u lokalnu bazu: " + e);
        }


    }

    public void dodajPitanjaSQL(ArrayList<Pitanje> listaPitanja, String nazivKviza) {
        try {
            SQLiteDatabase db = pitanjeDB.getWritableDatabase();
            ContentValues dodajPitanje = new ContentValues();
            for (Pitanje p : listaPitanja) {
                dodajOdgovoreSQL(p.getOdgovori(), p.getNaziv());
                dodajPitanje.put(PITANJE_ID, p.getNaziv());
                dodajPitanje.put(TACAN_ODGOVOR, p.getTacan());
                dodajPitanje.put(KVIZ_FK, nazivKviza);
                db.insert(PitanjeDB.DATABASE_TABLE, null, dodajPitanje);
                dodajPitanje.clear();
            }
        } catch (Exception e) {
            System.out.println("##############Greska prilikom dodavanja pitanja u lokalnu bazu: " + e);
        }
    }

    public void dodajKategorijeSQL(ArrayList<Kategorija> kat) {
        try {
            SQLiteDatabase db = kategorijaDB.getWritableDatabase();
            ContentValues dodajKategorije = new ContentValues();
            for (Kategorija k : listaKategorija) {
                if (k.getNaziv() != null) {
                    dodajKategorije.put(KategorijaDB.KATEGORIJA_ID, k.getNaziv());
                    dodajKategorije.put(IKONICA_ID, k.getId());
                    db.insert(KategorijaDB.DATABASE_TABLE, null, dodajKategorije);
                }
                dodajKategorije.clear();
            }

        } catch (Exception e) {
            System.out.println("##############Greska prilikom dodavanja kategorije u lokalnu bazu: " + e);
        }

    }

    public void dodajOdgovoreSQL(ArrayList<String> odgovori, String nazivPitanja) {
        try {
            SQLiteDatabase db = odgovorDB.getWritableDatabase();
            ContentValues dodajOdgovore = new ContentValues();
            for (String s : odgovori) {
                dodajOdgovore.put(ODGOVOR_ID, s);
                dodajOdgovore.put(PITANJE_ID, nazivPitanja);
                db.insert(OdgovorDB.DATABASE_TABLE, null, dodajOdgovore);
                dodajOdgovore.clear();
            }
        } catch (Exception e) {
            System.out.println("##############Greska prilikom dodavanja odgovora u lokalnu bazu: " + e);
        }
    }

    @Override
    public void azurirajPodatke(ArrayList<Kviz> oKv, ArrayList<Kviz> sKv) {
        //Koristi se prilikom klika na spinner ili listu (u ovisnosti od konfiguracije)
        if (isConnected) {
            odabraniKvizovi = oKv;
            listaKvizova = sKv;
            popuni();
            if (isItPortrait()) {
                mainListAdapter = new MainListAdapter(kvizoviAkt, listaKvizova, getResources());
                mainList.setAdapter(mainListAdapter);
                if (spinner.getSelectedItemPosition() != 0)
                    spinner.setSelection(0);
            } else {
                lfm = new ListaFrag();
                dfm = new DetailFrag();
                fragmentm.beginTransaction().replace(R.id.listPlace, lfm, lfm.getTag()).commitAllowingStateLoss();
                fragmentm.beginTransaction().replace(R.id.detailPlace, dfm, dfm.getTag()).commitAllowingStateLoss();
            }
        }
        popuniLokalnuBazu();
        odblokirajElemente();
    }

    @Override
    public void validacijaPitanja(ArrayList<String> listaPitanja) {
        //koristi se u drugim klasama
    }

    public void popuni() {
        //Popunjavanje potrebnih podataka za spinnere i Dodaj element list view-a
        if (categories.size() > 0) categories.clear();
        categories.add("Svi");
        for (Kategorija a : listaKategorija) {
            categories.add(a.getNaziv());
        }
        listaKvizova.add(new Kviz(null, null, null));
        odabraniKvizovi = kopiraj(listaKvizova, odabraniKvizovi);
    }

    public void categoriesFiller() {
        categories.add("Svi");
        for (Kategorija a : listaKategorija) {
            categories.add(a.getNaziv());
        }
    }


    private void dobaviPodatkeIzSQL() {
        try {
            String[] koloneRezultat = new String[]{KOLONA_ID, KVIZ_ID, KATEGORIJA_ID};
            String where = null;
            String whereArgs[] = null;
            String groupBy = null;
            String having = null;
            String order = null;
            SQLiteDatabase db = kvizDB.getWritableDatabase();
            Cursor cursor = db.query(KvizDB.DATABASE_TABLE, koloneRezultat, where, whereArgs, groupBy, having, order);
            Log.d("SQLite Kvizovi baza ", "Povucena lista kvizova, broj redova: " + cursor.getCount());
            cursor.moveToFirst();
            boolean naPoc = true;
            Kviz noviKviz = null;
            Kategorija kat = null;
            ArrayList<Pitanje> p = new ArrayList<>();
            dobaviSveKategorijeSQL();
            categoriesFiller();
            while (!cursor.isAfterLast()) {
                String naziv = cursor.getString(1);
                int inxKat = getCategoriesByName(cursor.getString(2));
                System.out.println("test" + inxKat);
                if (inxKat != 0) kat = listaKategorija.get(inxKat-1);
                else kat = null;
                p = dobaviListuPitanjaIzSQL(naziv);
                listaKvizova.add(new Kviz(naziv, p, kat));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("Greska prilikom ucitavanja kvizova: " + e);
        }
    }

    private void dobaviSveKategorijeSQL() {
        try {
            SQLiteDatabase db = kategorijaDB.getWritableDatabase();
            String[] koloneRezultat = new String[]{KategorijaDB.KOLONA_ID, KategorijaDB.KATEGORIJA_ID, IKONICA_ID};
            String where = null;
            String whereArgs[] = null;
            String groupBy = null;
            String having = null;
            String order = null;
            Cursor cursor = db.query(KategorijaDB.DATABASE_TABLE, koloneRezultat, where, whereArgs, groupBy, having, order);
            Log.d("SQLite Kategorija ", "Povucena kategorija iz baze , get count je : " + cursor.getCount());
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                listaKategorija.add(new Kategorija(cursor.getString(1), cursor.getString(2)));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("Nesto nije uredu sa ucitavanjem kategorija: " + e);
        }

    }

    private ArrayList<Pitanje> dobaviListuPitanjaIzSQL(String nazivKviza) {
        ArrayList<Pitanje> list = new ArrayList<>();
        try {
            SQLiteDatabase db = pitanjeDB.getWritableDatabase();
            String[] koloneRezultat = new String[]{PitanjeDB.KOLONA_ID, PitanjeDB.PITANJE_ID, PitanjeDB.TACAN_ODGOVOR, KVIZ_FK};
            String where = PitanjeDB.KVIZ_FK + " LIKE  '" + nazivKviza + "'";
            String whereArgs[] = null;
            String groupBy = null;
            String having = null;
            String order = null;
            Cursor cursor = db.query(PitanjeDB.DATABASE_TABLE, koloneRezultat, where, whereArgs, groupBy, having, order);
            Log.d("SQLite Pitanja ", "Povucena lista pitanja iz baze , broj pitanja je: " + cursor.getCount());
            cursor.moveToFirst();
            if (cursor.getCount() == 0) return list;
            ArrayList<String> listaOdgovora = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                String nazivPitanja = cursor.getString(1);
                String tacanOdg = cursor.getString(2);
                listaOdgovora = dobaviListuOdgovora(nazivPitanja);
                list.add(new Pitanje(nazivPitanja, nazivPitanja, tacanOdg, listaOdgovora));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {

            System.out.println("Nesto nije uredu sa ucitavanjem kategorija: " + e);
        }

        return list;
    }

    private ArrayList<String> dobaviListuOdgovora(String nazivPitanja) {
        ArrayList<String> listaOdgovora = new ArrayList<>();
        try {
            SQLiteDatabase db = odgovorDB.getWritableDatabase();
            String[] koloneRezultat = new String[]{OdgovorDB.KOLONA_ID, OdgovorDB.ODGOVOR_ID, OdgovorDB.PITANJE_ID};
            String where = OdgovorDB.PITANJE_ID + " LIKE  '" + nazivPitanja + "'";
            String whereArgs[] = null;
            String groupBy = null;
            String having = null;
            String order = null;
            Cursor cursor = db.query(OdgovorDB.DATABASE_TABLE, koloneRezultat, where, whereArgs, groupBy, having, order);
            Log.d("SQLite Odgovori ", "Povucena lista pitanja iz baze , broj odgovora je: " + cursor.getCount());
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                listaOdgovora.add(cursor.getString(1));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("Nesto nije uredu sa ucitavanjem odgovora: " + e);
        }
        return listaOdgovora;
    }

    private void dugiKlik(int mPosition) {
        //  Dugim klikom inicira poziv DodajKvizAkt (azuriranje ili dodavanje u ovisnosti od uslova)
        if (mPosition == odabraniKvizovi.size() - 1) {
            Intent dodajIntent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
            dodajIntent.putExtra("poz_kviza", -1);
            dodajIntent.putExtra("poz_kategorije", 0);
            KvizoviAkt.this.startActivityForResult(dodajIntent, 0);
        } else {
            Intent dodajIntent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
            dodajIntent.putExtra("poz_kviza", mPosition);
            int indexKategorije;
            try {
                if (odabraniKvizovi.get(mPosition).getKategorija() != null) {
                    indexKategorije = getCategoriesByName(odabraniKvizovi.get(mPosition).getKategorija().getNaziv());
                } else {
                    indexKategorije = -2;
                }
            } catch (Exception e) {
                System.out.println(e);
                return;
            }
            dodajIntent.putExtra("poz_kategorije", indexKategorije);
            dodajIntent.putExtra("naziv_kviza", odabraniKvizovi.get(mPosition).getNaziv());
            KvizoviAkt.this.startActivityForResult(dodajIntent, 0);
        }
    }

    public ArrayList<Kviz> kopiraj(ArrayList<Kviz> a, ArrayList<Kviz> b) {
        b.clear();
        for (Kviz x : a) {
            b.add(x);
        }
        return b;
    }

    public void dodajKviz(Kviz kviz) {
        listaKvizova.add(kviz);
    }

    public static ArrayList<String> getCategories() {
        return categories;
    }

    private boolean isItPortrait() {
        Configuration config = getResources().getConfiguration();
        return config.orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static int getCategoriesByName(String name) {
        int target = categories.indexOf(name);
        //if (target == -1) throw new IllegalArgumentException("Ne postoji kategorija");
        return target;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //-32 oznacava dodavanje novog kviza, dok -133 azuriranje
        //Result kod 9000 oznacava izlazak na back dugme
        // 32000 oznacava izlazak iz IgrajKvizAkt klikom na zavrsi kviz button
        // 9000 izlazak iz IgrajKvizAkt putem back buttona

        config = getResources().getConfiguration();
        blokirajElemente();
        if (resultCode == -32) {
            Toast toast = Toast.makeText(getApplicationContext(), "Ucitavam izmjene, pricekajte!", Toast.LENGTH_SHORT);
            toast.show();
            Bundle bundleOb = data.getExtras();
            ArrayList<Pitanje> novaPitanja = (ArrayList<Pitanje>) bundleOb.getSerializable("listaPitanja");

            novaPitanja.remove(novaPitanja.size() - 1);
            if (listaKvizova.size() > 0) listaKvizova.remove(listaKvizova.size() - 1);
            Kviz noviKviz = null;
            if ((data.getExtras().getInt("kategorija") - 1) != -1) {
                noviKviz = new Kviz(data.getStringExtra("naziv"), novaPitanja, listaKategorija.get(data.getExtras().getInt("kategorija") - 1));
            } else noviKviz = new Kviz(data.getStringExtra("naziv"), novaPitanja, null);
            listaKvizova.add(new Kviz(null, null, null));
            new Firebase(OCstatus.ADD_KVIZ, this, (Firebase.ProvjeriStatus) KvizoviAkt.this).execute(OCstatus.ADD_KVIZ, noviKviz);
            dodajKviz(noviKviz);

        } else if (resultCode == -133) {
            Toast toast = Toast.makeText(getApplicationContext(), "Ucitavam izmjene, pricekajte!", Toast.LENGTH_SHORT);
            toast.show();
            Bundle bundleOb = data.getExtras();
            ArrayList<Pitanje> novaPitanja = (ArrayList<Pitanje>) bundleOb.getSerializable("listaPitanja");
            novaPitanja.remove(novaPitanja.size() - 1);
            int pozicija = data.getExtras().getInt("pozicija");

            //Stari naziv za brisanje starog dokumenta
            String stariNaziv = odabraniKvizovi.get(pozicija).getNaziv();
            odabraniKvizovi.get(pozicija).setPitanja(novaPitanja);
            odabraniKvizovi.get(pozicija).setNaziv(data.getStringExtra("naziv"));

            if ((data.getExtras().getInt("kategorija") - 1) != -1) {
                odabraniKvizovi.get(pozicija).setKategorija(listaKategorija.get(data.getExtras().getInt("kategorija") - 1));
            } else {
                odabraniKvizovi.get(pozicija).setKategorija(null);
            }
            new Firebase(OCstatus.EDIT_KVIZ, this, (Firebase.ProvjeriStatus) KvizoviAkt.this).execute(OCstatus.EDIT_KVIZ, odabraniKvizovi.get(pozicija), stariNaziv);

        } else if (resultCode == 9000) {
            odblokirajElemente();
            refreshCategories();
        } else if (resultCode == 32000) {
            odblokirajElemente();
        } else if (resultCode == 10000) {
            refreshCategories();
            odblokirajElemente();
        } else if (resultCode == 12333) {
            odblokirajElemente();
        }

    }

    void refreshCategories() {
        //Osvjezavanje liste kategorija ukoliko su se desile izmjene
        categories.clear();
        categories.add("Svi");
        for (Kategorija k : listaKategorija) {
            if (k == null) continue;
            categories.add(k.getNaziv());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        if (isItPortrait()) spinner.setAdapter(dataAdapter);
    }

    @Override
    public void filterList(int i) {
        if (i < 0) return;

        if (isConnected)
            new Firebase(OCstatus.GET_SPINNER_CONTENT, this, (Firebase.ProvjeriStatus) KvizoviAkt.this).execute(OCstatus.GET_SPINNER_CONTENT, categories.get(i));
        else {

            if (listaKvizova.size() > 1) {

                dajKvizoveKategorije(categories.get(i));
            } else {
                odabraniKvizovi = kopiraj(listaKvizova, odabraniKvizovi);
            }

            fragmentm = getSupportFragmentManager();
            lfm = new ListaFrag();
            dfm = new DetailFrag();
            fragmentm.beginTransaction().replace(R.id.listPlace, lfm, lfm.getTag()).commitAllowingStateLoss();
            fragmentm.beginTransaction().replace(R.id.detailPlace, dfm, dfm.getTag()).commitAllowingStateLoss();
        }
    }

    @Override
    public void addKviz(int i) {
        dugiKlik(i);
    }

    @Override
    public void editKviz() {
        filterList(0);
    }

    @Override
    public void playKviz(int i) {
        if (i != odabraniKvizovi.size() - 1) {
            Intent newIntent = new Intent(KvizoviAkt.this, IgrajKvizAkt.class);
            newIntent.putExtra("kviz", (Serializable) odabraniKvizovi.get(i));
            KvizoviAkt.this.startActivityForResult(newIntent, 32000);
        }

    }


    public void onConnected() {
        ArrayListMultimap<String, Pair<Double, String>> ranglisteSQL = ugrabiSveRLizSQL();
        isConnected = true;
        blokirajElemente();
        new Firebase(OCstatus.GET_DB_CONTENT, this, (Firebase.ProvjeriStatus) KvizoviAkt.this).execute(OCstatus.GET_DB_CONTENT, "Svi");
        Toast toast = Toast.makeText(getApplicationContext(), "Internet je dostupan! Dobavljam podatke iz baze, sacekajte..", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onDisconnected() {
        isConnected = false;
        blokirajElemente();
        Toast toast = Toast.makeText(getApplicationContext(), "Internet nije dostupan, ucitavam iz lokalne baze...", Toast.LENGTH_SHORT);
        toast.show();
        clearAll();
        dobaviPodatkeIzSQL();
        popuni();
        odblokirajElemente();
        //Ukoliko je landscape, fragmenti se moraju refreshovati zbog adaptera
        if (!isItPortrait()) {
            fragmentm = getSupportFragmentManager();
            lfm = new ListaFrag();
            dfm = new DetailFrag();
            fragmentm.beginTransaction().replace(R.id.listPlace, lfm, lfm.getTag()).commitAllowingStateLoss();
            fragmentm.beginTransaction().replace(R.id.detailPlace, dfm, dfm.getTag()).commitAllowingStateLoss();
        }


    }

}
