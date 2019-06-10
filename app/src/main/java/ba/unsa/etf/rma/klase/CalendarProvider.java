package ba.unsa.etf.rma.klase;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class CalendarProvider {

    private Context activityContext=null;

    //advised to be static as it is  a list of the global events
    public static Map<String, String> listaDogadjaja = new HashMap<>();
    private Cursor eventCursor= null;

    //setup columns
    public static final String[] queryColums = new String[]{ CalendarContract.Events.DTSTART, CalendarContract.Events.TITLE};

    public CalendarProvider (Context context) {this.activityContext=context;}

    public void ucitajDogadjaje () {



       if (listaDogadjaja!=null && listaDogadjaja.size()!=0) listaDogadjaja.clear();
       try {
           eventCursor = activityContext.getContentResolver().query(CalendarContract.Events.CONTENT_URI, queryColums, null, null, null);
       }
       catch (Exception e) {
           System.out.println("Nesto nije uredu sa ucitavanjem aktivnosti kalendara " + e);
       }
       Log.d("Kalendar aktivnosti " , "Ucitavam aktivnosti kalendara!");

       try {
           while (eventCursor.moveToNext()) {
               String dat = eventCursor.getString(0);
               String imeDog = eventCursor.getString(1);
               listaDogadjaja.put(dat, imeDog);
           }
       }
       catch (Exception e) {
           System.out.println("Nesto nije uredu sa ucitavanjem aktivnosti kalendara " + e);
       }
        eventCursor.close();
    }

}
