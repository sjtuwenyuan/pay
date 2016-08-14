package com.wenyuan.web.boss.base;


import com.wenyuan.common.web.constants.SessionConstant;
import com.wenyuan.common.web.controller.ControllerSupport;
import com.wenyuan.facade.user.entity.PmsUser;

import javax.servlet.http.HttpSession;

/**
 * Created by Wen on 16/8/14.
 */
@SuppressWarnings("serial")
public class BaseController extends ControllerSupport implements UserLoginedAware{

    /**
     * 取出当前登录用户对象
     */
    @Override
    public PmsUser getLoginedUser(HttpSession httpSession) {
        PmsUser user = (PmsUser) httpSession.getAttribute(SessionConstant.USER_SESSION_KEY);
        return user;
    }
}
