package ba.unsa.etf.rma.klase;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.ArrayList;

import ba.unsa.etf.rma.R;

import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class OdgovoriListAdapter extends BaseAdapter implements View.OnClickListener  {
    /*********** Declare Used Variables *********/
    private Activity activity= null;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    Pitanje tempValues=null;
    int i=0;
    boolean global=false;

    //  CustomAdapter Constructor
    public  OdgovoriListAdapter(Activity a, ArrayList d, Resources resLocal) {

        // Take passed values
        activity = a;
        data=d;
        res = resLocal;

        // Layout inflator to call external xml layout ()

        inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    // What is the size of Passed Arraylist Size
    public int getCount() {

        if(data.size()<=0)
            return 1;
        return data.size();
    }



    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    // Create a holder Class to contain inflated xml file elements
    public static class ViewHolder{

        public TextView nazivPitanja;
        public ImageView dot;

    }

    // Depends upon data size called for each row , Create each ListView row
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            // Inflate tabitem.xml file for each row ( Defined below )
            vi = inflater.inflate(R.layout.pitanja_u_kvizu_item, null);

            //View Holder Object to contain tabitem.xml file elements

            holder = new ViewHolder();
            holder.nazivPitanja= (TextView) vi.findViewById(R.id.nazivPitanja);
            holder.dot= (ImageView) vi.findViewById(R.id.image);


            //***********  Set holder with LayoutInflater
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();


        if(data.size()<=0)
        {
            holder.nazivPitanja.setText("Dodaj Kviz");
            holder.dot.setImageResource(res.getIdentifier("ba.unsa.etf.rma:drawable/add_button" ,null,null));
            global=false;
        }
        else
        {
            // Get each Model object from Arraylist
            tempValues=null;
            tempValues = ( Pitanje ) data.get( position );

            //  Set Model values in Holder elements

            //   holder.ime.setText( tempValues.getIme() + tempValues.getPrezime() );
       /*     if (position==data.size()-1){
                holder.nazivPitanja.setText("Dodaj Kviz");
                holder.dot.setImageResource(res.getIdentifier("ba.unsa.etf.rma:drawable/add_button" ,null,null));
            }
            else {
                holder.nazivPitanja.setText(tempValues.getNaziv());
                holder.dot.setImageResource(res.getIdentifier("ba.unsa.etf.rma:drawable/blue_dot", null, null));
            }
           */
            //  vi.setOnClickListener(new OnItemClickListener( position ));
        }

        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }


    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            //  KvizoviAkt sct = (KvizoviAkt) activity;
            //   sct.onItemClick(mPosition);
        }
    }

}
