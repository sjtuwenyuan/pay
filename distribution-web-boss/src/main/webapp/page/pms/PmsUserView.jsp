<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/page/inc/taglib.jsp"%>
<style>
<!--
.pageFormContent fieldset label{
	width: 200px;
}
-->
</style>
<div class="pageContent">
	<form>
		<div class="pageFormContent" layoutH="60">

			<p style="width:99%">
				<label>用户姓名：</label>
				<td>${pmsUser.userName }</td>
			</p>
			<p style="width:99%">
				<label>用户登录名：</label>
			    <td>${pmsUser.userNo }</td>
			</p>
			<p style="width:99%">
				<label>创建时间：</label>
				<fmt:formatDate value="${pmsUser.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
			</p>
			<p style="width:99%">
				<label>手机号码：</label>
                <td>${pmsUser.mobileNo }</td>
			</p>
			<p style="width:99%">
				<label>状态：</label>
				<c:choose>
					<c:when test="${pmsUser.status eq 100 }">激活</c:when>
					<c:when test="${pmsUser.status eq 101 }">冻结</c:when>
					<c:otherwise>--</c:otherwise>
				</c:choose>
			</p>
			<p style="width:99%">
				<label>类型：</label>
				<c:forEach items="${UserTypeEnumList}" var="userTypeEnum">
					<c:if test="${pmsUser.userType ne null and pmsUser.userType eq userTypeEnum.value}">${userTypeEnum.desc}</c:if>
				</c:forEach>
			</p>
			<p style="width:99%;height:50px;">
				<label>描述：</label>
                <td>${pmsUser.remark }</td>
			</p>
			<p style="width:99%">
				<label>输错密码次数：</label>
                <td>${pmsUser.pwdErrorCount }</td>
			</p>
			<p style="width:99%">
				<label>最后输错密码时间：</label>
				<fmt:formatDate value="${pmsUser.pwdErrorTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
			</p>
			<p style="width:99%">
				<label>是否已更改过密码：</label>
				<c:if test="${pmsUser.isChangedPwd eq true}">是</c:if>
				<c:if test="${pmsUser.isChangedPwd eq false}">否</c:if>
			</p>

		</div>
		<div class="formBar">
			<ul>
				<li><div class="button"><div class="buttonContent"><button type="button" class="close">关闭</button></div></div></li>
			</ul>
		</div>
	</form>
</div>