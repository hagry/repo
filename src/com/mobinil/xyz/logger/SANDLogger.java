package com.mobinil.xyz.logger;

import org.apache.commons.logging.Log;

public class SANDLogger {

	private static SANDCustomLogger logger;

	public static Log getLogger() {
		if (logger == null) {
			logger = new SANDCustomLogger();
		}
		return logger;

	}

}
