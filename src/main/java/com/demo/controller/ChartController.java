package com.demo.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.demo.service.ChartService;

@Controller
@RequestMapping("/chart/")
public class ChartController {
	
	@Autowired
	private ChartService chartService;

	@RequestMapping(value="index")
	public String list(Model model) {
		return "chart/index";
	}
	
	@RequestMapping(value="show")
	public String show(@RequestParam(value = "date") String date, 
						@RequestParam(value = "service") String service, 
						@RequestParam(value = "method") String method, 
						Model model) {
		if ((StringUtils.isEmpty(date)) || (StringUtils.isEmpty(service))
				|| (StringUtils.isEmpty(method))) {
			model.addAttribute("result", false);
		} else {
			model.addAttribute("result", true);
		}
		model.addAttribute("date", date);
		model.addAttribute("service", service);
		model.addAttribute("method", method);
		return "chart/show";
	}
	
	@RequestMapping(value = "getServices")
	public String getServices(@RequestParam(value = "date") String date, HttpServletResponse response) {
		String result = getServicesToString(date);
		if (result != null) {
			try {
				PrintWriter writer = response.getWriter();
				writer.print(result);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@RequestMapping(value = "getMethods")
	public String getMethods(@RequestParam(value = "date") String date, 
							@RequestParam(value = "service") String service, 
							HttpServletResponse response) {
		String result = getMethodsToString(date, service);
		if (result != null) {
			try {
				PrintWriter writer = response.getWriter();
				writer.print(result);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@RequestMapping(value = "showChart")
	public String showChart(@RequestParam(value = "date") String date, 
							@RequestParam(value = "service") String service, 
							@RequestParam(value = "method") String method, 
							@RequestParam(value = "type") String type, 
							HttpServletResponse response) {
		try {
			byte[] data = chartService.getChartByte(date, service, method, type);
			if (data != null) {
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
	
	public String getMethodsToString(String date, String service) {
		StringBuffer sb = new StringBuffer();
		File[] methods = chartService.getMethods(date, service);
		if (methods != null) {
			for (File file : methods) {
				if (!sb.toString().equals("")) {
					sb.append(",");
				}
				sb.append(file.getName());
			}
		}
		return sb.toString();
	}
	
	
	public String getServicesToString(String date) {
		StringBuffer sb = new StringBuffer();
		File[] services = chartService.getServices(date);
		if (services != null) {
			for (File file : services) {
				if (!sb.toString().equals("")) {
					sb.append(",");
				}
				sb.append(file.getName());
			}
		}
		return sb.toString();
	}
	
}
