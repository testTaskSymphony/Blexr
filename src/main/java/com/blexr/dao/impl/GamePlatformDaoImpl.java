package com.blexr.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.blexr.dao.GamePlatformDao;
import com.blexr.entity.GamePlatform;

public class GamePlatformDaoImpl implements GamePlatformDao {
    
    private static final Logger logger = LoggerFactory.getLogger(GamePlatformDaoImpl.class);
    
    private String TABLE_NAME = "game_platform";
    
    private String COL_GAME_ID = "GAME_ID";
    private String COL_PLATFORM_NAME = "PLATFORM_NAME";
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Override
    public boolean insert(GamePlatform gamePlatform) {
	if (gamePlatform==null) {
	    return false;
	}
	String sqlCommand = "INSERT IGNORE INTO "+TABLE_NAME+" ("+COL_GAME_ID+", "+COL_PLATFORM_NAME+") VALUES (?,?) ";
	try {
	    int rows = jdbcTemplate.update(sqlCommand, new Object[] {gamePlatform.getGameId(), gamePlatform.getPlatformName()});
	    return rows >0;
	}catch (Exception e) {
	    logger.error("insert", e);
	}
	return false;    
    }

    @Override
    public List<GamePlatform> getAll() {
	String sqlCommand = "SELECT * FROM "+TABLE_NAME;
	try {
	    List<GamePlatform> gamePlatfromList = jdbcTemplate.query(sqlCommand, new BeanPropertyRowMapper<GamePlatform>(GamePlatform.class));
	    return gamePlatfromList;
	}
	catch (Exception e) {
	    logger.error("getAll", e);
	}
	return null;
    }

}
