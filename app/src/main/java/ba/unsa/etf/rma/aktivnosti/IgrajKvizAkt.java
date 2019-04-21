package ba.unsa.etf.rma.aktivnosti;


import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class IgrajKvizAkt extends AppCompatActivity   implements PitanjeFrag.UpdateListener, InformacijeFrag.UpdateListener {
    FrameLayout zaPit, zaInfo;

    Kviz kviz;
     ArrayList<Pitanje> preostalaPitanja= new ArrayList<>();
     ArrayList<Pitanje> odgovorenaPitanja= new ArrayList<>();
     int brojTacnih=0;
     double procenatTacnih=0;
     int inx=-1;
     String nazivKv = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igraj_kviz_akt);

        zaPit= (FrameLayout) findViewById(R.id.pitanjePlace);
        zaInfo= (FrameLayout) findViewById(R.id.informacijePlace);
        //init frags
        InformacijeFrag infoFrag= new InformacijeFrag();
        PitanjeFrag pitFrag= new PitanjeFrag();

        Bundle zaFragPit= new Bundle(), zaFragInfo= new Bundle();
         kviz= (Kviz) getIntent().getSerializableExtra("kviz");
         //prekopirajPitanja(kviz.getPitanja());
          preostalaPitanja= prekopirajPitanja(kviz.getPitanja());

          int a=-1;
          if ( preostalaPitanja!= null && !preostalaPitanja.isEmpty()) {
              a = getRandomIndex(preostalaPitanja.size());
              inx=a;
          }
          else {

          }


        nazivKv=kviz.getNaziv();
        zaFragInfo.putString("naziv_kviza", kviz.getNaziv());
        zaFragInfo.putDouble("procenat_tacnih", procenatTacnih);
        zaFragInfo.putInt("broj_tacnih", brojTacnih);
        if (preostalaPitanja==null) {  zaFragPit.putSerializable("pitanja", null);
            zaFragInfo.putInt ("broj_preostalih", 0);}
      else {
            zaFragPit.putSerializable("pitanja", preostalaPitanja.get(a));
            zaFragInfo.putInt ("broj_preostalih", preostalaPitanja.size());
        }


        infoFrag.setArguments(zaFragInfo);
        pitFrag.setArguments(zaFragPit);


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.informacijePlace, infoFrag,infoFrag.getTag()).commit();
        fragmentManager.beginTransaction().replace(R.id.pitanjePlace, pitFrag,infoFrag.getTag()).commit();


    }

    private ArrayList<Pitanje> prekopirajPitanja (ArrayList<Pitanje> a ) {
        ArrayList<Pitanje> v= new ArrayList<>();
        if (a.isEmpty()) return null;
        for (Pitanje x : a) {
            v.add(x);
        }
        return v;
    }

    int getRandomIndex (int x) {
        Random rand= new Random();
        return rand.nextInt(x);
    }


    @Override
    public void updateByAction(boolean tacno) {
        InformacijeFrag infoFrag= new InformacijeFrag();
        PitanjeFrag pitFrag= new PitanjeFrag();
        Bundle zaFragPit= new Bundle(), zaFragInfo= new Bundle();
        zaFragInfo.putString("naziv_kviza", nazivKv);

        if (preostalaPitanja.size()>1) {
            odgovorenaPitanja.add(preostalaPitanja.get(inx));
            preostalaPitanja.remove(inx);
            inx = getRandomIndex(preostalaPitanja.size());
        }
        else if (preostalaPitanja.size()==1) {
            odgovorenaPitanja.add(preostalaPitanja.get(0));
            preostalaPitanja.remove(0);
            inx =0;
        }
        if (preostalaPitanja.size()>=1) {
            if (tacno) brojTacnih++;
            procenatTacnih= (double) brojTacnih/odgovorenaPitanja.size();
            procenatTacnih=Math.round(procenatTacnih*10000.0)/10000.0;
            zaFragPit.putSerializable("pitanja", preostalaPitanja.get(inx));

            zaFragInfo.putInt("broj_tacnih", brojTacnih);
            zaFragInfo.putInt("broj_preostalih", preostalaPitanja.size());
            zaFragInfo.putString("naziv_kviza", kviz.getNaziv());
            zaFragInfo.putDouble("procenat_tacnih", procenatTacnih);


        }
       else if (preostalaPitanja.size()==0) {
            if (tacno) brojTacnih++;
            zaFragPit.putSerializable("pitanja", null);
            procenatTacnih= (double) brojTacnih/odgovorenaPitanja.size();
            procenatTacnih=Math.round(procenatTacnih*10000.0)/10000.0;
            zaFragInfo.putInt("broj_preostalih",0);

            zaFragInfo.putInt("broj_tacnih", brojTacnih);
            zaFragInfo.putDouble("procenat_tacnih", procenatTacnih);
        }
        infoFrag.setArguments(zaFragInfo);
        pitFrag.setArguments(zaFragPit);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.informacijePlace, infoFrag, infoFrag.getTag()).commit();
        fragmentManager.beginTransaction().replace(R.id.pitanjePlace, pitFrag, infoFrag.getTag()).commit();
    }

    @Override
    public void buttonClick ( ) {
        Intent zavrsi = getIntent();
        setResult(32000, zavrsi);
        finish();

    }
}
