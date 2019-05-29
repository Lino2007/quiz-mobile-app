package ba.unsa.etf.rma.aktivnosti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;
import com.maltaisn.icondialog.IconHelper;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Firebase;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;

public class DodajKategorijuAkt extends AppCompatActivity implements IconDialog.Callback , Firebase.ProvjeriStatus{
    EditText naziv, nazivIkone;
    Button dodajIkonu, dodajKategoriju;

    private boolean valid = false;
    public static Icon[] selectedIcons;
    String ikona, nazivKategorije;
    IconDialog iconDialog = new IconDialog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kategoriju_akt);

        naziv = (EditText) findViewById(R.id.etNaziv);
        nazivIkone = (EditText) findViewById(R.id.etIkona);
        dodajIkonu = (Button) findViewById(R.id.btnDodajIkonu);
        dodajKategoriju = (Button) findViewById(R.id.btnDodajKategoriju);


        nazivIkone.setEnabled(false);
        nazivIkone.setFocusable(false);

        dodajIkonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconDialog.setSelectedIcons(selectedIcons);
                iconDialog.show(getSupportFragmentManager(), "icon_dialog");
            }
        });

        dodajKategoriju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validiraj();
                if (valid) {
                    KvizoviAkt.listaKategorija.add(new Kategorija(nazivKategorije, ikona));
                    new Firebase(DodajKategorijuAkt.this.getApplicationContext()).execute (KvizoviAkt.OCstatus.ADD_KAT, new Kategorija(nazivKategorije,ikona));
                    setResult(-100);
                    finish();
                }
            }
        });



    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) setResult(-300);
        return super.onKeyDown(keyCode, event);
    }
  //
    @Override
    public void onIconDialogIconsSelected(Icon[] icons) {
        selectedIcons = icons;
        int idIkone = icons[0].getId();
        nazivIkone.setText((CharSequence) (Integer.toString(idIkone)));
    }

    private void validiraj() {
        ikona = nazivIkone.getText().toString();
        nazivKategorije = naziv.getText().toString();
        if (nazivKategorije.isEmpty()) naziv.setBackgroundColor(Color.parseColor("#E85F41"));
        else naziv.setBackgroundColor(Color.WHITE);

        if (ikona.isEmpty()) nazivIkone.setBackgroundColor(Color.parseColor("#E85F41"));
        else nazivIkone.setBackgroundColor(Color.WHITE);
        boolean naz = true, ik = true;
        if ((!(nazivKategorije.isEmpty())) && !(ikona.isEmpty())) {

            for (Kategorija test : KvizoviAkt.listaKategorija) {

                if (test.getNaziv().equals(nazivKategorije) || test.getNaziv().equals("Dodaj kategoriju")) {

                    naziv.setBackgroundColor(Color.parseColor("#E85F41"));
                    naz = false;
                }
            }
            if ( !(naz)) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                        .setTitle("Greska pri unosu kategorije")
                        .setMessage("Unesena kategorija vec postoji!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert);
                alertDialogBuilder.show();
                return;
            }
            naziv.setBackgroundColor(Color.WHITE);
            nazivIkone.setBackgroundColor(Color.WHITE);
            valid = true;
        }
    }

    @Override
    public void dobaviSpinerPodatke(ArrayList<Kviz> oKv, ArrayList<Kviz> sKv) {

    }

    @Override
    public void dobaviKategorije(ArrayList<Kategorija> kat) {

    }

    @Override
    public void dobaviPodatke(ArrayList<Kviz> oKv, ArrayList<Kviz> sKv, ArrayList<Kategorija> kat) {

    }

    @Override
    public void azurirajPodatke(ArrayList<Kviz> oKv, ArrayList<Kviz> sKv) {

    }
}
