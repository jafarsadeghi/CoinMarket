package mobile.sharif.coinmarket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
    private static final String KEY_ROWID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SHORTNAMR = "shortname";
    private static final String KEY_PRICE = "price";
    private static final String KEY_ONEHOUR = "onehour";
    private static final String KEY_ONEDAY = "oneday";
    private static final String KEY_SEVENDAY = "sevenday";
    private static final String TAG = "DBAdapter";

    private static final String DATABASE_NAME = "coins";
    private static final String DATABASE_TABLE = "coin";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "create table coin (_id integer primary key autoincrement, "
                    + "name text not null, shortname text not null, "
                    + "price int , onehour int, oneday int, sevenday int);";

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    DBAdapter(Context ctx) {
        this.context = ctx;
        Log.i(MainActivity.ACCOUNT_SERVICE, "reeeeeeeeeeeee");
        DBHelper = new DatabaseHelper(context);


    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS coin");
            onCreate(db);
        }
    }

    //---opens the database---
    DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    void close() {
        DBHelper.close();
    }


    long insertCoin(Coin coin) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, coin.getName());
        initialValues.put(KEY_SHORTNAMR, coin.getShort_name());
        initialValues.put(KEY_PRICE, coin.getPrice());
        initialValues.put(KEY_ONEHOUR, coin.getOne_hour_change());
        initialValues.put(KEY_ONEDAY, coin.getOne_day_change());
        initialValues.put(KEY_SEVENDAY, coin.getSeven_day_change());
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    Cursor getAllCoins() {
        return db.query(DATABASE_TABLE, new String[]{
                        KEY_ROWID,
                        KEY_NAME,
                        KEY_SHORTNAMR,
                        KEY_PRICE,
                        KEY_ONEHOUR,
                        KEY_ONEDAY,
                        KEY_SEVENDAY},
                null,
                null,
                null,
                null,
                null,
                null);
    }

    //---retrieves a particular title---
    public Cursor getCoin(long rowId) throws SQLException {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[]{
                                KEY_ROWID,
                                KEY_NAME,
                                KEY_SHORTNAMR,
                                KEY_PRICE,
                                KEY_ONEHOUR,
                                KEY_ONEDAY,
                                KEY_SEVENDAY
                        },
                        KEY_ROWID + "=" + rowId,
                        null,
                        null,
                        null,
                        null,
                        null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updateCoin(Coin coin) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, coin.getName());
        args.put(KEY_SHORTNAMR, coin.getShort_name());
        args.put(KEY_PRICE, coin.getPrice());
        args.put(KEY_ONEHOUR, coin.getOne_hour_change());
        args.put(KEY_ONEDAY, coin.getOne_day_change());
        args.put(KEY_SEVENDAY, coin.getSeven_day_change());
        return db.update(DATABASE_TABLE, args,
                KEY_ROWID + "=" + coin.getRow_id(), null) > 0;
    }
}
