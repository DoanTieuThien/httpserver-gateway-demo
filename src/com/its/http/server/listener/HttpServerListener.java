package com.its.http.server.listener;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.its.http.server.abstracthandle.AbstractHandle;
import com.its.http.server.model.AppConfigModel;
import com.its.http.server.model.ForwardModel;
import com.its.http.server.model.HttpExchangeModel;
import com.its.http.server.services.HttpHandleSoapRequest;
import com.its.http.server.services.HttpRefreshBussinessService;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HttpServerListener {
	private HttpServer _httpServer = null;
	private AppConfigModel appConfigModel = null;
	private HttpRefreshBussinessService httpRefreshBussinessService = null;
	private HttpHandleSoapRequest httpHandleSoapRequest = null;

	public HttpServerListener(AppConfigModel appConfig) {
		this.appConfigModel = appConfig;
	}

	public void start() throws Exception {
		int iPort = appConfigModel.getAppPort();
		start(iPort);
		String handleClassName = appConfigModel.getAbstractHandle();
		List<ForwardModel> forwardModels = appConfigModel.getForwardContextPaths();

		if (forwardModels == null || forwardModels.size() == 0) {
			return;
		}

		LinkedBlockingQueue<HttpExchangeModel> soapRequestQueue = null;
		if ("SOAP".equals(this.appConfigModel.getForwardProtocol())) {
			soapRequestQueue = new LinkedBlockingQueue<HttpExchangeModel>(this.appConfigModel.getMaxWaitRequestSoap());
			this.httpHandleSoapRequest = new HttpHandleSoapRequest(soapRequestQueue);
		}
		for (ForwardModel fw : forwardModels) {
			AbstractHandle abstractHandle = (AbstractHandle) Class.forName(handleClassName)
					.getConstructor(int.class, ForwardModel.class, List.class, LinkedBlockingQueue.class)
					.newInstance(appConfigModel.getHttpTimeout(), fw, appConfigModel.getBussinessNodes(),
							soapRequestQueue != null ? soapRequestQueue : null);
			addNewContext(fw.getContextPathFrom(), abstractHandle);
		}
		this.httpRefreshBussinessService = new HttpRefreshBussinessService(this.appConfigModel.getBussinessNodes());
		Thread t = new Thread(this.httpRefreshBussinessService);

		t.start();
	}

	public void start(int iPort) throws Exception {
		try {
			this._httpServer = HttpServer.create(new InetSocketAddress(iPort), 0);
			this._httpServer.start();
		} catch (Exception exp) {
			throw exp;
		}
	}

	public void addNewContext(String contextPath, HttpHandler httpHandle) {
		this._httpServer.createContext(contextPath, httpHandle);
	}

	public void addNewContext(String contextPath, HttpHandler httpHandle, Filter filter) {
		this._httpServer.createContext(contextPath, httpHandle).getFilters().add(filter);
	}

	public void stop() {
		if (this._httpServer != null) {
			this._httpServer.stop(0);
		}
		if (this.httpRefreshBussinessService != null) {
			this.httpRefreshBussinessService.stop();
		}
	}
}
