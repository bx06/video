package com.ops.www.center.system.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.ops.www.common.util.RecordHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author 作者 cp
 * @version 创建时间：2020年7月14日 下午4:17:35
 * 
 */
@Slf4j
@Component
public class DefaultRecordHandler implements RecordHandler {

	@Override
	public void handleError(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		log.error("\n" + sw);
	}
}
