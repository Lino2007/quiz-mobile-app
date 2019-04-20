package ba.unsa.etf.rma.fragmenti;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.GridViewAdapter;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFrag extends Fragment  {

     GridView grid;
     public GridViewAdapter gridViewAdapter=null;

    private Activity activity;
    ListFunction instanca;

    public DetailFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View iv=inflater.inflate(R.layout.fragment_detail, container, false);
        grid= (GridView) iv.findViewById(R.id.gridKvizovi);
        gridViewAdapter= new GridViewAdapter(getActivity(), KvizoviAkt.odabraniKvizovi, getResources() );
        grid.setAdapter(gridViewAdapter);
        System.out.println("xxxxxxxxxxxxxx");
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position!= KvizoviAkt.odabraniKvizovi.size() -1 )
                instanca.playKviz(position);

            }
        });

        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                instanca.addKviz(pos);

                gridViewAdapter= new GridViewAdapter(getActivity(), KvizoviAkt.odabraniKvizovi, getResources() );
                grid.setAdapter(gridViewAdapter);

                return true;
            }
        });

        return iv;

    }

    @Override
    public void onResume() {
        gridViewAdapter= new GridViewAdapter(getActivity(), KvizoviAkt.odabraniKvizovi, getResources() );
        grid.setAdapter(gridViewAdapter);
        super.onResume();
    }

    public interface ListFunction {
        public void addKviz (int i);
        public void editKviz (int i);
        public void playKviz (int i);
    }

    @Override
    public void onAttach (Context context) {
        super.onAttach(context);
        activity=(Activity)context;
        try {
            instanca = (ListFunction) activity;
        }
        catch (Exception e) {
            //
        }
    }

}
