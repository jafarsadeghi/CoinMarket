package mobile.sharif.coinmarket;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class APIInterface {

    private static String coin_info_api_key = "60cf371d-fb56-4719-acba-ff1d0094e413";
    private SQLiteDatabase db;
    private FeedReaderDbHelper dbHelper;

    public APIInterface(SQLiteDatabase db, FeedReaderDbHelper dbHelper) {
        this.db = db;
        this.dbHelper = dbHelper;
    }

    private void extractCoinFromResponse(String response) {
        try {
            JSONArray arr = new JSONObject(response).getJSONArray("data");
            for (int i = 0; i < 10; i++) {
                JSONObject obj = arr.getJSONObject(i);
                String name = obj.getString("name");
                String short_name = obj.getString("symbol");
                JSONObject changes = obj.getJSONObject("quote").getJSONObject("USD");
                Double price = changes.getDouble("price");
                Double one_hour = changes.getDouble("percent_change_1h");
                Double one_day = changes.getDouble("percent_change_24h");
                Double seven_day = changes.getDouble("percent_change_7d");
                Coin coin = new Coin(name, short_name, price, one_hour, one_day, seven_day);
                retrieveCoinPicFromApi(coin);
            }
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

    void retrieveCoinFromApi() {

        OkHttpClient okHttpClient = new OkHttpClient();
        String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        String url = HttpUrl.parse(uri).newBuilder().build().toString();

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
                    extractCoinFromResponse(resp);
                }
            }
        });
    }

    private void extractCoinInfoFromResponse(String response, Coin coin) {
        try {
            String logo_path = new JSONObject(response).getJSONObject("data")
                    .getJSONObject(coin.getShort_name()).getString("logo");
            coin.setLogo_path(logo_path);
            dbHelper.putCoin(db, coin);
        } catch (Exception e) {
            Log.i("JSON", e.toString());
        }
    }


    private void retrieveCoinPicFromApi(Coin coin) {

        OkHttpClient okHttpClient = new OkHttpClient();
        String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/info";
        String url = HttpUrl.parse(uri).newBuilder()
                .addQueryParameter("symbol", coin.getShort_name()).build().toString();

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
}


