package com.morening.readilyorm;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.morening.readilyorm.bean.CustomerBean;
import com.morening.readilyorm.bean.TestingData;
import com.morening.readilyorm.core.StatementOperator;

import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * Created by morening on 2018/9/12.
 */

@RunWith(AndroidJUnit4.class)
public class TestInsertWithStatement extends TestInsertBase{

    @Before
    @Override
    public void setUp() {
        readilyORM = new ReadilyORM.Builder(InstrumentationRegistry.getTargetContext())
                .name("test").version(1).type(CustomerBean.class).operator(new StatementOperator()).build();
        customerBean = TestingData.getCustomerBean();
    }
}
