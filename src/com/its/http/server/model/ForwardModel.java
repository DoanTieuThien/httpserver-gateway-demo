package com.its.http.server.model;

import java.io.Serializable;

public class ForwardModel implements Serializable {
	private String contextPathFrom;
	private String contextPathTo;

	public String getContextPathFrom() {
		return contextPathFrom;
	}

	public void setContextPathFrom(String contextPathFrom) {
		this.contextPathFrom = contextPathFrom;
	}

	public String getContextPathTo() {
		return contextPathTo;
	}

	public void setContextPathTo(String contextPathTo) {
		this.contextPathTo = contextPathTo;
	}
}
