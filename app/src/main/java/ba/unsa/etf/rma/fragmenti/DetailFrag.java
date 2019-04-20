package ba.unsa.etf.rma.fragmenti;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.GridViewAdapter;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFrag extends Fragment {

     GridView grid;
     public GridViewAdapter gridViewAdapter=null;
     public DetailFrag instanca=null;

    public DetailFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View iv=inflater.inflate(R.layout.fragment_detail, container, false);
        instanca=this;
        grid= (GridView) iv.findViewById(R.id.gridKvizovi);
        gridViewAdapter= new GridViewAdapter(getActivity(), KvizoviAkt.listaKvizova, getResources() );
        grid.setAdapter(gridViewAdapter);

        return iv;

    }

}
