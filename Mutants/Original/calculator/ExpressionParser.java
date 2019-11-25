package calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ExpressionParser {
	public static List<String> transformEnd(String expre) {
        List<String> sb = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        expre = expre.replaceAll("(\\D)", "o$1o");
        String[] esp = expre.trim().split("o");

        for (int i = 0; i < esp.length; i++) {
            String s = esp[i].trim();
            if (isNamber(s)) {
                sb.add(s);
            } else if (!s.isEmpty()) {

                if (s.charAt(0) == ')') {
                    while (stack.peek().charAt(0) != '(') {
                        sb.add(stack.pop());
                    }
                    stack.pop();
                } else {
                    if (!stack.isEmpty() && !isMaxExp(s.charAt(0), stack.peek().charAt(0))) {
                        while (!stack.isEmpty() && !isMaxExp(s.charAt(0), stack.peek().charAt(0))) {
                            sb.add(stack.pop());
                        }
                        stack.push(s);
                    } else {
                        stack.push(s);
                    }
                }
            }
        }
        while (!stack.isEmpty()) {
            sb.add(stack.pop());
        }
        return sb;
    }

    // if a number
    public static boolean isNamber(String str) {
        try {
            Double.parseDouble(str);

        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }

    public static boolean isMaxExp(char exp1, char exp2) {
        if (exp1 == '(')
            return true;
        if (exp2 == ')')
            return true;
        if (transExp(exp1) > transExp(exp2))
            return true;
        return false;
    }

    public static int transExp(char exp) {
        int re = 0;
        switch (exp) {
            case '*':
            case '/':
                re = 2;
                break;
            case '+':
            case '-':
                re = 1;
                break;
        }
        return re;
    }
}
