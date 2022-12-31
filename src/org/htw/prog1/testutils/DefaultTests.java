package org.htw.prog1.testutils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class DefaultTests {

    private StringBuilder errorBuilder;

    public DefaultTests() {
        errorBuilder = new StringBuilder();
    }

    public Object constructorTest(ConstructorData cdata) {
        try {
            Constructor c = TestUtilities.getContructor(cdata.getKlass(), cdata.getArgumentKlasses());
            Object obj = TestUtilities.getInstance(c, errorBuilder, cdata.getArguments());
            return obj;
        } catch (TestException e) {
            fail(e.getTestExceptionMessage());
        } catch(Exception e) {
            fail("Irgendetwas ist schief gelaufen: " + TestUtilities.getTraceString(e, 3));
        }
        return null;
    }

    public Object staticMethodTest(MethodData mdata, int roundDecimals) {
        try {
            Method m = TestUtilities.getMethod(mdata.getKlass(), mdata.getMethodName(), mdata.getReturnKlass(), mdata.isStaticMethod(), mdata.getArgumentKlasses());
            return TestUtilities.assertCallEquals(errorBuilder, roundDecimals, mdata, null, m);
        } catch (TestException e) {
            fail(e.getTestExceptionMessage());
        } catch(Exception e) {
            fail("Irgendetwas ist schief gelaufen: " + TestUtilities.getTraceString(e, 3));
        }
        return null;
    }

    public Object singleMethodTest(ConstructorData cdata, MethodData mdata, int roundDecimals) {
        try {
            Constructor c = TestUtilities.getContructor(cdata.getKlass(), cdata.getArgumentKlasses());
            Method m = TestUtilities.getMethod(mdata.getKlass(), mdata.getMethodName(), mdata.getReturnKlass(), mdata.isStaticMethod(), mdata.getArgumentKlasses());
            Object obj = TestUtilities.getInstance(c, errorBuilder, cdata.getArguments());
            return TestUtilities.assertCallEquals(errorBuilder, roundDecimals, mdata, obj, m);
        } catch (TestException e) {
            fail(e.getTestExceptionMessage());
        } catch(Exception e) {
            fail("Irgendetwas ist schief gelaufen: " + TestUtilities.getTraceString(e, 3));
        }
        return null;
    }

    public Object methodListOnSameObjectTest(ConstructorData cdata, MethodData[] mdatas, int roundDecimals, boolean returnLastMethodResult) {
        try {
            Constructor c = TestUtilities.getContructor(cdata.getKlass(), cdata.getArgumentKlasses());
            Object obj = TestUtilities.getInstance(c, errorBuilder, cdata.getArguments());
            return methodListOnSameObjectTest(obj, mdatas, roundDecimals, returnLastMethodResult);
        } catch (TestException e) {
            fail(e.getTestExceptionMessage());
        } catch(Exception e) {
            fail("Irgendetwas ist schief gelaufen: " + TestUtilities.getTraceString(e, 3));
        }
        return null;
    }

    /**
     * Tests consecutive chains of methods. First, the object is constructed following cdata. Then, all methods from the first MethodData[]
     * in mdata are run on the object. The result of the last method is expected to be an object, and on that object, all methods from the
     * second MethodData[] in mdata are run. And so on and so forth.
     * @param cdata
     * @param mdatas
     * @param roundDecimals
     */
    public void methodChainTest(ConstructorData cdata, MethodData[][] mdatas, int roundDecimals) {
        try {
            Constructor c = TestUtilities.getContructor(cdata.getKlass(), cdata.getArgumentKlasses());
            Object obj = TestUtilities.getInstance(c, errorBuilder, cdata.getArguments());
            for(MethodData[] mdata : mdatas) {
                obj = methodListOnSameObjectTest(obj, mdata, roundDecimals, true);
            }
        } catch (TestException e) {
            fail(e.getTestExceptionMessage());
        } catch(Exception e) {
            fail("Irgendetwas ist schief gelaufen: " + TestUtilities.getTraceString(e, 3));
        }
    }

    public Object methodListOnSameObjectTest(Object obj, MethodData[] mdatas, int roundDecimals, boolean returnLastMethodResult) {
        try {
            Object res = null;
            for(MethodData mdata : mdatas) {
                Method m = TestUtilities.getMethod(mdata.getKlass(), mdata.getMethodName(), mdata.getReturnKlass(), mdata.isStaticMethod(), mdata.getArgumentKlasses());
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