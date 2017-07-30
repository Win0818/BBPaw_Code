package com.worldchip.bbp.bbpawmanager.cn.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.worldchip.bbp.bbpawmanager.cn.model.GrowthMessage;
import com.worldchip.bbp.bbpawmanager.cn.model.Information;

public class TestDataUtils {

	private static final String[] GROWUP_DATA_ARR = {"2015.04.16", "2015.04.17",
			"2015.04.18", "2015.04.19", "2015.04.20", "2015.04.21",
			"2015.04.22", "2015.04.23", "2015.04.24", "2015.04.25",
			"2015.04.26", "2015.04.27"};
	
	private static final String[] STUDY_TIME_ARR = {"10:30 - 11:30","13:30 - 14:30","15:30 - 17:00"};
	
	/**
	 * 成长日志测试数据
	 * 
	 * @param from
	 * @param to
	 * @param clear
	 */
	public static List<GrowthMessage> generateGrowUpDatas() {
		List<GrowthMessage> dataList = new ArrayList<GrowthMessage>();
		for (int i=0; i < GROWUP_DATA_ARR.length; i++) {
			for (int j=0; j < STUDY_TIME_ARR.length; j++) {
				GrowthMessage growupMessage = new GrowthMessage();
				growupMessage.setDate(GROWUP_DATA_ARR[i]);
				growupMessage.setStudyConetnt(GROWUP_DATA_ARR[i] + " --- "+STUDY_TIME_ARR[j] +" 学习内容!");
				growupMessage.setStudyTime(STUDY_TIME_ARR[j]);
				growupMessage.setIconPath("");
				dataList.add(growupMessage);
			}
		}
		return dataList;
	}

	/**
	 * 专家资讯测试数据
	 * 
	 * @param from
	 * @param to
	 * @param clear
	 */
	public static List<Information> generateInformationDatas() {
		List<Information> dataList = new ArrayList<Information>();
		for (int i=0; i < 20; i++) {
			Random random = new Random();
			Information inform = new Information();
			inform.setIcon("");
			inform.setMsgType(random.nextInt(4));
			inform.setTitle("Title --- "+i);
			inform.setContent("测试数据 --- >>>>>>>>>>>" +"第 "+i +" 条!" +" 类型: "+inform.getMsgType());
			inform.setRead(random.nextBoolean());
			inform.setDate("20:2"+i);
			dataList.add(inform);
		}
		return dataList;
	}
	
	public static final String mainTestText = " <p> <span style=\"color:#444444;\">你说什么啊"
	+"天才少年萧炎在创造了家族空前绝后的修炼纪录后突然成了废人，整整三年时间，家族冷遇，旁人轻视，被未婚妻退婚……种种打击接踵而至。就在他即将绝望的时候，一缕幽魂从他手上的戒指里浮现，一扇全新的大门在面前开启！这里是属于斗气的世界，没有花俏艳丽的魔法，有的，仅仅是繁衍到巅峰的斗气！"
	+"天才少年萧炎在创造了家族空前绝后的修炼纪录后突然成了废人，整整三年时间，家族冷遇，旁人轻视，被未婚妻退婚……种种打击接踵而至。就在他即将绝望的时候，一缕幽魂从他手上的戒指里浮现，一扇全新的大门在面前开启！这里是属于斗气的世界，没有花俏艳丽的魔法，有的，仅仅是繁衍到巅峰的斗气！"
	+"天才少年萧炎在创造了家族空前绝后的修炼纪录后突然成了废人，整整三年时间，家族冷遇，旁人轻视，被未婚妻退婚……种种打击接踵而至。就在他即将绝望的时候，一缕幽魂从他手上的戒指里浮现，一扇全新的大门在面前开启！这里是属于斗气的世界，没有花俏艳丽的魔法，有的，仅仅是繁衍到巅峰的斗气！"
	+"天才少年萧炎在创造了家族空前绝后的修炼纪录后突然成了废人，整整三年时间，家族冷遇，旁人轻视，被未婚妻退婚……种种打击接踵而至。就在他即将绝望的时候，一缕幽魂从他手上的戒指里浮现，一扇全新的大门在面前开启！这里是属于斗气的世界，没有花俏艳丽的魔法，有的，仅仅是繁衍到巅峰的斗气！"
	+"</span></p>";
	
	public static final String mainTestText1 = " <p style=\"text-align:center;\"> "+
			"<span style=\"color:#444444;font-family:'Crimson Text', 'Times New Roman', Times, "+
			"serif;font-size:24px;line-height:30.2399997711182px;white-space:normal;background-color:#FFFFFF;\"> "+
			" <strong>默认功能 日期选择器</strong> "+
			" </span></p>";
}
