package com.demo.handler;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.model.Statistic;
import com.demo.service.StatisticService;


/**
 * ========================================================
 * 日 期：2016年5月5日 下午5:41:13
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
@Component
public class StatisticHandler {
	
	private static final Logger log = LoggerFactory.getLogger(StatisticHandler.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private final BlockingQueue<Statistic> queue = new LinkedBlockingQueue<Statistic>(100000);
	private volatile List<Statistic> sList = new ArrayList<Statistic>(1000);
	
	@Autowired
	private StatisticService statisticService;
	
	
	public StatisticHandler() {
		new Thread(new Runnable() {
            public void run() {
                try {
                	while (true) {
                		insertBatch(); //批量写入
                	}
                } catch (Throwable t) { // 防御性容错
                    try {
						Thread.sleep(5000); // 失败延迟
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                }
            }
        }).start();
	}
	
	/**
	 * 批量插入
	 */
	public void insertBatch() {
		try {
			Statistic entity = queue.take();
			sList.add(entity);
			if (sList.size() >= 50) {
				//批量插入数据库
				log.info("批量插入数据库成功:{}", sList.size());
				statisticService.addBatch(sList);
				sList.clear();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 放入队列，延时插入
	 * @param entity
	 */
	public void insertDelay(Statistic entity) {
		queue.offer(entity);
	}
	
	/**
	 * 即时插入
	 * @param entity
	 */
	public void insert(Statistic entity) {
		statisticService.insert(entity);
	}
	
	/**
	 * 获取表格字节数组
	 * @param startTime
	 * @param endTime
	 * @param service
	 * @param method
	 * @param type
	 * @return
	 */
	public byte[] getChartByte(String startTime, String endTime, String service, String method, String type) {
		return statisticService.getChartByte(startTime, endTime, service, method, type);
	}
	
	/**
	 * 查询指定日期的所有服务
	 * @param map
	 * @return
	 */
	public String getServices(Map<String, Object> map) {
		StringBuilder sb = new StringBuilder();
		List<Statistic> list = statisticService.getServices(map);
		for (Statistic s : list) {
			if (!sb.toString().equals("")) {
				sb.append(",");
			}
			sb.append(s.getService());
			
		}
		return sb.toString();
	}
	
	/**
	 * 查询指定日期，服务下的所有方法
	 * @param map
	 * @return
	 */
	public String getMethods(Map<String, Object> map) {
		StringBuilder sb = new StringBuilder();
		List<Statistic> list = statisticService.getMethods(map);
		for (Statistic s : list) {
			if (!sb.toString().equals("")) {
				sb.append(",");
			}
			sb.append(s.getMethod());
		}
		return sb.toString();
	}
	
	/**
	 * 构建image
	 * @param list
	 * @param startTime
	 * @param endTime
	 * @param service
	 * @param method
	 * @param type
	 * @return
	 */
	public static final BufferedImage buildImage(List<Statistic> list, String startTime, String endTime, String service, String method, String type) {
		BufferedImage image = null;
		Map<String, Long> data = new HashMap<String, Long>();
    	double[] summary = new double[4];
		//创建表格视图
		if ("success".equals(type)) {
			appendSuccessData(list, data, summary);
			image = createImage("ms", service, method, "request for time", data, summary);
		} else {
			appendConcurrentData(list, data, summary);
			image = createImage("count", service, method, "concurrent for time", data, summary);
		}
		return image;
	}
	
	/**
	 * 计算最大，最小，平均，及请求次数等数据
	 * @param file
	 * @param data
	 * @param summary
	 */
	private static final void appendSuccessData(List<Statistic> list, Map<String, Long> data, double[] summary) {
		if (list != null && list.size() > 0) {
			int sum = 0;
            int cnt = 0;
            
			for (Statistic s : list) {
				String key = sdf.format(s.getCreateTime());
				long value = s.getTime();
				if (!data.containsKey(key)) {
                	data.put(key, value);
                } else {
                	long maxVal = Math.max(data.get(key), value);
                	data.put(key, maxVal);
                }
				summary[0] = Math.max(summary[0], value);
                summary[1] = summary[1] == 0 ? value : Math.min(summary[1], value);
                sum += value;
                cnt ++;
			}
			
			summary[3] = cnt;
            summary[2] = sum / cnt;
		}
    }
	
	/**
	 * 创建表格视图
	 * @param key
	 * @param service
	 * @param method
	 * @param type
	 * @param data
	 * @param summary
	 * @return
	 */
	private static final BufferedImage createImage(String key, String service, String method, String type, Map<String, Long> data, double[] summary) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DecimalFormat numberFormat = new DecimalFormat("###,##0.##");
        TimeSeriesCollection xydataset = new TimeSeriesCollection();
        TimeSeries timeseries = new TimeSeries(type);
        for (Map.Entry<String, Long> entry : data.entrySet()) {
                try {
					timeseries.add(new Minute(dateFormat.parse(entry.getKey())), entry.getValue());
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
        }
        xydataset.addSeries(timeseries);
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(
                "max: " + numberFormat.format(summary[0]) + (summary[1] >=0 ? " min: " + numberFormat.format(summary[1]) : "") 
                + " avg: " + numberFormat.format(summary[2]) + (summary[3] >=0 ? " num: " + numberFormat.format(summary[3]) : ""), 
                toDisplayService(service) + "  " + method, key, xydataset, true, true, false);
        jfreechart.setBackgroundPaint(Color.WHITE);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        xyplot.setBackgroundPaint(Color.WHITE);
        xyplot.setDomainGridlinePaint(Color.GRAY);
        xyplot.setRangeGridlinePaint(Color.GRAY);
        xyplot.setDomainGridlinesVisible(true);
        xyplot.setRangeGridlinesVisible(true);
        DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
        dateaxis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));
        BufferedImage image = jfreechart.createBufferedImage(1500, 350);
        return image;
    }
	
	/**
	 * 计算最大，最小，平均，及请求次数等数据
	 * @param file
	 * @param data
	 * @param summary
	 */
	private static final void appendConcurrentData(List<Statistic> list, Map<String, Long> data, double[] summary) {
		if (list != null && list.size() > 0) {
			int sum = 0;
            int cnt = 0;
            
			for (Statistic s : list) {
				String key = sdf.format(s.getCreateTime());
				long value = s.getConcurrent();
				if (!data.containsKey(key)) {
                	data.put(key, value);
                } else {
                	long maxVal = Math.max(data.get(key), value);
                	data.put(key, maxVal);
                }
				summary[0] = Math.max(summary[0], value);
                summary[1] = summary[1] == 0 ? value : Math.min(summary[1], value);
                sum += value;
                cnt ++;
			}
			
			summary[3] = cnt;
            summary[2] = sum / cnt;
		}
    }
	
	/**
	 * @param service
	 * @return
	 */
	private static final String toDisplayService(String service) {
        int i = service.lastIndexOf('.');
        if (i >= 0) {
            return service.substring(i + 1);
        }
        return service;
    }
	
	/** 
     * 获得指定文件的byte数组 
     */  
    public static final byte[] getBytes(BufferedImage image){  
    	byte[] data = null;
    	try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();  
			ImageIO.write(image, "png", out);  
			data = out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}  
         return data;
    }
	
}

