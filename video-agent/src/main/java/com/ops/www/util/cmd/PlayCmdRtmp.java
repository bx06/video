package com.ops.www.util.cmd;

import com.ops.www.common.util.StringUtils;
import com.sun.jna.Platform;

/**
 * @author wangzr
 */
public class PlayCmdRtmp {

	private static String getRtsp(String rtsp, String userName, String passWord) {
		if (StringUtils.isBlank(userName) || StringUtils.isBlank(passWord)) {
			return rtsp;
		}
		String[] strArray = rtsp.split("//");
		return strArray[0] + "//" + userName + ":" + passWord + "@" + strArray[1];
	}

	private static String buildRtsp(String rtsp, String userName, String passWord) {
		if (Platform.isWindows()) {
			return "\"" + getRtsp(rtsp, userName, passWord) + "\"";
		} else {
			return getRtsp(rtsp, userName, passWord);
		}
	}

//	ffmpeg -i "rtsp://admin:yzfar123@192.168.0.167:554/cam/realmonitor?channel=1&subtype=0&unicast=true&proto=Onvif" -q 0 -f flv rtmp://127.0.0.1:1935/live/demo

	private static String cpuCmd(String rtsp, String userName, String passWord, String size, String rtmpIp, int port,
			String theme, int timeOut) {
		return "ffmpeg" + " -stimeout " + timeOut + "000000" + " -i " +
				buildRtsp(rtsp, userName, passWord) +
				" -q 0 -ar 44100 -f flv -s " + size +
				" -y -max_muxing_queue_size 9999 rtmp://" + rtmpIp + ":" + port + "/" +
				theme;
	}

	public static String playCmd(String rtsp, String userName, String passWord, String size, String rtmpIp, int port,
								 String theme, int timeOut) {
		return cpuCmd(rtsp, userName, passWord, size, rtmpIp, port, theme, timeOut);
	}
}
