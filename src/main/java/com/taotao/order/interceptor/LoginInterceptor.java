package com.taotao.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.common.utils.TaotaoResult;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserService;
/**
 * 拦截用户是否登录
 * @author Administrator
 *
 */
public class LoginInterceptor implements HandlerInterceptor{

	@Autowired
	private UserService userService;
	
	@Value("${TT_TOKEN}")
	private String TT_TOKEN;
	
	@Value("${SSO_URL}")
	private String SSO_URL;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 执行handler之前先执行此方法
		// 1.从cookie中取token信息
		String token = CookieUtils.getCookieValue(request, TT_TOKEN);
		// 2.如果取不到token，跳转到sso的登录页面，需要把当前请求的url作为参数传递给sso，sso登录成功之后跳转回请求的页面。
		if (StringUtils.isBlank(token)) {
			// 取当前请求的url
			StringBuffer requestURL = request.getRequestURL();
			// 跳转到登录页面
			response.sendRedirect(SSO_URL+"/page/login?url="+requestURL);
			// 拦截
			return false;
		}
		// 3.取到token，调用sso系统的服务判断用户是否登录
		TaotaoResult taotaoResult = userService.getUserByToken(token);
		// 4.如果用户为登录，即没取到用户信息。跳转到sso的登录页面
		if (taotaoResult.getStatus()!=200) {
			// 取当前请求的url
			StringBuffer requestURL = request.getRequestURL();
			// 跳转到登录页面
			response.sendRedirect(SSO_URL+"/page/login?url="+requestURL);
			// 拦截
			return false;
		}
		// 5.如果取到用户信息。放行。
		// 把用户信息放入request中
		TbUser user = (TbUser)taotaoResult.getData();
		request.setAttribute("user", user);
		// 返回true：放行，返回false：拦截
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		// handler执行之后，modelAndView返回之前
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// modelAndView返回之后，异常处理
		
	}

}
