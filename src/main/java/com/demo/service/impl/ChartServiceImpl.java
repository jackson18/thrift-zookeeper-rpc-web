package com.demo.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.demo.service.ChartService;

@Service
public class ChartServiceImpl implements ChartService {
	
	private static final String ROOT = "/opt/monitor/charts";

	@Override
	public File[] getServices(String date) {
		File rootFile = new File(ROOT);
		if ((StringUtils.isNotEmpty(date)) && (rootFile.isDirectory())) {
			File[] dateFiles = rootFile.listFiles();
			for (File dateFile : dateFiles) {
				if ((!dateFile.getName().equals(date)) || (!dateFile.isDirectory())) {
					continue;
				}
				File[] serviceFiles = dateFile.listFiles();
				return serviceFiles;
			}
		}
		return null;
	}

	@Override
	public File[] getMethods(String date, String service) {
		File rootFile = new File(ROOT);
		if ((StringUtils.isNotEmpty(date)) && (StringUtils.isNotEmpty(service)) && (rootFile.isDirectory())) {
			File[] dateFiles = rootFile.listFiles();
			for (File dateFile : dateFiles) {
				if ((!dateFile.getName().equals(date)) || (!dateFile.isDirectory()))
					continue;
				File[] serviceFiles = dateFile.listFiles();
				for (File serviceFile : serviceFiles) {
					if ((!serviceFile.getName().equals(service)) || (!serviceFile.isDirectory()))
						continue;
					File[] methodFiles = serviceFile.listFiles();
					return methodFiles;
				}
			}
		}
		return null;
	}

	@Override
	public byte[] getChartByte(String date, String service, String method, String type) {
		File chartFile = getChartFile(date, service, method, type);
	    return getBytes(chartFile);
	}
	
	public File getChartFile(String date, String service, String method, String type) {
		File rootFile = new File("/opt/monitor/charts");
		if ((StringUtils.isNotEmpty(date)) && (StringUtils.isNotEmpty(service)) && (StringUtils.isNotEmpty(method))
				&& (StringUtils.isNotEmpty(type)) && (rootFile.isDirectory())) {
			File[] dateFiles = rootFile.listFiles();
			for (File dateFile : dateFiles) {
				if ((!dateFile.getName().equals(date)) || (!dateFile.isDirectory()))
					continue;
				File[] serviceFiles = dateFile.listFiles();
				for (File serviceFile : serviceFiles) {
					if ((!serviceFile.getName().equals(service)) || (!serviceFile.isDirectory()))
						continue;
					File[] methodFiles = serviceFile.listFiles();
					for (File methodFile : methodFiles) {
						if ((!methodFile.getName().equals(method)) || (!methodFile.isDirectory()))
							continue;
						File[] typeFiles = methodFile.listFiles();
						for (File typeFile : typeFiles) {
							if (typeFile.getName().equals(type)) {
								return typeFile;
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	public static byte[] getBytes(File file) {
		byte[] buffer = null;
		try {
			if (file != null) {
				FileInputStream fis = new FileInputStream(file);
				ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
				byte[] b = new byte[1000];
				int n;
				while ((n = fis.read(b)) != -1) {
					bos.write(b, 0, n);
				}
				fis.close();
				bos.close();
				buffer = bos.toByteArray();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

}
