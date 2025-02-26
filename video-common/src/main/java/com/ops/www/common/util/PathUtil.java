package com.ops.www.common.util;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @author wangzr
 */
public final class PathUtil {
    private PathUtil() {
    }

    /**
     * 项目路径
     */
    public static String getProjectPath() {
        String projectPath = System.getProperties().getProperty("os.name").startsWith("Windows") ? "" : File.separator;
        projectPath += System.getProperty("user.dir") + File.separator;
        return projectPath;
    }

    /**
     * 创建目录
     *
     * @throws IOException 读写异常
     */

    public static void mkDirFile(String destDirName) throws IOException {
        final File parent = new File(destDirName).getParentFile();
        if (Objects.nonNull(parent)) {
            forceMkdir(parent);
        }
    }

    private static void forceMkdir(final File directory) throws IOException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                final String message = "File " + directory + " exists and is not a directory. Unable to create directory.";
                throw new IOException(message);
            }
        } else {
            if (!directory.mkdirs()) {
                if (!directory.isDirectory()) {
                    final String message = "Unable to create directory " + directory;
                    throw new IOException(message);
                }
            }
        }
    }

    public static String getFileName(String path) {
        return path.substring(path.lastIndexOf("\\") + 1);
    }

    public static String getFileNameWithOutExtend(String path) {
        String dot = ".";
        if (!path.contains(dot)) {
            return null;
        }
        return path.substring(path.replaceAll("\\\\", "/").lastIndexOf("/") + 1, path.lastIndexOf(dot));
    }
}
