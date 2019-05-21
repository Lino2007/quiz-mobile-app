package ba.unsa.etf.rma.klase;

import android.content.Context;
import android.os.AsyncTask;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
//import com.google.api.client.util.*;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStream;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;


public class Firebase extends AsyncTask {

    private Context context;

    public Firebase(Context context) {
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        InputStream is = context.getResources().openRawResource(R.raw.secret);
        GoogleCredential credentials=null;



        try

        {
            credentials = GoogleCredential.fromStream(is).
                    createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
        } catch(IOException e)

        {
            credentials=null;

        }
        try {
            if (credentials==null)  throw new Exception ("Greska pri dobavljanju tokena!");

            credentials.refreshToken();
        }
        catch ( Exception e) {
            System.out.println(e + "Nesto nije uredu sa tokenom.");
        }
        KvizoviAkt.TOKEN= credentials.getAccessToken();

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {


    }





}

