# jdbc + druid + 事务

StatementCallback和ConnectionCallback，一次连接执行多个sql

### 修改方法
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })

### 查询方法
@Transactional(readOnly = true)