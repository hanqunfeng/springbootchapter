package com.cas.controller;

import com.cas.utils.AuthenticationUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apereo.cas.services.RegexRegisteredService;
import org.apereo.cas.services.ReturnAllAttributeReleasePolicy;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>动态service</p>
 * Created by hanqf on 2020/9/13 20:58.
 */
@Api(tags = {"动态service控制器"})
//@Tag(name = "动态service控制器")
@RestController
@RequestMapping("/service")
public class ServiceController {

    private Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * {
     * "@class" : "org.apereo.cas.services.RegexRegisteredService",
     * "serviceId" : "^(https|http|imaps)://localhost:8081.*",
     * "name" : "client1",
     * "id" : 10000005,
     * "description" : "This service definition authorizes all application urls that support HTTPS and IMAPS protocols.",
     * "evaluationOrder" : 10,
     * "logoutType" : "BACK_CHANNEL",
     * "logoutUrl" : "http://localhost:8081/",
     * "attributeReleasePolicy": {
     * "@class": "org.apereo.cas.services.ReturnAllAttributeReleasePolicy"
     * },
     * "accessStrategy" : {
     * "@class" : "org.apereo.cas.services.DefaultRegisteredServiceAccessStrategy",
     * "enabled" : true,
     * "ssoEnabled" : true
     * }
     * }
     *
     * <p>注册service</p>
     *
     * @param serviceId       服务serviceId
     * @param name            服务名称
     * @param id              服务Id
     * @param evaluationOrder evaluationOrder
     * @param logoutUrl       单点logout地址，为服务首页地址
     * @return java.lang.Object
     * @author hanqf
     * 2020/9/13 21:11
     */
    @ApiOperation(value = "新增",notes = "新增动态service,注意serviceId的格式")
    //@Operation(summary = "新增",description = "新增动态service,注意serviceId的格式")
    @PostMapping("/addClient.do")
    public Object addClient(String serviceId, String name, long id, int evaluationOrder, String logoutUrl) {
        try {
            //serviceId = "^(https|imaps|http)://" + serviceId + ".*";
            RegexRegisteredService service = new RegexRegisteredService();
            ReturnAllAttributeReleasePolicy returnAllAttributeReleasePolicy = new ReturnAllAttributeReleasePolicy();
            service.setServiceId(serviceId);
            service.setId(id);
            service.setAttributeReleasePolicy(returnAllAttributeReleasePolicy);
            service.setName(name);
            //这个是为了单点登出而作用的
            service.setLogoutUrl(logoutUrl);
            service.setEvaluationOrder(evaluationOrder);

            servicesManager.save(service);
            //执行load让他生效
            servicesManager.load();
            ReturnMessage returnMessage = new ReturnMessage();
            returnMessage.setCode(200);
            returnMessage.setMessage("添加成功");
            return returnMessage;
        } catch (Exception e) {
            logger.error("注册service异常", e);
            ReturnMessage returnMessage = new ReturnMessage();
            returnMessage.setCode(500);
            returnMessage.setMessage("添加失败");
            return returnMessage;
        }
    }

    /**
     * 删除service异常
     * 方法执行报错，估计是内部逻辑bug，用try catch处理即可，不影响删除数据
     * 实际上在数据库中删除对应的数据即可，可以改用jdbcTemplate的方式删除数据
     * tableName:regexregisteredservice
     *
     * @param serviceId
     * @return
     */
    @ApiOperation(value = "删除",notes = "删除service,注意serviceId的格式")
    //@Operation(summary = "删除",description = "删除动态service,注意serviceId的格式")
    @PostMapping("/deleteClient.do")
    public Object deleteClient(String serviceId) {
        logger.info("deleteClient,serviceId="+serviceId);
        try {
            boolean has_data = false;

            //RegisteredService service = servicesManager.findServiceBy(serviceId);
            //if (service != null) {
            //    try {
            //        has_data = true;
            //        //servicesManager.delete(service); //执行会抛出异常，估计是cas内部逻辑bug
            //        servicesManager.delete(service.getId());
            //    } catch (Exception e) {
            //        e.printStackTrace();
            //    }
            //
            //}


            //这里注意6.xx和5.xx使用的表名称不一样
            List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from regex_registered_service where service_Id = '" + serviceId + "'");
            if (list != null && list.size() > 0) {
                has_data = true;
                jdbcTemplate.update("delete from regex_registered_service where service_Id = '" + serviceId + "'");
            }


            if (has_data) {
                //执行load生效
                servicesManager.load();

                ReturnMessage returnMessage = new ReturnMessage();
                returnMessage.setCode(200);
                returnMessage.setMessage("删除成功");
                return returnMessage;
            } else {
                ReturnMessage returnMessage = new ReturnMessage();
                returnMessage.setCode(200);
                returnMessage.setMessage("serviceId不存在");
                return returnMessage;
            }
        } catch (Exception e) {
            logger.error("删除service异常", e);
            ReturnMessage returnMessage = new ReturnMessage();
            returnMessage.setCode(500);
            returnMessage.setMessage("删除失败");
            return returnMessage;
        }
    }

    //@PreAuthorize("hasRole('admin')")
    //@PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "测试",notes = "无意义")
    //@Operation(summary = "测试",description = "无意义")
    @GetMapping(value = "/index.do",produces = "application/json; charset=UTF-8")
    public Object index(){
        String username = "none";
        if(AuthenticationUtil.isAuthenticated()){
            username = AuthenticationUtil.getUsername();
        }
        Map<String, String> map = new HashMap<>();
        map.put("username",username);

        logger.info("username=="+username);

        return map;
    }


    @ApiOperation(value = "查询",notes = "查询全部service")
    //@Operation(summary = "查询",description = "查询全部service")
    //默认返回格式是xml，所以这里加上了produces = "application/json; charset=UTF-8"
    @GetMapping(value = "/findClients.do",produces = "application/json; charset=UTF-8")
    public Object findClients() {
        logger.info("findClients");
        //Collection<RegisteredService> allServices = servicesManager.getAllServices();
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select service_Id,name,evaluation_order,logout_url from regex_registered_service");
        return list;
    }

    public class ReturnMessage {

        private Integer code;

        private String message;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
