package ba.unsa.etf.rma.aktivnosti;

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

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;

public class DodajKategorijuAkt extends AppCompatActivity implements IconDialog.Callback {
    EditText naziv, nazivIkone;
    Button dodajIkonu, dodajKategoriju;
    ImageView debil;
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
        debil = (ImageView) findViewById(R.id.imageView);
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

    @Override
    public void onIconDialogIconsSelected(Icon[] icons) {
        selectedIcons = icons;
        int idIkone = icons[0].getId();
        nazivIkone.setText((CharSequence) (Integer.toString(idIkone)));
    }

    private void validiraj() {
        ikona = nazivIkone.getText().toString();
        nazivKategorije = naziv.getText().toString();
        if (nazivKategorije.isEmpty()) naziv.setBackgroundColor(Color.RED);
        else naziv.setBackgroundColor(Color.WHITE);

        if (ikona.isEmpty()) nazivIkone.setBackgroundColor(Color.RED);
        else nazivIkone.setBackgroundColor(Color.WHITE);
        boolean naz = true, ik = true;
        if ((!(nazivKategorije.isEmpty())) && !(ikona.isEmpty())) {

            for (Kategorija test : KvizoviAkt.listaKategorija) {

                if (test.getNaziv().equals(nazivKategorije) || test.getNaziv().equals("Dodaj kategoriju")) {

                    naziv.setBackgroundColor(Color.RED);
                    naz = false;
                } else if (test.getId().equals(ikona)) {
                    nazivIkone.setBackgroundColor(Color.RED);
                    ik = false;

                }
            }
            if (!(ik) || !(naz)) return;
            naziv.setBackgroundColor(Color.WHITE);
            nazivIkone.setBackgroundColor(Color.WHITE);
            valid = true;
        }
    }
}
