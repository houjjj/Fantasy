package houjun.springboot.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.redis.jedis.pool.custom.test-on-borrow")
public class RedisConfiguration {

	// 验活属性https://help.aliyun.com/zh/redis/use-cases/jedispool-optimization?spm=a2c4g.11186623.help-menu-26340.d_4_7.73e921e0qQcGJP&scm=20140722.H_98726._.OR_help-T_cn~zh-V_1
	//向资源池借用连接时是否做连接有效性检测（ping）。检测到的无效连接将会被移除。
	@Value("${spring.redis.jedis.pool.custom.test-on-borrow:true}")
	private boolean testOnBorrow;

	@Value("${spring.redis.jedis.pool.custom.test-on-return:true}")
	private boolean testOnReturn;

	//是否在空闲资源监测时通过ping命令监测连接有效性，无效连接将被销毁。
	@Value("${spring.redis.jedis.pool.custom.test-while-idle:true}")
	private boolean testWhileIdle;

	//空闲资源的检测周期（单位为毫秒）
	@Value("${spring.redis.jedis.pool.custom.time-between-eviction-run-millis:30000}")
	private long timeBetweenEvictionRunsMillis;

	// 做空闲资源检测时，每次检测资源的个数。默认值3，-1检测所有，-2检测一半
	@Value("${spring.redis.jedis.pool.custom.num-tests-per-eviction-run:-1}")
	private int numTestsPerEvictionRun;// 每次保活检测, 都需要把500个连接都检测一遍. 如果设置为-2, 则每次检测1/2比例的的连接.

	// 这篇文章说的此参数未生效https://www.xinfinite.net/t/topic/3634
	// 资源池中资源的最小空闲时间（单位为毫秒），达到此值后空闲资源将被移除
	// 不要因为idleTime大于某个阈值从而把连接给删除掉. 这样可以防止无意义的大规模连接重建.默认60s
	@Value("${spring.redis.jedis.pool.custom.min-evictable-idle-time-millis:-1}")
	private int minEvictableIdleTimeMillis;



	@Bean
	JedisClientConfigurationBuilderCustomizer customizer() {
		return (builder) -> {
			GenericObjectPoolConfig poolConfig = builder.build().getPoolConfig().orElseGet(GenericObjectPoolConfig::new);
			poolConfig.setTestWhileIdle(testWhileIdle);
			poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
			poolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
			poolConfig.setTestOnBorrow(testOnBorrow);
			poolConfig.setTestOnReturn(testOnReturn);
			poolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
			builder.usePooling().poolConfig(poolConfig);
		};
	}
}