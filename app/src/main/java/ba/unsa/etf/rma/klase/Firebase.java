package ba.unsa.etf.rma.klase;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.json.JsonParser;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;



//import com.google.api.client.util.*;


public class Firebase extends AsyncTask {

    private Context context;
    private static final String urlLink ="https://firestore.googleapis.com/v1/projects/rma-spirala3-baza/databases/(default)/documents/" ;
    public static ArrayList<Pitanje> listaMogucih = new ArrayList<>();

    public interface UgrabiMoguca{
        public void poziv();
    }
    private UgrabiMoguca pozivatelj;
  /*  public Firebase (UgrabiMoguca poz) {
        pozivatelj=poz;
    } */


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

        if (opcode== KvizoviAkt.OCstatus.ADD_KVIZ) dodajKviz(objects);
        else if (opcode == KvizoviAkt.OCstatus.EDIT_KVIZ) editKviz(objects);
        else if (opcode == KvizoviAkt.OCstatus.ADD_PITANJE) dodajPitanje(objects);
        else if (opcode == KvizoviAkt.OCstatus.GET_MOGUCA) kreirajListuMogucih();







        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(null);
//          pozivatelj.poziv();

    }
    private void nullF () {}

    private void  dodajKviz (Object [] objects  ) {

        try {
            kreirajListuMogucih();
            Kviz noviKviz = (Kviz) objects[1];
            String nazivKviza= noviKviz.getNaziv();
            String idKategorije=null;
            try {
              idKategorije = noviKviz.getKategorija().getId();
            }
            catch (Exception e) {
                idKategorije="-1";
            }
            ArrayList<Pitanje> listaPitanja = new ArrayList<>();
            try {
               listaPitanja = noviKviz.getPitanja();
                System.out.println(listaPitanja.size()+ "˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙˙");
            }
            catch (Exception e) {
                Log.d("Dodavanje kviza", "Lista pitanja prazna, prekidam daljnji unos u bazu.");
                return ;
            }
            String linkExtension = "Kvizovi?documentId=" + nazivKviza + "&access_token=";
            Log.d ("Dodavanje kviza",  "Slanje podataka zapoceto." );
            URL url = new URL(urlLink + linkExtension +URLEncoder.encode( KvizoviAkt.TOKEN, "UTF-8"));
            System.out.println(url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //Postavka property-a
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept","application/json");

            String testString = " \"pitanja\": {\"arrayValue\": {\"values\": [{\"stringValue\": \"\"}]}";
            String listaPitanjaStr = "\"pitanja\":{\"arrayValue\": {\"values\": ";
            if (listaPitanja.size()==0) listaPitanjaStr+= "[]";
            else {
                listaPitanjaStr +="[";
                int i = 0;
                for (Pitanje x : listaPitanja) {
                    if (i < listaPitanja.size() - 1) {
                        listaPitanjaStr += "{ \"stringValue\":\"" + x.getNaziv() + "\"},";
                    } else listaPitanjaStr += "{ \"stringValue\":\"" + x.getNaziv() + "\"}]";
                    ++i;
                }
            }
            Log.d ("URL",  url.toString() );

               String jsonObjekt =  "{ \"fields\":{\"naziv\":{\"stringValue\":\""+nazivKviza + "\"},\"idKategorije\":{\"stringValue\":\""+idKategorije +"\"},"+ listaPitanjaStr+ "}}}}";


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


  /*
         String content=null, line;
        try {
            URL url = new URL(urlLink);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = rd.readLine()) != null) {
                content += line + "\n";
            }
        }
        catch (Exception e) {
            //...
        }
        System.out.println(content);
   */
    }


    private void dodajPitanje (Object [] objects) {


        try {
            Pitanje novoPitanje = (Pitanje) objects[1];
            String nazivPitanja= novoPitanje.getNaziv();

            if (daLiPostojiPitanje(nazivPitanja)) {
                Log.d ("Dodaj pitanje", "Pitanje vec postoji, preskacem dodavanje u bazu.");
                return ; }

            int indexTacnog = novoPitanje.getOdgovori().indexOf(novoPitanje.getTacan());

            ArrayList<String> listaOdgovora= novoPitanje.getOdgovori();
            String linkExtension = "Pitanja?documentId=" + nazivPitanja + "&access_token=";
            Log.d ("FAZA",  "SLANJE PODATAKA ZAPOCETO!" );
            URL url = new URL(urlLink + linkExtension +URLEncoder.encode( KvizoviAkt.TOKEN, "UTF-8"));
            System.out.println(url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //Postavka property-a
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept","application/json");


            String listaOdgovoraStr = "\"odgovori\":{\"arrayValue\": {\"values\": [";
            int i =0;
            for (String x : listaOdgovora ) {
                if (i< listaOdgovora.size()-1) {
                    listaOdgovoraStr+= "{ \"stringValue\":\"" + x + "\"},";
                }
                else    listaOdgovoraStr += "{ \"stringValue\":\"" + x + "\"}]";
                ++i;
            }
            Log.d ("URL",  url.toString() );

            String jsonObjekt =  "{ \"fields\":{\"naziv\":{\"stringValue\":\""+ nazivPitanja + "\"},\"indexTacnog\":{\"integerValue\":\""+ indexTacnog +"\"},"+ listaOdgovoraStr+ "}}}}";


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

    private void editKviz (Object[] objects) {
   try {
       String stariNaziv = (String) objects[2];
       String linkExtension = "Kvizovi/" + stariNaziv + "?access_token=";
       URL url = new URL(urlLink + linkExtension + URLEncoder.encode(KvizoviAkt.TOKEN, "UTF-8"));
       System.out.println(url);
       HttpURLConnection conn = (HttpURLConnection) url.openConnection();

       //Postavka property-a
       conn.setRequestMethod("DELETE");
       conn.setDoOutput(true);
       conn.setRequestProperty("Content-Type", "application/json");
       conn.setRequestProperty("Accept","application/json");
       conn.connect();
      String aktivirajRequest = conn.getResponseMessage();
      Log.d("Status brisanja: ", aktivirajRequest);
      dodajKviz(objects);

   }
   catch (Exception e) {
       System.out.println("Something went wrong");
   }
    }


    private boolean daLiPostojiPitanje (String nazivPitanja) {
        try {
            URL url = new URL(urlLink +  "Pitanja?access_token=" +URLEncoder.encode(KvizoviAkt.TOKEN, "UTF-8"));
            System.out.println(url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept","application/json");
           BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String content = "", line;
            while ((line = rd.readLine()) != null) {
                content += line + "\n";
            }
            JSONObject jsonObj = new JSONObject(content);
            JSONArray dokumenti= jsonObj.getJSONArray("documents");
            for (int i=0; i<dokumenti.length(); i++) {
                JSONObject doc = dokumenti.getJSONObject(i);
                if (doc.getString("name").contains(nazivPitanja)) return true;
            }

        }
        catch (Exception e) {
            System.out.println(e);
            return true;
        }
        return false;
    }

     private String streamToString (BufferedReader rd ) {
         String content = "", line;
        try {
            while ((line = rd.readLine()) != null) {
                content += line + "\n";
            }
        }
        catch (Exception e) {
            return null;
        }
        return content;
     }
    public void kreirajListuMogucih () {
        listaMogucih.clear();
        try {
            ArrayList<String> listaPitanjaKviza = new ArrayList<>();
            URL url = new URL(urlLink +  "Pitanja?access_token=" +URLEncoder.encode(KvizoviAkt.TOKEN, "UTF-8"));
            System.out.println(url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept","application/json");
            String content= streamToString( new BufferedReader(new InputStreamReader(connection.getInputStream())));
            connection.disconnect();
            JSONObject pitanja = new JSONObject(content);
            url = new URL(urlLink +  "Kvizovi?access_token=" +URLEncoder.encode(KvizoviAkt.TOKEN, "UTF-8"));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept","application/json");
            content= streamToString( new BufferedReader(new InputStreamReader(connection.getInputStream())));
            connection.disconnect();
            JSONObject kvizovi = new JSONObject(content);
            JSONArray listaPitanja = pitanja.getJSONArray("documents");


            //grabimo pitanja moguca
            for (int i =0; i<listaPitanja.length(); i++) {
                JSONObject jsonPitanje = listaPitanja.getJSONObject(i);
                jsonPitanje= jsonPitanje.getJSONObject("fields");
                JSONObject jsonNaziv = jsonPitanje.getJSONObject("naziv");
                String naziv=jsonNaziv.getString("stringValue");

                JSONObject pozT =  jsonPitanje.getJSONObject("indexTacnog");

               int pozicijaTacnog = pozT.getInt("integerValue");
               JSONObject pit =   jsonPitanje.getJSONObject("odgovori");
               JSONObject pit2 = pit.getJSONObject("arrayValue");
            //    System.out.println(pit2.toString() + "¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨");

               JSONArray lista = pit2.getJSONArray("values");

               ArrayList<String> odgovori = new ArrayList<>();
                  for (int j=0 ; j<lista.length(); j++) {
                      JSONObject arrVal = lista.getJSONObject(j);
                      odgovori.add (arrVal.getString("stringValue"));
                  }
              //  System.out.println(naziv +   "¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨");
                listaMogucih.add (new Pitanje (naziv, naziv, odgovori.get(pozicijaTacnog), odgovori ));
                  odgovori.clear();
            }

            JSONArray listaKvizova = kvizovi.getJSONArray("documents");
            for (int i = 0 ; i<listaKvizova.length(); i++) {
               JSONObject jsonObj = listaKvizova.getJSONObject(i);
               jsonObj= jsonObj.getJSONObject("fields");
               jsonObj= jsonObj.getJSONObject("pitanja");
               jsonObj= jsonObj.getJSONObject("arrayValue");
                JSONArray lista;
                try {
                    lista = jsonObj.getJSONArray("values");
                }
                catch (Exception e) {
                    continue;
                }

                for (int j=0 ; j<lista.length(); j++) {
                    JSONObject arrVal = lista.getJSONObject(j);
                    listaPitanjaKviza.add (arrVal.getString("stringValue"));
                }

            }

          System.out.println(listaPitanjaKviza.size() + "!!!!!!!!!!!!!!!!!!!" + listaMogucih.size());
             ArrayList<Pitanje> kopijaListeMogucih = kopiraj(listaMogucih);
            for (Pitanje x : listaMogucih) {
                for (String a : listaPitanjaKviza) {
                    if (a.equals(x.getNaziv())) kopijaListeMogucih.remove(x);
                }
            }
            listaMogucih=kopijaListeMogucih;

        }

        catch (Exception e) {
            System.out.println(e);
            return ;
        }

    }

     private ArrayList<Pitanje> kopiraj (ArrayList<Pitanje> x) {
        ArrayList<Pitanje> pit = new ArrayList<>();
      if (x.size()==0)  return null;
     for (Pitanje a : x) {
         pit.add (a);
     }
     return pit;
     }





}

