package com.taotao.order.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbItem;

/**
 * 订单确认页面处理Controller
 * @author Administrator
 *
 */
@Controller
public class OrderCartController {
	@Value("${CART_KEY}")
	private String CART_KEY;
	
	
	
	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request){
		// 用户必须是登录的状态
		// 取出用户id
		// 根据用户详细取收货地址列表--使用静态数据
		// 把收货地址列表取出传递给页面
		// 从cookie中取购物车商品列表展示到页面
		List<TbItem> cartList = getCartItemList(request);
		request.setAttribute("cartList", cartList);
		
		// 返回逻辑视图
		return "order-cart";
	}
	
	private List<TbItem> getCartItemList(HttpServletRequest request){
		// 从cookie中取购物车商品列表
		String json = CookieUtils.getCookieValue(request, CART_KEY, true);
		if (StringUtils.isBlank(json)) {
			// 如果没有值 返回一个空的list
			return new ArrayList<>();
		}
		List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
		return list;
	}
}
