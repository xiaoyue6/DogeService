package com.cuit.DogeMusicService.dao.impl;

import com.cuit.DogeMusicService.dao.ILoginDao;
import com.cuit.DogeMusicService.model.vo.LoginVo;
import com.cuit.DogeMusicService.model.vo.UserFriendVo;
import com.cuit.DogeMusicService.model.vo.UserMsgVo;

public class LoginDaoImpl extends BaseDaoImpl implements ILoginDao{
	
	@Override
	public UserMsgVo queryByUserIdAndPsw(LoginVo vo) {
		// TODO Auto-generated method stub
		UserMsgVo queryVo = this.sqlSessionRead.selectOne("login", vo);
		return null;
	}

	@Override
	public boolean registry(UserMsgVo vo) {
		// TODO Auto-generated method stub
		int result = this.sqlSessionWrite.insert("", vo);
		return false;
	}

}
