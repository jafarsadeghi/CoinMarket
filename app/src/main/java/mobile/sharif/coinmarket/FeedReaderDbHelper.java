package mobile.sharif.coinmarket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "coin";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SHORT_NAME = "short_name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_ONE_HOUR = "one_hour";
    public static final String COLUMN_ONE_DAY = "one_day";
    public static final String COLUMN_SEVEN_DAY = "seven_day";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "coins.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_SHORT_NAME + " TEXT," +
                    COLUMN_PRICE + " REAL," +
                    COLUMN_ONE_HOUR + " REAL," +
                    COLUMN_ONE_DAY + " REAL," +
                    COLUMN_SEVEN_DAY + " REAL)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    // If you change the database schema, you must increment the database version.


    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void putCoin(SQLiteDatabase db, Coin coin){
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderDbHelper.COLUMN_NAME, coin.getName());
        values.put(FeedReaderDbHelper.COLUMN_SHORT_NAME, coin.getShort_name());
        values.put(FeedReaderDbHelper.COLUMN_PRICE, coin.getPrice());
        values.put(FeedReaderDbHelper.COLUMN_ONE_HOUR, coin.getOne_hour_change());
        values.put(FeedReaderDbHelper.COLUMN_ONE_DAY, coin.getOne_day_change());
        values.put(FeedReaderDbHelper.COLUMN_SEVEN_DAY, coin.getSeven_day_change());

        // Insert the new row, returning the primary key value of the new row
        long row_id =  db.insert(FeedReaderDbHelper.TABLE_NAME, null, values);
        coin.setRow_id(row_id);
    }

    public Coin getCoin(Cursor cursor){
        String name = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COLUMN_NAME));
        String short_name = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COLUMN_SHORT_NAME));
        Double price = cursor.getDouble(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COLUMN_PRICE));
        Double one_hour = cursor.getDouble(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COLUMN_ONE_HOUR));
        Double one_day = cursor.getDouble(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COLUMN_ONE_DAY));
        Double seven_day = cursor.getDouble(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COLUMN_SEVEN_DAY));

        return new Coin(name,short_name, price,one_hour,one_day,seven_day);
    }
    public List<Coin> getAllCoins(SQLiteDatabase db,FeedReaderDbHelper dbHelper){
        Cursor cursor = db.query(FeedReaderDbHelper.TABLE_NAME,
                null, null, null, null, null, null);

        List<Coin> coins = new ArrayList<>();
        while (cursor.moveToNext()) {
            Coin coin = dbHelper.getCoin(cursor);
            coins.add(coin);
        }
        cursor.close();
        return coins;
    }

}

