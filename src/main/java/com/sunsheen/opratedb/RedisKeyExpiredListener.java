package com.sunsheen.opratedb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

import com.sunsheen.redis.PostGresSQLUtils;
import com.sunsheen.redis.RedisUtils;
import com.sunsheen.util.ComUtils;

import redis.clients.jedis.JedisPubSub;

public class RedisKeyExpiredListener extends JedisPubSub {
	private String path;
	
	public RedisKeyExpiredListener(String path) {
		this.path = path;
	}
	
	@Override
	public void onPSubscribe(String pattern, int subscribedChannels) {
		System.out.println("onPSubscribe " + pattern + " " + subscribedChannels);
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {
//		System.out.println("abcd");
		System.out.println(LocalDateTime.now().toString()+"onPMessage pattern " + pattern + " " + channel + " " + message);
		if (message.contains("access")) {
			String logId = message.substring(message.indexOf("-") + 1);
			updateUserOfflineLogninLog(logId);
		}

	}

	private void updateUserOfflineLogninLog(String logId) {
		String logsql = "UPDATE \"user_login_log\" set online_time = ?,offline_time = ?,status = ? where log_id = ?"; 
		
		Connection connlog = null;
		PreparedStatement pslog = null;
		try {
			connlog = PostGresSQLUtils.getDataSource(path).getConnection();
			pslog = connlog.prepareStatement(logsql);
			String userIdAndLoginTime = RedisUtils.getObject(logId, String.class);
			String startTime = "";
			if (!ComUtils.isNull(userIdAndLoginTime)) {
				startTime = userIdAndLoginTime.substring(userIdAndLoginTime.indexOf("-") + 1);
			}
			Date date = new Date();
			long time = ComUtils.timeConversion(startTime, ComUtils.DateToStr(date), "min");
			pslog.setObject(1, time);
			pslog.setTimestamp(2, ComUtils.DateToTimestamp(new Date()));
			pslog.setObject(3, "0");
			pslog.setObject(4, logId);
			pslog.execute();
			RedisUtils.delKey(logId);
			RedisUtils.delKey("access-" + logId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pslog != null) {
				try {
					pslog.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connlog != null) {
				try {
					connlog.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
