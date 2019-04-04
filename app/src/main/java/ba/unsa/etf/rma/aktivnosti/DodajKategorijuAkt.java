package ba.unsa.etf.rma.aktivnosti;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import ba.unsa.etf.rma.R;

public class DodajKategorijuAkt extends AppCompatActivity {
       EditText naziv,nazivIkone;
       Button dodajIkonu, dodajKategoriju;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kategoriju_akt);

        naziv = (EditText) findViewById(R.id.etNaziv);
        nazivIkone = (EditText) findViewById(R.id.etIkona);
        dodajIkonu= (Button) findViewById(R.id.btnDodajIkonu);
        dodajKategoriju= (Button) findViewById(R.id.btnDodajKategoriju);



    }
}
