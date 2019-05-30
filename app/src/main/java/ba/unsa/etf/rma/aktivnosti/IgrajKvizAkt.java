package ba.unsa.etf.rma.aktivnosti;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

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
    Context context=null;
    Kviz kviz;
     ArrayList<Pitanje> preostalaPitanja= new ArrayList<>();
     ArrayList<Pitanje> odgovorenaPitanja= new ArrayList<>();
     int brojTacnih=0;
     double procenatTacnih=0;
     int inx=-1;
     String nazivKv = new String();
   // private EditText result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igraj_kviz_akt);
        zaPit= (FrameLayout) findViewById(R.id.pitanjePlace);
        zaInfo= (FrameLayout) findViewById(R.id.informacijePlace);

        //init frags
        context=this;
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
   //finalpush
        try {
            fragmentManager.beginTransaction().replace(R.id.informacijePlace, infoFrag, infoFrag.getTag()).commit();
            fragmentManager.beginTransaction().replace(R.id.pitanjePlace, pitFrag, infoFrag.getTag()).commit();
        }
        catch (Exception e) {
            buttonClick();
        }


    }

    @Override
    public void krajKviza() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.layout_for_alert_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                             //   result.setText(userInput.getText());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
        //   @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                String ime= userInput.getText().toString();
                if ( ime==null || ime.length()==0) {
                    userInput.setBackgroundColor(getResources().getColor(R.color.crvena));
                    Toast.makeText(context, "Greska, niste unjeli validno ime i prezime, pokusajte ponovo ili kliknite cancel!", Toast.LENGTH_LONG).show();
                }
                else {
                    userInput.setBackgroundColor(Color.WHITE);
                    alertDialog.dismiss();
                }

            }
        });
    }

    @Override
    public void buttonClick ( ) {
        Intent zavrsi = getIntent();
        setResult(32000, zavrsi);
        finish();

    }
}
