package ba.unsa.etf.rma.baza;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RanglistaDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "mojaBaza.db";
    public static final String DATABASE_TABLE = "Rangliste";
    public static final int DATABASE_VERSION = 1;
    public static final String KOLONA_ID = "_id";
    public static final String IME_IGRACA ="imeIgraca";
    public static final String PROCENAT = "procenat";
    public static final String KVIZ_FK = "nazivKviza";


    private static final String DATABASE_CREATE = "create table " +
            DATABASE_TABLE + " (" + KOLONA_ID +
            " integer primary key autoincrement, " +
            IME_IGRACA + " text not null, " +
            KVIZ_FK + " text not null," +
            PROCENAT + " text not null);";


    public RanglistaDB (Context context, String name,
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
