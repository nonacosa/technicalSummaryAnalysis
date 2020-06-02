package com.pkwenda.github.delay;

import com.pkwenda.github.util.SnowFlakeIdWorker;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

/**
 * @author wenda.zhuang
 * @Date 2020/6/2 15:35
 * @Description 延时场景
 * @E-mail sis.nonacosa@gmail.com
 */

@RestController
@RequestMapping("/order")
public class DelayRest {
	@Resource
	private JdbcTemplate jdbcTemplate;


	@GetMapping("/submit")
	public String delayOrder() throws Exception {
//		String sql = "SELECT * FROM `order`";
//		List<Order> orders = jdbcTemplate.query(sql, new RowMapper<Order>() {
//			Order order = null;
//			@Override
//			public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
//				order = new Order();
//				order.setId(rs.getString("id"));
//				order.setName(rs.getString("name"));
//				return order;
//			}
//		});
//		System.out.println("查询结果");
//		for (Order order : orders) {
//			System.out.println(order.toString());
//		}
		int totalMessagesToSend = 1000;
		DefaultMQProducer producer = new DefaultMQProducer("mall");
		producer.setNamesrvAddr("localhost:9876");
		producer.start();
		producer.setRetryTimesWhenSendAsyncFailed(0);
		SnowFlakeIdWorker idWorker = new SnowFlakeIdWorker(0, 0);
		//submit 1000 order
		for (int i = 0; i < totalMessagesToSend; i++) {

			String id = String.valueOf(idWorker.nextId());
			String insertSql = "INSERT INTO `study`.`order`(`id`, `name`, `status`) VALUES "+String.format("('%s', '%s', %s)",id,"商品" + i,"0")+";";
			//业务写在 mq 前面，业务走完，在发送消息，防止出现宕机问题，引发消息在，业务 ID 丢失的情况
			jdbcTemplate.execute(insertSql);
			Message message = new Message("order_delay_id", (id).getBytes());
			// 设置延时等级3,这个消息将在1min之后发送(现在只支持固定的几个时间,详看delayTimeLevel)
			//private String messageDelayLevel = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
			message.setDelayTimeLevel(5);
			// 发送消息
			producer.send(message);



		}
		// 关闭生产者
		producer.shutdown();

		return "ok";
	}


	@GetMapping("/consumer")
	public void consumer() throws MQClientException {
		// 实例化消费者
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("mall");

		// 设置NameServer的地址
		consumer.setNamesrvAddr("localhost:9876");

		// 订阅一个或者多个Topic，以及Tag来过滤需要消费的消息
		consumer.subscribe("order_delay_id", "*");
		// 注册回调实现类来处理从broker拉取回来的消息
		consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
			String orderId = new String(msgs.get(0).getBody());
			// todo double check db or cache order_status
			String sql = String.format("UPDATE `study`.`order` SET `status` = 1 WHERE `id` = '%s'",orderId);
			jdbcTemplate.update(sql);
			System.out.printf("%s 已经设置订单号 %s 过期。 %n", Thread.currentThread().getName(), orderId);
			// 标记该消息已经被成功消费
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		});
		// 启动消费者实例
		consumer.start();
		System.out.printf("com.pkwenda.github.example.Consumer Started.%n");
	}


}
