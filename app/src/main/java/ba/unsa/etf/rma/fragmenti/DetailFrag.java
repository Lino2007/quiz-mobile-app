package ba.unsa.etf.rma.fragmenti;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.GridViewAdapter;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;
import ba.unsa.etf.rma.klase.Kviz;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFrag extends Fragment  {

    public GridView grid;
     public GridViewAdapter gridViewAdapter=null;
     public Resources rs;
   public Activity activity;
    ListFunction instanca;
    static boolean x= false;
    static boolean refresh=false;


    public DetailFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View iv=inflater.inflate(R.layout.fragment_detail, container, false);
        grid= (GridView) iv.findViewById(R.id.gridKvizovi);
        rs= getResources();
        activity= getActivity();

        gridViewAdapter= new GridViewAdapter(getActivity(), KvizoviAkt.odabraniKvizovi, rs );
        grid.setAdapter(gridViewAdapter);

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
                if (pos== KvizoviAkt.odabraniKvizovi.size()-1) {
                    x = true;
                    refresh=true;
                }
                instanca.editKviz();
                return true;
            }
        });

        return iv;

    }

    @Override
    public void onResume() {
        if (x) instanca.editKviz();
        System.out.println(KvizoviAkt.odabraniKvizovi.size() + "|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        gridViewAdapter= new GridViewAdapter(getActivity(), KvizoviAkt.odabraniKvizovi, getResources() );
        grid.setAdapter(gridViewAdapter);
        x=false;
        refresh=false;
        super.onResume();

    }



    public interface ListFunction {
        public void addKviz (int i);
        public void editKviz ();
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
