package org.hxz.service.frameworks.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings({"unused", "WeakerAccess"})
public class MathUtils {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(MathUtils.class);


    private static final Random     RANDOM          = new Random(System.nanoTime());
    private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================
    private static long seedUniquifier() {
        // L'Ecuyer, "Tables of Linear Congruential Generators of
        // Different Sizes and Good Lattice Structure", 1999
        for (; ; ) {
            long current = SEED_UNIQUIFIER.get();
            long next    = current * 181783497276652981L;
            if (SEED_UNIQUIFIER.compareAndSet(current, next))
                return next;
        }
    }

    /**
     * 获取一个随机种子
     *
     * @return 随机种子
     */
    public static long generateRandomSeed() {
        return seedUniquifier() ^ System.nanoTime();
    }

    /**
     * Interval: <code>[pMin, pMax]</code>
     *
     * @param pMin inclusive!
     * @param pMax inclusive!
     * @return random number
     */
    public static int random(final int pMin, final int pMax) {
        return pMin + RANDOM.nextInt((pMax - pMin) + 1);
    }

    /**
     * Interval: <code>[0, pMax)</code>
     *
     * @param pMax exclusive!
     * @return random number
     */
    public static int random(final int pMax) {
        return RANDOM.nextInt(pMax);
    }

    /**
     * Interval: <code>[pMin, pMax]</code>
     *
     * @param pMin inclusive!
     * @param pMax inclusive!
     * @return random number
     */
    public static long random(final long pMin, final long pMax) {
        long random = Math.abs(RANDOM.nextLong());
        return pMin + random % (pMax - pMin + 1);
    }

    /**
     * Interval: <code>[0, pMax)</code>
     *
     * @param pMax exclusive!
     * @return random number
     */
    public static long random(final long pMax) {
        long random = Math.abs(RANDOM.nextLong());
        return random % pMax;
    }

    /**
     * 是否是位掩码的标准位，即否是2的n次方
     *
     * @param pNumber 数字
     * @return true 是2的n次方
     */
    public static boolean isMaskBit(Long pNumber) {
        return pNumber > 0 && (pNumber & (pNumber - 1)) == 0;
    }

    /**
     * 数字字符串除法，指定精度范围，会四舍五入
     *
     * @param pA     除数
     * @param pB     被除数
     * @param pScale 精度
     * @return 结果BigDecimal
     */
    public static BigDecimal divide(String pA, String pB, int pScale) {
        BigDecimal a = new BigDecimal(pA);
        BigDecimal b = new BigDecimal(pB);

        return a.divide(b, pScale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 数字字符串除法，指定精度范围，会四舍五入
     *
     * @param pA     除数
     * @param pB     被除数
     * @param pScale 精度
     * @return 结果BigDecimal
     */
    public static BigDecimal divide(int pA, int pB, int pScale) {
        BigDecimal a = new BigDecimal(pA);
        BigDecimal b = new BigDecimal(pB);

        return a.divide(b, pScale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 数字字符串除法，指定精度范围，会四舍五入
     *
     * @param pA     除数
     * @param pB     被除数
     * @param pScale 精度
     * @return 结果BigDecimal
     */
    public static BigDecimal divide(long pA, long pB, int pScale) {
        BigDecimal a = new BigDecimal(pA);
        BigDecimal b = new BigDecimal(pB);

        return a.divide(b, pScale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 数字字符串相乘
     *
     * @param pA 数字字符串
     * @param pB 数字字符串
     * @return 结果BigDecimal
     */
    public static BigDecimal multiply(String pA, String pB) {
        BigDecimal a = new BigDecimal(pA);
        BigDecimal b = new BigDecimal(pB);

        return a.multiply(b);
    }

    /**
     * 数字字符串相乘
     *
     * @param pA 数字字符串
     * @param pB 数字字符串
     * @return 结果BigDecimal
     */
    public static BigDecimal multiply(int pA, int pB) {
        BigDecimal a = new BigDecimal(pA);
        BigDecimal b = new BigDecimal(pB);

        return a.multiply(b);
    }

    /**
     * 数字字符串相乘
     *
     * @param pA 数字字符串
     * @param pB 数字字符串
     * @return 结果BigDecimal
     */
    public static BigDecimal multiply(long pA, long pB) {
        BigDecimal a = new BigDecimal(pA);
        BigDecimal b = new BigDecimal(pB);

        return a.multiply(b);
    }

    /**
     * 将字符串解析为数字，指定精度，会四舍五入
     *
     * @param pNumber 数字字符串
     * @param pScale  精度
     * @return 结果BigDecimal
     */
    public static BigDecimal parseDouble(String pNumber, int pScale) {
        BigDecimal a = new BigDecimal(pNumber);
        return a.setScale(pScale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * if pA >= pB
     *
     * @param pA number string
     * @param pB number string
     * @return true: pA >= pB
     */
    public static boolean ge(String pA, String pB) {
        BigDecimal a = new BigDecimal(pA);
        BigDecimal b = new BigDecimal(pB);

        return a.compareTo(b) >= 0;
    }

    public static int multiplyHundred(String pValue) {
        BigDecimal b1 = new BigDecimal(pValue);
        BigDecimal b2 = new BigDecimal(Double.toString(100.0D));
        return b1.multiply(b2).intValue();
    }

    /**
     * 基于调度场算法（Shunting Yard Algorithm）计算表达结果
     * 目前支持加、减、乘、除、取模、取幂，支持括号，仅支持整数运算
     *
     * @param pExpression 表达式
     * @return 结果值
     */
    public static BigDecimal computeExpression(String pExpression) {
        return computeExpression(pExpression, 0, false);
    }
    public static BigDecimal computeExpression(String pExpression, int pScale) {
        return computeExpression(pExpression, pScale, false);
    }
    public static BigDecimal computeExpression(String pExpression, boolean isDebug) {
        return computeExpression(pExpression, 0, isDebug);
    }
    public static BigDecimal computeExpression(String pExpression, int pScale, boolean isDebug) {
        if (!StringUtil.isEmpty(pExpression)) {
            try {
                // trim expression
                pExpression = pExpression.replace(" ", "");
                ArrayDeque<String>    output     = new ArrayDeque<>();
                ArrayDeque<Character> operations = new ArrayDeque<>();
                for (int i = 0; i < pExpression.length(); i++) {
                    char token = pExpression.charAt(i);
                    int  precedence;
                    char associative;
                    switch (token) {
                        /* 当前字符是数字 */
                        case '.':
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            // 计算数字并加入output
                            BigDecimal number = new BigDecimal(0);
                            // 小数部位精度
                            int scale = -1;
                            while (true) {
                                if (token != '.') {
                                    if (scale == -1) {
                                        number = number.multiply(new BigDecimal(10)).add(new BigDecimal(token - '0'));
                                    } else {
                                        //LOG.dd("digit(%d): %s", scale, new BigDecimal(token - '0').divide(new BigDecimal(10).pow(scale), scale, BigDecimal.ROUND_UNNECESSARY));
                                        number = number.add(new BigDecimal(token - '0').divide(new BigDecimal(10).pow(scale), scale, BigDecimal.ROUND_UNNECESSARY));
                                    }
                                }

                                if (i < pExpression.length() - 1) {
                                    char nextToken = pExpression.charAt(i + 1);
                                    if ((nextToken >= '0' && nextToken <= '9') || nextToken == '.') {
                                        token = nextToken;
                                        // 遇到小数点，或者小数精度大于0时，数字已经进入小数部位，精度加一
                                        if (nextToken == '.') {
                                            if (scale > -1) {
                                                throw new RuntimeException("invalid number in expression: " + pExpression);
                                            } else {
                                                scale++;
                                            }
                                        } else {
                                            if (scale > -1) {
                                                scale++;
                                            }
                                        }
                                        i++;
                                    } else {
                                        // 下一个字符非数字，停止计算
                                        break;
                                    }
                                } else {
                                    // 表达式末尾，停止计算
                                    break;
                                }
                            }
                            output.add(String.valueOf(number));
                            break;
                        /* 当前字符是运算符 */
                        case '^':
                            // precedence = 3, associative = 'R'
                        case '*':
                        case '/':
                        case '%':
                            // precedence = 2, associative = 'L'
                        case '+':
                        case '-':
                            // precedence = 2, associative = 'L'
                            // 计算当前读取运算符的级别
                            associative = '^' == token ? 'R' : 'L';
                            precedence = ('^' == token ? 3 : ('+' == token || '-' == token ? 1 : 2));

                            // 如果运算符栈(operations)顶部存在运算符
                            while (operations.peek() != null) {
                                char operator = operations.peek();
                                // 此运算符不是左括号
                                // 并且
                                //    此运算符比当前读取运算符优先级更高
                                //    或者此运算符与当前读取运算符优先级相同，并且当前读取运算符是左结合性
                                // 弹出此运算符并加入到 output 中
                                // 其他情况结束判断
                                if (operator != '(') {
                                    int operatorPrecedence = ('^' == operator ? 3 : ('+' == operator || '-' == operator ? 1 : 2));
                                    if (operatorPrecedence > precedence
                                            || (operatorPrecedence == precedence && associative == 'L')) {
                                        output.add(String.valueOf(operations.pop()));
                                    } else {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                            operations.push(token);
                            break;
                        /* 当前字符是左括号 */
                        case '(':
                            // 输入为左括号
                            operations.push(token);
                            break;
                        /* 当前字符是右括号 */
                        case ')':
                            // 输入为左括号
                            // 如果运算符栈(operations)顶部存在运算符
                            //    此运算符不为左括号('(')时弹出并加入output中
                            boolean closed = false;
                            while (operations.peek() != null) {
                                if (operations.peek() != '(') {
                                    output.add(String.valueOf(operations.pop()));
                                } else {
                                    closed = true;
                                    operations.pop();
                                    break;
                                }
                            }
                            if (!closed) {
                                throw new RuntimeException("unclosed parenthesis in expression: " + pExpression);
                            }
                            break;
                        default:
                            throw new RuntimeException("invalid character '" + token + "' in expression: " + pExpression);
                    }
                }
                /* 表达式遍历完毕，处理运算符栈中的内容 */
                while (operations.peek() != null) {
                    output.add(String.valueOf(operations.pop()));
                }
                if(isDebug){
                    //LOG.debug("Shunting Yard: %s", output.stream().map(String::valueOf).collect(Collectors.joining(" ")));
                }

                ArrayDeque<BigDecimal> numbers = new ArrayDeque<>();
                String                 token;
                while ((token = output.peek()) != null) {
                    switch (token) {
                        case "^":
                        case "*":
                        case "/":
                        case "%":
                        case "+":
                        case "-":
                            BigDecimal b = numbers.pollLast();
                            BigDecimal a = numbers.pollLast();
                            if (a == null || b == null) {
                                throw new RuntimeException("invalid expression (miss numbers): " + pExpression);
                            }
                            switch (token) {
                                case "^":
                                    if(a.compareTo(BigDecimal.ZERO) < 0 && b.compareTo(new BigDecimal(b.longValue())) != 0){
                                        throw new RuntimeException("invalid power factor (" + a + ", " + b + "): " + pExpression);
                                    }
                                    numbers.add(BigDecimal.valueOf(Math.pow(a.doubleValue(), b.doubleValue())));
                                    break;
                                case "*":
                                    numbers.add(a.multiply(b));
                                    break;
                                case "/":
                                    numbers.add(a.divide(b, pScale, BigDecimal.ROUND_HALF_UP));
                                    break;
                                case "%":
                                    BigDecimal[] r = a.divideAndRemainder(b);
                                    numbers.add(r[1]);
                                    break;
                                case "+":
                                    numbers.add(a.add(b));
                                    break;
                                case "-":
                                    numbers.add(a.subtract(b));
                                    break;
                            }
                            break;
                        default:
                            numbers.add(new BigDecimal(token));
                            break;
                    }
                    output.pop();
                    if(isDebug){
                        //LOG.dd("numbers: %s", numbers.stream().map(String::valueOf).collect(Collectors.joining(" ")));
                    }
                }
                if (numbers.size() > 1) {
                    throw new RuntimeException("invalid expression (miss operators): " + pExpression);
                }
                BigDecimal result = numbers.pop();
                if(isDebug){
                    //LOG.dd("%s = %s", pExpression, result);
                }
                return result;
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(computeExpression("3 + 4.24 * 2.2 / ( 5 - 5 ) ^ 2 ^ 3", 12));
//        long start = System.nanoTime();
//        BigDecimal root = BigDecimal.valueOf(Math.pow(4, 9.18));
//        long end = System.nanoTime();
//        System.out.println("4^9.18:" + root + "  cost: " + (end - start)/1000000D);

//        BigDecimal a = new BigDecimal("1.00");
//        System.out.println(a.compareTo(new BigDecimal(a.longValue())));
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
