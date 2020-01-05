package com.xxun.watch.xunfriends.net;

public class WbApi {
	//正式ip
	public static final String BASE_IP ="https://friend.xunkids.com";
	//预发版本ip
	public static final String DEBUG_IP ="https://friend.xunkids.com";
	public static final boolean isDebug = false;
	public static final String SERVER_IP = (isDebug ? DEBUG_IP : BASE_IP);

	//设备发布朋友圈
	public static final String pushfc = SERVER_IP+"/pushfc";
	//拉取朋友圈：
	public static final String listfc = SERVER_IP+"/listfc";
	//朋友圈点赞：
	public static final String likefc = SERVER_IP+"/likefc";
	//头像昵称
	public static final String dvsinfo = SERVER_IP+"/dvsinfo";
	//检查朋友圈是否有更新
	public static final String checkfc = SERVER_IP+"/checkfc";

}
