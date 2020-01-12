package com.its.http.server.services;

import java.util.List;

import org.apache.log4j.Logger;

import com.its.http.server.model.BussinessNodeModel;
import com.its.http.server.utils.TelnetUtil;

public class HttpRefreshBussinessService implements Runnable {
	private final static Logger log = Logger.getLogger(HttpRefreshBussinessService.class);

	private int miServiceState = 1;
	private List<BussinessNodeModel> bussinessNodes;

	public void stop() {
		this.miServiceState = 0;
	}

	public HttpRefreshBussinessService(List<BussinessNodeModel> bussinessNodes) {
		this.bussinessNodes = bussinessNodes;
	}

	@Override
	public void run() {
		log.info("service check bussiness node alive started");
		while (this.miServiceState != 0) {
			try {
				if (this.bussinessNodes == null || this.bussinessNodes.size() == 0) {
					Thread.sleep(100);
					log.debug("no any bussiness node is found");
					continue;
				}

				for (BussinessNodeModel bu : this.bussinessNodes) {
					boolean oldState = bu.isAlive();
					boolean isAlive = TelnetUtil.checkHostAlive(bu.getHost(), bu.getPort(), bu.getTimeoutCheck());

					if (oldState != isAlive) {
						log.info("bussiness node host " + bu.getHost() + ", port " + bu.getPort() + ", state alive "
								+ isAlive);
					}
					bu.setAlive(isAlive);
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
		this.bussinessNodes = null;
		log.info("service check bussiness node alive stopped");
	}

}
