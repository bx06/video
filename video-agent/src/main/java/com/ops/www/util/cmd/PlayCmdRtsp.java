package com.ops.www.util.cmd;

import com.ops.www.common.dto.PlayConfig;
import com.ops.www.common.util.StringUtils;

/**
 * @author wangzr
 */
public class PlayCmdRtsp {

    private PlayCmdRtsp() {
    }

    private static String getRtsp(String rtsp, String userName, String passWord) {
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(passWord)) {
            return rtsp;
        }
        String[] strArray = rtsp.split("//");
        return strArray[0] + "//" + userName + ":" + passWord + "@" + strArray[1];
    }

    private static String buildRtsp(String rtsp, String userName, String passWord) {
//		if (Platform.isWindows()) {
//			return "\"" + getRtsp(rtsp, userName, passWord) + "\"";
//		} else {
        return getRtsp(rtsp, userName, passWord);
//		}
    }

    private static String cpuCmd(String rtsp, String userName, String passWord, String size, String wsIp, int port,
                                 String superSecret, String theme, int timeOut) {
        //		【sb.append("ffmpeg").append(" -stimeout ").append(timeOut).append("000").append(" -i ");】
//		【sb.append(" -r 30  -q 0 -ar 44100 -f mpegts -codec:v mpeg1video -s ").append(size);】
//		【sb.append(" -codec:a mp2 -muxdelay 0.001 -y -max_muxing_queue_size 9999 http://").append(wsIp).append(":")】
//		【		.append(port).append("/").append(superSecret).append("/").append(theme);】
        return "ffmpeg" + " -i " + buildRtsp(rtsp, userName, passWord) + " -q 0 -f mpegts -codec:v mpeg1video -s " + size + " http://" + wsIp + ":" + port + "/" + superSecret + "/" + theme;
    }

    private static String qsvCmd(String rtsp, String userName, String passWord, String size, String wsIp, int port,
                                 String superSecret, String theme, int timeOut) {
		return "ffmpeg -c:v h264_qsv " + "-stimeout " + timeOut + "000000" + " -i " +
				buildRtsp(rtsp, userName, passWord) +
				"  -r 30  -q 0 -ar 44100 -vcodec h264_qsv -f mpegts -codec:v mpeg1video -s " + size +
				" -codec:a mp2 -muxdelay 0.001 -y -max_muxing_queue_size 9999 http://" + wsIp + ":" +
				port + "/" + superSecret + "/" + theme;
    }

    private static String cudaCmd(String rtsp, String userName, String passWord, String size, String wsIp, int port,
                                  String superSecret, String theme, int timeOut) {
		return "ffmpeg  -c:v h264_cuvid " + "-stimeout " + timeOut + "000000" + " -i " +
				buildRtsp(rtsp, userName, passWord) +
				"  -r 30 -q 0 -ar 44100 -c:v h264_nvenc -f mpegts -codec:v mpeg1video -s " + size +
				" -codec:a mp2 -muxdelay 0.001 -y -max_muxing_queue_size 9999 http://" + wsIp + ":" +
				port + "/" + superSecret + "/" + theme;
    }

    public static String playCmd(byte type, String rtsp, String userName, String passWord, String size, String wsIp,
                                 int port, String superSecret, String theme, int timeOut) {
        switch (type) {
            case PlayConfig.TYPE_CPU:
                return cpuCmd(rtsp, userName, passWord, size, wsIp, port, superSecret, theme, timeOut);
            case PlayConfig.TYPE_CUDA:
                return cudaCmd(rtsp, userName, passWord, size, wsIp, port, superSecret, theme, timeOut);
            case PlayConfig.TYPE_QSV:
                return qsvCmd(rtsp, userName, passWord, size, wsIp, port, superSecret, theme, timeOut);
            default:
                throw new RuntimeException("Unsupported type: " + type + "!");
        }
    }

//    public static void main(String[] args) {
//        byte type = 0;
//        String rtsp = "rtsp://192.168.2.13";
//        String userName = "admin";
//        String passWord = "admin123456";
//        String size = "800x600";
//        String wsIp = "127.0.0.1";
//        int port = 8081;
//        String superSecret = "supersecret";
//        String theme = "live1";
//        String playCmd = playCmd(type, rtsp, userName, passWord, size, wsIp, port, superSecret, theme, 3);
//        System.out.println(playCmd);
//    }
}
