package com.wenyuan.common.web.controller;


import com.wenyuan.common.page.PageParam;
import com.wenyuan.common.web.dwz.DwzParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Wen on 16/8/7.
 */
public class ControllerSupport {
    public String getString(HttpServletRequest httpServletRequest, String para) {
        return httpServletRequest.getParameter(para);
    }

    /**
     * 根据参数名从HttpRequest中获取Integer类型的参数值，无值则返回null .
     *
     * @param key .
     * @return IntegerValue or null .
     */
    public Integer getInteger(HttpServletRequest httpServletRequest, String key) {
        String value = httpServletRequest.getParameter(key);
        if (StringUtils.isNotBlank(value)) {
            return Integer.parseInt(value);
        }
        return null;
    }

    /**
     * 获取分页参数，包含当前页、每页记录数.
     *
     * @return PageParam .
     */
    public PageParam getPageParam(HttpServletRequest httpServletRequest) {
        return new PageParam(getPageNum(httpServletRequest), getNumPerPage(httpServletRequest));
    }


    /**
     * 响应DWZ的ajax失败请求（statusCode="300"）,跳转到ajaxDone视图.
     *
     * @param message 提示消息.
     * @return ajaxDone .
     * @author WuShuicheng.
     */
    public ModelAndView operateError(HttpServletRequest httpServletRequest, ModelAndView modelAndView, String message) {
        ajaxDone(modelAndView, httpServletRequest, "300", message);
        modelAndView.setViewName("/common/operateError");
        return modelAndView;
    }

    /**
     * 根据request对象，获取页面提交过来的DWZ框架的AjaxDone响应参数值.
     *
     * @param statusCode 状态码.
     * @param message    操作结果提示消息.
     * @return DwzParam :封装好的DwzParam对象 .
     * @author WuShuicheng.
     */
    public DwzParam getDwzParam(HttpServletRequest httpServletRequest, String statusCode, String message) {
        // 获取DWZ Ajax响应参数值,并构造成参数对象
        String navTabId = httpServletRequest.getParameter("navTabId");
        String dialogId = httpServletRequest.getParameter("dialogId");
        String callbackType = httpServletRequest.getParameter("callbackType");
        String forwardUrl = httpServletRequest.getParameter("forwardUrl");
        String rel = httpServletRequest.getParameter("rel");
        return new DwzParam(statusCode, message, navTabId, dialogId, callbackType, forwardUrl, rel, null);
    }

    /**
     * 根据参数名从HttpRequest中获取Long类型的参数值，无值则返回null .
     *
     * @param key .
     * @return LongValue or null .
     */
    public Long getLong(HttpServletRequest httpServletRequest, String key) {
        String value = httpServletRequest.getParameter(key);
        if (StringUtils.isNotBlank(value)) {
            return Long.parseLong(value);
        }
        return null;
    }

    /**
     * 响应DWZ的Ajax成功请求（statusCode="200"）,<br/>
     * 跳转到operateSuccess视图，提示设置的消息内容.
     *
     * @param message 提示消息.
     * @return operateSuccess .
     * @author WuShuicheng.
     */
    public ModelAndView operateSuccess(HttpServletRequest httpServletRequest, ModelAndView modelAndView, String message) {
        ajaxDone(modelAndView, httpServletRequest, "200", message);
        modelAndView.setViewName("/common/operateSuccess");
        return modelAndView;
    }


    /**
     * 与DWZ框架结合的表单属性长度校验方法.
     *
     * @param propertyName 要校验的属性中文名称，如“登录名”.
     * @param property     要校验的属性值，如“gzzyzz”.
     * @param isRequire    是否必填:true or false.
     * @param minLength    最少长度:大于或等于0，如果不限制则可请设为0.
     * @param maxLength    最大长度:对应数据库字段的最大长度，如不限制则可设为0.
     * @return 校验结果消息，校验通过则返回空字符串 .
     */
    protected String lengthValidate(String propertyName, String property, boolean isRequire, int minLength, int maxLength) {

        int propertyLenght = strLengthCn(property);
        if (isRequire && propertyLenght == 0) {
            return propertyName + "不能为空，"; // 校验不能为空
        } else if (isRequire && minLength != 0 && propertyLenght < minLength) {
            return propertyName + "不能少于" + minLength + "个字符，"; // 必填情况下校验最少长度
        } else if (maxLength != 0 && propertyLenght > maxLength) {
            return propertyName + "不能多于" + maxLength + "个字符，"; // 校验最大长度
        } else {
            return ""; // 校验通过则返回空字符串 .
        }
    }
    // ///////////////////////////////////////////////////////////////
    // ///////////////// 结合DWZ-UI的分页参数获取方法 ///////////////////////////

    /**
     * 获取当前页（DWZ-UI分页查询参数）.<br/>
     * 如果没有值则默认返回1.
     *
     * @author WuShuicheng.
     */
    private int getPageNum(HttpServletRequest httpServletRequest) {
        // 当前页数
        String pageNumStr = httpServletRequest.getParameter("pageNum");
        int pageNum = 1;
        if (StringUtils.isNotBlank(pageNumStr)) {
            pageNum = Integer.valueOf(pageNumStr);
        }
        return pageNum;
    }

    /**
     * 获取每页记录数（DWZ-UI分页查询参数）.<br/>
     * 如果没有值则默认返回15.
     *
     * @author WuShuicheng.
     */
    private int getNumPerPage(HttpServletRequest httpServletRequest) {
        String numPerPageStr = httpServletRequest.getParameter("numPerPage");
        int numPerPage = 15;
        if (StringUtils.isNotBlank(numPerPageStr)) {
            numPerPage = Integer.parseInt(numPerPageStr);
        }
        return numPerPage;
    }

    /**
     * 响应DWZ的Ajax请求.
     *
     * @param modelAndView
     * @param statusCode   statusCode:{ok:200, error:300, timeout:301}.
     * @param message
     * @author WuShuicheng.
     */
    private void ajaxDone(ModelAndView modelAndView, HttpServletRequest httpServletRequest, String statusCode, String message) {
        DwzParam param = getDwzParam(httpServletRequest, statusCode, message);
        modelAndView.addObject(param);
    }

    /**
     * 获取字符串的长度，如果有中文，则每个中文字符计为3位 ，当字符串为空时返回0.
     *
     * @param str 字符串 .
     * @return 字符串的长度 .
     */
    private int strLengthCn(String str) {
        if (StringUtils.isBlank(str)) {
            return 0;
        }
        int valueLength = 0;
        final String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int num = 0; num < str.length(); num++) {
			/* 获取一个字符 */
            final String temp = str.substring(num, num + 1);
			/* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
				/* 中文字符长度为3 */
                valueLength += 3;
            } else {
				/* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength;
    }

}
