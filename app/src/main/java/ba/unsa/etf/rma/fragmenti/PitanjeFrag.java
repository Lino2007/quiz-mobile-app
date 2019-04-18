package ba.unsa.etf.rma.fragmenti;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.PitanjeFragAdapter;
import ba.unsa.etf.rma.aktivnosti.IgrajKvizAkt;
import ba.unsa.etf.rma.klase.Pitanje;

/**
 * A simple {@link Fragment} subclass.
 */
public class PitanjeFrag extends Fragment {
   Activity activity;
    Pitanje  pitanja = null;
    String tacan=null;
    ListView listaOdgovora;
    TextView nazivPitanja;
    ArrayList<String> som= new ArrayList<>();
    public static int pozicijaTacnog= -1;
    public static int kliknutaPozicija=-1;
     PitanjeFragAdapter odgAdapter;
   UpdateListener instanca;


    public PitanjeFrag() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View iv=inflater.inflate(R.layout.fragment_pitanje, container, false);
        listaOdgovora= (ListView) iv.findViewById(R.id.odgovoriPitanja);
        nazivPitanja= (TextView) iv.findViewById(R.id.tekstPitanja);
        Bundle bundleObj= this.getArguments();
        pitanja=  (Pitanje) bundleObj.getSerializable("pitanja");
        listaOdgovora.setEnabled(true);
        if (pitanja==null) {
            listaOdgovora.setAdapter(null);
            nazivPitanja.setText("Kviz je zavrsen");
        }
        else {
            som = pitanja.dajRandomOdgovore();
            odgAdapter = new PitanjeFragAdapter(getActivity(), som, getResources(), -1, -1);
            nazivPitanja.setText(pitanja.getNaziv());
            listaOdgovora.setAdapter(odgAdapter);
        }

        listaOdgovora.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tacan= pitanja.getTacan();
                pozicijaTacnog= som.indexOf(tacan);
                kliknutaPozicija=position;
                odgAdapter = new PitanjeFragAdapter(getActivity(),  som,getResources(), pozicijaTacnog , kliknutaPozicija);
                listaOdgovora.setAdapter(odgAdapter);


                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        listaOdgovora.setEnabled(false);
                     /*  if(!(nazivPitanja.getText().equals("Kviz je zavrsen")))  */ instanca.updateByAction( pozicijaTacnog==kliknutaPozicija);
                        pozicijaTacnog=kliknutaPozicija=-1;

                    }
                }, 2000);
                final Thread r = new Thread() {
                    public void run() {
                        // DO WORK

                        // Call function.
                        handler.postDelayed(this, 2000);


                    }
                };
                r.start(); // THIS IS DIFFERENT

            }
        });

        return iv;
    }

    @Override
    public void onAttach (Context context) {
        super.onAttach(context);
          activity=(Activity)context;
          try {
              instanca = (UpdateListener) activity;
          }
          catch (Exception e) {
              //
          }
    }

     public interface UpdateListener {
        public void updateByAction(boolean tacno);
     }


}
