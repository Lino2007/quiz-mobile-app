package ba.unsa.etf.rma.baza;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class KategorijaDB extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "mojaBaza.db";
    public static final String DATABASE_TABLE = "Kategorije";
    public static final int DATABASE_VERSION = 1;
    public static final String KOLONA_ID = "_id";
    public static final String KATEGORIJA_ID ="nazivKategorije";
    public static final String IKONICA_ID ="idIkonice";


    private static final String DATABASE_CREATE = "create table " +
            DATABASE_TABLE + " (" + KOLONA_ID +
            " integer primary key autoincrement, " +
            KATEGORIJA_ID + " text not null, " +
            IKONICA_ID + " text not null);";




    public KategorijaDB(Context context, String name,
                  SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
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
