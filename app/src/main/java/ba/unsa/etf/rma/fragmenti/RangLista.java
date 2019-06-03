package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;



public class RangLista extends Fragment {


    ListView lista;
    ArrayList<String> rangLista= new ArrayList<>();
    String naziv = new String ();
    TextView rangListaTekst;

    public RangLista() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View iv=inflater.inflate(R.layout.fragment_rang_lista, container, false);
        // Inflate the layout for this fragment
        lista= (ListView) iv.findViewById(R.id.ranglista);
        rangListaTekst = (TextView) iv.findViewById(R.id.rangListaTekst);
        Bundle bundleObj= this.getArguments();
        rangLista = (ArrayList<String>) bundleObj.getStringArrayList("ranglista");
        naziv = (String) bundleObj.getString("nazivKviza");
        rangListaTekst.setText("Ranglista za kviz: " + naziv);
       ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1 , rangLista);
         lista.setAdapter(arrayAdapter);
         return iv;
    }





}
