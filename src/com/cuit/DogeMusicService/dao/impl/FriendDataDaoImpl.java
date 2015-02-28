package com.cuit.DogeMusicService.dao.impl;

import java.util.List;

import com.cuit.DogeMusicService.dao.IFriendDataDao;
import com.cuit.DogeMusicService.model.vo.FriendVo;
import com.cuit.DogeMusicService.model.vo.LoginVo;
import com.cuit.DogeMusicService.model.vo.UserFriendVo;
import com.cuit.DogeMusicService.model.vo.UserMsgVo;

public class FriendDataDaoImpl implements IFriendDataDao{

	@Override
	public boolean addFriend(UserFriendVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delectFriend(UserFriendVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<FriendVo> queryFriendsList(LoginVo vo) {
		// TODO Auto-generated method stub
		return null;
	}

    /** 
     * {@inheritDoc}.
     */
    @Override
    public UserMsgVo queryUserMsgByID() {
        // TODO Auto-generated method stub
        return null;
    }

}
