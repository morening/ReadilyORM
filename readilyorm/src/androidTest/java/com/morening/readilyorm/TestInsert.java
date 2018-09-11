package com.morening.readilyorm;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.morening.readilyorm.bean.CustomerBean;
import com.morening.readilyorm.bean.LocationBean;
import com.morening.readilyorm.bean.OrderBean;
import com.morening.readilyorm.bean.TestingData;
import com.morening.readilyorm.exception.DatabaseOperationException;
import com.morening.readilyorm.exception.IllegalParameterException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by morening on 2018/9/6.
 */

@RunWith(AndroidJUnit4.class)
public class TestInsert {

    private ReadilyORM readilyORM = null;
    private CustomerBean customerBean = null;

    @Before
    public void setUp() {
        readilyORM = new ReadilyORM.Builder(InstrumentationRegistry.getTargetContext())
                .name("test").version(1).type(CustomerBean.class).build();
        customerBean = TestingData.getCustomerBean();
    }

    @Test
    public void insert_with_customerbean() throws DatabaseOperationException {
        List<CustomerBean> customerBeanList = readilyORM.insert(customerBean);
        Assert.assertNotNull(customerBeanList);
        Assert.assertEquals(1, customerBeanList.size());
        Assert.assertNotNull(customerBeanList.get(0).getId());
        Assert.assertNotNull(customerBeanList.get(0).getAge());
        Assert.assertNotNull(customerBeanList.get(0).getName());
        List<OrderBean> orderBeanList = customerBeanList.get(0).getOrders();
        Assert.assertNotNull(orderBeanList);
        Assert.assertEquals(4, orderBeanList.size());
        for (OrderBean orderBean: orderBeanList){
            Assert.assertNotNull(orderBean.getId());
            Assert.assertNotNull(orderBean.getPrice());
            Assert.assertNotNull(orderBean.getTime_stamp());
            Assert.assertEquals(orderBean.getLocation().getId(), orderBean.getFk_location_id());
            Assert.assertEquals(customerBeanList.get(0).getId(), orderBean.getRk_customer_id());
            Assert.assertNotNull(orderBean.getLocation());
            Assert.assertNotNull(orderBean.getLocation().getCountry());
            Assert.assertNotNull(orderBean.getLocation().getProvince());
            Assert.assertNotNull(orderBean.getLocation().getCity());
            Assert.assertNotNull(orderBean.getLocation().getDistrict());
            Assert.assertNotNull(orderBean.getLocation().getBlock());
        }
    }

    @Test
    public void insert_with_single_orderbean() throws DatabaseOperationException {
        OrderBean orderBean = customerBean.getOrders().get(0);
        List<OrderBean> orderBeanList = readilyORM.insert(orderBean);
        Assert.assertNotNull(orderBeanList);
        Assert.assertEquals(1, orderBeanList.size());
        LocationBean locationBean = orderBeanList.get(0).getLocation();
        Assert.assertNotNull(locationBean);
    }

    @Test
    public void insert_with_single_orderbean_without_location() throws DatabaseOperationException {
        OrderBean orderBean = customerBean.getOrders().get(0);
        orderBean.setLocation(null);
        List<OrderBean> orderBeanList = readilyORM.insert(orderBean);
        Assert.assertNotNull(orderBeanList);
        Assert.assertEquals(1, orderBeanList.size());
        LocationBean locationBean = orderBeanList.get(0).getLocation();
        Assert.assertNull(locationBean);
    }

    @Test
    public void insert_with_multi_orderbeans() throws DatabaseOperationException {
        OrderBean orderBean1 = customerBean.getOrders().get(0);
        OrderBean orderBean2 = customerBean.getOrders().get(1);
        List<OrderBean> orderBeanList = readilyORM.insert(orderBean1, orderBean2);
        Assert.assertNotNull(orderBeanList);
        Assert.assertEquals(2, orderBeanList.size());
    }

    @Test
    public void insert_with_list_orderbeans() throws DatabaseOperationException {
        List<OrderBean> orderBeanList = readilyORM.insert(customerBean.getOrders());
        Assert.assertNotNull(orderBeanList);
        Assert.assertEquals(4, orderBeanList.size());
    }

    @Test
    public void insert_with_single_locationbean() throws DatabaseOperationException {
        LocationBean locationBean = customerBean.getOrders().get(0).getLocation();
        List<LocationBean> locationBeanList = readilyORM.insert(locationBean);
        Assert.assertNotNull(locationBeanList);
        Assert.assertEquals(1, locationBeanList.size());
    }

    @Test
    public void insert_with_multi_locationbeans() throws DatabaseOperationException {
        LocationBean locationBean1 = customerBean.getOrders().get(0).getLocation();
        LocationBean locationBean2 = customerBean.getOrders().get(1).getLocation();
        List<LocationBean> locationBeanList = readilyORM.insert(locationBean1, locationBean2);
        Assert.assertNotNull(locationBeanList);
        Assert.assertEquals(2, locationBeanList.size());
    }

    @Test
    public void insert_with_list_locationbean() throws DatabaseOperationException {
        List<LocationBean> locationBeans = new ArrayList<>();
        for (int k=0; k<4; k++){
            locationBeans.add(customerBean.getOrders().get(k).getLocation());
        }
        List<LocationBean> locationBeanList = readilyORM.insert(locationBeans);
        Assert.assertNotNull(locationBeanList);
        Assert.assertEquals(4, locationBeanList.size());
    }

    @Test
    public void insert_with_customer_null_field() throws DatabaseOperationException {
        CustomerBean customerBean = new CustomerBean();
        customerBean.setName("morening");
        List<CustomerBean> customerBeans = readilyORM.insert(customerBean);
        Assert.assertNotNull(customerBeans);
        Assert.assertEquals(1, customerBeans.size());
        Assert.assertNotNull(customerBeans.get(0).getId());
        Assert.assertEquals(Integer.valueOf(0), customerBeans.get(0).getAge());
        Assert.assertNotNull(customerBeans.get(0).getName());
        Assert.assertNull(customerBeans.get(0).getOrders());
    }

    @Test(expected = IllegalParameterException.class)
    public void insert_throws_DatabaseOperationException_for_null_args() throws DatabaseOperationException {
        readilyORM.insert();
    }

    @Test(expected = DatabaseOperationException.class)
    public void insert_throws_DatabaseOperationException_for_customerbean_null_name() throws DatabaseOperationException {
        customerBean.setName(null);
        readilyORM.insert(customerBean);
    }

    @After
    public void tearDown() throws DatabaseOperationException {
        readilyORM.delete(new CustomerBean());
        readilyORM.delete(new OrderBean());
        readilyORM.delete(new LocationBean());
    }
}
