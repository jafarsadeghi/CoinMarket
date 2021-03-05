package mobile.sharif.coinmarket;

import android.view.View;
import android.widget.TextView;

import java.io.Serializable;

public class Coin implements Serializable {
    private long row_id;
    private String name;
    private String short_name;
    private Double price;
    private Double one_hour_change;
    private Double one_day_change;
    private Double seven_day_change;
    private String logo_path;

    public Coin() {
    }

    public Coin(String name, String short_name, Double price) {
        this.name = name;
        this.short_name = short_name;
        this.price = price;
    }

    public Coin(String name, String logo_path) {
        this.name = name;
        this.logo_path = logo_path;
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

    Double getPriceShow() {
        return price ;
    }

    Double getOne_hour_change() {
        return one_hour_change;
    }

    Double getOne_day_change() {
        return one_day_change;
    }

    Double getSeven_day_change() {
        return seven_day_change;
    }

    String getDisplay_name() {
        return short_name.toUpperCase() + " | " + name;
    }

    public String getLogo_path() {
        return logo_path;
    }

    public void setLogo_path(String logo_path) {
        this.logo_path = logo_path;
    }
}
