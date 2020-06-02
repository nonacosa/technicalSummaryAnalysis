package com.pkwenda.github.delay;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public String delayOrder(){
		String sql = "SELECT * FROM `order`";
		List<Order> orders = jdbcTemplate.query(sql, new RowMapper<Order>() {
			Order order = null;
			@Override
			public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
				order = new Order();
				order.setId(rs.getString("id"));
				order.setName(rs.getString("name"));
				return order;
			}
		});
		System.out.println("查询结果");
		for (Order order : orders) {
			System.out.println(order.toString());
		}
		return "ok";
	}


}
