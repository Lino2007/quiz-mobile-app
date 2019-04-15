package ba.unsa.etf.rma.aktivnosti;


import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import java.io.Serializable;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class IgrajKvizAkt extends AppCompatActivity {
    FrameLayout zaPit, zaInfo;

    Kviz kviz;
    ArrayList<Pitanje> preostala = new ArrayList<>();
    ArrayList<Pitanje> odgovorena= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igraj_kviz_akt);

        zaPit= (FrameLayout) findViewById(R.id.pitanjePlace);
        zaInfo= (FrameLayout) findViewById(R.id.informacijePlace);
        //init frags
        InformacijeFrag infoFrag= new InformacijeFrag();
        PitanjeFrag pitFrag= new PitanjeFrag();

         kviz= (Kviz) getIntent().getSerializableExtra("kviz");
         prekopirajPitanja(kviz.getPitanja());
        System.out.println(kviz.toString());

        Bundle zaFragPit= new Bundle(), zaFragInfo= new Bundle();
        zaFragInfo.putString("naziv_kviza", kviz.getNaziv());
        zaFragInfo.putInt ("broj_preostalih", preostala.size());
        infoFrag.setArguments(zaFragInfo);


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.informacijePlace, infoFrag,infoFrag.getTag()).commit();
        fragmentManager.beginTransaction().replace(R.id.pitanjePlace, pitFrag,infoFrag.getTag()).commit();


    }

    private void prekopirajPitanja (ArrayList<Pitanje> x) {
        for (Pitanje a: x) {
            preostala.add(a);
        }
    }
}
