package com.jstarcraft.core.event.rocketmq;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.junit.Test;

import com.jstarcraft.core.utility.StringUtility;

public class RockMqTestCase {

    Semaphore semaphore = new Semaphore(0);

    MessageListenerConcurrently listener = new MessageListenerConcurrently() {

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context) {
            for (MessageExt message : messages) {
                String body = new String(message.getBody(), StringUtility.CHARSET);
                System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "消费响应：msgId : " + message.getMsgId() + ",  msgBody : " + body);// 输出消息内容
                semaphore.release(1);
            }

            // 返回消费状态
            // CONSUME_SUCCESS 消费成功
            // RECONSUME_LATER 消费失败,需要重新消费
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

    };

    @Test
    public void testQueue() throws Exception {
        int size = 5;
        DefaultMQPushConsumer[] consumers = new DefaultMQPushConsumer[size];
        for (int index = 0; index < size; index++) {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("queue");
            consumer.setInstanceName("queueConsumer" + index);
            consumer.setNamesrvAddr("localhost:9876");
            consumer.setMessageModel(MessageModel.CLUSTERING);
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.setConsumeMessageBatchMaxSize(1000);
            consumer.subscribe("queue-message", "*");
            consumer.registerMessageListener(listener);
            consumer.start();
            consumers[index] = consumer;
        }

        DefaultMQProducer producer = new DefaultMQProducer("queue");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        for (int index = 0; index < 10; index++) {
            Message message = new Message("queue-message", "tag", ("RocketMQ 集群模式 " + index).getBytes(StringUtility.CHARSET));
            producer.send(message);
        }

        semaphore.acquire(10);

        producer.shutdown();
        for (int index = 0; index < size; index++) {
            consumers[index].shutdown();
        }
    }

    @Test
    public void testTopic() throws Exception {
        int size = 5;
        DefaultMQPushConsumer[] consumers = new DefaultMQPushConsumer[size];
        for (int index = 0; index < size; index++) {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("topic");
            consumer.setInstanceName("topicConsumer" + index);
            consumer.setNamesrvAddr("localhost:9876");
            consumer.setMessageModel(MessageModel.BROADCASTING);
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.setConsumeMessageBatchMaxSize(1000);
            consumer.subscribe("topic-message", "*");
            consumer.registerMessageListener(listener);
            consumer.start();
            consumers[index] = consumer;
        }

        DefaultMQProducer producer = new DefaultMQProducer("topic");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        for (int index = 0; index < 10; index++) {
            Message message = new Message("topic-message", "tag", ("RocketMQ 广播模式 " + index).getBytes(StringUtility.CHARSET));
            producer.send(message);
        }

        semaphore.acquire(50);

        producer.shutdown();
        for (int index = 0; index < size; index++) {
            consumers[index].shutdown();
        }
    }

}
