package ba.unsa.etf.rma.baza;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class KvizDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "mojaBaza.db";
    public static final String DATABASE_TABLE = "Kvizovi";
    public static final int DATABASE_VERSION = 1;
    public static final String KOLONA_ID = "_id";
    public static final String KVIZ_ID ="nazivKviza";
    public static final String KATEGORIJA_ID ="kategorija";


    private static final String DATABASE_CREATE = "create table " +
            DATABASE_TABLE + " (" + KOLONA_ID +
            " integer primary key autoincrement, " +
            KVIZ_ID + " text not null, " +
            KATEGORIJA_ID + " text not null);";




    public KvizDB(Context context, String name,
                               CursorFactory factory, int version) {
        super(context, name, factory, version);
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("____________________________________________________________________ASDFSAFASFASFASFASFASFASFASFASFASf");
       db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        // db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
// Kreiranje nove
        onCreate(db);

    }
}
