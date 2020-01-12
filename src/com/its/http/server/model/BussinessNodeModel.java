package com.its.http.server.model;

import java.io.Serializable;

public class BussinessNodeModel implements Serializable {
	private String appNodeName;
	private String host;
	private int port;
	private boolean isAlive;
	private int timeoutCheck;

	public String getAppNodeName() {
		return appNodeName;
	}

	public void setAppNodeName(String appNodeName) {
		this.appNodeName = appNodeName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHttpRequest() {
		return "http://" + this.host + ":" + this.port;
	}

	public String getHttpsRequest() {
		return "https://" + this.host + ":" + this.port;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public int getTimeoutCheck() {
		return timeoutCheck;
	}

	public void setTimeoutCheck(int timeoutCheck) {
		this.timeoutCheck = timeoutCheck;
	}
}
