package ba.unsa.etf.rma.aktivnosti;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.provider.AlarmClock;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Random;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.fragmenti.RangLista;
import ba.unsa.etf.rma.klase.Firebase;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import java.lang.Object.*;

public class IgrajKvizAkt extends AppCompatActivity implements PitanjeFrag.UpdateListener, InformacijeFrag.UpdateListener, Firebase.Rangliste {
    FrameLayout zaPit, zaInfo;
    Context context = null;

    Kviz kviz;
    ArrayList<Pitanje> preostalaPitanja = new ArrayList<>();
    ArrayList<Pitanje> odgovorenaPitanja = new ArrayList<>();
    int brojTacnih = 0;
    double procenatTacnih = 0;
    static double procT = 0;
    int inx = -1;
    String nazivKv = new String();
   // Intent alarmClock = null;
    public  boolean timerIstekao= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igraj_kviz_akt);

        zaPit = (FrameLayout) findViewById(R.id.pitanjePlace);
        zaInfo = (FrameLayout) findViewById(R.id.informacijePlace);

        //Uspostava fragmenata
        context = this;
        InformacijeFrag infoFrag = new InformacijeFrag();
        PitanjeFrag pitFrag = new PitanjeFrag();

        Bundle zaFragPit = new Bundle(), zaFragInfo = new Bundle();
        kviz = (Kviz) getIntent().getSerializableExtra("kviz");
        preostalaPitanja = prekopirajPitanja(kviz.getPitanja());

        int a = -1;
        if (preostalaPitanja != null && !preostalaPitanja.isEmpty()) {
            System.out.println("....................................................");
            a = getRandomIndex(preostalaPitanja.size());
            int minute = (int)Math.round((double)preostalaPitanja.size()/2);
            final long milis= (minute*60*1000)+5;
            Toast toast =  Toast.makeText(getApplicationContext(), "Kviz je zapoceo, vrijeme preostalo: " +  minute + " minuta.", Toast.LENGTH_LONG);
            toast.show();
            Intent alarmClock = new Intent (AlarmClock.ACTION_SET_TIMER);
            alarmClock.putExtra (AlarmClock.EXTRA_LENGTH ,  minute *60);
            alarmClock.putExtra (AlarmClock.EXTRA_VIBRATE, true);
            alarmClock.putExtra (AlarmClock.EXTRA_SKIP_UI, true);
            alarmClock.putExtra(AlarmClock.EXTRA_MESSAGE, true);
            IgrajKvizAkt.this.startActivity(alarmClock);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //timerIstekao=true;
                  //  updateByAction(false);

                    Toast toast =  Toast.makeText(getApplicationContext(), "Kviz je zavrsio!", Toast.LENGTH_LONG);
                    toast.show();


                }
            }, milis);
            final Thread r = new Thread() {
                public void run() {
                    // DO WORK

                    // Call function.
                    handler.postDelayed(this, milis);



                }
            };
            r.start();
            inx = a;
        }
        nazivKv = kviz.getNaziv();
        zaFragInfo.putString("naziv_kviza", kviz.getNaziv());
        zaFragInfo.putDouble("procenat_tacnih", procenatTacnih);
        zaFragInfo.putInt("broj_tacnih", brojTacnih);
        if (preostalaPitanja == null) {
            zaFragPit.putSerializable("pitanja", null);
            zaFragInfo.putInt("broj_preostalih", 0);
        } else {
            zaFragPit.putSerializable("pitanja", preostalaPitanja.get(a));
            zaFragInfo.putInt("broj_preostalih", preostalaPitanja.size());
        }

        infoFrag.setArguments(zaFragInfo);
        pitFrag.setArguments(zaFragPit);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.informacijePlace, infoFrag, infoFrag.getTag()).commit();
        fragmentManager.beginTransaction().replace(R.id.pitanjePlace, pitFrag, infoFrag.getTag()).commit();


    }

    private ArrayList<Pitanje> prekopirajPitanja(ArrayList<Pitanje> a) {
        ArrayList<Pitanje> v = new ArrayList<>();
        if (a.isEmpty()) return null;
        for (Pitanje x : a) {
            v.add(x);
        }
        return v;
    }

    int getRandomIndex(int x) {
        Random rand = new Random();
        return rand.nextInt(x);
    }

    @Override
    public void updateByAction(boolean tacno) {
        //Po odgovoru na pitanje se vrse akcije azuriranja statistika, i ucitavanja novih pitanja
        InformacijeFrag infoFrag = new InformacijeFrag();
        PitanjeFrag pitFrag = new PitanjeFrag();
        Bundle zaFragPit = new Bundle(), zaFragInfo = new Bundle();
        zaFragInfo.putString("naziv_kviza", nazivKv);
        if (timerIstekao) {
            procenatTacnih = (double) brojTacnih/ (odgovorenaPitanja.size() + preostalaPitanja.size());
            procenatTacnih = Math.round(procenatTacnih * 10000.0) / 10000.0;
            zaFragPit.putSerializable("pitanja", null);
            zaFragInfo.putInt("broj_preostalih", 0);
            zaFragInfo.putDouble("procenat_tacnih", procenatTacnih);
            zaFragInfo.putInt("broj_tacnih", brojTacnih);

            infoFrag.setArguments(zaFragInfo);
            pitFrag.setArguments(zaFragPit);
               preostalaPitanja.clear();
            FragmentManager fragmentManager = getSupportFragmentManager();

            try {
                fragmentManager.beginTransaction().replace(R.id.informacijePlace, infoFrag, infoFrag.getTag()).commit();
                fragmentManager.beginTransaction().replace(R.id.pitanjePlace, pitFrag, infoFrag.getTag()).commit();
            } catch (Exception e) {
                buttonClick();
            }
        }

        if (preostalaPitanja.size() > 1) {
            odgovorenaPitanja.add(preostalaPitanja.get(inx));
            preostalaPitanja.remove(inx);
            inx = getRandomIndex(preostalaPitanja.size());
        } else if (preostalaPitanja.size() == 1) {
            odgovorenaPitanja.add(preostalaPitanja.get(0));
            preostalaPitanja.remove(0);
            inx = 0;
        }
        if (preostalaPitanja.size() >= 1) {
            if (tacno) brojTacnih++;
            procenatTacnih = (double) brojTacnih / odgovorenaPitanja.size();
            procenatTacnih = Math.round(procenatTacnih * 10000.0) / 10000.0;
            zaFragPit.putSerializable("pitanja", preostalaPitanja.get(inx));
            procT = procenatTacnih;
            zaFragInfo.putInt("broj_tacnih", brojTacnih);
            zaFragInfo.putInt("broj_preostalih", preostalaPitanja.size());
            zaFragInfo.putString("naziv_kviza", kviz.getNaziv());
            zaFragInfo.putDouble("procenat_tacnih", procenatTacnih);
        } else if (preostalaPitanja.size() == 0) {
            if (tacno) brojTacnih++;
            zaFragPit.putSerializable("pitanja", null);
            procenatTacnih = (double) brojTacnih / odgovorenaPitanja.size();
            procenatTacnih = Math.round(procenatTacnih * 10000.0) / 10000.0;
            procT = procenatTacnih;
            zaFragInfo.putInt("broj_preostalih", 0);
            zaFragInfo.putInt("broj_tacnih", brojTacnih);
            zaFragInfo.putDouble("procenat_tacnih", procenatTacnih);
        }
        infoFrag.setArguments(zaFragInfo);
        pitFrag.setArguments(zaFragPit);

        FragmentManager fragmentManager = getSupportFragmentManager();

        try {
            fragmentManager.beginTransaction().replace(R.id.informacijePlace, infoFrag, infoFrag.getTag()).commit();
            fragmentManager.beginTransaction().replace(R.id.pitanjePlace, pitFrag, infoFrag.getTag()).commit();
        } catch (Exception e) {
            buttonClick();
        }


    }

    @Override
    public void krajKviza() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.layout_for_alert_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        // Uspostava alert dijaloga
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            //   @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                String ime = userInput.getText().toString();
                if (ime == null || ime.length() == 0) {
                    userInput.setBackgroundColor(getResources().getColor(R.color.crvena));
                    Toast.makeText(context, "Greska, niste unjeli validno ime i prezime, pokusajte ponovo ili kliknite cancel!", Toast.LENGTH_LONG).show();
                } else {
                    String imeIgraca = userInput.getText().toString();
                    new Firebase(KvizoviAkt.OCstatus.GET_RL, getApplicationContext(), (Firebase.Rangliste) IgrajKvizAkt.this).execute(KvizoviAkt.OCstatus.GET_RL, nazivKv, imeIgraca, procenatTacnih * 100);
                    procT = 0;
                    userInput.setBackgroundColor(Color.WHITE);
                    alertDialog.dismiss();
                }

            }
        });
    }

    @Override
    public void buttonClick() {
        //Forsirani izlaz
        Intent zavrsi = getIntent();
        setResult(32000, zavrsi);
        finish();
    }

    @Override
    public void getRangliste(ArrayList<String> rl) {
        Bundle zaRangFrag = new Bundle();
        RangLista rlf = new RangLista();
        FragmentManager fragmentManager = getSupportFragmentManager();
        zaRangFrag.putStringArrayList("ranglista", rl);
        zaRangFrag.putString("nazivKviza", nazivKv);
        rlf.setArguments(zaRangFrag);
        fragmentManager.beginTransaction().replace(R.id.pitanjePlace, rlf, rlf.getTag()).commitAllowingStateLoss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent x = getIntent();
            setResult(12333, x);
        }
        return super.onKeyDown(keyCode, event);
    }
}
