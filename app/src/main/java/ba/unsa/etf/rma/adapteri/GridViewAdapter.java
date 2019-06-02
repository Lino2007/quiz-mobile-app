package ba.unsa.etf.rma.adapteri;


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

import com.maltaisn.icondialog.Icon;

import com.maltaisn.icondialog.IconDialog;
import com.maltaisn.icondialog.IconHelper;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class GridViewAdapter extends BaseAdapter  implements  IconDialog.Callback {
    /*********** Declare Used Variables *********/
    private Activity activity = null;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    public Resources res;
    Kviz tempValues = null;
    int i = 0;
    boolean global = false;
    IconDialog icondialog = new IconDialog();

    //  CustomAdapter Constructor
    public  GridViewAdapter(Activity a, ArrayList d, Resources resLocal) {

        // Take passed values
        activity = a;
        data = d;
        res = resLocal;

        // Layout inflator to call external xml layout ()
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    // What is the size of Passed Arraylist Size
    public int getCount() {

        if (data.size() <= 0)
            return 1;
        return data.size();
    }


    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onIconDialogIconsSelected(Icon[] icons) {

    }

    // Create a holder Class to contain inflated xml file elements
    public static class ViewHolder {

        public TextView nazivKviza, brPitanja;
        public ImageView dot;

    }

    // Depends upon data size called for each row , Create each ListView row
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {

            // Inflate tabitem.xml file for each row ( Defined below )
            vi = inflater.inflate(R.layout.grid_item, null);


            holder = new ViewHolder();
            holder.nazivKviza = (TextView) vi.findViewById(R.id.grid_naziv_kviza);
            holder.dot = (ImageView) vi.findViewById(R.id.grid_icon);
            holder.brPitanja= (TextView) vi.findViewById(R.id.grid_br_pitanja);

            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();


        if (data.size() <= 0) {
            holder.nazivKviza.setText("Dodaj Kviz");
            holder.dot.setImageResource(res.getIdentifier("ba.unsa.etf.rma:drawable/add_button", null, null));
            global = false;
        } else {

            tempValues = null;
            tempValues = (Kviz) data.get(position);


            if (position == data.size() - 1) {
                holder.nazivKviza.setText("Dodaj Kviz");
                holder.brPitanja.setText(" ");
                holder.dot.setImageResource(res.getIdentifier("ba.unsa.etf.rma:drawable/add_button", null, null));
            } else {
                holder.nazivKviza.setText(tempValues.getNaziv());
                if (tempValues.getPitanja()!=null) {
                    System.out.println(tempValues.getPitanja().size());
                  int x= tempValues.getPitanja().size();
                    holder.brPitanja.setText(Integer.toString(x));
                }
                else {
                    holder.brPitanja.setText("0");
                }

                if (!(tempValues.getKategorija() == null) && !(tempValues.getKategorija().getId().isEmpty())) {


                    final IconHelper iconHelper = IconHelper.getInstance(activity.getApplicationContext());
                    iconHelper.addLoadCallback(new IconHelper.LoadCallback() {
                        @Override
                        public void onDataLoaded() {
                            int value = -1;

                            if (!(tempValues.getKategorija() == null))
                                value = Integer.parseInt(tempValues.getKategorija().getId());

                            if (value != -1) {
                                holder.dot.setImageDrawable(iconHelper.getIcon(value).getDrawable(activity.getApplicationContext()));
                            }

                        }
                    });

                }

            }

        }

        return vi;
    }



}
