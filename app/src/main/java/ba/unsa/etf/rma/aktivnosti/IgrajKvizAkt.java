package ba.unsa.etf.rma.aktivnosti;


import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import java.io.Serializable;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.klase.Kviz;

public class IgrajKvizAkt extends AppCompatActivity {
    FrameLayout zaPit, zaInfo;

    Kviz kviz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igraj_kviz_akt);

        zaPit= (FrameLayout) findViewById(R.id.pitanjePlace);
        zaInfo= (FrameLayout) findViewById(R.id.informacijePlace);
        //init frags
        InformacijeFrag infoFrag= new InformacijeFrag();
        PitanjeFrag pitFrag= new PitanjeFrag();
  /*     Bundle bundle= getIntent().getExtras();
        kviz= (Kviz) bundle.getSerializable("kviz"); */
  kviz= (Kviz) getIntent().getSerializableExtra("kviz");
        System.out.println(kviz.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.informacijePlace, infoFrag,infoFrag.getTag()).commit();
        fragmentManager.beginTransaction().replace(R.id.pitanjePlace, pitFrag,infoFrag.getTag()).commit();

    //  pitFrag= (PitanjeFrag)fragmentManager.findFragmentById(R.id.pitanjePlace);
     //   infoFrag= (InformacijeFrag)fragmentManager.findFragmentById(R.id.informacijePlace);

       System.out.println(pitFrag  + " " + infoFrag);


    }
}
