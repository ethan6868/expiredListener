package com.sunsheen.redis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import com.sunsheen.opratedb.RedisKeyExpiredListener;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

@SuppressWarnings("resource")
public class RedisUtils {
	private static JedisCluster jedis = getJedisCluster();
	private static int expireTime = -1;
	public final static int halfHour = 30 * 60;
	public final static int hour = 1 * 60 * 60;
	public final static int dayTime = 24 * 60 * 60;
	public final static int weekTime = 7 * 24 * 60 * 60;
	public final static int monthTime = 30 * 24 * 60 * 60;
	public final static int yearTime = 365 * 24 * 60 * 60;

	public static JedisCluster getJedisCluster() {
		Set<HostAndPort> set = getClusterInfo();
		if (jedis == null) {
			jedis = new JedisCluster(set, 5000, 1000);
		}
		return jedis;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T getObject(String key, Class T) {
		String objectStr = jedis.get(key);
		T list = (T) SerializeUtil.unserialize(objectStr);
		return list;
	}

	public static Set<HostAndPort> getClusterInfo() {
		Properties properties = new Properties();
		// 使用ClassLoader加载properties配置文件生成对应的输入流
		InputStream in = RedisUtils.class.getClassLoader().getResourceAsStream("redis.properties");
		// 使用properties对象加载输入流
		try {
			properties.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String property = properties.getProperty("redis_cluster_nodes");
		String[] split = property.split(",");
		List<String> nodes = Arrays.asList(split);
		Set<HostAndPort> clusterInfo = new HashSet<HostAndPort>();
		for (String node : nodes) {
			String trim = StringUtils.trim(node);
			String[] split2 = trim.split(":");
			clusterInfo.add(
					new HostAndPort(StringUtils.trim(split2[0]), Integer.parseInt(StringUtils.trim(split2[1]))));
		}
		return clusterInfo;
	}

	public static void delKey(String key) {
		jedis.del(key);
	}

	public static Map<String, String> getMapData(String key) {
		Map<String, String> data = jedis.hgetAll(key);
		return data;
	}

	public static void startListener(String redispath,String postgrespath) {
		try {
			Properties properties = new Properties();
			// 使用ClassLoader加载properties配置文件生成对应的输入流
			InputStream in = RedisUtils.class.getClassLoader().getResourceAsStream(redispath);
			// 使用properties对象加载输入流
			properties.load(in);
			String property = properties.getProperty("redis_cluster_nodes");
			String[] split = property.split(",");
			List<String> nodes = Arrays.asList(split);
			Set<HostAndPort> clusterInfo = new HashSet<HostAndPort>();
			for (String node : nodes) {
				String trim = StringUtils.trim(node);
				String[] split2 = trim.split(":");
				clusterInfo.add(
						new HostAndPort(StringUtils.trim(split2[0]), Integer.parseInt(StringUtils.trim(split2[1]))));
			}
			JedisCluster jc = new JedisCluster(clusterInfo);
			Boolean exists = jc.exists("subscribe");
			ExecutorService executor = Executors.newFixedThreadPool(6);
			if (!exists) {
				Map<String, JedisPool> clusterNodes = jc.getClusterNodes();
				for (String k : clusterNodes.keySet()) {
					JedisPool jp = clusterNodes.get(k);
					final Jedis jedis = jp.getResource();
					config(jedis);
					executor.submit(new Runnable() {

						public void run() {
							jedis.psubscribe(new RedisKeyExpiredListener(postgrespath), "__key*__:*");
						}
					});
				}
				jc.set("subscribe", "subscribed");// 已经订阅
				System.out.println("----------------------------已经订阅---------------------------");
			} else {
				// 已经订阅，不需要重复订阅
				System.out.println("------------------------------已经订阅，不需要重复订阅-------------------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void config(Jedis jedis) {
		String parameter = "notify-keyspace-events";
		List<String> notify = jedis.configGet(parameter);
		if (notify.get(1).equals("")) {
			jedis.configSet(parameter, "Ex");
		}
	}

	public static void main(String[] args) throws IOException {
		startListener(args[0],args[1]);

	}

}
