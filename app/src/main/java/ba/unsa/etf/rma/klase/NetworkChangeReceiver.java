package ba.unsa.etf.rma.klase;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;

public class NetworkChangeReceiver extends BroadcastReceiver {

    ConnecitivityChangeAction instanca= null;
    Activity activity=null;

    public void setContext (Context context) {
        activity=(Activity)context;
        try {
            instanca = (ConnecitivityChangeAction) activity;
        }
        catch (Exception e) {
            //
        }
    }




    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = NetworkUtil.getConnectivityStatusString(context);
//        setContext(context);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                System.out.println("Interneta nema+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

            } else {
                System.out.println("Interneta ima+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            }
        }
    }


    public interface ConnecitivityChangeAction {
        public void onConnected ();
        public void onDisconnected ();
    }
}