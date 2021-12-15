import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * Created by hxz on 2021/12/9 17:50.
 */

public class KafkaTest {

    public static void main(String[] args) {
        try {
            // 先监听，再发送消息
            new Thread(() -> {
                try {
                    consume();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            produce();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void produce() throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.195.235:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30000);

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(props);
        try {
            Future<RecordMetadata> future = kafkaProducer.send(new ProducerRecord<String, String>("topic1", "这是测试文本"));
            if(future.isDone()){
                System.out.println(future.get().toString());
            }

        }
        finally {
            kafkaProducer.close();
        }
    }

    private static void consume() throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.195.235:9092");
        props.put("group.id", "test-consumer-group");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "earliest");

        KafkaConsumer consumer = new KafkaConsumer<>(props);
        try {
            consumer.subscribe(Arrays.asList("topic1"));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records)
                {
                    System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                }
            }
        }
        finally {
            consumer.close();
        }
    }

}
