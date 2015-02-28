package com.cuit.DogeMusicService.service;

import com.cuit.DogeMusicService.model.vo.LoginVo;
import com.cuit.DogeMusicService.model.vo.UserMsgVo;

public interface ILoginService {
	public UserMsgVo login(LoginVo vo);
}
