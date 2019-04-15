package ba.unsa.etf.rma.fragmenti;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.PitanjeFragAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PitanjeFrag extends Fragment {


    ListView listaOdgovora;
    TextView nazivPitanja;

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
        ArrayList<String> som= new ArrayList<>();
        PitanjeFragAdapter odgAdapter = new PitanjeFragAdapter(getActivity(),  som,getResources());
        listaOdgovora.setAdapter(null);
        return iv;
    }

}
