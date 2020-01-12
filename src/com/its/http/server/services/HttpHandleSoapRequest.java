package com.its.http.server.services;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;
import com.its.http.server.model.HttpExchangeModel;

public class HttpHandleSoapRequest implements Runnable {
	private final static Logger log = Logger.getLogger(HttpHandleSoapRequest.class);

	private int miServiceState = 1;
	private LinkedBlockingQueue<HttpExchangeModel> soapRequestQueue;
	private ThreadPoolExecutor threadPoolExecutor = null;

	public void stop() {
		this.miServiceState = 0;
	}

	public HttpHandleSoapRequest(LinkedBlockingQueue<HttpExchangeModel> soapRequestQueue) {
		this.soapRequestQueue = soapRequestQueue;
	}

	@Override
	public void run() {
		log.info("service handle soap request started");
		this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
		while (this.miServiceState != 0) {
			try {
				if (this.threadPoolExecutor.getActiveCount() > 9) {
					log.debug("waiting request release induring process, active handle request "
							+ this.threadPoolExecutor.getActiveCount());
					Thread.sleep(200);
					continue;
				}
				HttpExchangeModel exchangeModel = this.soapRequestQueue.poll();

				if (exchangeModel != null) {
					log.info("found soap request, start work " + exchangeModel.getRequestBody());
					SoapWorker work = new SoapWorker(exchangeModel);
					this.threadPoolExecutor.execute(work);
					continue;
				}
			} catch (Exception exp) {
				log.error("error process", exp);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (this.threadPoolExecutor != null) {
			this.threadPoolExecutor.shutdown();
			while (!this.threadPoolExecutor.isShutdown()) {
				log.info("waiting shutdown thread");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (this.soapRequestQueue != null) {
			this.soapRequestQueue.clear();
			this.soapRequestQueue = null;
		}
		log.info("service handle soap request stopped");
	}
}
