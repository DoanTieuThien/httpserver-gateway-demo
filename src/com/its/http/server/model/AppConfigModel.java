package com.its.http.server.model;

import java.io.Serializable;
import java.util.List;

import com.its.http.server.abstracthandle.AbstractHandle;

public class AppConfigModel implements Serializable {
	private int appPort;
	private int httpTimeout;
	private int timeoutCheck;
	private int maxWaitRequestSoap;
	private String forwardProtocol;
	private String abstractHandle;
	private List<ForwardModel> forwardContextPaths;
	private List<BussinessNodeModel> bussinessNodes;

	public int getAppPort() {
		return appPort;
	}

	public void setAppPort(int appPort) {
		this.appPort = appPort;
	}

	public int getHttpTimeout() {
		return httpTimeout;
	}

	public void setHttpTimeout(int httpTimeout) {
		this.httpTimeout = httpTimeout;
	}

	public int getTimeoutCheck() {
		return timeoutCheck;
	}

	public void setTimeoutCheck(int timeoutCheck) {
		this.timeoutCheck = timeoutCheck;
	}

	public int getMaxWaitRequestSoap() {
		return maxWaitRequestSoap;
	}

	public void setMaxWaitRequestSoap(int maxWaitRequestSoap) {
		this.maxWaitRequestSoap = maxWaitRequestSoap;
	}

	public String getForwardProtocol() {
		return forwardProtocol;
	}

	public void setForwardProtocol(String forwardProtocol) {
		this.forwardProtocol = forwardProtocol;
	}

	public String getAbstractHandle() {
		return abstractHandle;
	}

	public void setAbstractHandle(String abstractHandle) {
		this.abstractHandle = abstractHandle;
	}

	public List<ForwardModel> getForwardContextPaths() {
		return forwardContextPaths;
	}

	public void setForwardContextPaths(List<ForwardModel> forwardContextPaths) {
		this.forwardContextPaths = forwardContextPaths;
	}

	public List<BussinessNodeModel> getBussinessNodes() {
		return bussinessNodes;
	}

	public void setBussinessNodes(List<BussinessNodeModel> bussinessNodes) {
		this.bussinessNodes = bussinessNodes;
	}
}
