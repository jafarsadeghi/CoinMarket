package mobile.sharif.coinmarket;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ProgressBar;

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

    private final static String coin_info_api_key = "60cf371d-fb56-4719-acba-ff1d0094e413";
    private SQLiteDatabase db;
    private DbHelper dbHelper;
    private int start = 1;
    private int step = 10;

    public APIInterface(SQLiteDatabase db, DbHelper dbHelper) {
        this.db = db;
        this.dbHelper = dbHelper;
        String backup_api_key = "023fe52d-b34f-457d-8bf3-5715987cfc08";
//        coin_info_api_key = backup_api_key;
    }

    private void extractCoinFromResponse(String response, ProgressBar progressBar) {
        try {
            JSONArray arr = new JSONObject(response).getJSONArray("data");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String name = obj.getString("name");
                String short_name = obj.getString("symbol");
                int rank = obj.getInt("cmc_rank");
                JSONObject changes = obj.getJSONObject("quote").getJSONObject("USD");
                Double price = changes.getDouble("price");
                Double one_hour = changes.getDouble("percent_change_1h");
                Double one_day = changes.getDouble("percent_change_24h");
                Double seven_day = changes.getDouble("percent_change_7d");
                Coin coin = new Coin(name, short_name, price, one_hour, one_day, seven_day, rank);
                retrieveCoinPicFromApi(coin);
                progressBar.setProgress((i + 1) * 10);
            }
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

    void retrieveCoinFromApi(ProgressBar progressBar) {

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
                    .getJSONObject(coin.getShort_name()).getString("logo");
            coin.setLogo(logo);
            dbHelper.putCoin(db, coin);
        } catch (Exception e) {
            Log.i("JSOND", "");
            e.printStackTrace();
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

    public void resetStart() {
        this.start = 1;
    }
}


