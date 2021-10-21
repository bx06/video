package com.ops.www.util;

import java.lang.reflect.Field;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ops.www.common.util.ProcessUtil;
import com.sun.jna.Platform;

/**
 * @author wangzr
 */
public class PidUtil {

	protected static Logger logger = LogManager.getLogger();

	public static long getPid(Process process) {
		long pid = -1;
		Field field;
		if (Platform.isWindows()) {
			try {
				field = process.getClass().getDeclaredField("handle");
				field.setAccessible(true);
				pid = Kernel32.INSTANCE.getProcessId((Long) field.get(process));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (Platform.isLinux() || Platform.isAIX()) {
			try {
				Class<?> clazz = Class.forName("java.lang.UNIXProcess");
				field = clazz.getDeclaredField("pid");
				field.setAccessible(true);
				pid = (Integer) field.get(process);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return pid;
	}

	public static void killPid(long pid) {
		String cmd = null;
		if (Platform.isWindows()) {
			cmd = "taskkill -PID <" + pid + "> -F";
		} else if (Platform.isLinux() || Platform.isAIX()) {
			cmd = "kill -s 9 " + pid;
		}
		if (cmd == null) {
			return;
		}
		logger.debug("start do shell [{}]...", cmd);
		Objects.requireNonNull(ProcessUtil.doCmd("killPid " + pid, cmd, null, null, 0)).waitClose();
		logger.debug("end do shell [{}]", cmd);

	}

	public static String killProcessCmd(String name) {
		String cmd = null;
		if (Platform.isWindows()) {
			cmd = "taskkill /f /t /im " + name + ".exe";
		} else if (Platform.isLinux() || Platform.isAIX()) {
			cmd = "pkill " + name;
		}
		return cmd;
	}

	public static void killProcess(String name) {
		String cmd = killProcessCmd(name);
		logger.debug("start do shell [{}]...", cmd);
		Objects.requireNonNull(ProcessUtil.doCmd("killPid " + name, cmd, null, null, 0)).waitClose();
		logger.debug("end do shell [{}]", cmd);
	}
}
