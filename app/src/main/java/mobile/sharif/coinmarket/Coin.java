package mobile.sharif.coinmarket;

import java.io.Serializable;
import java.text.DecimalFormat;

public class Coin implements Serializable {
    private long row_id;
    private String name;
    private String short_name;
    private Double price;
    private Double one_hour_change;
    private Double one_day_change;
    private Double seven_day_change;
    private String logo;

    public Coin() {
    }


    public Coin(String name, String short_name, Double price, Double one_hour_change, Double one_day_change, Double seven_day_change, String logo) {
        this.name = name;
        this.short_name = short_name;
        this.price = price;
        this.one_hour_change = one_hour_change;
        this.one_day_change = one_day_change;
        this.seven_day_change = seven_day_change;
        this.logo = logo;
    }

    public Coin(String name, String short_name, Double price, Double one_hour_change, Double one_day_change, Double seven_day_change) {
        this.name = name;
        this.short_name = short_name;
        this.price = price;
        this.one_hour_change = one_hour_change;
        this.one_day_change = one_day_change;
        this.seven_day_change = seven_day_change;
    }

    public long getRow_id() {
        return row_id;
    }

    public void setRow_id(long row_id) {
        this.row_id = row_id;
    }

    String getName() {
        return name;
    }

    String getShort_name() {
        return short_name;
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
        return short_name.toUpperCase() + " | " + name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
