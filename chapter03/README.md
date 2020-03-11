# 如何在非springboot项目中获取属性文件对象、@Conditional

@Conditional(<? extends Condition>)基于条件判断初始化对象
不同环境配置@Profile("dev")

## SpringEL表达式：@Value("#{configProperties.username?.toUpperCase()}")

### 数学运算
@value ("#{1+2}")
private int run;
### 浮点数比较运算
@value ("#{beanName.pi == 3.14f}")
private boolean piFlag;
### 字符串比较运算
@Value ("#{beanName.str eq 'Spring Boot'}")
private boolean strFlag;
### 字符串连接
@Value ("#{beanName.str + '连接字符串'}")
private string strApp = null;
### 三元运算
@Value ("#{beanName.d > 1000 ? '大于' : '小于'}")
private string resultDesc = null;