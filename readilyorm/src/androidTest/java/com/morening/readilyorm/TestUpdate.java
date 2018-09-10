package com.morening.readilyorm;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.morening.readilyorm.ReadilyORM;
import com.morening.readilyorm.bean.CustomerBean;
import com.morening.readilyorm.bean.LocationBean;
import com.morening.readilyorm.bean.OrderBean;
import com.morening.readilyorm.bean.TestingData;
import com.morening.readilyorm.exception.DatabaseOperationException;

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
public class TestUpdate {

    private ReadilyORM readilyORM = null;
    private CustomerBean customerBean = null;

    private static final String TESTING_DATA_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Integer TESTING_DATA_INTEGER = Integer.MAX_VALUE;
    private static final List<OrderBean> TESTING_DATA_ORDERBEAN_LIST = new ArrayList<>();
    static {
        for (int k=0; k<4; k++){
            TESTING_DATA_ORDERBEAN_LIST.add(new OrderBean());
        }
    }
    private static final LocationBean TESTING_DATA_LOCATIONBEAN = new LocationBean();

    @Before
    public void setUp() throws DatabaseOperationException {
        readilyORM = new ReadilyORM.Builder(InstrumentationRegistry.getTargetContext())
                .name("test").version(1).type(CustomerBean.class).build();
        CustomerBean testingData = TestingData.getCustomerBean();
        List<CustomerBean> temp = readilyORM.insert(testingData);
        customerBean = temp.get(0);
    }

    @Test
    public void update_with_empty_customerbean() throws DatabaseOperationException {
        CustomerBean customerBean = new CustomerBean();
        List<CustomerBean> customerBeanList = readilyORM.retrieve(customerBean);
        Assert.assertNotNull(customerBeanList);
        Assert.assertEquals(1, customerBeanList.size());
        CustomerBean customer = customerBeanList.get(0);
        Assert.assertNotNull(customer);
        customerBean.setId(customer.getId());
        List<CustomerBean> customerBeans = readilyORM.update(customerBean);
        Assert.assertNotNull(customerBeans);
        Assert.assertEquals(1, customerBeans.size());
        Assert.assertNotNull(customerBeans.get(0));
        Assert.assertNull(customerBeans.get(0).getName());
        Assert.assertNull(customerBeans.get(0).getAge());
        Assert.assertNull(customerBeans.get(0).getOrders());
    }



    @Test
    public void update_with_customerbean_primitive_field() throws DatabaseOperationException {
        customerBean.setAge(TESTING_DATA_INTEGER);
        customerBean.setName(TESTING_DATA_STRING);
        List<CustomerBean> customerBeanList = readilyORM.update(customerBean);
        Assert.assertNotNull(customerBeanList);
        Assert.assertEquals(1, customerBeanList.size());
        Assert.assertNotNull(customerBeanList.get(0));
        Assert.assertEquals(TESTING_DATA_STRING, customerBeanList.get(0).getName());
        Assert.assertEquals(TESTING_DATA_INTEGER, customerBeanList.get(0).getAge());
        Assert.assertNotNull(customerBeanList.get(0).getOrders());
    }

    @Test
    public void update_with_customerbean_bean_field() throws DatabaseOperationException {
        for (int k=0; k<4; k++){
            TESTING_DATA_ORDERBEAN_LIST.get(k).setId(customerBean.getOrders().get(k).getId());
            TESTING_DATA_ORDERBEAN_LIST.get(k).setRk_customer_id(customerBean.getId());
        }
        customerBean.setOrders(TESTING_DATA_ORDERBEAN_LIST);
        List<CustomerBean> customerBeanList = readilyORM.update(customerBean);
        Assert.assertNotNull(customerBeanList);
        Assert.assertEquals(1, customerBeanList.size());
        Assert.assertNotNull(customerBeanList.get(0));
        Assert.assertEquals(customerBean.getName(), customerBeanList.get(0).getName());
        Assert.assertEquals(customerBean.getAge(), customerBeanList.get(0).getAge());
        Assert.assertEquals(TESTING_DATA_ORDERBEAN_LIST, customerBean.getOrders());
    }

    @Test
    public void update_with_empty_orderbean() throws DatabaseOperationException {
        OrderBean orderBean = new OrderBean();
        List<OrderBean> orderBeanList = readilyORM.retrieve(orderBean);
        Assert.assertNotNull(orderBeanList);
        Assert.assertEquals(4, orderBeanList.size());
        OrderBean order = orderBeanList.get(0);
        Assert.assertNotNull(order);
        orderBean.setId(order.getId());
        List<OrderBean> OrderBeans = readilyORM.update(orderBean);
        Assert.assertNotNull(OrderBeans);
        Assert.assertEquals(1, OrderBeans.size());
        Assert.assertNotNull(OrderBeans.get(0));
        Assert.assertEquals(null, OrderBeans.get(0).getPrice());
        Assert.assertNull(OrderBeans.get(0).getFk_location_id());
        Assert.assertNull(OrderBeans.get(0).getRk_customer_id());
        Assert.assertEquals(null, OrderBeans.get(0).getTime_stamp());
        Assert.assertNull(OrderBeans.get(0).getLocation());
    }

    @Test
    public void update_with_orderbean_primitive_field() throws DatabaseOperationException {
        OrderBean orderBean = customerBean.getOrders().get(0);
        orderBean.setPrice(TESTING_DATA_STRING);
        orderBean.setTime_stamp(TESTING_DATA_STRING);
        orderBean.setFk_location_id(TESTING_DATA_INTEGER);
        orderBean.setRk_customer_id(TESTING_DATA_INTEGER);
        List<OrderBean> orderBeanList = readilyORM.update(orderBean);
        Assert.assertNotNull(orderBeanList);
        Assert.assertEquals(1, orderBeanList.size());
        Assert.assertNotNull(orderBeanList.get(0));
        Assert.assertEquals(TESTING_DATA_STRING, orderBeanList.get(0).getPrice());
        Assert.assertEquals(TESTING_DATA_STRING, orderBeanList.get(0).getTime_stamp());
        Assert.assertEquals(TESTING_DATA_INTEGER, orderBeanList.get(0).getFk_location_id());
        Assert.assertEquals(TESTING_DATA_INTEGER, orderBeanList.get(0).getRk_customer_id());
        Assert.assertNotNull(orderBeanList.get(0).getLocation());
    }

    @Test
    public void update_with_orderbean_bean_field() throws DatabaseOperationException {
        OrderBean orderBean = customerBean.getOrders().get(0);
        Log.d("sunning", "id="+orderBean.getId());
        TESTING_DATA_LOCATIONBEAN.setId(orderBean.getId());
        orderBean.setLocation(TESTING_DATA_LOCATIONBEAN);
        List<OrderBean> orderBeanList = readilyORM.update(orderBean);
        Assert.assertNotNull(orderBeanList);
        Assert.assertEquals(1, orderBeanList.size());
        Assert.assertNotNull(orderBeanList.get(0));
        Assert.assertEquals(orderBean.getPrice(), orderBeanList.get(0).getPrice());
        Assert.assertEquals(orderBean.getTime_stamp(), orderBeanList.get(0).getTime_stamp());
        Assert.assertEquals(orderBean.getFk_location_id(), orderBeanList.get(0).getFk_location_id());
        Assert.assertEquals(orderBean.getRk_customer_id(), orderBeanList.get(0).getRk_customer_id());
        Assert.assertEquals(TESTING_DATA_LOCATIONBEAN, orderBeanList.get(0).getLocation());
    }

    @Test
    public void update_with_empty_locationbean() throws DatabaseOperationException {
        LocationBean locationBean = new LocationBean();
        List<LocationBean> locationBeanList = readilyORM.retrieve(locationBean);
        Assert.assertNotNull(locationBeanList);
        Assert.assertEquals(4, locationBeanList.size());
        LocationBean location = locationBeanList.get(0);
        Assert.assertNotNull(location);
        locationBean.setId(location.getId());
        List<LocationBean> locationBeans = readilyORM.update(locationBean);
        Assert.assertNotNull(locationBeans);
        Assert.assertEquals(1, locationBeans.size());
        Assert.assertNotNull(locationBeans.get(0));
        Assert.assertEquals(null, locationBeans.get(0).getCountry());
        Assert.assertEquals(null, locationBeans.get(0).getProvince());
        Assert.assertEquals(null, locationBeans.get(0).getCity());
        Assert.assertEquals(null, locationBeans.get(0).getDistrict());
        Assert.assertEquals(null, locationBeans.get(0).getBlock());
    }

    @Test
    public void update_with_locationbean_primitive_field() throws DatabaseOperationException {
        LocationBean locationBean = customerBean.getOrders().get(0).getLocation();
        locationBean.setCountry(TESTING_DATA_STRING);
        locationBean.setProvince(TESTING_DATA_STRING);
        locationBean.setCity(TESTING_DATA_STRING);
        locationBean.setDistrict(TESTING_DATA_STRING);
        locationBean.setBlock(TESTING_DATA_STRING);
        List<LocationBean> locationBeanList = readilyORM.update(locationBean);
        Assert.assertNotNull(locationBeanList);
        Assert.assertEquals(1, locationBeanList.size());
        Assert.assertNotNull(locationBeanList.get(0));
        Assert.assertEquals(TESTING_DATA_STRING, locationBeanList.get(0).getCountry());
        Assert.assertEquals(TESTING_DATA_STRING, locationBeanList.get(0).getProvince());
        Assert.assertEquals(TESTING_DATA_STRING, locationBeanList.get(0).getCity());
        Assert.assertEquals(TESTING_DATA_STRING, locationBeanList.get(0).getDistrict());
        Assert.assertEquals(TESTING_DATA_STRING, locationBeanList.get(0).getBlock());
    }

    @Test(expected = DatabaseOperationException.class)
    public void update_throws_DatabaseOperationException_for_null_args() throws DatabaseOperationException {
        readilyORM.update();
    }

    @After
    public void tearDown() throws DatabaseOperationException {
        readilyORM.delete(new CustomerBean());
        readilyORM.delete(new OrderBean());
        readilyORM.delete(new LocationBean());
    }
}
