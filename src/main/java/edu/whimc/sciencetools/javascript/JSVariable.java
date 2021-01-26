package edu.whimc.sciencetools.javascript;

import java.util.HashMap;
import java.util.Map;

import edu.whimc.sciencetools.javascript.JSEngine.Placeholder;

public class JSVariable {

    public static final double DEFAULT_VALUE = 1.0;

    public final Placeholder placeholder;
    public final double value;

    public JSVariable(Placeholder ph, double val) {
        this.placeholder = ph;
        this.value = val;
    }

    public class ConversionVariable extends JSVariable {
        public ConversionVariable(double toConvert) {
            super(JSEngine.Placeholder.VALUE, toConvert);
        }
    }

    private String substitute(JSExpression expr) {
        return JSVariable.substitute(expr.getExpression(), this);
    }

    public static String substituteVariables(JSExpression expr, JSVariable... vars) {
        Map<Placeholder, Double> vals = new HashMap<>();
        for (JSVariable var : vars) {
            vals.put(var.placeholder, var.value);
        }

        String res = expr.getExpression();
        for (Placeholder ph : Placeholder.values()) {
            res = JSVariable.substitute(res, ph, vals.getOrDefault(ph, DEFAULT_VALUE));
        }

        return res;
    }

    public static String substitute(JSExpression expr, JSVariable var) {
        return JSVariable.substitute(expr, var.placeholder, var.value);
    }

    private static String substitute(JSExpression expr, Placeholder ph, double val) {
        return expr.getExpression().replace(ph.getKey(), String.valueOf(val));
    }

}
