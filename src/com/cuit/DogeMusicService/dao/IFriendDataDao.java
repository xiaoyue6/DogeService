package com.cuit.DogeMusicService.dao;

import java.util.List;

import com.cuit.DogeMusicService.model.vo.FriendVo;
import com.cuit.DogeMusicService.model.vo.LoginVo;
import com.cuit.DogeMusicService.model.vo.UserFriendVo;
import com.cuit.DogeMusicService.model.vo.UserMsgVo;
/**
 * 好友相关操作接口
 * */
public interface IFriendDataDao {
	/**
	 * 添加好友
	 * */
	public boolean addFriend(UserFriendVo vo);
	/**
	 * 删除好友
	 * */
	public boolean delectFriend(UserFriendVo vo);
	/**
	 * 查询用户所有的好友信息
	 * */
	public List<FriendVo> queryFriendsList(LoginVo vo);
	/**
	 * 根据Id来查询用户或好友的相关信息
	 * */
	public UserMsgVo queryUserMsgByID();
}
