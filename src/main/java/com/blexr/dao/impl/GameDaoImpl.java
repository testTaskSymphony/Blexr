package com.blexr.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.blexr.dao.GameDao;
import com.blexr.entity.Game;

public class GameDaoImpl implements GameDao {

    private static final Logger logger = LoggerFactory.getLogger(GameDaoImpl.class);
    
    private String TABLE_NAME = "game";
    
    private String COL_ID = "ID";
    private String COL_NAME = "NAME";
    private String COL_DETAILS = "DETAILS";
    private String COL_IMAGE_ID = "IMAGE_ID";
    private String COL_URL = "URL";
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Override
    public Integer insert(Game game) {
	if (game==null) return null;
	
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put(COL_NAME, game.getName());
	    parameters.put(COL_DETAILS, game.getDetails());
	    parameters.put(COL_URL, game.getUrl());
	    try {
		parameters.put(COL_IMAGE_ID, game.getImage().getId());
	    }
	    catch (NullPointerException npe) {
		
	    }

	    SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource());

	    simpleJdbcInsert.setTableName(TABLE_NAME);
	    simpleJdbcInsert.setGeneratedKeyName("ID");

	    Number insertedId = simpleJdbcInsert.executeAndReturnKey(parameters);
	    return insertedId.intValue();
	} catch (Exception e) {
	    logger.error("Exception insert", e);
	    return null;
	}
    }

    @Override
    public boolean delete(Integer id) {
	if (id==null) {
	    return false;
	}
	String sqlCommand = "DELETE FROM "+TABLE_NAME+ " WHERE "+COL_ID+"=?";

	try {
	    int rows = jdbcTemplate.update(sqlCommand, new Object[] {id});
	    return rows > 0;
	}
	catch (Exception e) {
	    logger.error("delete", e);
	}
	return false;
    }

    @Override
    public List<Game> getAll() {
	String sqlCommand = "SELECT * FROM "+TABLE_NAME;
	try {
	    List<Game> games = jdbcTemplate.query(sqlCommand, new BeanPropertyRowMapper<Game>(Game.class));
	    return games;
	}
	catch (Exception e) {
	    logger.error("getAll", e);
	}
	return null;
    }

    @Override
    public boolean insertBatch(List<Game> games) {
	if (games==null || games.size()==0) {
	    return false;
	}
		    
	String cqlCommand = "INSERT INTO "+TABLE_NAME+" ("+COL_NAME+","+COL_DETAILS+","+COL_IMAGE_ID+","+COL_URL+") VALUES (?, ?, ?, ?)";
	
	try {
	    jdbcTemplate.batchUpdate(cqlCommand, new BatchPreparedStatementSetter() {
		@Override
		public void setValues(PreparedStatement ps, int i) throws SQLException {
		    int j=1;
		    ps.setString(j++, games.get(i).getName());
		    ps.setString(j++, games.get(i).getDetails());
		    ps.setInt(j++, games.get(i).getImage().getId());
		    ps.setString(j++, games.get(i).getUrl());
		}
		        
		@Override
		public int getBatchSize() {
		    return games.size();
		}
	    });
	}
	catch (Exception e) {
	    logger.error("insertBatch games", e);
	    return false;
	}
	return true;
    }

}
