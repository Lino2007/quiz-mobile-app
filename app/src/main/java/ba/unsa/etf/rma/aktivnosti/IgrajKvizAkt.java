package ba.unsa.etf.rma.aktivnosti;


import android.content.res.Configuration;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;

public class IgrajKvizAkt extends AppCompatActivity {
    FrameLayout zaPit, zaInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igraj_kviz_akt);

        zaPit= (FrameLayout) findViewById(R.id.pitanjePlace);
        zaInfo= (FrameLayout) findViewById(R.id.informacijePlace);
        //init frags
        InformacijeFrag infoFrag;
        PitanjeFrag  pitFrag;


        FragmentManager fragmentManager = getSupportFragmentManager();
       pitFrag= (PitanjeFrag)fragmentManager.findFragmentById(R.id.pitanjePlace);
        infoFrag= (InformacijeFrag)fragmentManager.findFragmentById(R.id.informacijePlace);
        //System.out.println(pitFrag  + "  ---"   + infoFrag);
     // fragmentManager.beginTransaction()./*add(R.id.pitanjePlace, pitFrag).*/add(R.id.informacijePlace, infoFrag).commit();
   /*    pitFrag= (PitanjeFrag)fragmentManager.findFragmentById(R.id.pitanjePlace);
       if (pitFrag==null) {
           fragmentManager.beginTransaction().replace(R.id.pitanjePlace, pitFrag).commit();
       } */

      //  fragmentManager.beginTransaction().replace(R.id.pitanjePlace, pitFrag).commit();
     /*   FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction(); */

    }
}
