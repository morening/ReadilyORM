package com.morening.readilyorm;

import com.morening.readilyorm.bean.CustomerBean;
import com.morening.readilyorm.bean.LocationBean;
import com.morening.readilyorm.bean.OrderBean;
import com.morening.readilyorm.exception.DatabaseOperationException;
import com.morening.readilyorm.exception.IllegalParameterException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by morening on 2018/9/12.
 */

abstract public class TestDeleteBase {

    protected ReadilyORM readilyORM = null;
    protected CustomerBean customerBean = null;

    abstract public void setUp() throws DatabaseOperationException;

    @Test
    public void delete_with_empty_customerbean() throws DatabaseOperationException {
        List<CustomerBean> customerBeanList = readilyORM.delete(new CustomerBean());
        Assert.assertNotNull(customerBeanList);
        Assert.assertEquals(1, customerBeanList.size());
        Assert.assertNotNull(customerBeanList.get(0).getId());
        Assert.assertNotNull(customerBeanList.get(0).getName());
        Assert.assertNotNull(customerBeanList.get(0).getAge());
        Assert.assertNotNull(customerBeanList.get(0).getOrders());
        Assert.assertEquals(4, customerBeanList.get(0).getOrders().size());
        for (int k=0; k<4; k++){
            Assert.assertNotNull(customerBeanList.get(0).getOrders().get(k));
            Assert.assertNotNull(customerBeanList.get(0).getOrders().get(k).getId());
            Assert.assertNotNull(customerBeanList.get(0).getOrders().get(k).getPrice());
            Assert.assertNotNull(customerBeanList.get(0).getOrders().get(k).getTime_stamp());
            Assert.assertNotNull(customerBeanList.get(0).getOrders().get(k).getFk_location_id());
            Assert.assertNotNull(customerBeanList.get(0).getOrders().get(k).getRk_customer_id());
            Assert.assertNotNull(customerBeanList.get(0).getOrders().get(k).getLocation());
            Assert.assertNotNull(customerBeanList.get(0).getOrders().get(k).getLocation().getId());
            Assert.assertNotNull(customerBeanList.get(0).getOrders().get(k).getLocation().getCountry());
            Assert.assertNotNull(customerBeanList.get(0).getOrders().get(k).getLocation().getProvince());
            Assert.assertNotNull(customerBeanList.get(0).getOrders().get(k).getLocation().getCity());
            Assert.assertNotNull(customerBeanList.get(0).getOrders().get(k).getLocation().getDistrict());
            Assert.assertNotNull(customerBeanList.get(0).getOrders().get(k).getLocation().getBlock());
        }

        List<CustomerBean> customerBeans = readilyORM.retrieve(new CustomerBean());
        Assert.assertNotNull(customerBeans);
        Assert.assertEquals(0, customerBeans.size());
    }

    @Test
    public void delete_with_full_customerbean() throws DatabaseOperationException {
        List<CustomerBean> customerBeanList = readilyORM.delete(customerBean);
        Assert.assertNotNull(customerBeanList);
        Assert.assertEquals(1, customerBeanList.size());

        List<CustomerBean> customerBeans = readilyORM.retrieve(customerBean);
        Assert.assertNotNull(customerBeans);
        Assert.assertEquals(0, customerBeans.size());
    }

    @Test
    public void delete_with_empty_orderbean() throws DatabaseOperationException {
        List<OrderBean> orderBeanList = readilyORM.delete(new OrderBean());
        Assert.assertNotNull(orderBeanList);
        Assert.assertEquals(4, orderBeanList.size());

        List<OrderBean> orderBeans = readilyORM.retrieve(new OrderBean());
        Assert.assertNotNull(orderBeans);
        Assert.assertEquals(0, orderBeans.size());
    }

    @Test
    public void delete_with_full_orderbean() throws DatabaseOperationException {
        List<OrderBean> orderBeanList = readilyORM.delete(customerBean.getOrders().get(0));
        Assert.assertNotNull(orderBeanList);
        Assert.assertEquals(1, orderBeanList.size());

        List<OrderBean> orderBeans = readilyORM.retrieve(customerBean.getOrders().get(0));
        Assert.assertNotNull(orderBeans);
        Assert.assertEquals(0, orderBeans.size());
    }

    @Test
    public void delete_with_empty_locationbean() throws DatabaseOperationException {
        List<LocationBean> locationBeanList = readilyORM.delete(new LocationBean());
        Assert.assertNotNull(locationBeanList);

        List<LocationBean> locationBeans = readilyORM.retrieve(new LocationBean());
        Assert.assertNotNull(locationBeans);
    }

    @Test
    public void delete_with_full_locationbean() throws DatabaseOperationException {
        List<LocationBean> locationBeanList = readilyORM.delete(customerBean.getOrders().get(0).getLocation());
        Assert.assertNotNull(locationBeanList);
        Assert.assertEquals(1, locationBeanList.size());

        List<LocationBean> locationBeans = readilyORM.retrieve(customerBean.getOrders().get(0).getLocation());
        Assert.assertNotNull(locationBeans);
        Assert.assertEquals(0, locationBeans.size());
    }

    @Test(expected = IllegalParameterException.class)
    public void delete_throws_DatabaseOperationException_for_null_args() throws DatabaseOperationException {
        readilyORM.delete();
    }

    @After
    public void tearDown() throws DatabaseOperationException {
        readilyORM.delete(new CustomerBean());
        readilyORM.delete(new OrderBean());
        readilyORM.delete(new LocationBean());
    }
}
