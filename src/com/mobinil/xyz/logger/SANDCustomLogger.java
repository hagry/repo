package com.mobinil.xyz.logger;

import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

public class SANDCustomLogger implements Log {

	private Logger customLogger;

	public SANDCustomLogger() {
		customLogger = Logger.getLogger("SANDCustomLogger.Class");
	}

	public void error(Object message) {
		customLogger.error(message);
	}

	public void debug(Object arg0) {
		customLogger.debug(arg0);
	}

	public void debug(Object arg0, Throwable arg1) {
		customLogger.debug(arg0, arg1);
	}

	public void error(Object arg0, Throwable arg1) {
		customLogger.error(arg0, arg1);
	}

	public void fatal(Object arg0) {
		customLogger.fatal(arg0);
	}

	public void fatal(Object arg0, Throwable arg1) {
		customLogger.fatal(arg0, arg1);
	}

	public void info(Object arg0) {
		customLogger.info(arg0);
	}

	public void info(Object arg0, Throwable arg1) {
		customLogger.info(arg0, arg1);
	}

	public boolean isDebugEnabled() {
		return customLogger.isDebugEnabled();
	}

	@SuppressWarnings("deprecation")
	public boolean isErrorEnabled() {
		return customLogger.isEnabledFor(Priority.ERROR);
	}

	@SuppressWarnings("deprecation")
	public boolean isFatalEnabled() {
		return customLogger.isEnabledFor(Priority.FATAL);
	}

	@SuppressWarnings("deprecation")
	public boolean isInfoEnabled() {
		return customLogger.isEnabledFor(Priority.INFO);
	}

	public boolean isTraceEnabled() {
		return customLogger.isTraceEnabled();
	}

	@SuppressWarnings("deprecation")
	public boolean isWarnEnabled() {
		return customLogger.isEnabledFor(Priority.WARN);
	}

	public void trace(Object arg0) {
		customLogger.trace(arg0);
	}

	public void trace(Object arg0, Throwable arg1) {
		customLogger.trace(arg0, arg1);
	}

	public void warn(Object arg0) {
		customLogger.warn(arg0);
	}

	public void warn(Object arg0, Throwable arg1) {
		customLogger.warn(arg0, arg1);
	}

}
