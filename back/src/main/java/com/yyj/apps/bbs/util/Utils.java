package com.yyj.apps.bbs.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class Utils {
    public static void closeSilence(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 连接两个路径
     *
     * @param paths
     * @return
     */
    public static String concatPath(String... paths) {
        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            if (path.endsWith("/") || path.endsWith("\\")) {
                path = path.substring(0, path.length() - 1);
            }
            if (path.startsWith("/") || path.startsWith("\\")) {
                sb.append(path);
            } else {
                sb.append("/").append(path);
            }
        }
        return sb.toString();
    }

    /**
     * 读取文本文件
     * @param file 需要读取的文件
     * @param charset 文件字符串
     * @return
     * @throws IOException
     */
    public static String loadContent(File file, String charset) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), charset);
    }
	/**
	 * 根据用户ID hashcode获取表名
	 * @param userid
	 * @return
	 *author maxinbo
	 */
	public static synchronized String getTableName(int tableIdx) {
		// 计算需要入库的位置
				if(tableIdx>=100) {
					return "crm_msg.crm_messsages_"+tableIdx;
				}else if(tableIdx>=10&&tableIdx<100){
					return "crm_msg.crm_messsages_0"+tableIdx;
				}else {
					return "crm_msg.crm_messsages_00"+tableIdx;
				}
				
	}
	
	
}
