package com.chhliu.springboot.quartz.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("taskJob")
public class TaskJob {
	private static final Logger LOGGER =  LoggerFactory.getLogger(TaskJob.class);
	public void doJob(){
		LOGGER.info("hello spring boot, i'm the king of the world!!!");
	}
}
