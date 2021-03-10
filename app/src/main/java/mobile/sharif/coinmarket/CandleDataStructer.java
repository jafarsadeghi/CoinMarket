package mobile.sharif.coinmarket;

import com.github.mikephil.charting.data.CandleEntry;

import java.util.ArrayList;

public class CandleDataStructer {
    public enum Range{
        WEEKLY , MONTHLY
    }

    public static ArrayList<CandleEntry> candleEntries = new ArrayList<>();
    public static Range rangeOfcandlesToDo;
    public static void addCandle(StringCandle candle) {
        candleEntries.add(makeCandleEntry(candle.high, candle.low, candle.open, candle.close));
    }

    public static CandleEntry makeCandleEntry(String high, String low, String open, String close) {
        return new CandleEntry(candleEntries.size(),
                Float.parseFloat(high),
                Float.parseFloat(low),
                Float.parseFloat(open),
                Float.parseFloat(close)
        );
    }

    public static void update(ArrayList<StringCandle> candles) {
        candleEntries = new ArrayList<CandleEntry>();
        for (StringCandle candle : candles) {
            addCandle(candle);
        }
    }
    public static void setDataForTest(){
        for(int i = 0 ; i< 30; i++){
            candleEntries.add(new CandleEntry(i , i+55 , i+52 , i+53 , i+54));
        }
    }
}
