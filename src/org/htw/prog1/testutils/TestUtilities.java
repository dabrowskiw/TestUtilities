package org.htw.prog1.testutils;

import java.lang.reflect.*;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class TestUtilities {

    public static String getArgumentsValues(Object... arguments) {
        StringBuilder args = new StringBuilder();
        for(int i=0; i<arguments.length; i++) {
//            args.append(arguments[i].toString());
            args.append(objectToString(arguments[i], 3));
            if(i != arguments.length-1) {
                args.append(", ");
            }
        }
        return args.toString();
    }

    public static String getArgumentsList(Class... arguments) {
        StringBuilder args = new StringBuilder();
        for(int i=0; i<arguments.length; i++) {
            args.append(arguments[i].getCanonicalName());
            if(i != arguments.length-1) {
                args.append(", ");
            }
        }
        return args.toString();
    }

    public static Object getCallResult(StringBuilder errorBuilder, Object toCallObj, Method toCall, Object... arguments) throws Exception {
        Object res = toCall.invoke(toCallObj, arguments);
        errorBuilder.append("Nach Aufruf von " + toCall.getName() + "(" + getArgumentsValues(arguments) + "): ");
        return res;
    }

    public static Object roundValue(Object toRound, int roundDecimals) {
        if(toRound.getClass().equals(Double.class) && roundDecimals != 0) {
            toRound = (Double)(Math.round(Math.pow(10, roundDecimals)*(Double)toRound)/Math.pow(10, roundDecimals));
        }
        else if(toRound.getClass().equals(double.class) && roundDecimals != 0) {
            toRound = (double)(Math.round(Math.pow(10, roundDecimals)*(double)toRound)/Math.pow(10, roundDecimals));
        }
        else if(toRound.getClass().equals(Float.class) && roundDecimals != 0) {
            toRound = (Float)(float)(Math.round(Math.pow(10, roundDecimals)*(Float)toRound)/Math.pow(10, roundDecimals));
        }
        else if(toRound.getClass().equals(float.class) && roundDecimals != 0) {
            toRound = (float)(Math.round(Math.pow(10, roundDecimals)*(float)toRound)/Math.pow(10, roundDecimals));
        }
        return toRound;
    }

    public static String objectEquals(Object o1, Object o2, int roundDecimals) {
        if(o1.getClass().isArray() && o2.getClass().isArray()) {
            if(Array.getLength(o1) != Array.getLength(o2)) {
                return " (unterschiedliche Array-Längen)";
            }
            for(int i=0; i<Array.getLength(o1); i++) {
                if(objectEquals(Array.get(o1, i), Array.get(o2, i), roundDecimals) != null) {
                    return " (Unterschied an Position " + i + ": " +
                            objectToString(Array.get(o1, i), roundDecimals) + " != " + objectToString(Array.get(o2, i), roundDecimals) + ")";
                }
            }
            return null;
        }
        o1 = roundValue(o1, roundDecimals);
        o2 = roundValue(o2, roundDecimals);
        return o1.equals(o2)?null:"";
    }

    public static String objectToString(Object o, int roundDecimals) {
        if(o.getClass().isArray()) {
            StringBuilder res = new StringBuilder();
            int length = Array.getLength(o);
            res.append("Array der Länge " + length + " [");
            for(int i=0; i<length; i++) {
                res.append(objectToString(Array.get(o, i), roundDecimals));
                if(i!=length-1) {
                    res.append(", ");
                }
            }
            res.append("]");
            return res.toString();
        }
        o = roundValue(o, roundDecimals);
        return o.toString();
    }

    public static boolean testForNull(StringBuilder errorBuilder, Object result, Object expected, Method toCall, Object... arguments) throws TestException {
        if(result == null && expected == null) {
            return true;
        }
        if(result == null && expected != null) {
            errorBuilder.append("Methoden-Aufruf " + toCall.getName() + "(" + getArgumentsValues(arguments) + ") liefert null, aber erwartet war \"" + expected.toString() + "\"");
            throw new TestException(errorBuilder.toString());
        }
        if(result != null && expected == null) {
            errorBuilder.append("Methoden-Aufruf " + toCall.getName() + "(" + getArgumentsValues(arguments) + ") liefert \"" + result.toString() + "\", aber erwartet war null");
            throw new TestException(errorBuilder.toString());
        }
        return false;
    }

    public static Object assertCallEquals(StringBuilder errorBuilder, int roundDecimals, MethodData mdata, Object toCallObj) throws Exception {
        Method toCall = getMethod(mdata.getKlass(), mdata.getMethodName(), mdata.getReturnKlass(), mdata.isStaticMethod(), mdata.getArgumentKlasses());
        return assertCallEquals(errorBuilder, roundDecimals, mdata, toCallObj, toCall);
    }

    public static Object assertCallEquals(StringBuilder errorBuilder, int roundDecimals, MethodData mdata, Object toCallObj, Method toCall) throws Exception {
        Object expected = mdata.getExpected();
        Object[] arguments = mdata.getArguments();
        if(toCallObj == null && !mdata.isStaticMethod()) {
            errorBuilder.append("Bei Versuch von Methoden-Aufruf " + toCall.getName() + "(" + getArgumentsValues(arguments) +
                    "): Objekt ist null.");
            throw new TestException(errorBuilder.toString());
        }
        errorBuilder.append("Nach Methoden-Aufruf " + toCall.getName() + "(" + getArgumentsValues(arguments) + "): ");
        Object res = toCall.invoke(toCallObj, arguments);
        if(mdata.getReturnKlass().equals(void.class)) {
            return res;
        }
        if(testForNull(errorBuilder, res, expected, toCall, arguments)) {
            return res;
        }
        if(mdata.isOnlyCheckReturnType()) {
            return res;
        }
        String differenceMessage = objectEquals(res, expected, roundDecimals);
        if(differenceMessage != null) {
            errorBuilder.append("liefert \"" +
                    objectToString(res, roundDecimals) + "\", aber erwartet war \"" + objectToString(expected, roundDecimals) + "\"" +
                    (roundDecimals==0?"":" (gerundet auf " + roundDecimals + " Nachkomma-Stellen)") + differenceMessage);
            throw new TestException(errorBuilder.toString());
        }
        return res;
    }

    public static Object getInstance(Constructor c, StringBuilder errorBuilder, Object... arguments) throws TestException {
        Object res = null;
        try {
            res = c.newInstance(arguments);
        } catch(Exception e) {
            throw new TestException("Exception bei Constructor-Aufruf " + c.getName() + "(" + getArgumentsValues(arguments) + "): " + getTraceString(e, 2));
        }
        errorBuilder.append("Nach Constructor-Aufruf " + c.getName() + "(" + getArgumentsValues(arguments) + "): ");
        return res;
    }

    public static Constructor getContructor(Class klass, Class... arguments) throws Exception {
        Constructor res = null;
        try {
            res = klass.getConstructor(arguments);
        } catch(NoSuchMethodException e) {
            throw new TestException("Kein Constructor " + klass.getName() + "(" + getArgumentsList(arguments) + ")" + " in der Klasse " + klass.getName() + " gefunden.");
        }
        return res;
    }

    public static Method getMethod(Class klass, String name, Class returnType, boolean isStatic, Class... arguments) throws Exception {
        Method res = null;
        try {
            res = klass.getMethod(name, arguments);
        }
        catch(Exception e) {
            throw new TestException("Keine public-Methode " + returnType.getCanonicalName() + " " + name + "(" + getArgumentsList(arguments) + ") in der Klasse " + klass.getName() + " definiert.");
        }
        if(!res.getReturnType().equals(returnType)) {
            throw new TestException("Methode " + returnType.getCanonicalName() + " " + name + "(" + getArgumentsList(arguments) + ") in Klasse " + klass.getName() + " sollte " + returnType.getName() + " zurückgeben, gibt aber " + res.getReturnType() + " zurück.");
        }
        if(isStatic && !Modifier.isStatic(res.getModifiers())) {
            throw new TestException("Methode " + returnType.getCanonicalName() + " " + name + "(" + getArgumentsList(arguments) + ") in Klasse " + klass.getName() + " sollte static sein, ist es aber nicht.");
        }
        return res;
    }

    public static String getTraceString(Exception e, int numLines) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        if(e.getCause() == null) {
            pw.print("(Der Fehler liegt wahrscheinlich nicht bei Ihnen, melden Sie sich bitte beim Dozierenden) ");
            e.printStackTrace(pw);
        }
        else {
            e.getCause().printStackTrace(pw);
        }
        String res = sw.toString();
        String[] resLines = res.split("\n");
        StringBuilder resBuilder = new StringBuilder();
        for(int i=0; i<numLines && i<resLines.length; i++) {
            resBuilder.append(resLines[i]);
            resBuilder.append("; ");
        }
        return resBuilder.toString();
    }


    public static String getTraceString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.getCause().printStackTrace(pw);
        return sw.toString();
    }

    public static String joinArray(double[] vals) {
        StringBuilder b = new StringBuilder();
        for(int i=0; i<vals.length; i++) {
            b.append(vals[i] + (i==(vals.length-1)?"":", "));
        }
        return b.toString();
    }

    public static String joinArray(char[] vals) {
        StringBuilder b = new StringBuilder();
        for(int i=0; i<vals.length; i++) {
            b.append("'" + vals[i] + "'" + (i==(vals.length-1)?"":", "));
        }
        return b.toString();
    }

    public static String joinArray(int[] vals) {
        StringBuilder b = new StringBuilder();
        for(int i=0; i<vals.length; i++) {
            b.append(vals[i] + (i==(vals.length-1)?"":", "));
        }
        return b.toString();
    }

    public static String joinArray(Object vals, Field age, Field status) throws Exception {
        StringBuilder b = new StringBuilder();
        int len = Array.getLength(vals);
        for(int i=0; i<len; i++) {
            char st = status.getChar(Array.get(vals, i));
            double ag = age.getDouble(Array.get(vals, i));
            b.append("Patient(" + st + ", " + ag + ")" + (i==(len-1)?"":", "));
        }
        return b.toString();
    }

    public static String joinArray(String[] vals) {
        StringBuilder b = new StringBuilder();
        for(int i=0; i<vals.length; i++) {
            b.append(vals[i] + "\n");
        }
        return b.toString();
    }
}
