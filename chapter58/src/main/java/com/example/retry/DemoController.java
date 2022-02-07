package com.example.retry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1></h1>
 * Created by hanqf on 2022/1/21 17:19.
 */

@RestController
@Slf4j
public class DemoController {


    @Autowired
    private RetryService retryService;

    @RequestMapping("/retry/{count}")
    public String show(@PathVariable Integer count){
        try {
            return retryService.call(count);
        } catch (Exception e) {
            log.error("RetryController.show Exception",e);
            return "Hello SUCCESS";
        }
    }
}
