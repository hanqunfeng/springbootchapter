package com.example;

import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.remoting.RPCHook;

/**
 * acl工具类
 * Created by hanqf on 2025/10/31 10:30.
 */


public class AclUtil {

    private static final String ACL_ACCESS_KEY = "mqadmin";

    private static final String ACL_SECRET_KEY = "1234567";

    public static RPCHook getAclRPCHook() {
        return new AclClientRPCHook(new SessionCredentials(ACL_ACCESS_KEY,ACL_SECRET_KEY));
    }
}
