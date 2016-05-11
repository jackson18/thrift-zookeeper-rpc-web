package com.demo.service;

import java.io.File;

/**
 * ========================================================
 * 日 期：2016年5月11日 下午2:10:41
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public interface ChartService {

	public File[] getServices(String date);

	public File[] getMethods(String date, String service);

	public byte[] getChartByte(String date, String service, String method, String type);
	
}
