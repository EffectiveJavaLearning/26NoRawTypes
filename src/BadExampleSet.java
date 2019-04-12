import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author LightDance
 * @date 2018/9/16
 */
public class BadExampleSet {

    // Fails at runtime - unsafeAdd method uses a raw type (List)!

    public static void example1() {
        List<String> strings = new ArrayList<>();
        unsafeAdd(strings, Integer.valueOf(42));
        // Has compiler-generated cast
        String s = strings.get(0);
    }

    //若将List形参换成List<Object>之后，会发现上面调用的那一句报错类型不匹配

    static void unsafeAdd(List list, Object o) {
        list.add(o);
    }




    // 集合中元素类型未知时使用原始类型的例子，不好。

    static int numElementsInCommon(Set s1, Set s2) {
        int result = 0;
        for (Object o1 : s1) {
            if (s2.contains(o1)) {
                result++;
            }
        }
        return result;
    }

    //推荐像这样使用无界通配符

    static int numElementsInCommon2(Set<?> s1, Set<?> s2) {
        int result = 0;
        for (Object o1 : s1) {
            if (s2.contains(o1)) {
                result++;
            }
        }
        return result;
    }

    public static void main(String[] args) {
    }

}
