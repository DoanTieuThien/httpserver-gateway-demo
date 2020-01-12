package com.its.http.server.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.its.http.server.model.AppConfigModel;
import com.its.http.server.model.BussinessNodeModel;
import com.its.http.server.model.ForwardModel;

public class ToolUtil {
	public final static String APP_PROP_KEY = "app";
	public final static String PORT_PROP_KEY = "port";
	public final static String FORWARD_PROP_KEY = "forward";
	public final static String CONTEXTPATHFROM_PROP_KEY = "context-path-from";
	public final static String CONTEXTPATHTO_PROP_KEY = "context-path-to";
	public final static String BUSSINESSNODE_PROP_KEY = "bussiness-node";
	public final static String HOST_PROP_KEY = "host";
	public final static String HANDLECLASS_PROP_KEY = "handle-class";
	public final static String HTTPTIMEOUT_PROP_KEY = "http-timeout";
	public final static String TIMEOUTCHECK_PROP_KEY = "timeout-check-bussiness";
	public final static String FORWARDPROTOCOL_PROP_KEY = "forward-protocol";
	public final static String MAXWAITREQUESTSOAP_PROP_KEY = "max-wait-request-soap";
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static AppConfigModel loadConfigFromHashMap(HashMap configMap) {
		AppConfigModel appConfigModel = new AppConfigModel();

		try {
			LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) configMap.get(APP_PROP_KEY);

			appConfigModel.setAppPort((int) data.get(PORT_PROP_KEY));
			appConfigModel.setAbstractHandle((String) data.get(HANDLECLASS_PROP_KEY));
			appConfigModel.setHttpTimeout(((int) data.get(HTTPTIMEOUT_PROP_KEY)) * 1000);
			appConfigModel.setTimeoutCheck(((int) data.get(TIMEOUTCHECK_PROP_KEY)) * 1000);
			appConfigModel.setForwardProtocol((String) data.get(FORWARDPROTOCOL_PROP_KEY));
			appConfigModel.setTimeoutCheck((int) data.get(MAXWAITREQUESTSOAP_PROP_KEY));
			
			Object dataForward = configMap.get(FORWARD_PROP_KEY);

			if (dataForward != null) {
				ArrayList<LinkedHashMap<String, String>> arrayList = (ArrayList) dataForward;
				List<ForwardModel> forwardList = new ArrayList<ForwardModel>();

				for (LinkedHashMap<String, String> fw : arrayList) {
					ForwardModel forwardModel = new ForwardModel();

					forwardModel.setContextPathFrom(fw.get(CONTEXTPATHFROM_PROP_KEY));
					forwardModel.setContextPathTo(fw.get(CONTEXTPATHTO_PROP_KEY));
					forwardList.add(forwardModel);
				}
				appConfigModel.setForwardContextPaths(forwardList);
			}
			LinkedHashMap nodeInfo = (LinkedHashMap) configMap.get(BUSSINESSNODE_PROP_KEY);
			List<BussinessNodeModel> bussinessNodeList = new ArrayList<BussinessNodeModel>();
			Iterator<String> bussniessNames = nodeInfo.keySet().iterator();

			while (bussniessNames.hasNext()) {
				String buName = bussniessNames.next();
				LinkedHashMap buInfo = (LinkedHashMap) nodeInfo.get(buName);
				BussinessNodeModel bussinessNodeModel = new BussinessNodeModel();

				bussinessNodeModel.setAppNodeName(buName);
				bussinessNodeModel.setPort((int) buInfo.get(PORT_PROP_KEY));
				bussinessNodeModel.setHost((String) buInfo.get(HOST_PROP_KEY));
				bussinessNodeList.add(bussinessNodeModel);
			}
			appConfigModel.setBussinessNodes(bussinessNodeList);
		} catch (Exception exp) {
			exp.getSuppressed();
		}
		return appConfigModel;
	}
}
