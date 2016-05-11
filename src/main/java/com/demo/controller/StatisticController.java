package com.demo.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.demo.handler.StatisticHandler;
import com.demo.handler.VisualHandler;
import com.demo.model.Statistic;
import com.demo.service.StatisticService;

@Controller
@RequestMapping("/statistic/")
public class StatisticController {
	
	@Autowired
	private StatisticService statisticService;
	@Autowired
	private StatisticHandler statisticHandler;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	/**
	 * 监控首页
	 * @return
	 */
	@RequestMapping(value="index")
	public String index() {
		return "statistic/index";
	}
	
	/**
	 * 跳转到show.jsp页面
	 * @return
	 */
	@RequestMapping(value="show")
	public String show(@RequestParam(value = "startTime") String startTime, 
					@RequestParam(value = "endTime") String endTime, 
					@RequestParam(value = "service") String service, 
					@RequestParam(value = "method") String method, 
					Model model) {
		if (StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime) || StringUtils.isEmpty(service) 
				|| StringUtils.isEmpty(method)) {
			model.addAttribute("result", false);
		} else {
			model.addAttribute("result", true);
		}
		model.addAttribute("startTime", startTime);
		model.addAttribute("endTime", endTime);
		model.addAttribute("service", service);
		model.addAttribute("method", method);
		return "statistic/show";
	}
	
	/**
	 * 显示监控表格
	 * @return
	 */
	@RequestMapping(value="showChart")
	public String showChart(@RequestParam(value = "startTime") String startTime, 
						@RequestParam(value = "endTime") String endTime, 
						@RequestParam(value = "service") String service, 
						@RequestParam(value = "method") String method, 
						@RequestParam(value = "type") String type, 
						Model model, HttpServletResponse response) {
		try {
			//获取图片数据
			byte[] data = getChartByte(startTime, endTime, service, method, type);
			if (data != null) {
				//输出到页面
				response.setContentType("multipart/form-data");
				ServletOutputStream out = response.getOutputStream();
				out.write(data);
				out.flush();
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取所有rpc服务
	 * @return
	 */
	@RequestMapping(value="getServices")
	public String getServices(@RequestParam(value = "startTime") String startTime, 
							@RequestParam(value = "endTime") String endTime,
							HttpServletResponse response) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		String result = statisticHandler.getServices(map);
		if (result != null) {
			output(result, response);
		}
		return null;
	}
	
	/**
	 * 获取所有rpc服务对应的方法
	 * @return
	 */
	@RequestMapping(value="getMethods")
	public String getMethods(@RequestParam(value = "startTime") String startTime, 
						@RequestParam(value = "endTime") String endTime,
						@RequestParam(value = "service") String service,
						HttpServletResponse response) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		map.put("service", service);
		String result = statisticHandler.getMethods(map);
		if (result != null) {
			output(result, response);
		}
		return null;
	}
	
	/**
	 * rpc统计数据持久化
	 * rpc服务需要请求此接口
	 * @return
	 */
	@RequestMapping(value="add")
	public String addStatistic(@RequestParam(value = "service") String service, 
			@RequestParam(value = "method") String method,
			@RequestParam(value = "time") long time,
			@RequestParam(value = "concurrent") int concurrent,
			@RequestParam(value = "createTime") String createTime,
			@RequestParam(value = "isError") int isError,
			HttpServletResponse response) {
		try {
			if (StringUtils.isNotEmpty(createTime)) {
				Date createDate = sdf.parse(createTime);
				Statistic entity = new Statistic(service, method, time, concurrent, createDate, isError);
				statisticHandler.insertDelay(entity);
			}
		} catch (ParseException e) {
			System.out.println("createTime: " +createTime);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 输出工具方法
	 * @param msg
	 */
	private void output(String msg, HttpServletResponse response) {
		try {
			PrintWriter writer = response.getWriter();  
			writer.print(msg);  
			writer.flush();  
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] getChartByte(String startTime, String endTime, String service, String method, String type) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		map.put("service", service);
		map.put("method", method);
		//从数据库中获取相应数据
		List<Statistic> list = statisticService.getStatisticByQuery(map);
		//生成统计表格
		BufferedImage image = VisualHandler.buildImage(list, startTime, endTime, service, method, type);
		return VisualHandler.getBytes(image);
	}
	
}
