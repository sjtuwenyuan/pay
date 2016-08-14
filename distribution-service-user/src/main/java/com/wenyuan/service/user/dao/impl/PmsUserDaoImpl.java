package com.wenyuan.service.user.dao.impl;


import com.wenyuan.commom.core.dao.BaseDaoImpl;
import com.wenyuan.facade.user.entity.PmsUser;
import com.wenyuan.service.user.dao.PmsUserDao;
import org.springframework.stereotype.Repository;

/**
 * 
 * @描述: 用户表数据访问层接口实现类.
 * @作者: WuShuicheng .
 * @创建时间: 2013-7-22,下午5:51:47 .
 * @版本: 1.0 .
 */
@Repository("pmsUserDao")
public class PmsUserDaoImpl extends BaseDaoImpl<PmsUser> implements PmsUserDao {

	/**
	 * 根据用户登录名获取用户信息.
	 * 
	 * @param userNo
	 *            .
	 * @return user .
	 */

	public PmsUser findByUserNo(String userNo) {
		return super.getSqlSession().selectOne(getStatement("findByUserNo"), userNo);
	}

}
