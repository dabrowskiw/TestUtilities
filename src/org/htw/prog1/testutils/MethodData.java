package org.htw.prog1.testutils;

public class MethodData extends TestData {
    private Object expected;
    private String methodName;
    private Class returnKlass;
    private boolean onlyCheckReturnType;
    private boolean staticMethod;

    public MethodData(Class klass, String methodName, Object expected, boolean makePrimitive, boolean onlyCheckReturnType, boolean staticMethod, Object... arguments) {
        super(klass, makePrimitive, arguments);
        this.expected = expected;
        this.methodName = methodName;
        if(onlyCheckReturnType && expected instanceof Class) {
            this.returnKlass = (Class)expected;
        }
        else {
            this.returnKlass = makePrimitive ? getPrimitiveClass(expected) : expected.getClass();
        }
        this.onlyCheckReturnType = onlyCheckReturnType;
        this.staticMethod = staticMethod;
    }

    public Class getReturnKlass() {
        return returnKlass;
    }

    public Object getExpected() {
        return expected;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean isOnlyCheckReturnType() {
        return onlyCheckReturnType;
    }

    public boolean isStaticMethod() {
        return staticMethod;
    }
}