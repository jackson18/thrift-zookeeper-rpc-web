package com.demo.service;

import java.util.List;
import java.util.Map;

import com.demo.model.Statistic;


/**
 * ========================================================
 * 日 期：2016年5月5日 下午5:43:18
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public interface StatisticService {
	
	public void addBatch(List<Statistic> list);
	
	public void insert(Statistic entity);

	public List<Statistic> getStatisticByQuery(Map<String, Object> map);
	
	public List<Statistic> getServices(Map<String, Object> map);
	
	public List<Statistic> getMethods(Map<String, Object> map);
	
	public void deleteByDay();
	
	public byte[] getChartByte(String startTime, String endTime, String service, String method, String type);
	
}
