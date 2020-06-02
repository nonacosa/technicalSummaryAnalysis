package com.pkwenda.github.example;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;

/**
 * @author wenda.zhuang
 * @Date 2020/6/2 01:05
 * @Description 消费：https://github.com/pkwenda/Blog/issues/31
 * @E-mail sis.nonacosa@gmail.com
 */
public class Consumer {

	public static void main(String[] args) throws InterruptedException, MQClientException {

		// 实例化消费者
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name");

		// 设置NameServer的地址
		consumer.setNamesrvAddr("localhost:9876");

		// 订阅一个或者多个Topic，以及Tag来过滤需要消费的消息
		consumer.subscribe("TopicTest", "*");
		// 注册回调实现类来处理从broker拉取回来的消息
		consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
			System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
			// 标记该消息已经被成功消费
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		});
		// 启动消费者实例
		consumer.start();
		System.out.printf("com.pkwenda.github.example.Consumer Started.%n");
	}
}
