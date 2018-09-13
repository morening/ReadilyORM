package com.morening.readilyorm;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.morening.readilyorm.bean.CustomerBean;
import com.morening.readilyorm.bean.TestingData;
import com.morening.readilyorm.exception.DatabaseOperationException;

import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Created by morening on 2018/9/6.
 */

@RunWith(AndroidJUnit4.class)
public class TestDelete extends TestUpdateBase{

    @Before
    @Override
    public void setup() throws DatabaseOperationException {
        readilyORM = new ReadilyORM.Builder(InstrumentationRegistry.getTargetContext())
                .name("test").version(1).type(CustomerBean.class).build();
        CustomerBean testingData = TestingData.getCustomerBean();
        List<CustomerBean> temp = readilyORM.insert(testingData);
        customerBean = temp.get(0);
    }
}
