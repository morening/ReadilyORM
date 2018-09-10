package com.morening.readilyorm.bean;


import com.morening.readilyorm.annotations.ToOne;

/**
 * Created by morening on 2018/9/1.
 */
public class OrderBean {

    private Integer id;

    private String time_stamp;

    private String price;

    @ToOne(fk = "fk_location_id")
    private LocationBean location;

    private Integer rk_customer_id;

    private Integer fk_location_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public LocationBean getLocation() {
        return location;
    }

    public void setLocation(LocationBean location) {
        this.location = location;
    }

    public Integer getRk_customer_id() {
        return rk_customer_id;
    }

    public void setRk_customer_id(Integer rk_customer_id) {
        this.rk_customer_id = rk_customer_id;
    }

    public Integer getFk_location_id() {
        return fk_location_id;
    }

    public void setFk_location_id(Integer fk_location_id) {
        this.fk_location_id = fk_location_id;
    }
}
