package com.wenyuan.web.boss.base;


import com.wenyuan.facade.user.entity.PmsUser;

import javax.servlet.http.HttpSession;

public interface UserLoginedAware {

	/**
	 * 取得登录的用户
	 * 
	 * @return
	 */
	public PmsUser getLoginedUser(HttpSession httpSession);
}
