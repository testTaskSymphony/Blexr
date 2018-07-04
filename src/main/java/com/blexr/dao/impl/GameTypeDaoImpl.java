package com.blexr.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.blexr.dao.GameTypeDao;
import com.blexr.entity.GameType;

public class GameTypeDaoImpl implements GameTypeDao {
    
    private static final Logger logger = LoggerFactory.getLogger(GameTypeDaoImpl.class);
    
    private String TABLE_NAME = "game_type";
    
    private String COL_GAME_ID = "GAME_ID";
    private String COL_GAME_TYPE = "GAME_TYPE";
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Override
    public boolean insert(GameType gameType) {
	if (gameType==null) {
	    return false;
	}
	String sqlCommand = "INSERT IGNORE INTO "+TABLE_NAME+" ("+COL_GAME_ID+", "+COL_GAME_TYPE+") VALUES (?,?) ";
	try {
	    int rows = jdbcTemplate.update(sqlCommand, new Object[] {gameType.getGameId(), gameType.getGameType()});
	    return rows >0;
	}catch (Exception e) {
	    logger.error("insert", e);
	}
	return false;   
    }

    @Override
    public List<GameType> getAll() {
	String sqlCommand = "SELECT * FROM "+TABLE_NAME;
	try {
	    List<GameType> gameTypeList = jdbcTemplate.query(sqlCommand, new BeanPropertyRowMapper<GameType>(GameType.class));
	    return gameTypeList;
	}
	catch (Exception e) {
	    logger.error("getAll", e);
	}
	return null;
    }

}
