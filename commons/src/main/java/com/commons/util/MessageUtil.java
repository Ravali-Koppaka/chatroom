package com.commons.util;

import java.io.OutputStream;

import com.commons.exceptions.MessageFormatException;

public class MessageUtil {

	public static final String INVALID_USER = "No such user";
	private static final char MESSAGE_SEPARATOR = ':';
	private static final String QUIT_MESSAGE = "Quit";
	

	public static boolean isQuitMessage(String message) {
		return QUIT_MESSAGE.equals(message);
	}

	private static int findMessageSeparator(String message) {
		int index = message.indexOf(MESSAGE_SEPARATOR);
		if (index < 0) {
			throw new MessageFormatException(
					"Wrong message format(missing message separator " + MESSAGE_SEPARATOR + " )");
		} else {
			return index;
		}
	}

	public static String getReceiver(String message) throws MessageFormatException {
		return message.substring(0, findMessageSeparator(message));
	}

	public static String getMessage(String message) throws MessageFormatException {
		return message.substring(findMessageSeparator(message) + 1, message.length());
	}

	public static String createMessage(String userName, String message) {
		return userName + MESSAGE_SEPARATOR + message;
	}

	public static void sendQuitMessage(OutputStream opStream) {
		IOUtil.write(opStream, QUIT_MESSAGE);
	}
}
