package mobile.sharif.coinmarket;

import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ProgressBar;

import com.github.mikephil.charting.data.CandleEntry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APIInterface {

    private static String coin_info_api_key = "60cf371d-fb56-4719-acba-ff1d0094e413";
    private SQLiteDatabase db;
    public DbHelper dbHelper;
    private int start = 1;
    private final int step = 10;
    public ArrayList<CandleEntry> candleEntries = new ArrayList<>();

    public APIInterface(SQLiteDatabase db, DbHelper dbHelper) {
        this.db = db;
        this.dbHelper = dbHelper;
        String backup_api_key = "023fe52d-b34f-457d-8bf3-5715987cfc08";
//        coin_info_api_key = backup_api_key;
    }

    public APIInterface() {
    }

    private void extractCoinFromResponse(String response, ProgressBar progressBar) {
        try {
            JSONArray arr = new JSONObject(response).getJSONArray("data");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String name = obj.getString("name");
                String symbol = obj.getString("symbol");
                int rank = obj.getInt("cmc_rank");
                JSONObject changes = obj.getJSONObject("quote").getJSONObject("USD");
                Double price = changes.getDouble("price");
                Double one_hour = changes.getDouble("percent_change_1h");
                Double one_day = changes.getDouble("percent_change_24h");
                Double seven_day = changes.getDouble("percent_change_7d");
                Coin coin = new Coin(name, symbol, price, one_hour, one_day, seven_day, rank);
                retrieveCoinPicFromApi(coin);
                progressBar.setProgress((i + 1) * 10);
            }
            Log.i("end", "end");
            start += step;
        } catch (Exception e) {
            Log.i("JSON", e.toString());
        }
    }

    Request getCustomRequest(String url) {
        return new Request.Builder().url(url)
                .addHeader("X-CMC_PRO_API_KEY", coin_info_api_key)
                .addHeader("Accept", "application/json")
                .build();
    }

    public void retrieveCoinFromApi(ProgressBar progressBar) {

        OkHttpClient okHttpClient = new OkHttpClient();
        String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        String url = HttpUrl.parse(uri).newBuilder().addQueryParameter("start", String.valueOf(start))
                .addQueryParameter("limit", String.valueOf(step)).build().toString();
        Request request = getCustomRequest(url);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("TAG", e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    String resp = response.body().string();
                    extractCoinFromResponse(resp, progressBar);
                }
            }
        });
    }

    private void extractCoinInfoFromResponse(String response, Coin coin) {
        try {
            String logo = new JSONObject(response).getJSONObject("data")
                    .getJSONObject(coin.getSymbol()).getString("logo");
            coin.setLogo(logo);
            dbHelper.putCoin(db, coin);
        } catch (Exception e) {
            Log.i("JSON", "");
            e.printStackTrace();
        }
    }


    private void retrieveCoinPicFromApi(Coin coin) {

        OkHttpClient okHttpClient = new OkHttpClient();
        String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/info";
        String url = HttpUrl.parse(uri).newBuilder()
                .addQueryParameter("symbol", coin.getSymbol()).build().toString();

        Request request = getCustomRequest(url);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("API", e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    String resp = response.body().string();
                    extractCoinInfoFromResponse(resp, coin);
                }
            }
        });
    }

    public void resetStart() {
        this.start = 1;
    }

    public enum Range {
        weekly,
        oneMonth,
    }

    public void getCandles(String symbol, Range range) {
        String CANDLE_ALI_KEY = "04561E3F-671F-415B-B164-B237BB8399B7";

        OkHttpClient okHttpClient = new OkHttpClient();

        String miniUrl;
        switch (range) {
            case weekly:
                miniUrl = "period_id=1DAY".concat("&time_end=".concat(getCurrentDate()).concat("&limit=7"));
                break;
            case oneMonth:
                miniUrl = "period_id=1DAY".concat("&time_end=".concat(getCurrentDate()).concat("&limit=30"));
                break;
            default:
                miniUrl = "";
        }


        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://rest.coinapi.io/v1/ohlcv/".concat(symbol).concat("/USD/history?".concat(miniUrl)))
                .newBuilder();

        String url = urlBuilder.build().toString();

        final Request request = new Request.Builder().url(url)
                .addHeader("X-CoinAPI-Key", CANDLE_ALI_KEY)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { Log.v("TAG", e.getMessage()); }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    extractCandlesFromResponse(response.body().string());
                }
            }
        });

    }

    private void extractCandlesFromResponse(String responseString) {
        try {
            JSONArray arr = new JSONArray(responseString);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                float open = (float)obj.getDouble("price_open");
                float close = (float)obj.getDouble("price_close");
                float high = (float)obj.getDouble("price_high");
                float low = (float)obj.getDouble("price_low");
                CandleEntry entry = new CandleEntry(i , high , low , open , close);
                candleEntries.add(entry);
            }
        } catch (Exception e) {
            Log.i("JSON", e.toString());
        }
    }

    public String getCurrentDate() {
        Date d = new Date();
        CharSequence s  = DateFormat.format("yyyy-MM-dd", d.getTime());
        return s.toString();
    }


}


