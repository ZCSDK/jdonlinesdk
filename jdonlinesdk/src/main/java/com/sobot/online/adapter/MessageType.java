package com.sobot.online.adapter;

public interface MessageType {

	// user(用户)：0。robot(机器人)：1。costom(客服)：2

	/* 文本消息 */
	int Message_Robot_Text = 0;
	int Message_Costom_Text = 1;
	int Message_User_Text = 2;

	/* 图片的消息 */
	int Message_Robot_Image = 3;
	int Message_Costom_Image = 4;
	int Message_User_Image = 5;

	/* 语音的消息 */
	int Message_Robot_Voice = 6;
	int Message_Costom_Voice = 7;
	int Message_User_Voice = 8;

	/* gif 类型的消息 */
	int Message_GIF = 9;

	/* 提醒消息类型 */
	int Message_Remind = 10;
	/* 富文本 */
	int Message_Robot_TextandImg = 11;/* 机器人的富文本 */
	int Message_Costom_TextandImg = 12;/* 客服的富文本 */
	int Message_User_TextandImg = 13;/* 用户的富文本 */

	/* 实时推送的轨迹消息 */
	int Message_VISIT_TRAIL = 14;

	/*机器人  多轮会话模板 1 */
	int MSG_TYPE_ROBOT_TEMPLATE1 = 15;
	/*机器人  多轮会话模板 2*/
	int MSG_TYPE_ROBOT_TEMPLATE2 = 16;
	/*机器人  多轮会话模板 3*/
	int MSG_TYPE_ROBOT_TEMPLATE3 = 17;
	/*机器人  多轮会话模板 4*/
	int MSG_TYPE_ROBOT_TEMPLATE4 = 18;
	/*机器人  多轮会话模板 5  无模版情况下显示的view*/
	int MSG_TYPE_ROBOT_TEMPLATE5 = 19;
	/*多轮会话  右边类型 */
	int MSG_TYPE_MULTI_ROUND_R = 20;

	int Message_TYPE_FILE_R = 21;//发送的文件消息
	int Message_TYPE_FILE_L = 22;//收到的文件消息
	int MESSAGE_TYPE_LOCATION_L = 23;//收到的位置消息
	int MESSAGE_TYPE_VIDEO_L = 24;//收到的视频消息


	int MESSAGE_TYPE_SENSITIVE = 25;//敏感词类型的文本消息
	/* 主动邀请评价 */
	int Message_Remind_Evaluate = 26;

	/**
	 * 商品卡片右侧
	 */
	int MSG_TYPE_CARD_R = 27;
	/**
	 * 订单卡片右侧消息
	 */
	int MSG_TYPE_ROBOT_ORDERCARD_R = 28;
	/**
	 * 订单卡片左侧侧消息
	 */
	int MSG_TYPE_ROBOT_ORDERCARD_L = 29;
	/**
	 * 商品卡片左侧
	 */
	int MSG_TYPE_CARD_L = 30;

}