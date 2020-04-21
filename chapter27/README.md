# springboot 动态刷新外部属性文件 @PropertySource(value = "file:demo.properties")
* 本例通过controller请求进行刷新
```
    @RequestMapping("/refresh")
    public String refreshpro() {
        externalPropertiesRefresh.refresh();
        return "refresh properties success";
    }

    @RequestMapping("/{beanName}/refresh")
    public String refreshProByBeanName(@PathVariable String beanName) {
        externalPropertiesRefresh.refresh(beanName);
        return "refresh properties success for " + beanName;
    }
```
