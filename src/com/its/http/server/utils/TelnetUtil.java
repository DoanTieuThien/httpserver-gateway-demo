package com.its.http.server.utils;

import java.net.InetSocketAddress;
import java.net.Socket;

public class TelnetUtil {
	public static boolean checkHostAlive(String host, int port, int timeout) {
		boolean isAlive = false;
		Socket s = null;

		try {
			timeout = timeout * 1000;
			s = new Socket();
			s.setSoTimeout(timeout);
			s.connect(new InetSocketAddress(host, port), timeout);
			isAlive = true;
		} catch (Exception exp) {
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (Exception e) {
				}
				s = null;
			}
		}
		return isAlive;
	}
}
