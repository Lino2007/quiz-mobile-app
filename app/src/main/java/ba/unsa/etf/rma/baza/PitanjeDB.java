package ba.unsa.etf.rma.baza;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PitanjeDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "mojaBaza.db";
    public static final String DATABASE_TABLE = "Pitanja";
    public static final int DATABASE_VERSION = 1;
    public static final String KOLONA_ID = "_id";
    public static final String PITANJE_ID ="nazivPitanja";
    public static final String TACAN_ODGOVOR ="tacanOdgovor";
    public static final String KVIZ_FK = "nazivKviza";


    private static final String DATABASE_CREATE = "create table " +
            DATABASE_TABLE + " (" + KOLONA_ID +
            " integer primary key autoincrement, " +
           PITANJE_ID + " text not null, " +
            TACAN_ODGOVOR + " text not null, " +
          KVIZ_FK + " text not null);";


    public PitanjeDB (Context context, String name,
                        CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }
}
