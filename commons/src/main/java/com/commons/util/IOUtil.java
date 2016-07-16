package com.commons.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.commons.exceptions.SocketIOException;

public class IOUtil {

	private static final Logger logger = Logger.getLogger(IOUtil.class.getName());

	// TODO bufferedReader
	public static String read(InputStream inputStream) {
		try {
			byte[] dataBytes = new byte[inputStream.available()];
			inputStream.read(dataBytes);
			return new String(dataBytes);
		} catch (IOException e) {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e1) {
				logger.log(Level.SEVERE, "I/O Error on closing stream", e1);
			}
			throw new SocketIOException("I/O Error on reading from Socket", e);
		}
	}
	
	public static void write(OutputStream outputStream, byte[] dataBytes) {
		try {
			outputStream.write(dataBytes);
		} catch (IOException e) {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e1) {
				logger.log(Level.SEVERE, "I/O Error on closing stream", e1);
			}
			throw new SocketIOException("I/O Error on writing to Socket", e);
		}
	}

	public static void write(OutputStream outputStream, String message) {
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
		try {
			bufferedWriter.write(message);
			bufferedWriter.newLine();
			bufferedWriter.flush();
		} catch (IOException e) {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e1) {
				logger.log(Level.SEVERE, "I/O Error on closing stream", e1);
			}
			throw new SocketIOException("I/O Error on writing to Socket", e);
		}
	}
}
