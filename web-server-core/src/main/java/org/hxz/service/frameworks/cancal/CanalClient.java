//package org.hxz.service.frameworks.cancal;
//
//import com.alibaba.otter.canal.client.CanalConnector;
//import com.alibaba.otter.canal.client.CanalConnectors;
//import com.alibaba.otter.canal.protocol.CanalEntry;
//import com.alibaba.otter.canal.protocol.Message;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.hxz.service.frameworks.db.DbConfig;
//import org.hxz.service.frameworks.utils.GsonUtil;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.net.InetSocketAddress;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by hxz on 2021/12/14 14:54.
// */
//
//@Component
//public class CanalClient implements ApplicationRunner {
//
//    private static final Logger logger = LogManager.getLogger(CanalClient.class);
//
//    @Resource(name = "myRedisTemplate")
//    private RedisTemplate<String, String> redisTemplate;
//
//    private static final String TABLE_NAME = "user";
//    private static final String PRIMARY_KEY = "uid";
//    private static final String SEPARATOR = ":";
//
//
//    private static final String CANAL_SERVER_HOST = "127.0.0.1";
//    private static final int CANAL_SERVER_PORT = 11111;
//    private static final String CANAL_INSTANCE = "instance";
//    private static final String USERNAME = "canal";
//    private static final String PASSWORD = "canal";
//
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        // 创建链接
//        CanalConnector connector = CanalConnectors.newSingleConnector(
//                new InetSocketAddress(CANAL_SERVER_HOST, CANAL_SERVER_PORT),
//                CANAL_INSTANCE, USERNAME, PASSWORD);
//        int batchSize = 1000;
//        try {
//            logger.info("启动 canal 数据同步...");
//            connector.connect();
//            connector.subscribe(".*\\..*");
//            connector.rollback();
//            while (true) {
//                // 获取指定数量的数据
//                Message message = connector.getWithoutAck(batchSize);
//                long batchId = message.getId();
//                int size = message.getEntries().size();
//                if (batchId == -1 || size == 0) {
//                    try {
//                        // 时间间隔1000毫秒
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    syncEntry(message.getEntries());
//                }
//                connector.ack(batchId); // 提交确认
//                // connector.rollback(batchId); // 处理失败, 回滚数据
//            }
//        } finally {
//            connector.disconnect();
//        }
//    }
//
//
//
//    private void syncEntry(List<CanalEntry.Entry> entrys) {
//        for (CanalEntry.Entry entry : entrys) {
//            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN
//                    || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
//                continue;
//            }
//
//            CanalEntry.RowChange rowChange;
//            try {
//                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
//            } catch (Exception e) {
//                throw new RuntimeException("ERROR data:" + entry.toString(), e);
//            }
//
//            CanalEntry.EventType eventType = rowChange.getEventType();
//            logger.info("================> binlog[{}:{}] , name[{},{}] , eventType : {}",
//                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
//                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
//                    eventType);
//
//            String tableName = entry.getHeader().getTableName();
//            if (!TABLE_NAME.equalsIgnoreCase(tableName)) continue;
//            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
//                if (eventType == CanalEntry.EventType.INSERT) {
//                    printColumn(rowData.getAfterColumnsList());
//                    redisInsert(tableName, rowData.getAfterColumnsList());
//                } else if (eventType == CanalEntry.EventType.UPDATE) {
//                    printColumn(rowData.getAfterColumnsList());
//                    redisUpdate(tableName, rowData.getAfterColumnsList());
//                } else if (eventType == CanalEntry.EventType.DELETE) {
//                    printColumn(rowData.getBeforeColumnsList());
//                    redisDelete(tableName, rowData.getBeforeColumnsList());
//                }
//            }
//        }
//    }
//
//    private void redisInsert(String tableName, List<CanalEntry.Column> columns) {
//        Map<String,String> json = new HashMap<>();
//        for (CanalEntry.Column column : columns) {
//            json.put(column.getName(), column.getValue());
//        }
//        for (CanalEntry.Column column : columns) {
//            if (PRIMARY_KEY.equalsIgnoreCase(column.getName())) {
//                String key = tableName + SEPARATOR + column.getValue();
//                redisTemplate.opsForValue().set(key, GsonUtil.toJson(json));
//                logger.info("redis数据同步新增，key：" + key);
//                break;
//            }
//        }
//    }
//
//    private void redisUpdate(String tableName, List<CanalEntry.Column> columns) {
//        Map<String,String> json = new HashMap<>();
//        for (CanalEntry.Column column : columns) {
//            json.put(column.getName(), column.getValue());
//        }
//        for (CanalEntry.Column column : columns) {
//            if (PRIMARY_KEY.equalsIgnoreCase(column.getName())) {
//                String key = tableName + SEPARATOR + column.getValue();
//                redisTemplate.opsForValue().set(key, GsonUtil.toJson(json));
//                logger.info("redis数据同步更新，key：" + key);
//                break;
//            }
//        }
//    }
//
//    private void redisDelete(String tableName, List<CanalEntry.Column> columns) {
//        for (CanalEntry.Column column : columns) {
//            if (PRIMARY_KEY.equalsIgnoreCase(column.getName())) {
//                String key = tableName + SEPARATOR + column.getValue();
//                redisTemplate.delete(key);
//                logger.info("redis数据同步删除，key：" + key);
//                break;
//            }
//        }
//    }
//
//    private void printColumn(List<CanalEntry.Column> columns) {
//        for (CanalEntry.Column column : columns) {
//            logger.info(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
//        }
//    }
//
//
//
//}
