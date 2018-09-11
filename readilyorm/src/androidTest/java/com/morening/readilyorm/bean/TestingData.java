package com.morening.readilyorm.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by morening on 2018/9/6.
 */

public class TestingData {

    public static CustomerBean getCustomerBean(){
        return build();
    }

    private static CustomerBean build(){
        CustomerBean customerBean = new CustomerBean();
        customerBean.setName(randomString(10));
        customerBean.setAge((int)(Math.random()*100));
        List<OrderBean> orders = new ArrayList<>();
        for (int k=0; k<4; k++){
            OrderBean orderBean = new OrderBean();
            orderBean.setTime_stamp(currentTime(k));
            orderBean.setPrice(randomInt(3));

            LocationBean locationBean = new LocationBean();
            locationBean.setCountry(randomString(8));
            locationBean.setProvince(randomString(6));
            locationBean.setCity(randomString(4));
            locationBean.setDistrict(randomString(2));
            locationBean.setBlock(randomString(1));
            orderBean.setLocation(locationBean);

            orders.add(orderBean);
        }
        customerBean.setOrders(orders);

        return customerBean;
    }

    private static String currentTime(long increment){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
        return sdf.format(System.currentTimeMillis()+increment);
    }

    private static String randomInt(int size){
        StringBuilder sb = new StringBuilder();
        for (int k=0; k<size; k++){
            int random = (int)(Math.random()*10);
            sb.append(random);
        }

        return sb.toString();
    }

    private static String randomString(int size){
        StringBuilder sb = new StringBuilder();
        for (int k=0; k<size; k++){
            int random = (int)(Math.random()*26);
            sb.append(String.valueOf((char)('A'+random)));
        }

        return sb.toString();
    }
}

