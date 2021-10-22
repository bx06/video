package com.ops.www.util;

import com.ops.www.common.util.ProcessUtil;
import com.sun.jna.Platform;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author wangzr
 */
@Slf4j
public class PidUtil {

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
		log.debug("start do shell [{}]...", cmd);
		Objects.requireNonNull(ProcessUtil.doCmd("killPid " + pid, cmd, null, null, 0)).waitClose();
		log.debug("end do shell [{}]", cmd);

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
		log.debug("start do shell [{}]...", cmd);
		Objects.requireNonNull(ProcessUtil.doCmd("killPid " + name, cmd, null, null, 0)).waitClose();
		log.debug("end do shell [{}]", cmd);
	}
}
