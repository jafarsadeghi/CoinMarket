package mobile.sharif.coinmarket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "coin";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SHORT_NAME = "short_name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_ONE_HOUR = "one_hour";
    private static final String COLUMN_ONE_DAY = "one_day";
    private static final String COLUMN_SEVEN_DAY = "seven_day";
    private static final String COLUMN_LOGO = "logo";
    private static final String COLUMN_RANK = "rank";

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
                    COLUMN_RANK + " REAL," +
                    COLUMN_LOGO + " TEXT)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    // If you change the database schema, you must increment the database version.


    public DbHelper(Context context) {
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

    public void putCoin(SQLiteDatabase db, Coin coin) {
        if (hasCoin(db, coin)) {
            return;
        }
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_NAME, coin.getName());
        values.put(DbHelper.COLUMN_SHORT_NAME, coin.getShort_name());
        values.put(DbHelper.COLUMN_PRICE, coin.getPrice());
        values.put(DbHelper.COLUMN_ONE_HOUR, coin.getOne_hour_change());
        values.put(DbHelper.COLUMN_ONE_DAY, coin.getOne_day_change());
        values.put(DbHelper.COLUMN_SEVEN_DAY, coin.getSeven_day_change());
        if (coin.getLogo() != null) {
            values.put(DbHelper.COLUMN_LOGO, coin.getLogo());
        }
        values.put(DbHelper.COLUMN_RANK, coin.getRank());
        // Insert the new row, returning the primary key value of the new row
        long row_id = db.insert(DbHelper.TABLE_NAME, null, values);
        coin.setRow_id(row_id);
    }

    public Coin getCoin(Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NAME));
        String short_name = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_SHORT_NAME));
        Double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_PRICE));
        Double one_hour = cursor.getDouble(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_ONE_HOUR));
        Double one_day = cursor.getDouble(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_ONE_DAY));
        Double seven_day = cursor.getDouble(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_SEVEN_DAY));
        String logo = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_LOGO));
        int rank = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_RANK));
        return new Coin(name, short_name, price, one_hour, one_day, seven_day, logo, rank);
    }

    public ArrayList<Coin> getAllCoins(SQLiteDatabase db, DbHelper dbHelper, ProgressBar progres) {
        String sortOrder = COLUMN_RANK + " ASC";
        Cursor cursor = db.query(DbHelper.TABLE_NAME,
                null, null, null, null, null, sortOrder);

        ArrayList<Coin> coins = new ArrayList<>();
        double length = cursor.getCount();
        while (cursor.moveToNext()) {
            Coin coin = dbHelper.getCoin(cursor);
            coins.add(coin);
            progres.setProgress((int)Math.round((cursor.getPosition() / length) * 100));
            try{
                Thread.sleep(10);
            } catch (Exception e){
                Log.i("Error", e.getMessage());
            }

        }
        progres.setProgress(0);
        cursor.close();
        return coins;
    }

    public boolean hasCoin(SQLiteDatabase db, Coin coin) {
        String selection = COLUMN_NAME + " = ?";
        String[] selectionArgs = {coin.getName()};

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs,
                null, null, null);
        boolean has_coin = cursor.getCount() > 0;
        cursor.close();
        return has_coin;
    }
}

