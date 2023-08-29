package com.example.r2dbc;

import io.r2dbc.proxy.core.QueryExecutionInfo;
import io.r2dbc.proxy.listener.ProxyMethodExecutionListener;
import io.r2dbc.proxy.support.QueryExecutionInfoFormatter;
import lombok.extern.slf4j.Slf4j;

/**
 * <h1>基于 r2dbc-proxy 的 sql日志打印</h1>
 * Created by hanqf on 2023/8/29 16:39.
 */


@Slf4j
public class LogExecutionListener implements ProxyMethodExecutionListener {

    private final QueryExecutionInfoFormatter queryFormatter = new QueryExecutionInfoFormatter()
            .showTime()
            .showQuery()
            .newLine()
            .showBindings()
            .newLine();


    @Override
    public void afterExecuteOnStatement(QueryExecutionInfo queryExecutionInfo) {
        afterExecuteQuery(queryExecutionInfo);
    }

    private void afterExecuteQuery(QueryExecutionInfo queryExecutionInfo) {
        String stringBuilder = this.queryFormatter.format(queryExecutionInfo);
        log.info(stringBuilder);
    }
}


