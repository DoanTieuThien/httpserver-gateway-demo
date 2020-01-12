package com.its.http.server.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import com.sun.net.httpserver.HttpExchange;

public class HttpTrasferUtil {
	public static String readRequest(HttpExchange exchange, int byteAvailable) {
		StringBuilder body = new StringBuilder();

		try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), "utf-8")) {
			char[] buffer = new char[byteAvailable];
			int read = 0;

			while ((read = reader.read(buffer)) != -1 && byteAvailable > 0) {
				body.append(buffer, 0, read);
				byteAvailable = byteAvailable - read;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return body.toString();
	}

	public static String readRequest(InputStream in, int byteAvailable) {
		StringBuilder body = new StringBuilder();

		if (in == null) {
			return "";
		}
		try (InputStreamReader reader = new InputStreamReader(in, "utf-8")) {
			char[] buffer = new char[byteAvailable];
			int read = 0;

			while ((read = reader.read(buffer)) != -1 && byteAvailable > 0) {
				body.append(buffer, 0, read);
				byteAvailable = byteAvailable - read;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return body.toString();
	}

	public static void sendResponse(HttpExchange exchange, String data, int responseCode) throws Exception {
		OutputStream out = null;

		try {
			if (data == null) {
				data = "";
			}
			ByteBuffer buffer = Charset.forName("UTF-8").encode(data);
			byte[] bytes = new byte[buffer.remaining()];
			buffer.get(bytes);
			int dataLength = bytes.length;

			exchange.sendResponseHeaders(responseCode, dataLength);
			if (dataLength > 0) {
				out = exchange.getResponseBody();
				out.write(bytes);
				out.flush();
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
				out = null;
			}
		}
	}
}
