package com.its.http.server.main;

import java.io.File;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;

import com.its.http.server.listener.HttpServerListener;
import com.its.http.server.utils.ToolUtil;

public class HttpServerAppManager {
	private static String APP_CONFIG_FILE = "config/app.json";

	public static void main(String[] args) {
		HttpServerListener httpServerListener = null;
		try {
			ObjectMapper omp = new ObjectMapper();
			HashMap appConfig = omp.readValue(new File(APP_CONFIG_FILE), HashMap.class);

			httpServerListener = new HttpServerListener(ToolUtil.loadConfigFromHashMap(appConfig));
			httpServerListener.start();
		} catch (Exception exp) {
			exp.printStackTrace();
			if (httpServerListener != null) {
				httpServerListener.stop();
			}
		}
	}
}
