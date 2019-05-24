package ba.unsa.etf.rma.klase;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;



//import com.google.api.client.util.*;


public class Firebase extends AsyncTask {

    private Context context;
    private static final String urlLink ="https://firestore.googleapis.com/v1/projects/rma-spirala3-baza/databases/(default)/documents/" ;

    public Firebase(Context context) {
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object... objects) {
        KvizoviAkt.OCstatus opcode = (KvizoviAkt.OCstatus) objects[0];

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

        if (opcode== KvizoviAkt.OCstatus.ADDKVIZ) dodajKviz(objects);






        return null;
    }

    @Override
    protected void onPostExecute(Object o) {


    }

    private void  dodajKviz (Object [] objects  ) {

        try {
            String linkExtension = "Kvizovi?access_token=";
            Log.d ("FAZA",  "SLANJE PODATAKA ZAPOCETO!" );
            URL url = new URL(urlLink + linkExtension +URLEncoder.encode( KvizoviAkt.TOKEN, "UTF-8"));
            System.out.println(url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Kviz noviKviz = (Kviz) objects[1];
            //Postavka property-a
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept","application/json");
            String nazivKviza= noviKviz.getNaziv();
            String idKategorije= noviKviz.getKategorija().getId();
            ArrayList<Pitanje> listaPitanja= noviKviz.getPitanja();
            String testString = " \"pitanja\": {\"arrayValue\": {\"values\": [{\"stringValue\": \"\"}]}";
            String listaPitanjaStr = "\"pitanja\":{\"arrayValue\": {\"values\": [";
            int i =0;
            for (Pitanje x : listaPitanja ) {
                if (i< listaPitanja.size()-1) {
                    listaPitanjaStr += "{ \"stringValue\":\"" + x.getNaziv() + "\"},";
                }
                else    listaPitanjaStr += "{ \"stringValue\":\"" + x.getNaziv() + "\"}]";
                ++i;
            }
            Log.d ("URL",  url.toString() );

            String jsonObjekt = "{ \"fields\":{\"naziv\":{\"stringValue\":\""+nazivKviza + "\"},\"idKategorije\":{\"stringValue\":\""+idKategorije +"\"},"+ listaPitanjaStr+ "}}}}";


            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonObjekt);
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }





}

