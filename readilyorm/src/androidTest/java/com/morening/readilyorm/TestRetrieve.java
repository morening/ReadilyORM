package com.morening.readilyorm;

import android.support.test.InstrumentationRegistry;

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

import java.util.List;

/**
 * Created by morening on 2018/9/6.
 */

public class TestRetrieve {

    private ReadilyORM readilyORM = null;
    private CustomerBean customerBean = null;

    @Before
    public void setUp() throws DatabaseOperationException {
        readilyORM = new ReadilyORM.Builder(InstrumentationRegistry.getTargetContext())
                .name("test").version(1).type(CustomerBean.class).build();
        CustomerBean testingData = TestingData.getCustomerBean();
        List<CustomerBean> temp = readilyORM.insert(testingData);
        customerBean = temp.get(0);
    }

    @Test
    public void retrieve_with_empty_customerbean() throws DatabaseOperationException {
        List<CustomerBean> customerBeanList = readilyORM.retrieve(new CustomerBean());
        Assert.assertEquals(1, customerBeanList.size());
        CustomerBean customerBean = customerBeanList.get(0);
        Assert.assertNotNull(customerBean);
        Assert.assertNotNull(customerBean.getOrders());
        Assert.assertEquals(4, customerBean.getOrders().size());
        for (int k=0; k<4; k++){
            Assert.assertNotNull(customerBean.getOrders().get(k));
            Assert.assertEquals(customerBean.getOrders().get(k).getRk_customer_id(), customerBean.getId());
            Assert.assertNotNull(customerBean.getOrders().get(k).getLocation());
            Assert.assertEquals(customerBean.getOrders().get(k).getFk_location_id(), customerBean.getOrders().get(k).getLocation().getId());
        }
    }

    @Test
    public void retrieve_with_full_customerbean() throws DatabaseOperationException {
        List<CustomerBean> customerBeanList = readilyORM.retrieve(customerBean);
        Assert.assertEquals(1, customerBeanList.size());
        CustomerBean customerBean = customerBeanList.get(0);
        Assert.assertNotNull(customerBean);
        Assert.assertNotNull(customerBean.getOrders());
        Assert.assertEquals(4, customerBean.getOrders().size());
        for (int k=0; k<4; k++){
            Assert.assertNotNull(customerBean.getOrders().get(k).getLocation());
        }
    }

    @Test
    public void retrieve_with_customerbean_id() throws DatabaseOperationException {
        CustomerBean customer = new CustomerBean();
        customer.setId(customerBean.getId());
        List<CustomerBean> customerBeanList = readilyORM.retrieve(customer);
        Assert.assertEquals(1, customerBeanList.size());
        CustomerBean customerBean = customerBeanList.get(0);
        Assert.assertNotNull(customerBean);
        Assert.assertNotNull(customerBean.getOrders());
        Assert.assertEquals(4, customerBean.getOrders().size());
        for (int k=0; k<4; k++){
            Assert.assertNotNull(customerBean.getOrders().get(k).getLocation());
        }
    }

    @Test
    public void retrieve_with_customerbean_name() throws DatabaseOperationException {
        CustomerBean customer = new CustomerBean();
        customer.setName(customerBean.getName());
        List<CustomerBean> customerBeanList = readilyORM.retrieve(customer);
        Assert.assertEquals(1, customerBeanList.size());
        CustomerBean customerBean = customerBeanList.get(0);
        Assert.assertNotNull(customerBean);
        Assert.assertNotNull(customerBean.getOrders());
        Assert.assertEquals(4, customerBean.getOrders().size());
        for (int k=0; k<4; k++){
            Assert.assertNotNull(customerBean.getOrders().get(k).getLocation());
        }
    }

    @Test
    public void retrieve_with_customerbean_age() throws DatabaseOperationException {
        CustomerBean customer = new CustomerBean();
        customer.setAge(customerBean.getAge());
        List<CustomerBean> customerBeanList = readilyORM.retrieve(customer);
        Assert.assertEquals(1, customerBeanList.size());
        CustomerBean customerBean = customerBeanList.get(0);
        Assert.assertNotNull(customerBean);
        Assert.assertNotNull(customerBean.getOrders());
        Assert.assertEquals(4, customerBean.getOrders().size());
        for (int k=0; k<4; k++){
            Assert.assertNotNull(customerBean.getOrders().get(k).getLocation());
        }
    }

    @Test
    public void retrieve_with_empty_orderbean() throws DatabaseOperationException {
        List<OrderBean> orderBeanList = readilyORM.retrieve(new OrderBean());
        Assert.assertNotNull(orderBeanList);
        Assert.assertEquals(4, orderBeanList.size());
        for (int k=0; k<4; k++){
            Assert.assertNotNull(orderBeanList.get(k));
            Assert.assertNotNull(orderBeanList.get(k).getLocation());
        }
    }

    @Test
    public void retrieve_with_full_orderbean() throws DatabaseOperationException {
        OrderBean orderBean = customerBean.getOrders().get(0);
        List<OrderBean> orderBeanList = readilyORM.retrieve(orderBean);
        Assert.assertNotNull(orderBeanList);
        Assert.assertEquals(1, orderBeanList.size());
        Assert.assertNotNull(orderBeanList.get(0));
        Assert.assertNotNull(orderBeanList.get(0).getLocation());
    }

    @Test
    public void retrieve_with_orderbean_id() throws DatabaseOperationException {
        OrderBean orderBean = customerBean.getOrders().get(0);
        OrderBean order = new OrderBean();
        order.setId(orderBean.getId());
        List<OrderBean> orderBeanList = readilyORM.retrieve(order);
        Assert.assertNotNull(orderBeanList);
        Assert.assertEquals(1, orderBeanList.size());
        Assert.assertNotNull(orderBeanList.get(0));
        Assert.assertNotNull(orderBeanList.get(0).getLocation());
    }

    @Test
    public void retrieve_with_orderbean_fk_location_id() throws DatabaseOperationException {
        OrderBean orderBean = customerBean.getOrders().get(0);
        OrderBean order = new OrderBean();
        order.setFk_location_id(orderBean.getFk_location_id());
        List<OrderBean> orderBeanList = readilyORM.retrieve(order);
        Assert.assertNotNull(orderBeanList);
        Assert.assertEquals(1, orderBeanList.size());
        Assert.assertNotNull(orderBeanList.get(0));
        Assert.assertNotNull(orderBeanList.get(0).getLocation());
    }

    @Test
    public void retrieve_with_orderbean_rk_customer_id() throws DatabaseOperationException {
        OrderBean orderBean = customerBean.getOrders().get(0);
        OrderBean order = new OrderBean();
        order.setRk_customer_id(orderBean.getRk_customer_id());
        List<OrderBean> orderBeanList = readilyORM.retrieve(order);
        Assert.assertNotNull(orderBeanList);
        Assert.assertEquals(4, orderBeanList.size());
        Assert.assertNotNull(orderBeanList.get(0));
        Assert.assertNotNull(orderBeanList.get(0).getLocation());
    }

    @Test
    public void retrieve_with_orderbean_time_stamp() throws DatabaseOperationException {
        OrderBean orderBean = customerBean.getOrders().get(0);
        OrderBean order = new OrderBean();
        order.setTime_stamp(orderBean.getTime_stamp());
        List<OrderBean> orderBeanList = readilyORM.retrieve(order);
        Assert.assertNotNull(orderBeanList);
        Assert.assertEquals(1, orderBeanList.size());
        Assert.assertNotNull(orderBeanList.get(0));
        Assert.assertNotNull(orderBeanList.get(0).getLocation());
    }

    @Test
    public void retrieve_with_orderbean_price() throws DatabaseOperationException {
        OrderBean orderBean = customerBean.getOrders().get(0);
        OrderBean order = new OrderBean();
        order.setPrice(orderBean.getPrice());
        List<OrderBean> orderBeanList = readilyORM.retrieve(order);
        Assert.assertNotNull(orderBeanList);
        Assert.assertEquals(1, orderBeanList.size());
        Assert.assertNotNull(orderBeanList.get(0));
        Assert.assertNotNull(orderBeanList.get(0).getLocation());
    }

    @Test
    public void retrieve_with_empty_locationbean() throws DatabaseOperationException {
        List<LocationBean> locationBeanList = readilyORM.retrieve(new LocationBean());
        Assert.assertNotNull(locationBeanList);
        for (int k=0; k<4; k++){
            Assert.assertNotNull(locationBeanList.get(k));
        }
    }

    @Test
    public void retrieve_with_full_locationbean() throws DatabaseOperationException {
        LocationBean locationBean = customerBean.getOrders().get(0).getLocation();
        List<LocationBean> locationBeanList = readilyORM.retrieve(locationBean);
        Assert.assertNotNull(locationBeanList);
        Assert.assertEquals(1, locationBeanList.size());
        Assert.assertNotNull(locationBeanList.get(0));
    }

    @Test
    public void retrieve_with_locationbean_id() throws DatabaseOperationException {
        LocationBean locationBean = customerBean.getOrders().get(0).getLocation();
        LocationBean location = new LocationBean();
        location.setId(locationBean.getId());
        List<LocationBean> locationBeanList = readilyORM.retrieve(location);
        Assert.assertNotNull(locationBeanList);
        Assert.assertEquals(1, locationBeanList.size());
        Assert.assertNotNull(locationBeanList.get(0));
    }

    @Test
    public void retrieve_with_locationbean_country() throws DatabaseOperationException {
        LocationBean locationBean = customerBean.getOrders().get(0).getLocation();
        LocationBean location = new LocationBean();
        location.setCountry(locationBean.getCountry());
        List<LocationBean> locationBeanList = readilyORM.retrieve(location);
        Assert.assertNotNull(locationBeanList);
        Assert.assertEquals(1, locationBeanList.size());
        Assert.assertNotNull(locationBeanList.get(0));
    }

    @Test
    public void retrieve_with_locationbean_province() throws DatabaseOperationException {
        LocationBean locationBean = customerBean.getOrders().get(0).getLocation();
        LocationBean location = new LocationBean();
        location.setProvince(locationBean.getProvince());
        List<LocationBean> locationBeanList = readilyORM.retrieve(location);
        Assert.assertNotNull(locationBeanList);
        Assert.assertEquals(1, locationBeanList.size());
        Assert.assertNotNull(locationBeanList.get(0));
    }

    @Test
    public void retrieve_with_locationbean_city() throws DatabaseOperationException {
        LocationBean locationBean = customerBean.getOrders().get(0).getLocation();
        LocationBean location = new LocationBean();
        location.setCity(locationBean.getCity());
        List<LocationBean> locationBeanList = readilyORM.retrieve(location);
        Assert.assertNotNull(locationBeanList);
        Assert.assertEquals(1, locationBeanList.size());
        Assert.assertNotNull(locationBeanList.get(0));
    }

    @Test
    public void retrieve_with_locationbean_district() throws DatabaseOperationException {
        LocationBean locationBean = customerBean.getOrders().get(0).getLocation();
        LocationBean location = new LocationBean();
        location.setDistrict(locationBean.getDistrict());
        List<LocationBean> locationBeanList = readilyORM.retrieve(location);
        Assert.assertNotNull(locationBeanList);
        Assert.assertEquals(1, locationBeanList.size());
        Assert.assertNotNull(locationBeanList.get(0));
    }

    @Test
    public void retrieve_with_locationbean_Country() throws DatabaseOperationException {
        LocationBean locationBean = customerBean.getOrders().get(0).getLocation();
        LocationBean location = new LocationBean();
        location.setCountry(locationBean.getCountry());
        List<LocationBean> locationBeanList = readilyORM.retrieve(location);
        Assert.assertNotNull(locationBeanList);
        Assert.assertEquals(1, locationBeanList.size());
        Assert.assertNotNull(locationBeanList.get(0));
    }

    @Test(expected = IllegalParameterException.class)
    public void retrieve_throws_DatabaseOperationException_for_null_args() throws DatabaseOperationException {
        readilyORM.retrieve();
    }

    @After
    public void tearDown() throws DatabaseOperationException {
        readilyORM.delete(new CustomerBean());
        readilyORM.delete(new OrderBean());
        readilyORM.delete(new LocationBean());
    }
}
