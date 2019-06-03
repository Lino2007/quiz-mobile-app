package ba.unsa.etf.rma.fragmenti;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListaFrag extends Fragment   {

    ListUpdater instanca;
    ListView listaKategorija;
    Activity activity= null;
    public ListaFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vi= inflater.inflate(R.layout.fragment_lista, container, false);
        listaKategorija= (ListView) vi.findViewById(R.id.listaKategorija);
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1 , KvizoviAkt.categories);
        listaKategorija.setAdapter(arrayAdapter);

        listaKategorija.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast toast = Toast.makeText(getActivity(), "Ucitavaju se kvizovi, molimo sacekajte!", Toast.LENGTH_SHORT);
                toast.show();
              instanca.filterList(position);
                ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1 , KvizoviAkt.categories);
                listaKategorija.setAdapter(arrayAdapter);
            }
        });

        return vi;
    }



    public interface ListUpdater {
        public void filterList (int i);
    }

    @Override
    public void onResume() {
        //Refresh liste
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1 , KvizoviAkt.categories);
        listaKategorija.setAdapter(arrayAdapter);
        super.onResume();
    }


    @Override
    public void onAttach (Context context) {
        super.onAttach(context);
        activity=(Activity)context;
        try {
            instanca = (ListUpdater) activity;
        }
        catch (Exception e) {
            //
        }
    }

}
