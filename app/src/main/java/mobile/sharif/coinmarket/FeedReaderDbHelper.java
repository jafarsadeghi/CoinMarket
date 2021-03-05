package mobile.sharif.coinmarket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "coin";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SHORT_NAME = "short_name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_ONE_HOUR = "one_hour";
    private static final String COLUMN_ONE_DAY = "one_day";
    private static final String COLUMN_SEVEN_DAY = "seven_day";
    private static final String COLUMN_LOGO_PATH = "logo_path";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "coins.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_SHORT_NAME + " TEXT," +
                    COLUMN_PRICE + " REAL," +
                    COLUMN_ONE_HOUR + " REAL," +
                    COLUMN_ONE_DAY + " REAL," +
                    COLUMN_SEVEN_DAY + " REAL," +
                    COLUMN_LOGO_PATH + " TEXT)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    // If you change the database schema, you must increment the database version.


    FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void deleteAllData(SQLiteDatabase db) {
        db.execSQL("delete from " + TABLE_NAME);
    }

    void putCoin(SQLiteDatabase db, Coin coin) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderDbHelper.COLUMN_NAME, coin.getName());
        values.put(FeedReaderDbHelper.COLUMN_SHORT_NAME, coin.getShort_name());
        values.put(FeedReaderDbHelper.COLUMN_PRICE, coin.getPrice());
        values.put(FeedReaderDbHelper.COLUMN_ONE_HOUR, coin.getOne_hour_change());
        values.put(FeedReaderDbHelper.COLUMN_ONE_DAY, coin.getOne_day_change());
        values.put(FeedReaderDbHelper.COLUMN_SEVEN_DAY, coin.getSeven_day_change());
        if (coin.getLogo_path() != null){
            values.put(FeedReaderDbHelper.COLUMN_LOGO_PATH, coin.getLogo_path());
        }
        // Insert the new row, returning the primary key value of the new row
        long row_id = db.insert(FeedReaderDbHelper.TABLE_NAME, null, values);
        coin.setRow_id(row_id);
    }

    private Coin getCoin(Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COLUMN_NAME));
        String short_name = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COLUMN_SHORT_NAME));
        Double price = cursor.getDouble(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COLUMN_PRICE));
        Double one_hour = cursor.getDouble(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COLUMN_ONE_HOUR));
        Double one_day = cursor.getDouble(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COLUMN_ONE_DAY));
        Double seven_day = cursor.getDouble(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COLUMN_SEVEN_DAY));
        return new Coin(name, short_name, price, one_hour, one_day, seven_day);
    }

    ArrayList<Coin> getAllCoins(SQLiteDatabase db, FeedReaderDbHelper dbHelper) {
        Cursor cursor = db.query(FeedReaderDbHelper.TABLE_NAME,
                null, null, null, null, null, null);

        ArrayList<Coin> coins = new ArrayList<>();
        while (cursor.moveToNext()) {
            Coin coin = dbHelper.getCoin(cursor);
            coins.add(coin);
        }
        cursor.close();
        return coins;
    }

}

