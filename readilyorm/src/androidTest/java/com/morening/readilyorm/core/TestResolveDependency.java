package com.morening.readilyorm.core;

import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.util.Log;

import com.morening.readilyorm.bean.CustomerBean;
import com.morening.readilyorm.bean.LocationBean;
import com.morening.readilyorm.bean.OrderBean;
import com.morening.readilyorm.util.PrimitiveTypes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TestResolveDependency {

    private static final String TAG = TestResolveDependency.class.getSimpleName();

    private DependencyCache cache = null;

    @Before
    public void setUp() throws ClassNotFoundException {
        cache = new DependencyCache();
        DependencyResolver resolver = new DependencyResolver(cache);
        resolver.resolve(CustomerBean.class);
    }

    @Test
    public void resolveCustomerBean(){
        Dependency customerBeanDep = cache.getDependency(CustomerBean.class);
        assertEquals(customerBeanDep.type, CustomerBean.class);
        assertEquals(customerBeanDep.columns.size(), 3);
        for (Dependency.Column column: customerBeanDep.columns){
            if (TextUtils.equals(column.nameInDb, "name")){
                assertEquals(column.typeInDb, PrimitiveTypes.COLUMN_TYPE_TEXT);
                assertEquals(column.fieldType, String.class);
            } else if (TextUtils.equals(column.nameInDb, "age")){
                assertEquals(column.typeInDb, PrimitiveTypes.COLUMN_TYPE_INTEGER);
                assertEquals(column.fieldType, Integer.class);
            } else if (TextUtils.equals(column.nameInDb, "id")){
                assertEquals(column.typeInDb, PrimitiveTypes.COLUMN_TYPE_INTEGER);
                assertEquals(column.fieldType, Integer.class);
            } else {
                Log.d(TAG, column.nameInDb +" "+column.typeInDb+" "+column.fieldType);
            }
        }
        assertEquals(customerBeanDep.preConditions, null);
        assertEquals(customerBeanDep.postConditions.size(), 1);
        assertEquals(customerBeanDep.postConditions.get(0).genericType, OrderBean.class);
        assertEquals(customerBeanDep.postConditions.get(0).fieldName, "orders");
        assertEquals(customerBeanDep.postConditions.get(0).nameInDb, "rk_customer_id");
    }

    @Test
    public void resolveOrderBean(){
        Dependency OrderBeanDep = cache.getDependency(OrderBean.class);
        assertEquals(OrderBeanDep.type, OrderBean.class);
        assertEquals(OrderBeanDep.columns.size(), 5);
        for (Dependency.Column column: OrderBeanDep.columns){
            if (TextUtils.equals(column.nameInDb, "time_stamp")){
                assertEquals(column.typeInDb, PrimitiveTypes.COLUMN_TYPE_TEXT);
                assertEquals(column.fieldType, String.class);
            } else if (TextUtils.equals(column.nameInDb, "price")){
                assertEquals(column.typeInDb, PrimitiveTypes.COLUMN_TYPE_TEXT);
                assertEquals(column.fieldType, String.class);
            } else if (TextUtils.equals(column.nameInDb, "location")){
                assertEquals(column.typeInDb, PrimitiveTypes.COLUMN_TYPE_INTEGER);
                assertEquals(column.fieldType, LocationBean.class);
            } else if (TextUtils.equals(column.nameInDb, "rk_customer_id")){
                assertEquals(column.typeInDb, PrimitiveTypes.COLUMN_TYPE_INTEGER);
                assertEquals(column.fieldType, Integer.class);
            } else if (TextUtils.equals(column.nameInDb, "fk_location_id")){
                assertEquals(column.typeInDb, PrimitiveTypes.COLUMN_TYPE_INTEGER);
                assertEquals(column.fieldType, Integer.class);
            } else if (TextUtils.equals(column.nameInDb, "id")){
                assertEquals(column.typeInDb, PrimitiveTypes.COLUMN_TYPE_INTEGER);
                assertEquals(column.fieldType, Integer.class);
            } else {
                Log.d(TAG, column.nameInDb +" "+column.typeInDb+" "+column.fieldType);
            }
        }
        assertEquals(OrderBeanDep.preConditions.size(), 1);
        assertEquals(OrderBeanDep.preConditions.get(0).genericType, LocationBean.class);
        assertEquals(OrderBeanDep.preConditions.get(0).fieldName, "location");
        assertEquals(OrderBeanDep.preConditions.get(0).nameInDb, "fk_location_id");
        assertEquals(OrderBeanDep.postConditions, null);
    }

    @Test
    public void resolveLocationBean(){
        Dependency locationBeanDep = cache.getDependency(LocationBean.class);
        assertEquals(locationBeanDep.type, LocationBean.class);
        assertEquals(locationBeanDep.columns.size(), 6);
        for (Dependency.Column column: locationBeanDep.columns){
            if (TextUtils.equals(column.nameInDb, "country")){
                assertEquals(column.typeInDb, PrimitiveTypes.COLUMN_TYPE_TEXT);
                assertEquals(column.fieldType, String.class);
            } else if (TextUtils.equals(column.nameInDb, "province")){
                assertEquals(column.typeInDb, PrimitiveTypes.COLUMN_TYPE_TEXT);
                assertEquals(column.fieldType, String.class);
            } else if (TextUtils.equals(column.nameInDb, "city")){
                assertEquals(column.typeInDb, PrimitiveTypes.COLUMN_TYPE_TEXT);
                assertEquals(column.fieldType, String.class);
            } else if (TextUtils.equals(column.nameInDb, "district")){
                assertEquals(column.typeInDb, PrimitiveTypes.COLUMN_TYPE_TEXT);
                assertEquals(column.fieldType, String.class);
            } else if (TextUtils.equals(column.nameInDb, "block")){
                assertEquals(column.typeInDb, PrimitiveTypes.COLUMN_TYPE_TEXT);
                assertEquals(column.fieldType, String.class);
            } else if (TextUtils.equals(column.nameInDb, "id")){
                assertEquals(column.typeInDb, PrimitiveTypes.COLUMN_TYPE_INTEGER);
                assertEquals(column.fieldType, Integer.class);
            } else {
                Log.d(TAG, column.nameInDb +" "+column.typeInDb+" "+column.fieldType);
            }
        }
        assertEquals(locationBeanDep.preConditions, null);
        assertEquals(locationBeanDep.postConditions, null);
    }
}
