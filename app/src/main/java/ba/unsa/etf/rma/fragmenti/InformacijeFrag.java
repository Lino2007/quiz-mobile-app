package ba.unsa.etf.rma.fragmenti;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ba.unsa.etf.rma.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InformacijeFrag extends Fragment {

    TextView nazivKviza, brTacnih, brPreostalih, procenat;
    Button kraj;


    public InformacijeFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle newData= new Bundle();
           View iv=inflater.inflate(R.layout.fragment_informacije, container, false);
        nazivKviza= (TextView) iv.findViewById(R.id.infNazivKviza);
        brTacnih=   (TextView) iv.findViewById(R.id.infBrojTacnihPitanja);
        brPreostalih=  (TextView) iv.findViewById(R.id.infBrojPreostalihPitanja);
        procenat=  (TextView) iv.findViewById(R.id.infProcenatTacni);
        kraj = (Button) iv.findViewById(R.id.btnKraj);
         newData= this.getArguments();
          String x= newData.getString("naziv_kviza");
          String y=  Integer.toString( newData.getInt("broj_preostalih"));
          int brojTacnih = newData.getInt("broj_tacnih");
          double procenatTacnih= newData.getDouble("procenat_tacnih") * 100;

          brTacnih.setText (Integer.toString(brojTacnih));
          procenat.setText( Double.toString(procenatTacnih) + " %");
        nazivKviza.setText(x);
        brPreostalih.setText (y);



        return iv;
    }

}
