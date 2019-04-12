import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 泛型出现在Java之前的集合声明，但这么干是不符规范的。
 *
 * 就像下面这个例子，本期望List中只放int型，结果操作失误放进去了很多奇奇怪怪的东西，
 * 虽然编译时可能有模糊的警告，但并不会报错，于是运行时就炸了：
 * java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Integer
 *
 * 不要以为类型转换问题只要注意一下就可以避免，例如BigInteger的实例一个不小心很容易就会被插入到
 * 本来希望只有BigDecimal的集合中。
 *
 * @author LightDance
 * @date 2018/9/16
 */
public class BadGenericsExample {

    //千万别这么干，不符规范。

    private static List intList;

    private static List<Object> anyTypeList;

    private static List<String> stringList;

    /** ?通配符仅允许对null进行操作*/
    private static List<?> questList;


    static void initAnyTypeList(){
        stringList = new ArrayList<>();
        anyTypeList = new ArrayList<>();
        intList = new ArrayList();
        anyTypeList.add(stringList);
        intList.add(stringList);
        questList = new ArrayList<>();
        questList.add(null);
    }

    public static void main(String[] args) {
        intList = new ArrayList();
        intList.add("ss");
        intList.add(20);
        intList.add(BigInteger.valueOf(225553135));
        for (int i = 0; i < intList.size(); i++) {
            System.out.println(((String)intList.get(i)) + 1);
        }
    }
}
