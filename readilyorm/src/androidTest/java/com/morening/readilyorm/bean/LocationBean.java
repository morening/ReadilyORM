package com.morening.readilyorm.bean;

/**
 * Created by morening on 2018/9/1.
 */
public class LocationBean {

    private Integer id;

    private String country;

    private String province;

    private String city;

    private String district;

    private String block;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    @Override
    public String toString() {
        return String.format("country=%s, province=%s, city=%s, district=%s, block=%s", country, province, city, district, block);
    }
}
