package com.its.http.server.abstracthandle;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.its.http.server.model.BussinessNodeModel;
import com.its.http.server.model.ForwardModel;
import com.its.http.server.model.HttpExchangeModel;
import com.its.http.server.model.ResponseModel;
import com.its.http.server.utils.HttpMethod;
import com.its.http.server.utils.HttpReturnCode;
import com.its.http.server.utils.HttpTrasferUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class AbstractHandle implements HttpHandler {
	private final static Logger log = Logger.getLogger(AbstractHandle.class);

	protected int httpTimeout = 120 * 1000;
	protected ForwardModel forwardModel = null;
	protected List<BussinessNodeModel> bussinessNodeModels = null;

	/*
	 * soap request queue
	 */
	protected LinkedBlockingQueue<HttpExchangeModel> soapRequestQueue;

	public AbstractHandle(int httpTimeout, ForwardModel forwardModel, List<BussinessNodeModel> bussinessNodeModels,
			LinkedBlockingQueue<HttpExchangeModel> soapRequestQueue) {
		this.httpTimeout = httpTimeout;
		this.forwardModel = forwardModel;
		this.bussinessNodeModels = bussinessNodeModels;
		this.soapRequestQueue = soapRequestQueue;
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		String requestMethod = httpExchange.getRequestMethod();
		String requestState = HttpReturnCode.HTPP_NO_ANY_IMPL;

		log.info("http found request method " + requestMethod + " from address " + httpExchange.getRemoteAddress()
				+ ", url " + httpExchange.getRequestURI().getPath());
		try {
			if (this.soapRequestQueue != null) {
				if (requestMethod.equals(HttpMethod.POST)) {
					log.info("http POST request handle");
					requestState = handlePOST(httpExchange);
				} else {
					log.info("http not found url request handle");
					requestState = HttpReturnCode.HTPP_404_NOTFOUND_IMPL;
					HttpTrasferUtil.sendResponse(httpExchange, "", HttpReturnCode.HTPP_404_NOTFOUND);
				}
				return;
			}
			switch (requestMethod) {
			case HttpMethod.GET:
				log.info("http GET request handle");
				requestState = handleGET(httpExchange);
				break;
			case HttpMethod.POST:
				log.info("http POST request handle");
				requestState = handlePOST(httpExchange);
				break;
			case HttpMethod.UPDATE:
				log.info("http UPDATE request handle");
				requestState = handleUPDATE(httpExchange);
				break;
			case HttpMethod.DELETE:
				log.info("http DELETE request handle");
				requestState = handleDELETE(httpExchange);
				break;
			case HttpMethod.OPTIONS:
				log.info("http OPTIONS request handle");
				requestState = handleOPTIONS(httpExchange);
				break;
			default:
				log.info("http not found url request handle");
				requestState = HttpReturnCode.HTPP_404_NOTFOUND_IMPL;
				HttpTrasferUtil.sendResponse(httpExchange, "", HttpReturnCode.HTPP_404_NOTFOUND);
				break;
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			requestState = HttpReturnCode.HTTP_FAILED;
			log.error("http error request process", exp);
		} finally {
			log.info("http finished request, state " + requestState);
			httpExchange.close();
		}
	}

	protected String handleGET(HttpExchange httpExchange) throws Exception {
		return HttpReturnCode.HTPP_NO_ANY_IMPL;
	}

	protected String handlePOST(HttpExchange httpExchange) throws Exception {
		return HttpReturnCode.HTPP_NO_ANY_IMPL;
	}

	protected String handleUPDATE(HttpExchange httpExchange) throws Exception {
		return HttpReturnCode.HTPP_NO_ANY_IMPL;
	}

	protected String handleDELETE(HttpExchange httpExchange) throws Exception {
		return HttpReturnCode.HTPP_NO_ANY_IMPL;
	}

	protected String handleOPTIONS(HttpExchange httpExchange) throws Exception {
		return HttpReturnCode.HTPP_NO_ANY_IMPL;
	}

	protected ResponseModel handleRequest(String method, String data, Headers requestHeaders, Headers responseHeaders)
			throws Exception {
		String resObject = null;
		HttpURLConnection http = null;
		InputStream in = null;
		ResponseModel res = new ResponseModel();
		try {
			log.info("starting forward from link " + this.forwardModel.getContextPathFrom() + ", to link "
					+ this.forwardModel.getContextPathTo() + ", method " + method);
			String urlRequest = loadRequestRandom();
			log.info("request to url  " + urlRequest);

			if ("".equals(urlRequest)) {
				throw new Exception("url request not found any node alive");
			}
			URL url = new URL(urlRequest);
			URLConnection con = url.openConnection();

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
	private String loadRequestRandom() {
		String urlRequest = "";

		if (this.bussinessNodeModels == null || bussinessNodeModels.size() == 0) {
			return urlRequest;
		}
		List<BussinessNodeModel> bussinessNodeModelsTemp = new ArrayList<BussinessNodeModel>();

		for (BussinessNodeModel bu : this.bussinessNodeModels) {
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
		String link = this.forwardModel.getContextPathTo();

		if (!link.startsWith("/")) {
			link = "/" + link;
		}
		urlRequest = bussinessNodeModelsTemp.get(index).getHttpRequest() + link;
		return urlRequest;
	}
}
