package com.pkwenda.github.delay;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wenda.zhuang
 * @Date 2020/6/2 16:20
 * @Description 测试延时队列实体
 * @E-mail sis.nonacosa@gmail.com
 */
@Data
@Accessors(chain = true)
public class Order {

	private String id;
	private String name;
	private String status;
}
