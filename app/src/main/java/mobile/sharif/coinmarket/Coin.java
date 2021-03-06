package mobile.sharif.coinmarket;

import java.io.Serializable;
import java.text.DecimalFormat;

public class Coin implements Serializable {
    private long row_id;
    private String name;
    private String symbol;
    private Double price;
    private Double one_hour_change;
    private Double one_day_change;
    private Double seven_day_change;
    private String logo;
    private int rank;

    public Coin() {
    }

    public Coin(String name, String symbol, Double price, Double one_hour_change, Double one_day_change, Double seven_day_change, String logo, int rank) {
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.one_hour_change = one_hour_change;
        this.one_day_change = one_day_change;
        this.seven_day_change = seven_day_change;
        this.logo = logo;
        this.rank = rank;
    }

    public Coin(String name, String symbol, Double price, Double one_hour_change, Double one_day_change, Double seven_day_change, int rank) {
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.one_hour_change = one_hour_change;
        this.one_day_change = one_day_change;
        this.seven_day_change = seven_day_change;
        this.rank = rank;
    }

    public void setRow_id(long row_id) {
        this.row_id = row_id;
    }

    public String getName() {
        return name;
    }

    String getSymbol() {
        return symbol;
    }

    String getPrice() {
        return price + "$";
    }

    String getPriceShow() {
        return new DecimalFormat("######.####").format(price) + "$";
    }

    Double getOne_hour_change() {
        return one_hour_change;
    }

    String showOne_hour_change() {
        return "1h: " + new DecimalFormat("##.##").format(one_hour_change);
    }

    Double getOne_day_change() {
        return one_day_change;
    }

    String showOne_day_change() {
        return "1D: " + new DecimalFormat("##.##").format(one_day_change);
    }

    Double getSeven_day_change() {
        return seven_day_change;
    }

    String showSeven_hour_change() {
        return "7D: " + new DecimalFormat("##.##").format(seven_day_change);
    }

    String getDisplay_name() {
        return symbol.toUpperCase() + " | " + name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getRank(){
        return rank;
    }
}
