package com.demo.dao;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.demo.model.Statistic;


/**
 * ========================================================
 * 日 期：2016年5月5日 下午5:43:32
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
@Transactional(propagation=Propagation.REQUIRES_NEW,readOnly=false,isolation=Isolation.DEFAULT)
public interface StatisticDao {
	
	public void addBatch(List<Statistic> list);
	
	public void insert(Statistic entity);

	public List<Statistic> getStatisticByQuery(Map<String, Object> map);
	
	public List<Statistic> getServices(Map<String, Object> map);
	
	public List<Statistic> getMethods(Map<String, Object> map);
	
	public void deleteByDay();
	
}
