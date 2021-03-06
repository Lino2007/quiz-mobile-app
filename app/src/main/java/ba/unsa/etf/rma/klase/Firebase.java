package ba.unsa.etf.rma.klase;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.json.JsonParser;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Array;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.TreeMap;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;



public class Firebase extends AsyncTask {
    private ArrayList<String> zaValidacijuPitanja = new ArrayList<>();
    private Context context;
    private static final String urlLink ="https://firestore.googleapis.com/v1/projects/rma-spirala3-baza/databases/(default)/documents/" ;
    public static ArrayList<Pitanje> listaMogucih = new ArrayList<>();
    private ArrayList<Kategorija> ucitaneKategorije = new ArrayList<>();
    private ArrayList<String>  rangList = new ArrayList<>();
    private KvizoviAkt.OCstatus globalniStatus = KvizoviAkt.OCstatus.UNDEFINED;
    private Map<Double,String> mapRangLista = new TreeMap<>(Collections.<Double>reverseOrder());
    private Map<Integer,Pair<Double,String>> conversionMap = new TreeMap<>();

    private ArrayList<Kviz> ucitaniKvizovi = new ArrayList<>();
    private ArrayList<Kviz> ucitaniOdabraniKvizovi= new ArrayList<>();
    private ArrayListMultimap<String, Pair<Double,String>> sveRangListe = ArrayListMultimap.create();

    //Mjesto za intefejse
    public interface ProvjeriStatus{
       public void dobaviSpinerPodatke (ArrayList<Kviz> oKv, ArrayList<Kviz> sKv);
        public void dobaviKategorije (ArrayList<Kategorija> kat);
        public void dobaviPodatke (ArrayList<Kviz> oKv, ArrayList<Kviz> sKv, ArrayList<Kategorija> kat);
        public void azurirajPodatke  (ArrayList<Kviz> oKv, ArrayList<Kviz> sKv);
        public void validacijaPitanja (ArrayList<String> listaPitanja);
    }
    public interface Rangliste {
        public void getRangliste (ArrayList<String> rl);
        public void ugrabiSve (ArrayListMultimap<String, Pair<Double,String>> rl);
    }

    private ProvjeriStatus pozivatelj;
    private Rangliste poziv;

  public Firebase (KvizoviAkt.OCstatus stat, Context context, Rangliste poziv) {
      this.globalniStatus= stat;
      this.context = context;
      this.poziv=poziv;
  }

    public Firebase(Context context) {
        this.context = context;
    }

    public Firebase (KvizoviAkt.OCstatus stat, Context context, ProvjeriStatus pozivatelj) {
           this.globalniStatus= stat;
        this.context = context;
        this.pozivatelj=pozivatelj;
    }

    @Override
    protected Object doInBackground(Object... objects) {
        KvizoviAkt.OCstatus opcode = (KvizoviAkt.OCstatus) objects[0];

        //Proces dobavljanja tokena
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


         //Pozivi metoda na osnovu akcije definirane signalom
        if (opcode== KvizoviAkt.OCstatus.ADD_KVIZ) {
            ucitajKategorije();
            dodajKviz(objects);
            ucitajKvizove("Svi");
            kreirajListuMogucih();
        }
        else if (opcode == KvizoviAkt.OCstatus.EDIT_KVIZ)  {
            ucitajKategorije();
            editKviz(objects);
            ucitajKvizove("Svi");
            kreirajListuMogucih();
        }
        else if (opcode == KvizoviAkt.OCstatus.ADD_PITANJE) {
            dodajPitanje(objects);

        }
        else if (opcode == KvizoviAkt.OCstatus.GET_MOGUCA) kreirajListuMogucih();
        else if (opcode == KvizoviAkt.OCstatus.GET_KATEGORIJE || opcode== KvizoviAkt.OCstatus.V_GET_KATEGORIJE) {
            ucitajKategorije();
        }
        else if (opcode == KvizoviAkt.OCstatus.ADD_KAT)  dodajKategoriju(objects);
        else if (opcode == KvizoviAkt.OCstatus.GET_DB_CONTENT)  {

            ucitajKategorije();
            ucitajKvizove((String) objects[1]);
        }
        else if (opcode == KvizoviAkt.OCstatus.GET_SPINNER_CONTENT)  {
            ucitajKategorije();
            ucitajKvizove((String) objects[1]);
        }
        else if (opcode== KvizoviAkt.OCstatus.GET_RL) {

            ucitajRanglistu(objects);
        }
        else if (globalniStatus == KvizoviAkt.OCstatus.IMPORT_PITANJA_CHECK) {
            vratiPitanja();
        }
        else if (opcode == KvizoviAkt.OCstatus.IMPORT_PITANJA_ADD) {
            ucitajListuPitanja(objects);
        }
        else if (opcode == KvizoviAkt.OCstatus.GET_ALL_RL) {
            dajSveRangListe();
        }
        else if (opcode == KvizoviAkt.OCstatus.UPDATE_RL) {
            updateRangLists(objects);
           // dajSveRangListe();
        }
        return null;
    }

    private void azurirajNazivKvizaRL (String stariNaziv) {
        try {
            String linkExtension = "Rangliste/" + stariNaziv + "?access_token=";
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

        }
        catch (Exception e) {
            System.out.println("Something went wrong");
        }

    }


    private void dajSveRangListe () {

        try {
            URL url = new URL("https://firestore.googleapis.com/v1/projects/rma-spirala3-baza/databases/(default)/documents/Rangliste?access_token=" + URLEncoder.encode(KvizoviAkt.TOKEN, "UTF-8"));
            System.out.println(url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            System.out.println(connection.getResponseMessage() + "+test");
            String content = streamToString(new BufferedReader(new InputStreamReader(connection.getInputStream())));
            System.out.println(content);
            JSONObject dokumenti = new JSONObject(content);
            JSONArray statistike = dokumenti.getJSONArray("documents");
            if (statistike.length() == 0) throw new Exception("Array statistike je prazan!");


            for (int j =0; j<statistike.length() ; j++) {
                JSONObject doc = statistike.getJSONObject(j);
                doc = doc.getJSONObject("fields");
                JSONObject jsn = doc.getJSONObject("nazivKviza");
                 String nazKviza = jsn.getString("stringValue");
                JSONObject poz = doc.getJSONObject("brojPozicija");
                int brojPozicija = poz.getInt("integerValue");
                JSONObject polja = doc;
                polja = polja.getJSONObject("lista");
                polja = polja.getJSONObject("mapValue");

                polja = polja.getJSONObject("fields");
                for (Integer i = 1; i <= brojPozicija; i++) {
                    JSONObject mapVal = polja.getJSONObject(i.toString());
                    mapVal = mapVal.getJSONObject("mapValue");
                    mapVal = mapVal.getJSONObject("fields");
                    String naziv = new String(dajIme(mapVal));
                    mapVal = mapVal.getJSONObject(naziv);
                    String procen = mapVal.getString("stringValue");
                    Double procenat = Double.parseDouble(procen);

                    sveRangListe.put(nazKviza, new Pair<Double,String>(procenat,naziv));

                }

            }

        }
            catch (Exception e) {
                System.out.println("##### Greska pri ucitavanju svih kvizova: "+ e);
            }
    }

    private void updateRangLists (Object[] objects) {
      globalniStatus=KvizoviAkt.OCstatus.ADD_RL;
        ArrayListMultimap<String, Pair<Double,String>> lista =  ( ArrayListMultimap<String, Pair<Double,String>>) objects[1];
        for (Map.Entry<String, Pair<Double,String>> x : lista.entries()) {
            Object[] newObject = new Object[] { KvizoviAkt.OCstatus.ADD_RL,  x.getKey(),   x.getValue().second , x.getValue().first};
            ucitajRanglistu(newObject );
        }

    }
   public void ucitajRanglistu (Object[] objects) {
      mapRangLista.clear();
        String imeIgraca =null;
        double procent =0;
            String nazivKviza=null;
        if (globalniStatus== KvizoviAkt.OCstatus.ADD_RL || globalniStatus== KvizoviAkt.OCstatus.GET_RL) {
            imeIgraca =(String) objects[2];
            procent =(double) objects[3];
            procent= Math.round(procent);
            nazivKviza=(String) objects[1];
        }
        else {
         nazivKviza = (String) objects[2];
        }

        String query = "{\n" +
                "  \"structuredQuery\": {\n" +
                "    \"where\" : {\n" +
                "     \"fieldFilter\" : { \n" + "    \"field\": {\"fieldPath\": \"nazivKviza\"}, \n"  +
                "       \"op\": \"EQUAL\" ,   \n" +
                "        \"value\": {\"stringValue\": \"" + nazivKviza  + "\"}\n" +  " }\n" +   "  },\n" +
                "            \"select\": { \"fields\": [  {\"fieldPath\": \"brojPozicija\"},  {\"fieldPath\": \"lista\"},  {\"fieldPath\": \"nazivKviza\"}]}, \n" +
                "             \"from\": [{\"collectionId\": \"Rangliste\"}],\n" +
                "              \"limit\": 1000 \n            "+
                "      }\n" +
                "}";
        try {
            URL url = new URL("https://firestore.googleapis.com/v1/projects/rma-spirala3-baza/databases/(default)/documents:runQuery?access_token=" + URLEncoder.encode(KvizoviAkt.TOKEN, "UTF-8"));
            System.out.println(url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            try (OutputStream os =connection.getOutputStream()) {
                byte[] input = query.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            System.out.println(connection.getResponseMessage() + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

          ArrayList<String> rangLista = new ArrayList<>();
          String  content= streamToString( new BufferedReader(new InputStreamReader(connection.getInputStream())));
          content = "{\"documents\":" + content + "}";
            System.out.println(content);
          JSONObject dokumenti= new JSONObject(content);
            JSONArray statistike =  dokumenti.getJSONArray("documents");
            if (statistike.length()==0) throw new Exception("Array statistike je prazan!");

            JSONObject doc = statistike.getJSONObject(0);
            doc = doc.getJSONObject("document");
            doc = doc.getJSONObject("fields");
            JSONObject poz = doc.getJSONObject("brojPozicija");
            int brojPozicija = poz.getInt("integerValue");
            JSONObject polja = doc;
            polja = polja.getJSONObject("lista");
            polja= polja.getJSONObject("mapValue");

            polja = polja.getJSONObject("fields");
            for (Integer i=1; i<=brojPozicija; i++) {
                JSONObject mapVal = polja.getJSONObject(i.toString());
                mapVal= mapVal.getJSONObject("mapValue");
                mapVal = mapVal.getJSONObject("fields");
                String naziv =  new String (dajIme(mapVal));
                mapVal = mapVal.getJSONObject(naziv);
               String procen = mapVal.getString("stringValue");
               Double procenat = Double.parseDouble(procen) + (double) i/10000;
               mapRangLista.put(procenat ,naziv);
            }
           if (globalniStatus== KvizoviAkt.OCstatus.ADD_RL || globalniStatus== KvizoviAkt.OCstatus.GET_RL ) dodajIgraca(nazivKviza,imeIgraca,procent);
           else {

              azurirajNazivKvizaRL(nazivKviza);
               addRanglist(objects);

           }
        }
        catch (Exception e) {
            if (globalniStatus== KvizoviAkt.OCstatus.ADD_RL || globalniStatus== KvizoviAkt.OCstatus.GET_RL )  dodajIgraca(nazivKviza,imeIgraca,procent);
            else  {
                azurirajNazivKvizaRL(nazivKviza);
                addRanglist(objects);

            }
            System.out.println("QUERY ISSUE: " + e);
        }
    }

    private String dajIme (JSONObject a) {
      //Preuzimanje imena kviza za ranglistu iz stringa koji na pocetku ima "<ime>"
        try {
            String obj = a.toString();

            int i=0;
            while (i<obj.length()) {
                if (obj.charAt(i)=='"') {
                    int j=i+1;
                    while (j<obj.length()) {
                        if (obj.charAt(j)=='"') return obj.substring(i+1, j);
                        j++;
                    }
                }
                i++;
            }

        }
        catch (Exception e) {
            System.out.println(e);
        }
        return  null;
    }


    private void ucitajKvizove (String katStr) {
        boolean t_signal = false;
        if (katStr.equals("Svi") )  t_signal=true;

        ArrayList<Kviz> odKvizovi = new ArrayList<>();
        ArrayList<Kviz> svKvizovi = new ArrayList<>();


     try {
            ArrayList<Pitanje> listaPitanjaKviza = new ArrayList<>();
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

            ArrayList<Pitanje> listaSvihPitanja = new ArrayList<>();
            try {
                JSONArray listaPitanja = pitanja.getJSONArray("documents");

                //grabimo pitanja moguca
                for (int i = 0; i < listaPitanja.length(); i++) {
                    JSONObject jsonPitanje = listaPitanja.getJSONObject(i);
                    jsonPitanje = jsonPitanje.getJSONObject("fields");
                    JSONObject jsonNaziv = jsonPitanje.getJSONObject("naziv");
                    String naziv = jsonNaziv.getString("stringValue");
                    JSONObject pozT = jsonPitanje.getJSONObject("indexTacnog");
                    int pozicijaTacnog = pozT.getInt("integerValue");
                    JSONObject pit = jsonPitanje.getJSONObject("odgovori");
                    JSONObject pit2 = pit.getJSONObject("arrayValue");
                    JSONArray lista = pit2.getJSONArray("values");
                    ArrayList<String> odgovori = new ArrayList<>();
                    for (int j = 0; j < lista.length(); j++) {
                        JSONObject arrVal = lista.getJSONObject(j);
                        odgovori.add(arrVal.getString("stringValue"));
                    }
                    listaSvihPitanja.add(new Pitanje(naziv, naziv, odgovori.get(pozicijaTacnog), odgovori));

                }
            }
            catch (Exception e) {

            }


            JSONArray listaKvizova = kvizovi.getJSONArray("documents");

            for (int i = 0 ; i<listaKvizova.length(); i++) {
               listaPitanjaKviza.clear();

                JSONObject jsonObj = listaKvizova.getJSONObject(i);

                jsonObj= jsonObj.getJSONObject("fields");
                //za naziv
                JSONObject jsonNaziv = jsonObj.getJSONObject("naziv");
                String naziv= jsonNaziv.getString("stringValue");

                //za Id kategorije
                JSONObject jsonKategorija = jsonObj.getJSONObject("idKategorije");
                String kategorija =  jsonKategorija.getString("stringValue");

                Kategorija k = dajKategorijuPoStringu (kategorija);

                if (!t_signal &&(k==null || (k!=null && !k.getNaziv().equals(katStr)))) continue;
                jsonObj= jsonObj.getJSONObject("pitanja");
                jsonObj= jsonObj.getJSONObject("arrayValue");
                JSONArray lista;
                try {
                    lista = jsonObj.getJSONArray("values");
                }
                catch (Exception e) {
                    odKvizovi.add (new Kviz (naziv, new ArrayList<Pitanje>(), k));
                    svKvizovi.add (new Kviz (naziv,  new ArrayList<Pitanje>(), k));
                    continue;
                }

                for (int j=0 ; j<lista.length(); j++) {
                    JSONObject arrVal = lista.getJSONObject(j);
                    String nazivPitanja = arrVal.getString("stringValue");
                    Pitanje p = dajPitanjePoStringu(nazivPitanja,listaSvihPitanja);
                    listaPitanjaKviza.add (p);
                }
               ArrayList<Pitanje> zaKv = vratiListuPitanja(listaPitanjaKviza);
                odKvizovi.add (new Kviz (naziv, zaKv, k));
                svKvizovi.add (new Kviz (naziv, zaKv, k));
            }

              ucitaniKvizovi= svKvizovi;
              ucitaniOdabraniKvizovi = odKvizovi;
     }
     catch ( Exception e) {
         System.out.println(e);
     }

    }

    private void dodajKategoriju(Object [] objects) {
        try {
            Kategorija kat = (Kategorija) objects[1];
            String nazivKategorije = kat.getNaziv();
            String idIkonice = kat.getId();
            String jsonObjekt = "{ \"fields\":{\"naziv\":{\"stringValue\":\"" + nazivKategorije + "\"},\"idIkonice\":{\"integerValue\":\"" + idIkonice  + "\"},}}"  ;

            String linkExtension = "Kategorije?documentId=" + nazivKategorije + "&access_token=";
            Log.d ("FAZA",  "SLANJE PODATAKA ZAPOCETO!" );
            URL url = new URL(urlLink + linkExtension +URLEncoder.encode( KvizoviAkt.TOKEN, "UTF-8"));
            System.out.println(url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //Postavka property-a
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept","application/json");

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

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(null);
        System.out.println(globalniStatus);
      if (globalniStatus== KvizoviAkt.OCstatus.GET_DB_CONTENT )  pozivatelj.dobaviPodatke(ucitaniOdabraniKvizovi,ucitaniKvizovi,ucitaneKategorije);
      else if (globalniStatus== KvizoviAkt.OCstatus.GET_SPINNER_CONTENT) pozivatelj.dobaviSpinerPodatke(ucitaniOdabraniKvizovi,ucitaniKvizovi);
      else if (globalniStatus==KvizoviAkt.OCstatus.ADD_KVIZ || globalniStatus==KvizoviAkt.OCstatus.EDIT_KVIZ)   pozivatelj.azurirajPodatke(ucitaniOdabraniKvizovi,ucitaniKvizovi);
      else if (globalniStatus== KvizoviAkt.OCstatus.V_GET_KATEGORIJE) pozivatelj.dobaviKategorije(ucitaneKategorije);
      else if (globalniStatus== KvizoviAkt.OCstatus.GET_RL || globalniStatus==KvizoviAkt.OCstatus.ADD_RL)  poziv.getRangliste(rangList);
      else if (globalniStatus== KvizoviAkt.OCstatus.IMPORT_PITANJA_CHECK) pozivatelj.validacijaPitanja(zaValidacijuPitanja);
      else if (globalniStatus== KvizoviAkt.OCstatus.GET_ALL_RL) poziv.ugrabiSve(sveRangListe);
      else if (globalniStatus== KvizoviAkt.OCstatus.UPDATE_RL) poziv.getRangliste(null);
      globalniStatus=KvizoviAkt.OCstatus.UNDEFINED;
  }


    private Pitanje dajPitanjePoStringu (String s, ArrayList<Pitanje> x) {
        int i=0;
        for (Pitanje a : x) {

            if (s.equals(x.get(i).getNaziv())) return a;
                i++;
        }
        System.out.println(s  + "Pitanje koje navodno ne postoji" + x.size());
         return null;
    }

    private Kategorija dajKategorijuPoStringu (String s) {
        int i=0;

        for (Kategorija a : ucitaneKategorije) {

            if (s.equals(a.getNaziv()))  {

                return a;
            }
            i++;
        }
        return null;
    }

    private void addRanglist (Object ... object) {
        Kviz noviKviz = (Kviz) object[1];
        String nazivKviza = noviKviz.getNaziv();
        String jsonObj = "{ \"fields\": {\n" +
                "        \"lista\": {\n" +
                "          \"mapValue\": {\n" +
                "            \"fields\": {";
        String map = jsonObj+ " ";
        rangList.clear();
        int i=0;
        for (Map.Entry<Double,String> obj : mapRangLista.entrySet()) {
            rangList.add ("Pozicija "+ ++i + " Ime i prezime: " + obj.getValue() + "\nProcenat tacnih: "+ Math.round(obj.getKey()) + "%");
            map += " \"" + i + "\": {\n" +
                    "                \"mapValue\": {\n" +
                    "                  \"fields\": {\n" +
                    "                   " + "\"" + obj.getValue() + "\": {\n" +
                    "                      \"stringValue\": \"" + obj.getKey() + "\"\n" +
                    "                    }\n" +
                    "                  }\n" +
                    "                }\n" +
                    "              }";
            if (i<mapRangLista.size()) {
                map+=",";
            }
            else {
                map+="  }\n" +
                        "          }\n" +
                        "        },";
            }

        }

        map+= "    \"nazivKviza\": {\n" +
                "          \"stringValue\": \"" +nazivKviza+ "\"\n" +
                "        },\n" +
                "        \"brojPozicija\": {\n" +
                "          \"integerValue\": \""+mapRangLista.size()+"\"\n" +
                "        }\n" +
                "      } }";
        if (mapRangLista.size()==0) map= "{}";
        System.out.println(map);
        try {
            URL url = new URL("https://firestore.googleapis.com/v1/projects/rma-spirala3-baza/databases/(default)/documents/Rangliste/" + nazivKviza+ "?access_token=" + URLEncoder.encode(KvizoviAkt.TOKEN, "UTF-8"));
            System.out.println(url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("PATCH");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");



            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(map);

            System.out.println(connection.getResponseMessage() + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        }


        catch (Exception e) {

        }

    }


    private void addRanglistIzSQL (Object ... object) {
        Kviz noviKviz = (Kviz) object[1];
        String nazivKviza = noviKviz.getNaziv();
        String jsonObj = "{ \"fields\": {\n" +
                "        \"lista\": {\n" +
                "          \"mapValue\": {\n" +
                "            \"fields\": {";
        String map = jsonObj+ " ";
        rangList.clear();
        int i=0;
        for (Map.Entry<Double,String> obj : mapRangLista.entrySet()) {
            rangList.add ("Pozicija "+ ++i + " Ime i prezime: " + obj.getValue() + "\nProcenat tacnih: "+ Math.round(obj.getKey()) + "%");
            map += " \"" + i + "\": {\n" +
                    "                \"mapValue\": {\n" +
                    "                  \"fields\": {\n" +
                    "                   " + "\"" + obj.getValue() + "\": {\n" +
                    "                      \"stringValue\": \"" + obj.getKey() + "\"\n" +
                    "                    }\n" +
                    "                  }\n" +
                    "                }\n" +
                    "              }";
            if (i<mapRangLista.size()) {
                map+=",";
            }
            else {
                map+="  }\n" +
                        "          }\n" +
                        "        },";
            }

        }

        map+= "    \"nazivKviza\": {\n" +
                "          \"stringValue\": \"" +nazivKviza+ "\"\n" +
                "        },\n" +
                "        \"brojPozicija\": {\n" +
                "          \"integerValue\": \""+mapRangLista.size()+"\"\n" +
                "        }\n" +
                "      } }";
        if (mapRangLista.size()==0) map= "{}";
        System.out.println(map);
        try {
            URL url = new URL("https://firestore.googleapis.com/v1/projects/rma-spirala3-baza/databases/(default)/documents/Rangliste/" + nazivKviza+ "?access_token=" + URLEncoder.encode(KvizoviAkt.TOKEN, "UTF-8"));
            System.out.println(url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("PATCH");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");



            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(map);

            System.out.println(connection.getResponseMessage() + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        }


        catch (Exception e) {

        }

    }

    private void dodajIgraca(String nazivKviza, String imeIgraca, double procent) {

           mapRangLista.put(procent,imeIgraca);


          String jsonObj = "{ \"fields\": {\n" +
                  "        \"lista\": {\n" +
                  "          \"mapValue\": {\n" +
                  "            \"fields\": {";
          String map = jsonObj+ " ";
          rangList.clear();
          int i=0;
          for (Map.Entry<Double,String> obj : mapRangLista.entrySet()) {
              rangList.add ("Pozicija "+ ++i + " Ime i prezime: " + obj.getValue() + "\nProcenat tacnih: "+ Math.round(obj.getKey()) + "%");
              map += " \"" + i + "\": {\n" +
                      "                \"mapValue\": {\n" +
                      "                  \"fields\": {\n" +
                      "                   " + "\"" + obj.getValue() + "\": {\n" +
                      "                      \"stringValue\": \"" + Math.round(obj.getKey()) + "\"\n" +
                      "                    }\n" +
                      "                  }\n" +
                      "                }\n" +
                      "              }";
              if (i<mapRangLista.size()) {
                  map+=",";
                       }
              else {
                 map+="  }\n" +
                         "          }\n" +
                         "        },";
              }

          }

          map+= "    \"nazivKviza\": {\n" +
                  "          \"stringValue\": \"" +nazivKviza+ "\"\n" +
                  "        },\n" +
                  "        \"brojPozicija\": {\n" +
                  "          \"integerValue\": \""+mapRangLista.size()+"\"\n" +
                  "        }\n" +
                  "      } }";
        System.out.println(map);
          try {
              URL url = new URL("https://firestore.googleapis.com/v1/projects/rma-spirala3-baza/databases/(default)/documents/Rangliste/" + nazivKviza+ "?access_token=" + URLEncoder.encode(KvizoviAkt.TOKEN, "UTF-8"));
              System.out.println(url);
              HttpURLConnection connection = (HttpURLConnection) url.openConnection();
              connection.setDoInput(true);
              connection.setRequestMethod("PATCH");
              connection.setRequestProperty("Content-Type", "application/json");
              connection.setRequestProperty("Accept", "application/json");



              DataOutputStream os = new DataOutputStream(connection.getOutputStream());
              os.writeBytes(map);

              System.out.println(connection.getResponseMessage() + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
              try(BufferedReader br = new BufferedReader(
                      new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                  StringBuilder response = new StringBuilder();
                  String responseLine = null;
                  while ((responseLine = br.readLine()) != null) {
                      response.append(responseLine.trim());
                  }
                  System.out.println(response.toString());
              }
          }


          catch (Exception e) {

          }
    }

    private void  dodajKviz (Object [] objects  ) {

        try {
            kreirajListuMogucih();
            String stariNaziv =null;
            Kviz noviKviz = (Kviz) objects[1];
            String nazivKviza= noviKviz.getNaziv();
            String idKategorije=null;
            if (objects.length>2)
            stariNaziv = (String) objects[2];
            try {
              idKategorije = noviKviz.getKategorija().getNaziv();
            }
            catch (Exception e) {
                idKategorije="Svi";
            }
            ArrayList<Pitanje> listaPitanja = new ArrayList<>();

            try {
               listaPitanja = noviKviz.getPitanja();

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
                if ( stariNaziv!=null && !stariNaziv.equals(nazivKviza))   ucitajRanglistu(objects);
                kreirajListuMogucih();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void dodajPitanje (Object [] objects) {


        try {
            Pitanje novoPitanje = (Pitanje) objects[1];
            String nazivPitanja = novoPitanje.getNaziv();
            if (daLiPostojiPitanje(nazivPitanja)) {
                Log.d("Dodaj pitanje", "Pitanje vec postoji, preskacem dodavanje u bazu.");
                return;
            }


            int indexTacnog = novoPitanje.getOdgovori().indexOf(novoPitanje.getTacan());

            ArrayList<String> listaOdgovora = novoPitanje.getOdgovori();
            String linkExtension = "Pitanja?documentId=" + nazivPitanja + "&access_token=";
            Log.d("FAZA", "SLANJE PODATAKA ZAPOCETO!");
            URL url = new URL(urlLink + linkExtension + URLEncoder.encode(KvizoviAkt.TOKEN, "UTF-8"));
            System.out.println(url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //Postavka property-a
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");


            String listaOdgovoraStr = "\"odgovori\":{\"arrayValue\": {\"values\": [";
            int i = 0;
            for (String x : listaOdgovora) {
                if (i < listaOdgovora.size() - 1) {
                    listaOdgovoraStr += "{ \"stringValue\":\"" + x + "\"},";
                } else listaOdgovoraStr += "{ \"stringValue\":\"" + x + "\"}]";
                ++i;
            }
            Log.d("URL", url.toString());

            String jsonObjekt = "{ \"fields\":{\"naziv\":{\"stringValue\":\"" + nazivPitanja + "\"},\"indexTacnog\":{\"integerValue\":\"" + indexTacnog + "\"}," + listaOdgovoraStr + "}}}}";


            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonObjekt);
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        } catch (Exception e) {
            System.out.println("Problem sa ucitavanjem pitanja " + e);
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
       kreirajListuMogucih();
   }
   catch (Exception e) {
       System.out.println("Something went wrong");
   }
    }

    private ArrayList<Pitanje> vratiListuPitanja (ArrayList<Pitanje> x) {
      //Duboko kopiranje
        ArrayList<Pitanje> avs= new ArrayList<>();
        for (Pitanje xy: x  ) {
            avs.add(xy);
        }
        return avs;
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
                String docP = doc.getString("name");
               String[] lista = docP.split("/");
               if (lista[lista.length-1].contains(nazivPitanja)) return true;
            }

        }
        catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return false;
    }

    private  void ucitajListuPitanja (Object ... obj) {
      //Svako pitanje se pojedinacno dodaje iz prosljedjene liste (CSV import)
      ArrayList<Pitanje> listaPitanja = (ArrayList<Pitanje>) obj[1];
      for (Pitanje p : listaPitanja) {
         obj[1] =  p;
          dodajPitanje(obj);
      }

    }

     private void vratiPitanja () {
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
                String docP = doc.getString("name");
                String[] lista = docP.split("/");
                zaValidacijuPitanja.add(lista[lista.length-1]);
            }

        }
        catch (Exception e) {
            System.out.println(e);

        }

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


               JSONArray lista = pit2.getJSONArray("values");

               ArrayList<String> odgovori = new ArrayList<>();
                  for (int j=0 ; j<lista.length(); j++) {
                      JSONObject arrVal = lista.getJSONObject(j);
                      odgovori.add (arrVal.getString("stringValue"));
                  }

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


             ArrayList<Pitanje> kopijaListeMogucih = kopiraj(listaMogucih);
            for (Pitanje x : listaMogucih) {
                for (String a : listaPitanjaKviza) {
                    if (a.equals(x.getNaziv())) kopijaListeMogucih.remove(x);
                }
            }
            listaMogucih=kopijaListeMogucih;
           // System.out.println(listaPitanjaKviza.size() + "!!!!!!!!!!!!!!!!!!!" + listaMogucih.size());
        }

        catch (Exception e) {
            System.out.println(e);
            return ;
        }

    }

    private void ucitajKategorije () {
         ArrayList<Kategorija> nKat= new ArrayList<>();
        try {


            URL url = new URL(urlLink +  "Kategorije?access_token=" +URLEncoder.encode(KvizoviAkt.TOKEN, "UTF-8"));
            System.out.println(url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept","application/json");
            String content= streamToString( new BufferedReader(new InputStreamReader(connection.getInputStream())));
            connection.disconnect();
            JSONObject kategorije = new JSONObject(content);
            JSONArray listaKategorija = kategorije.getJSONArray("documents");

            for (int i =0; i<listaKategorija.length(); i++) {
                JSONObject jsonKat = listaKategorija.getJSONObject(i);
                jsonKat = jsonKat.getJSONObject("fields");
                JSONObject jsonNaziv = jsonKat.getJSONObject("naziv");
                String naziv = jsonNaziv.getString("stringValue");
                JSONObject jsonIkona = jsonKat.getJSONObject("idIkonice");
                String idIkone = jsonIkona.getString("integerValue");
                nKat.add(new Kategorija(naziv,idIkone));
            }
            ucitaneKategorije=nKat;
            }
        catch ( Exception e) {
            System.out.println(e);
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

