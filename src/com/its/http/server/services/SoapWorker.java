package com.its.http.server.services;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.its.http.server.model.BussinessNodeModel;
import com.its.http.server.model.HttpExchangeModel;
import com.its.http.server.model.ResponseModel;
import com.its.http.server.utils.HttpMethod;
import com.its.http.server.utils.HttpReturnCode;
import com.its.http.server.utils.HttpTrasferUtil;
import com.sun.net.httpserver.Headers;

public class SoapWorker implements Runnable {
	private final static Logger log = Logger.getLogger(SoapWorker.class);
	private HttpExchangeModel exchangeModel = null;

	public SoapWorker(HttpExchangeModel exchangeModel) {
		this.exchangeModel = exchangeModel;
	}

	@Override
	public void run() {
		try {
			log.info("starting handle soap request");
			Headers requestHeaders = this.exchangeModel.getHttpExchange().getRequestHeaders();
			Headers responseHeaders = new Headers();
			ResponseModel res = handleRequest(HttpMethod.POST, requestHeaders, responseHeaders);
			String dataResponse = "";
			int responseCode = res.getReponseCode();

			if (responseCode == HttpReturnCode.HTTP_OK) {
				dataResponse = res.getPayload() == null ? "" : (String) res.getPayload();
			} else {
				dataResponse = res.getMessage() == null ? "" : (String) res.getMessage();
			}
			HttpTrasferUtil.sendResponse(this.exchangeModel.getHttpExchange(), dataResponse, responseCode);
		} catch (Exception exp) {
			log.error("error process soap request", exp);
		} finally {
			log.info("finished handle soap request");
		}
	}

	private ResponseModel handleRequest(String method, Headers requestHeaders, Headers responseHeaders)
			throws Exception {
		String resObject = null;
		HttpURLConnection http = null;
		InputStream in = null;
		ResponseModel res = new ResponseModel();
		try {
			String data = this.exchangeModel.getRequestBody();
			String urlRequest = this.exchangeModel.getHttpExchange().getRequestURI().getPath();
			log.info("starting forward from link " + urlRequest + ", to link " + urlRequest + ", method " + method);
			String urlFullRequest = loadRequestRandom(urlRequest);
			log.info("request to url  " + urlRequest);

			if ("".equals(urlFullRequest)) {
				throw new Exception("url request not found any node alive");
			}
			URL url = new URL(urlFullRequest);
			URLConnection con = url.openConnection();
			int httpTimeout = this.exchangeModel.getHttpTimeout();

			http = (HttpURLConnection) con;
			http.setConnectTimeout(httpTimeout);
			http.setReadTimeout(httpTimeout);
			http.setRequestMethod(method); // PUT is another valid option
			http.setDoOutput(true);
			if (requestHeaders.containsKey("Expect")) {
				requestHeaders.remove("Expect");
			}
			Iterator<String> keys = requestHeaders.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				http.setRequestProperty(key, requestHeaders.getFirst(key));
			}
			if (data != null) {
				http.getOutputStream().write(data.getBytes());
				http.getOutputStream().flush();
			}
			int responseCode = http.getResponseCode();

			responseHeaders.putAll(http.getHeaderFields());
			int contentLength = http.getContentLength();

			if (responseCode == HttpReturnCode.HTTP_OK) {
				in = http.getInputStream();
				resObject = HttpTrasferUtil.readRequest(in, contentLength);
				res.setPayload(resObject);
			} else {
				in = http.getInputStream();
				if (contentLength > 0) {
					resObject = HttpTrasferUtil.readRequest(in, contentLength);
					res.setMessage(resObject);
				}
			}
			responseHeaders.remove(null);
			res.setReponseCode(responseCode);
			log.info(resObject);
		} catch (Exception exp) {
			resObject = exp.getMessage();
			log.error(resObject, exp);
			throw exp;
		} finally {
			if (in != null) {
				in.close();
				in = null;
			}
			if (http != null) {
				http.disconnect();
			}
		}
		return res;
	}

	// private
	private String loadRequestRandom(String link) {
		String urlRequest = "";
		List<BussinessNodeModel> bussinessNodeModels = this.exchangeModel.getBussinessNodeModels();

		if (bussinessNodeModels == null || bussinessNodeModels.size() == 0) {
			return urlRequest;
		}
		List<BussinessNodeModel> bussinessNodeModelsTemp = new ArrayList<BussinessNodeModel>();

		for (BussinessNodeModel bu : bussinessNodeModels) {
			if (bu.isAlive()) {
				bussinessNodeModelsTemp.add(bu);
			}
		}
		int bussinessSize = bussinessNodeModelsTemp.size();

		if (bussinessSize == 0) {
			return "";
		}
		Random random = new Random();
		int index = random.nextInt(bussinessSize);
		if (!link.startsWith("/")) {
			link = "/" + link;
		}
		urlRequest = bussinessNodeModelsTemp.get(index).getHttpRequest() + link;
		return urlRequest;
	}

}
