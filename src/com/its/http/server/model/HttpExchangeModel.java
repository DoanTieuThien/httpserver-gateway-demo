package com.its.http.server.model;

import java.io.Serializable;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

public class HttpExchangeModel implements Serializable {
	private String requestBody;
	private List<BussinessNodeModel> bussinessNodeModels;
	private HttpExchange httpExchange;
	private int httpTimeout;

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public List<BussinessNodeModel> getBussinessNodeModels() {
		return bussinessNodeModels;
	}

	public void setBussinessNodeModels(List<BussinessNodeModel> bussinessNodeModels) {
		this.bussinessNodeModels = bussinessNodeModels;
	}

	public HttpExchange getHttpExchange() {
		return httpExchange;
	}

	public void setHttpExchange(HttpExchange httpExchange) {
		this.httpExchange = httpExchange;
	}

	public int getHttpTimeout() {
		return httpTimeout;
	}

	public void setHttpTimeout(int httpTimeout) {
		this.httpTimeout = httpTimeout;
	}
}
