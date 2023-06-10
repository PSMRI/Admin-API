package com.iemr.admin.utils;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

import com.iemr.admin.utils.config.ConfigProperties;
import com.iemr.admin.utils.redis.RedisStorage;

@EnableAutoConfiguration
public class CommonMain {
	@Bean
	public ConfigProperties configProperties() {
		return new ConfigProperties();
	}

	@Bean
	public RedisHttpSessionConfiguration redisSession() {
		return new RedisHttpSessionConfiguration();
	}

	@Bean
	public RedisStorage redisStorage() {
		return new RedisStorage();
	}

}
