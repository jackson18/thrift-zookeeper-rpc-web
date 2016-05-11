package com.demo.trigger;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.service.StatisticService;

/**
 * ========================================================
 * 日 期：2016年5月11日 下午2:10:51
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
@Service
public class CleanTrigger {
	
	private static final Logger log = LoggerFactory.getLogger(CleanTrigger.class);
	@Autowired
	private StatisticService statisticService;
	
	public void execute() {
		log.info("定时清理任务开始,时间:{}", Calendar.getInstance().getTime());
		statisticService.deleteByDay();
		log.info("定时清理任务结束,时间:{}", Calendar.getInstance().getTime());
	}

	public StatisticService getStatisticService() {
		return statisticService;
	}

	public void setStatisticService(StatisticService statisticService) {
		this.statisticService = statisticService;
	}
	
}
