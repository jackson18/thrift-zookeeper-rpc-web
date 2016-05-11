package com.demo.service.impl;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.dao.StatisticDao;
import com.demo.handler.StatisticHandler;
import com.demo.model.Statistic;
import com.demo.service.StatisticService;

/**
 * ========================================================
 * 日 期：2016年5月5日 下午5:43:04
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
@Service
public class StatisticServiceImpl implements StatisticService {
	
	
	@Autowired
	private StatisticDao statisticDao;

	
	@Override
	public void addBatch(List<Statistic> list) {
		statisticDao.addBatch(list);
	}
	
	@Override
	public void insert(Statistic entity) {
		statisticDao.insert(entity);
	}

	@Override
	public List<Statistic> getStatisticByQuery(Map<String, Object> map) {
		return statisticDao.getStatisticByQuery(map);
	}

	@Override
	public List<Statistic> getServices(Map<String, Object> map) {
		return statisticDao.getServices(map);
	}
	
	@Override
	public List<Statistic> getMethods(Map<String, Object> map) {
		return statisticDao.getMethods(map);
	}
	
	@Override
	public void deleteByDay() {
		statisticDao.deleteByDay();
	}

	@Override
	public byte[] getChartByte(String startTime, String endTime, String service, String method, String type) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		map.put("service", service);
		map.put("method", method);
		//从数据库中获取相应数据
		List<Statistic> list = statisticDao.getStatisticByQuery(map);
		//生成统计表格
		BufferedImage image = StatisticHandler.buildImage(list, startTime, endTime, service, method, type);
		return StatisticHandler.getBytes(image);
	}

}
