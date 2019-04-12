import java.util.List;
import java.util.Set;

/**
 * Java5以前，需要手动从集合(collection)中转换数据类型，如果不小心转错，运行的时候很可能崩溃或者各种问题。
 * 而java5推出泛型之后，通过泛型，可以告诉编译器，各集合中允许什么类型的对象出现，然后编译器自动完成转换，
 * 并可以在编译时报告是否出现类型错误。这令程序更加安全、清晰，但也要付出一些代价。因此需要我们了解，
 * 如何才能最大化收益、最小化代价。
 *
 * 首先简介一下泛型。泛型，定义声明中具有一个或多个类型参数的类或者接口被称为泛型类或者泛型接口，统称泛型。
 * 例如{@link java.util.List}中，"public interface List<E> extends Collection<E> ",其中的"E"
 * 就是其元素类型。虽然全称List<E>(List of E)，但为了简洁，通常还是会叫它List.
 *
 * 每个泛型类型定义一组参数化类型(parameterized types)，格式为泛型类名(或接口名) + 用尖括号(< >)
 * 括起来的类或接口参数，比如List<String>就表示一个"元素全部为String类型的List"(String 对应之前的E)
 *
 * 每个泛型定义一个原始类型(raw type)，即不带任何实参的泛型。例如List<E>的原始类型是List.
 * 原始类型的行为表现得就像所有类型信息都从类型声明中删除了一样，它的存在主要是为兼容预泛型
 * (pre-generics 也可能意为出现泛型之前的)的代码.
 *
 * 泛型出现之前的Java版本中，一般会这么干{@link BadGenericsExample}，虽然在Java9中仍然不报错，
 * 但完全不符合规范，就像上面那个例子，原则上我们希望错误发现得越早越好，
 * 但编译时可能只有很不明显易被忽视的警告，运行时才会出现报错。因此，对于泛型来说，
 * 应当在类型声明而不是注释中。这样就可以在编译时立即报出错在哪里。
 *
 * 只要你的代码中没有抑制(suppress , 见item27)warning，那么编译器会在检索元素时自动进行类型转换，
 * 并保证其运行时不会出错。
 *
 * 如前所述，使用泛型的原始类型虽然合法，但会失去一切泛型的安全性和可读性优势。
 * 原始类型的出现只是为了兼容之前的代码，并令它们能够与泛型互相操作。这种需求也成为迁移兼容性
 * (migration compatibility)
 *
 * 虽然不应使用原始类型，但可以通过设置泛型后尖括号中的类型参数来允许多种对象混合操作，比如List<Object>.
 * List<Object>与原始类型List的区别在于，原始类型并不属于泛型体系，而List<Object>则是声明了
 * “我可以接收任何类型的实例”。另外，传参时将List<String>类型的实例传给List是没问题的，
 * 但换作List<Object>就不可以，考虑一下这个例子{@link BadExampleSet}，当我们把
 * {@link BadExampleSet#unsafeAdd(List, Object)}中的List形参换成List<Object>之后，会发现报错，
 * 提醒我们类型不匹配。
 *
 * 可能有时候会倾向于对类型未知且无关紧要的集合类使用原始类型，比如这个获取两集合共同元素个数的例子
 * {@link BadExampleSet#numElementsInCommon(Set, Set)}，其安全的替代方法是使用无界泛型类型
 * (<?>,尖括号中放问号)，表示希望使用泛型类型但不关心实际类型是什么，就像这样：
 * {@link BadExampleSet#numElementsInCommon2(Set, Set)}。其与原始类型的区别也在于安全性上，
 * 通配符是有安全保证的，原始类型能放入任意类型的元素，存在类型安全隐患；但通配符则不会，
 * 无法将任意类型的元素(null除外)放进Collection<?>里面，否则编译时就会提示有错。
 * 虽然仅允许对null进行操作很奇怪，但它已经尽力了，这样既可以防止不小心加入各种奇怪类型实例，
 * 又可以明确地告诉你你无法确定取出来实例的类型是什么。如果这样你无法接受，那么可以用泛型方法，
 * 或者限制通配符(见item 30,31)
 *
 * 不过也有个别不得不使用原始类型的例外。
 *      1.在对.class文件的描述中必须使用原始类型。虽然数组(如String[].class)，基本类型(int.class)
 *      跟泛型的原始类型(List.class)都是没问题的，但泛型就白搭。换句话讲，List.class是允许的，
 *      List<?>.class或者List<String>.class就不合法。
 *      2.另一个例外跟instanceof运算符相关。由于泛型在实际运行时会将其泛化信息替换(erase，擦除)掉，
 *      因此除了无界泛型类型通配符"<?>"之外，对其他泛型使用instanceof都是非法的。然而使用无界泛型类型
 *      却跟直接使用原始类型完全没有区别，但由于敲"<?>"比较麻烦，所以就干脆全都免了，
 *      大家统一在使用instanceof的时候用原始类型。下面给个例子：{@link #recommendType(Object)}
 *
 * @author LightDance
 * @date 2018/9/16
 */
public class NoRawTypes {
    private static List<String> stringList;

    static void example(){
        //下面这行代码是错误的，会提示illegal generic type for instanceof
        //System.out.println(stringList instanceof List<String>);

        //这样用原始类型对其操作就不会报错了
        System.out.println(stringList instanceof List);
    }

    /**
     * 此处推荐这么干，由于已经非常确定o一定"is a" Set<?>, 所以可以放心强转，
     * 不会出现warning.
     *
     * @param o 使用instanceof运算符的实例
     */
    static void recommendType(Object o){
        if (o instanceof Set){
            Set<?> s = (Set <?>) o;
            //...
        }
    }

}
