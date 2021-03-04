package mobile.sharif.coinmarket;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class APIInterface {

    private static String coin_info_api_key = "60cf371d-fb56-4719-acba-ff1d0094e413";
    ArrayList<Coin> coins = new ArrayList<>();
    private void extractCoinFromResponse(String response) {
        try {
            JSONArray arr = new JSONObject(response).getJSONArray("data");
            for (int i = 0; i < 10; i++)
            {
                JSONObject obj = arr.getJSONObject(i);
                String name = obj.getString("name");
                String short_name = obj.getString("symbol");
                JSONObject changes = obj.getJSONObject("quote").getJSONObject("USD");
                Double price = changes.getDouble("price");
                Double one_hour = changes.getDouble("percent_change_1h");
                Double one_day = changes.getDouble("percent_change_24h");
                Double seven_day = changes.getDouble("percent_change_7d");
                Coin coin = new Coin(name,short_name,price,one_hour,one_day,seven_day);
                coins.add(coin);
            }

        } catch (Exception e){
            Log.i("JSON", e.toString());
        }
    }

    ArrayList<Coin> getCoins() {

        OkHttpClient okHttpClient = new OkHttpClient();
        String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        HttpUrl.Builder urlBuilder = HttpUrl.parse(uri).newBuilder();

        String url = urlBuilder.build().toString();

        final Request request = new Request.Builder().url(url)
                .addHeader("X-CMC_PRO_API_KEY", coin_info_api_key)
                .addHeader("Accept", "application/json")
                .build();
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
                    Log.i("Success", "CHECK point");
                    extractCoinFromResponse(resp);
                    Log.i("Success", "CHECK point 1");
                }
            }
        });
        return coins;
    }
}


