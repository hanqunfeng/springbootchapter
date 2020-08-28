# chapter37----Springboot Junit5 断言


参考 ：Junit5 入门系列 https://blog.csdn.net/ryo1060732496/category_9276134.html

JUnit4 | JUnit5 | 说明
---- | ---- | ----
@Test | @Test | 表示该方法是一个测试方法。JUnit5与JUnit 4的@Test注解不同的是，它没有声明任何属性，因为JUnit Jupiter中的测试扩展是基于它们自己的专用注解来完成的。这样的方法会被继承，除非它们被覆盖
@BeforeClass | @BeforeAll | 表示使用了该注解的方法应该在当前类中所有使用了@Test @RepeatedTest、@ParameterizedTest或者@TestFactory注解的方法之前 执行；
@AfterClass |@AfterAll |表示使用了该注解的方法应该在当前类中所有使用了@Test、@RepeatedTest、@ParameterizedTest或者@TestFactory注解的方法之后执行；
@Before| @BeforeEach |表示使用了该注解的方法应该在当前类中每一个使用了@Test、@RepeatedTest、@ParameterizedTest或者@TestFactory注解的方法之前 执行
@After| @AfterEach |表示使用了该注解的方法应该在当前类中每一个使用了@Test、@RepeatedTest、@ParameterizedTest或者@TestFactory注解的方法之后 执行
@Ignore| @Disabled |用于禁用一个测试类或测试方法
@Category| @Tag| 用于声明过滤测试的tags，该注解可以用在方法或类上；类似于TesgNG的测试组或JUnit 4的分类。
@Parameters| @ParameterizedTest |表示该方法是一个参数化测试
@RunWith |@ExtendWith |@Runwith就是放在测试类名之前，用来确定这个类怎么运行的
@Rule| @ExtendWith |Rule是一组实现了TestRule接口的共享类，提供了验证、监视TestCase和外部资源管理等能力
@ClassRule| @ExtendWith| @ClassRule用于测试类中的静态变量，必须是TestRule接口的实例，且访问修饰符必须为public。
@Rule  | @TempDir | 临时目录,@Rule 声明于TemporaryFolder,@TempDir 声明于Path or File
