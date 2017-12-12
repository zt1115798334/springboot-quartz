package com.chhliu.springboot.quartz.entity;

import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.chhliu.springboot.quartz.config.QuartzConfigProperties;

/**
 * 描述：将quartz的xml配置文件去配置化
 * @author chhliu
 * 创建时间：2017年4月11日 下午7:41:21
 * @version 1.2.0
 */
@Configuration
public class QuartzConfig {
	
	@Autowired
	private QuartzConfigProperties properties;
	
	/**
	 * attention:
	 * Details：初始化JobDetail
	 * @author chhliu
	 * 创建时间：2017年4月11日 下午6:17:06
	 * @param task
	 * @return
	 * MethodInvokingJobDetailFactoryBean
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@Bean(name = "jobDetail")
	public MethodInvokingJobDetailFactoryBean detailFactoryBean() throws ClassNotFoundException, InstantiationException, IllegalAccessException {// ScheduleTask为需要执行的任务
		MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
		/*
		 *  是否并发执行
		 *  例如每5s执行一次任务，但是当前任务还没有执行完，就已经过了5s了，
		 *  如果此处为true，则下一个任务会执行，如果此处为false，则下一个任务会等待上一个任务执行完后，再开始执行
		 */
		jobDetail.setConcurrent(properties.isConcurrent());
		
		/*
		 * 为需要执行的实体类对应的对象
		 */
		String targetObject = properties.getTargetObject();
		jobDetail.setTargetBeanName(targetObject);
		
		/*
		 * 通过这几个配置，告诉JobDetailFactoryBean我们需要定时执行targetObject类中的properties.getTargetMethod()方法
		 */
		jobDetail.setTargetMethod(properties.getTargetMethod());
		return jobDetail;
	}
	
	/**
	 * attention:
	 * Details：实例化JobTrigger
	 * @author chhliu
	 * 创建时间：2017年4月11日 下午7:39:14
	 * @param jobDetail
	 * @return
	 * CronTriggerFactoryBean
	 */
	@Bean(name = "jobTrigger")
	public CronTriggerFactoryBean cronJobTrigger(MethodInvokingJobDetailFactoryBean jobDetail) {
		CronTriggerFactoryBean tigger = new CronTriggerFactoryBean();
		tigger.setJobDetail(jobDetail.getObject());
		tigger.setCronExpression(properties.getCronExpression());
		return tigger;

	}
	
	/**
	 * attention:
	 * Details：实例化Scheduler
	 * @author chhliu
	 * 创建时间：2017年4月11日 下午7:39:35
	 * @param cronJobTrigger
	 * @return
	 * SchedulerFactoryBean
	 */
	@Bean(name = "scheduler")
	public SchedulerFactoryBean schedulerFactory(Trigger cronJobTrigger) {
		SchedulerFactoryBean bean = new SchedulerFactoryBean();
		// 注册触发器
		bean.setTriggers(cronJobTrigger);
		// 通过applicationContextSchedulerContextKey属性配置获取spring上下文
		bean.setApplicationContextSchedulerContextKey(properties.getApplicationContextSchedulerContextKey());
		// 关闭任务的时候，是否等待任务执行完毕
		bean.setWaitForJobsToCompleteOnShutdown(properties.isWaitForJobsToCompleteOnShutdown());
		return bean;
	}
}
