package com.morening.readilyorm.core.bean;


import com.morening.readilyorm.annotations.NotNull;
import com.morening.readilyorm.annotations.ToMany;

import java.util.List;

/**
 * Created by morening on 2018/8/30.
 */
public class CustomerBean {

    private Integer id;

    @NotNull
    private String name;

    private Integer age;

    @ToMany(rk = "rk_customer_id", type = OrderBean.class)
    private List<OrderBean> orders;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public List<OrderBean> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderBean> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return String.format("id=%s, name=%s, age=%s", id, name, age);
    }
}
