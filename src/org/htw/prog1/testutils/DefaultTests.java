package org.htw.prog1.testutils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class DefaultTests {

    public static Object constructorTest(ConstructorData cdata) {
        try {
            Constructor c = TestUtilities.getContructor(cdata.getKlass(), cdata.getArgumentKlasses());
            StringBuilder errorBuilder = new StringBuilder();
            Object obj = TestUtilities.getInstance(c, errorBuilder, cdata.getArguments());
            return obj;
        } catch (TestException e) {
            fail(e.getTestExceptionMessage());
        } catch(Exception e) {
            fail("Irgendetwas ist schief gelaufen: " + TestUtilities.getTraceString(e, 3));
        }
        return null;
    }

    public static Object staticMethodTest(MethodData mdata, int roundDecimals) {
        try {
            Method m = TestUtilities.getMethod(mdata.getKlass(), mdata.getMethodName(), mdata.getReturnKlass(), mdata.getArgumentKlasses());
            StringBuilder errorBuilder = new StringBuilder();
            return TestUtilities.assertCallEquals(errorBuilder, roundDecimals, mdata, null, m);
        } catch (TestException e) {
            fail(e.getTestExceptionMessage());
        } catch(Exception e) {
            fail("Irgendetwas ist schief gelaufen: " + TestUtilities.getTraceString(e, 3));
        }
        return null;
    }

    public static Object singleMethodTest(ConstructorData cdata, MethodData mdata, int roundDecimals) {
        try {
            Constructor c = TestUtilities.getContructor(cdata.getKlass(), cdata.getArgumentKlasses());
            Method m = TestUtilities.getMethod(mdata.getKlass(), mdata.getMethodName(), mdata.getReturnKlass(), mdata.getArgumentKlasses());
            StringBuilder errorBuilder = new StringBuilder();
            Object obj = TestUtilities.getInstance(c, errorBuilder, cdata.getArguments());
            return TestUtilities.assertCallEquals(errorBuilder, roundDecimals, mdata, obj, m);
        } catch (TestException e) {
            fail(e.getTestExceptionMessage());
        } catch(Exception e) {
            fail("Irgendetwas ist schief gelaufen: " + TestUtilities.getTraceString(e, 3));
        }
        return null;
    }

    public static Object methodListOnSameObjectTest(Object obj, MethodData[] mdatas, int roundDecimals, boolean returnLastMethodResult) {
        return methodListOnSameObjectTest(obj, mdatas, roundDecimals, returnLastMethodResult, new StringBuilder());
    }

    public static Object methodListOnSameObjectTest(ConstructorData cdata, MethodData[] mdatas, int roundDecimals, boolean returnLastMethodResult) {
        try {
            Constructor c = TestUtilities.getContructor(cdata.getKlass(), cdata.getArgumentKlasses());
            StringBuilder errorBuilder = new StringBuilder();
            Object obj = TestUtilities.getInstance(c, errorBuilder, cdata.getArguments());
            return methodListOnSameObjectTest(obj, mdatas, roundDecimals, returnLastMethodResult, errorBuilder);
        } catch (TestException e) {
            fail(e.getTestExceptionMessage());
        } catch(Exception e) {
            fail("Irgendetwas ist schief gelaufen: " + TestUtilities.getTraceString(e, 3));
        }
        return null;
    }

    private static Object methodListOnSameObjectTest(Object obj, MethodData[] mdatas, int roundDecimals, boolean returnLastMethodResult, StringBuilder errorBuilder) {
        try {
            Object res = null;
            for(MethodData mdata : mdatas) {
                Method m = TestUtilities.getMethod(mdata.getKlass(), mdata.getMethodName(), mdata.getReturnKlass(), mdata.getArgumentKlasses());
                res = TestUtilities.assertCallEquals(errorBuilder, roundDecimals, mdata, obj, m);
            }
            if(returnLastMethodResult) {
                return res;
            }
            return obj;
        } catch (TestException e) {
            fail(e.getTestExceptionMessage());
        } catch(Exception e) {
            fail("Irgendetwas ist schief gelaufen: " + TestUtilities.getTraceString(e, 3));
        }
        return null;

    }
}