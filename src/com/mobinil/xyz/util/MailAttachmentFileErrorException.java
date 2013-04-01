package com.mobinil.xyz.util;

public class MailAttachmentFileErrorException extends RuntimeException {

	private static final long serialVersionUID = 81866658880977062L;

	public MailAttachmentFileErrorException() {
	}

	public MailAttachmentFileErrorException(String message) {
		super(message);
	}

	public MailAttachmentFileErrorException(Throwable cause) {
		super(cause);
	}

	public MailAttachmentFileErrorException(String message, Throwable cause) {
		super(message, cause);
	}
}