package ba.unsa.etf.rma.baza;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OdgovorDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "mojaBaza.db";
    public static final String DATABASE_TABLE = "Odgovori";
    public static final int DATABASE_VERSION = 1;
    public static final String KOLONA_ID = "_id";
    public static final String NAZIV_ODGOVORA ="nazivOdgovora";
    public static final String PITANJE_ID ="nazivPitanja";


    private static final String DATABASE_CREATE = "create table " +
            DATABASE_TABLE + " (" + KOLONA_ID +
            " integer primary key autoincrement, " +
            NAZIV_ODGOVORA + " text not null, " +
            PITANJE_ID + " text not null);";




    public OdgovorDB(Context context, String name,
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
