package com.wenyuan.web.boss.controller;

import com.wenyuan.common.page.PageBean;
import com.wenyuan.facade.user.entity.PmsUser;
import com.wenyuan.facade.user.enums.UserStatusEnum;
import com.wenyuan.facade.user.enums.UserTypeEnum;
import com.wenyuan.facade.user.service.PmsUserFacade;
import com.wenyuan.web.boss.base.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wen on 16/8/6.
 */
@Controller
public class PmsUserController extends BaseController {
    private static Log log = LogFactory.getLog(PmsUserController.class);

    @Autowired
    private PmsUserFacade pmsUserFacade;

// /////////////////////////////////// 用户管理   //////////////////////////////////////////

    /**
     * 分页列出用户信息，并可按登录名获姓名进行查询.
     *
     * @return listPmsUser or operateError .
     */
    @RequestMapping("/pms_listPmsUser")
    public ModelAndView listPmsUser(HttpServletRequest httpServletRequest, HttpSession httpSession) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            Map<String, Object> paramMap = new HashMap<String, Object>(); // 业务条件查询参数
            paramMap.put("userNo", getString(httpServletRequest, "userNo")); // 用户登录名（精确查询）
            paramMap.put("userName", getString(httpServletRequest, "userName")); // 用户姓名（模糊查询）
            paramMap.put("status", getInteger(httpServletRequest, "status")); // 状态

            PageBean pageBean = pmsUserFacade.listPage(getPageParam(httpServletRequest), paramMap);
//            this.pushData(pageBean);
            modelAndView.addObject(pageBean);
            PmsUser pmsUser = getLoginedUser(httpSession);// 获取当前登录用户对象
            modelAndView.addObject("currUserNo", pmsUser.getUserNo());
            // 回显查询条件值
            modelAndView.addAllObjects(paramMap);
            modelAndView.addObject("UserStatusEnumList", UserStatusEnum.values());
            modelAndView.addObject("UserStatusEnum", UserStatusEnum.toMap());
            modelAndView.addObject("UserTypeEnumList", UserTypeEnum.values());
            modelAndView.addObject("UserTypeEnum", UserTypeEnum.toMap());
            modelAndView.setViewName("/pms/PmsUserList");
            return modelAndView;
        } catch (Exception e) {
            log.error("== listPmsUser exception:", e);
            return operateError(httpServletRequest, modelAndView, "获取数据失败");
        }
    }

    /**
     * 查看用户详情.
     *
     * @return .
     */
    @RequestMapping("/pms_viewPmsUserUI")
    public ModelAndView viewPmsUserUI(HttpServletRequest httpServletRequest) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            Long userId = getLong(httpServletRequest, "id");
            PmsUser pmsUser = pmsUserFacade.getById(userId);
            if (pmsUser == null) {
                return operateError(httpServletRequest, modelAndView, "无法获取要查看的数据");
            }

            modelAndView.addObject("UserStatusEnumList", UserStatusEnum.values());
            modelAndView.addObject("UserStatusEnum", UserStatusEnum.toMap());
            modelAndView.addObject("UserTypeEnumList", UserTypeEnum.values());
            modelAndView.addObject("UserTypeEnum", UserTypeEnum.toMap());

            modelAndView.addObject(pmsUser);
            modelAndView.setViewName("/pms/PmsUserView");
            return modelAndView;
        } catch (Exception e) {
            log.error("== viewPmsUserUI exception:", e);
            return operateError(httpServletRequest, modelAndView, "获取数据失败");
        }
    }

    /**
     * 转到修改用户界面
     *
     * @return PmsUserEdit or operateError .
     */
    @RequestMapping("/pms_editPmsUserUI")
    public ModelAndView editPmsUserUI(HttpServletRequest httpServletRequest, HttpSession httpSession) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            Long id = getLong(httpServletRequest, "id");
            PmsUser pmsUser = pmsUserFacade.getById(id);
            if (pmsUser == null) {
                return operateError(httpServletRequest, modelAndView, "无法获取要修改的数据");
            }

            // 普通用户没有修改超级管理员的权限
            if (UserTypeEnum.ADMIN.getValue().equals(getLoginedUser(httpSession).getUserType()) && UserTypeEnum.ADMIN.getValue().equals(pmsUser.getUserType())) {
                return operateError(httpServletRequest, modelAndView, "权限不足");
            }

            modelAndView.addObject(pmsUser);

            modelAndView.addObject("UserStatusEnum", UserStatusEnum.toMap());
            modelAndView.addObject("UserTypeEnum", UserTypeEnum.toMap());

            modelAndView.setViewName("/pms/PmsUserEdit");
            return modelAndView;
        } catch (Exception e) {
            log.error("== editPmsUserUI exception:", e);
            return operateError(httpServletRequest, modelAndView, "获取修改数据失败");
        }
    }

    /**
     * 删除用户
     *
     * @return
     */
    @RequestMapping("/pms_deleteUserStatus")
    public ModelAndView deleteUserStatus(HttpServletRequest httpServletRequest, HttpSession httpSession) {
        ModelAndView modelAndView = new ModelAndView();
        long id = getLong(httpServletRequest, "id");
        pmsUserFacade.deleteUserById(id);
        return operateSuccess(httpServletRequest, modelAndView, "操作成功");
    }

    /**
     * 保存修改后的用户信息
     *
     * @return operateSuccess or operateError .
     */
    @RequestMapping("/pms_editPmsUser")
    public ModelAndView editPmsUser(HttpServletRequest httpServletRequest, HttpSession httpSession) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            Long id = getLong(httpServletRequest, "id");

            PmsUser pmsUser = pmsUserFacade.getById(id);
            if (pmsUser == null) {
                return operateError(httpServletRequest, modelAndView, "无法获取要修改的用户信息");
            }

            // 普通用户没有修改超级管理员的权限
            if ("0".equals(getLoginedUser(httpSession).getUserType()) && "1".equals(pmsUser.getUserType())) {
                return operateError(httpServletRequest, modelAndView, "权限不足");
            }

            pmsUser.setRemark(getString(httpServletRequest, "remark"));
            pmsUser.setMobileNo(getString(httpServletRequest, "mobileNo"));
            pmsUser.setUserName(getString(httpServletRequest, "userName"));
            // 修改时不能修状态
            // pmsUser.setStatus(getInteger("status"));


            // 表单数据校验
            String validateMsg = validatePmsUser(pmsUser);
            if (StringUtils.isNotBlank(validateMsg)) {
                return operateError(httpServletRequest, modelAndView,validateMsg); // 返回错误信息
            }

            pmsUserFacade.update(pmsUser);
            return operateSuccess(httpServletRequest, modelAndView,"操作成功");
        } catch (Exception e) {
            log.error("== editPmsUser exception:", e);
            return operateError(httpServletRequest, modelAndView,"更新用户信息失败");
        }
    }
    /**
     * 校验Pms用户表单数据.
     *
     * @param user
     *            用户信息.
     * @return
     */
    private String validatePmsUser(PmsUser user) {
        String msg = ""; // 用于存放校验提示信息的变量
        msg += lengthValidate("真实姓名", user.getUserName(), true, 2, 15);
        msg += lengthValidate("登录名", user.getUserName(), true, 3, 50);

        // 登录密码
        String userPwd = user.getUserPwd();
        String userPwdMsg = lengthValidate("登录密码", userPwd, true, 6, 50);
		/*
		 * if (StringUtils.isBlank(loginPwdMsg) &&
		 * !ValidateUtils.isAlphanumeric(loginPwd)) { loginPwdMsg +=
		 * "登录密码应为字母或数字组成，"; }
		 */
        msg += userPwdMsg;

        // 手机号码
        String mobileNo = user.getMobileNo();
        String mobileNoMsg = lengthValidate("手机号", mobileNo, true, 0, 12);
        msg += mobileNoMsg;

        // 状态
        Integer status = user.getStatus();
        if (status == null) {
            msg += "请选择状态，";
        } else if (status.intValue() < 100 || status.intValue() > 101) {
            msg += "状态值不正确，";
        }

        msg += lengthValidate("描述", user.getRemark(), true, 3, 100);
        return msg;
    }
}
