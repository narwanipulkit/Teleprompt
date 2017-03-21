package pn3.teleprompt;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by pulkitnarwani on 21/03/17.
 */

public class DataProvider extends ContentProvider {


    static String DATABASE_NAME="script";
    static int DATABASE_VERSION=1;


    SQLiteDatabase db;

    private static class DatabaseHelper extends SQLiteOpenHelper {


        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE data (_id INTEGER PRIMARY KEY AUTOINCREMENT , title TEXT NOT NULL , data TEXT NOT NULL)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS data");
            onCreate(db);
        }
    }


    @Override
    public boolean onCreate() {

        DatabaseHelper dbHelper=new DatabaseHelper(getContext());
        db=dbHelper.getWritableDatabase();
        if(db==null)
            return false;
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        SQLiteQueryBuilder sqb=new SQLiteQueryBuilder();
        sqb.setTables("data");
        Cursor c=sqb.query(db,strings,s,strings1,null,null,s1);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.e("Insert","2DB");
        db.insert("data","",contentValues);
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return db.delete("data",s,strings);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        db.update("data",contentValues,s,strings);
        return 0;
    }
}
