package org.htw.prog1.testutils;

public class TestData {
    private Object[] arguments;
    private Class klass;
    private Class[] argumentKlasses;

    public TestData(Class klass, boolean makePrimitive, Object... arguments) {
        this.klass = klass;
        this.arguments = arguments;
        argumentKlasses = new Class[arguments.length];
        for(int i=0; i<arguments.length; i++) {
            if(makePrimitive) {
                argumentKlasses[i] = getPrimitiveClass(arguments[i]);
            }
            else {
                argumentKlasses[i] = arguments[i].getClass();
            }
        }
    }

    protected Class getPrimitiveClass(Object in) {
        if (Integer.class.equals(in.getClass())) {
            return int.class;
        } else if (Double.class.equals(in.getClass())) {
            return double.class;
        } else if (Float.class.equals(in.getClass())) {
            return float.class;
        } else if (Boolean.class.equals(in.getClass())) {
            return boolean.class;
        } else if (Character.class.equals(in.getClass())) {
            return char.class;
        } else if (Byte.class.equals(in.getClass())) {
            return byte.class;
        }
        return in.getClass();
    }

    public Object[] getArguments() {
        return arguments;
    }

    public Class getKlass() {
        return klass;
    }

    public Class[] getArgumentKlasses() {
        return argumentKlasses;
    }

}