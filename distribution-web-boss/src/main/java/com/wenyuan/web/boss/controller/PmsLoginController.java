package com.wenyuan.web.boss.controller;

import com.wenyuan.common.web.constants.SessionConstant;
import com.wenyuan.facade.user.entity.PmsUser;
import com.wenyuan.facade.user.enums.UserStatusEnum;
import com.wenyuan.facade.user.enums.UserTypeEnum;
import com.wenyuan.facade.user.service.PmsUserFacade;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * Created by Wen on 16/8/6.
 */
@Controller
public class PmsLoginController {

    private static final Log log = LogFactory.getLog(PmsLoginController.class);

//    @RequestMapping("/hello")
//    public ModelAndView hello(){
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("/hello");
//        return modelAndView;
//    }

    @Autowired
    private PmsUserFacade pmsUserFacade;

    /**
     * 进入登录页面.
     *
     * @return
     */

    @RequestMapping("/login_loginPage")
    public ModelAndView loginPage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/login");
        return modelAndView;
    }

    @RequestMapping("/login_userLogin")
    public ModelAndView userLogin(HttpServletRequest httpServletRequest, HttpSession httpSession){
        ModelAndView modelAndView = new ModelAndView();
        try {
            // 明文用户名
            String userNo = httpServletRequest.getParameter("userNo");
            if (StringUtils.isBlank(userNo)) {
                modelAndView.setViewName("/login");
                modelAndView.addObject("userNoMsg", "用户名不能为空");
                return modelAndView;
            }
            modelAndView.addObject("userNo", userNo);
            PmsUser user = pmsUserFacade.findUserByUserNo(userNo);
            if (user == null) {
                log.warn("== no such user");
                modelAndView.setViewName("/login");
                modelAndView.addObject("userNoMsg", "用户名或密码不正确");
                return modelAndView;
            }

            if (user.getStatus().intValue() == UserStatusEnum.INACTIVE.getValue()) {
                log.warn("== 帐号【" + userNo + "】已被冻结");
                modelAndView.setViewName("/login");
                modelAndView.addObject("userNoMsg", "该帐号已被冻结");
                return modelAndView;
            }
            String pwd = httpServletRequest.getParameter("userPwd");
            if (StringUtils.isBlank(pwd)) {
                modelAndView.setViewName("/login");
                modelAndView.addObject("userPwdMsg", "密码不能为空");
                return modelAndView;
            }
            // 加密明文密码
            // 验证密码
            if (user.getUserPwd().equals(DigestUtils.sha1Hex(pwd))) {
                // 用户信息，包括登录信息和权限
                httpSession.setAttribute(SessionConstant.USER_SESSION_KEY, user);

                // 将主帐号ID放入Session
                if (UserTypeEnum.MAIN_USER.getValue().equals(user.getUserType())) {
                    httpSession.setAttribute(SessionConstant.MAIN_USER_ID_SESSION_KEY, user.getId());
                } else if (UserTypeEnum.SUB_USER.getValue().equals(user.getUserType())) {
                    httpSession.setAttribute(SessionConstant.MAIN_USER_ID_SESSION_KEY, user.getMainUserId());
                } else {
                    // 其它类型用户的主帐号ID默认为0
                    httpSession.setAttribute(SessionConstant.MAIN_USER_ID_SESSION_KEY, 0L);
                }

                modelAndView.addObject("userNo", userNo);
                modelAndView.addObject("lastLoginTime", user.getLastLoginTime());

                try {
                    // 更新登录数据
                    user.setLastLoginTime(new Date());
                    user.setPwdErrorCount(0); // 错误次数设为0
                    pmsUserFacade.update(user);

                } catch (Exception e) {
                    modelAndView.setViewName("/login");
                    modelAndView.addObject("errorMsg", e.getMessage());
                    return modelAndView;
                }

                // 判断用户是否重置了密码，如果重置，弹出强制修改密码页面;
                modelAndView.setViewName("/index");
                modelAndView.addObject("isChangePwd", user.getIsChangedPwd());
                return modelAndView;
            } else {
                // 密码错误
                log.warn("== wrongPassword");
                // 错误次数加1
                Integer pwdErrorCount = user.getPwdErrorCount();
                if (pwdErrorCount == null) {
                    pwdErrorCount = 0;
                }
                user.setPwdErrorCount(pwdErrorCount + 1);
                user.setPwdErrorTime(new Date()); // 设为当前时间
                String msg = "";
                if (user.getPwdErrorCount().intValue() >= SessionConstant.WEB_PWD_INPUT_ERROR_LIMIT) {
                    // 超5次就冻结帐号
                    user.setStatus(UserStatusEnum.INACTIVE.getValue());
                    msg += "<br/>密码已连续输错【" + SessionConstant.WEB_PWD_INPUT_ERROR_LIMIT + "】次，帐号已被冻结";
                } else {
                    msg += "<br/>密码错误，再输错【" + (SessionConstant.WEB_PWD_INPUT_ERROR_LIMIT - user.getPwdErrorCount().intValue()) + "】次将冻结帐号";
                }
                pmsUserFacade.update(user);
                modelAndView.setViewName("/login");
                modelAndView.addObject("userPwdMsg", msg);
                return modelAndView;
            }

        } catch (RuntimeException e) {
            log.error("login exception:", e);
            modelAndView.setViewName("/login");
            modelAndView.addObject("errorMsg", "登录出错");
            return modelAndView;
        } catch (Exception e) {
            log.error("login exception:", e);
            modelAndView.setViewName("/login");
            modelAndView.addObject("errorMsg", "登录出错");
            return modelAndView;
        }
    }

    /**
     * 跳转到退出确认页面.
     *
     * @return LogOutConfirm.
     */
    @RequestMapping("/login_logoutConfirm")
    public ModelAndView logoutConfirm() {
        log.info("== logoutConfirm");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/logoutConfirm");
        return modelAndView;
    }


}
