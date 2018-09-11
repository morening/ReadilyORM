package com.morening.readilyorm.core;

import android.support.test.runner.AndroidJUnit4;

import com.morening.readilyorm.core.bean.CustomerBean;
import com.morening.readilyorm.exception.IllegalFieldException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by morening on 2018/9/11.
 */

@RunWith(AndroidJUnit4.class)
public class TestDependencyValidator {

    private static final String TAG = TestDependencyValidator.class.getSimpleName();

    private DependencyCache cache = null;

    @Before
    public void setup() {
        cache = new DependencyCache();
        DependencyResolver resolver = new DependencyResolver(cache);
        resolver.resolve(CustomerBean.class);
    }

    @Test(expected = IllegalFieldException.class)
    public void validate() {
        DependencyValidator.validate(cache);
    }
}
