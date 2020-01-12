package com.its.http.server.handle;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.its.http.server.abstracthandle.AbstractHandle;
import com.its.http.server.model.BussinessNodeModel;
import com.its.http.server.model.ForwardModel;
import com.its.http.server.model.HttpExchangeModel;
import com.its.http.server.model.ResponseModel;
import com.its.http.server.utils.HttpMethod;
import com.its.http.server.utils.HttpReturnCode;
import com.its.http.server.utils.HttpTrasferUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class HandleHttpRequest extends AbstractHandle {

	public HandleHttpRequest(int httpTimeout, ForwardModel forwardModel, List<BussinessNodeModel> bussinessNodeModels,
			LinkedBlockingQueue<HttpExchangeModel> soapRequestQueue) {
		super(httpTimeout, forwardModel, bussinessNodeModels, soapRequestQueue);
	}

	@Override
	protected String handleGET(HttpExchange httpExchange) throws Exception {
		String requestState = HttpReturnCode.HTTP_SUCCESSED;

		try {
			String contentLength = httpExchange.getRequestHeaders().getFirst("Content-Length");
			String data = null;

			if (contentLength != null) {
				data = HttpTrasferUtil.readRequest(httpExchange, Integer.parseInt(contentLength));
			}
			Headers requestHeaders = new Headers();
			Headers responseHeaders = new Headers();

			requestHeaders.putAll(httpExchange.getRequestHeaders());
			ResponseModel res = handleRequest(HttpMethod.GET, data, requestHeaders, responseHeaders);
			httpExchange.getResponseHeaders().putAll(responseHeaders);
			String dataResponse = "";
			int responseCode = res.getReponseCode();

			if (responseCode == HttpReturnCode.HTTP_OK) {
				dataResponse = res.getPayload() == null ? "" : (String) res.getPayload();
			} else {
				dataResponse = res.getMessage() == null ? "" : (String) res.getMessage();
			}
			HttpTrasferUtil.sendResponse(httpExchange, dataResponse, responseCode);
		} catch (Exception exp) {
			exp.printStackTrace();
			requestState = HttpReturnCode.HTTP_FAILED;
			HttpTrasferUtil.sendResponse(httpExchange, exp.getMessage(), HttpReturnCode.HTPP_404_NOTFOUND);
		}
		return requestState;
	}

	@Override
	protected String handlePOST(HttpExchange httpExchange) throws Exception {
		String requestState = HttpReturnCode.HTTP_SUCCESSED;

		try {
			String contentLength = httpExchange.getRequestHeaders().getFirst("Content-Length");
			String data = null;

			if (contentLength != null) {
				data = HttpTrasferUtil.readRequest(httpExchange, Integer.parseInt(contentLength));
			}

			if (this.soapRequestQueue != null) {
				HttpExchangeModel httpExchangeModel = new HttpExchangeModel();

				httpExchangeModel.setHttpExchange(httpExchange);
				httpExchangeModel.setHttpTimeout(this.httpTimeout);
				httpExchangeModel.setRequestBody(data);
				httpExchangeModel.setBussinessNodeModels(this.bussinessNodeModels);
				if (this.soapRequestQueue.remainingCapacity() < 2) {
					HttpTrasferUtil.sendResponse(httpExchange, "Queue soap request is full", HttpReturnCode.HTTP_OK);
				} else {
					this.soapRequestQueue.put(httpExchangeModel);
				}
			} else {
				Headers requestHeaders = new Headers();
				Headers responseHeaders = new Headers();

				requestHeaders.putAll(httpExchange.getRequestHeaders());
				ResponseModel res = handleRequest(HttpMethod.GET, data, requestHeaders, responseHeaders);
				int responseCode = res.getReponseCode();
				String dataResponse = "";

				if (responseCode == HttpReturnCode.HTTP_OK) {
					dataResponse = res.getPayload() == null ? "" : (String) res.getPayload();
				} else {
					dataResponse = res.getMessage() == null ? "" : (String) res.getMessage();
				}
				HttpTrasferUtil.sendResponse(httpExchange, dataResponse, responseCode);
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			requestState = HttpReturnCode.HTTP_FAILED;
		}
		return requestState;
	}
}
