package io.doeasy.retry.expression;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * 
 * @author wangdong1
 * 
 */
public class SimpleExpressionParser {

    private final ExpressionParser expressionParser = new SpelExpressionParser();

    private final Map<String, Expression> expressionMap = new ConcurrentHashMap<String, Expression>();

    private EvaluationContext createContext(Object[] args, Exception exception) {
        return new RetryEvaluationContext(exception);
    }

    public boolean parse(String expression, Method method, Object[] args, Exception exception) {
        EvaluationContext ctx = createContext(args, exception);
        String key = toString(method, expression);
        Expression exp = expressionMap.get(key);

        if (null == exp) {
            exp = expressionParser.parseExpression(expression);
            expressionMap.put(key, exp);
        }

        return exp.getValue(ctx, Boolean.class);
    }

    private String toString(Method method, String expression) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getDeclaringClass().getName());
        sb.append("#");
        sb.append(method.toString());
        sb.append("#");
        sb.append(expression);
        return sb.toString();
    }
}
