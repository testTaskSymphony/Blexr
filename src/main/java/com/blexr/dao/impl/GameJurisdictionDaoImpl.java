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

import com.blexr.dao.GameJurisdictionDao;
import com.blexr.entity.GameJurisdiction;

public class GameJurisdictionDaoImpl implements GameJurisdictionDao {
    
    private static final Logger logger = LoggerFactory.getLogger(GameJurisdictionDaoImpl.class);
    
    private String TABLE_NAME = "game_jurisdiction";
    
    private String COL_GAME_ID = "GAME_ID";
    private String COL_JURISDICTION_ID = "JURISDICTION_ID";
    
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public boolean insert(GameJurisdiction gameJurisdiction) {
	if (gameJurisdiction==null) {
	    return false;
	}
	
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put(COL_GAME_ID, gameJurisdiction.getGame_id());
	    parameters.put(COL_JURISDICTION_ID, gameJurisdiction.getJurisdiction_id());

	    SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource());

	    simpleJdbcInsert.setTableName(TABLE_NAME);

	    int insertedId = simpleJdbcInsert.execute(parameters);
	    return insertedId>0;
	} catch (Exception e) {
	    logger.error("Exception insert", e);
	    return false;
	}
    }

    @Override
    public boolean delete(Integer gameId, Integer jurisdictionId) {
	if (gameId==null || jurisdictionId==null) {
	    return false;
	}
	String sqlCommand = "DELETE FROM "+TABLE_NAME+ " WHERE "+COL_GAME_ID+"=? AND "+COL_JURISDICTION_ID+"=? ";

	try {
	    int rows = jdbcTemplate.update(sqlCommand, new Object[] {gameId, jurisdictionId});
	    return rows > 0;
	}
	catch (Exception e) {
	    logger.error("delete", e);
	}
	return false;
    }

    @Override
    public List<GameJurisdiction> getAll() {
	String sqlCommand = "SELECT * FROM "+TABLE_NAME;
	try {
	    List<GameJurisdiction> gameJurisdictionList = jdbcTemplate.query(sqlCommand, new BeanPropertyRowMapper<GameJurisdiction>(GameJurisdiction.class));
	    return gameJurisdictionList;
	}
	catch (Exception e) {
	    logger.error("getAll", e);
	}
	return null;
    }

    @Override
    public boolean insertBatch(List<GameJurisdiction> gameJurisdictionList) {
	if (gameJurisdictionList==null || gameJurisdictionList.size()==0) {
	    return false;
	}

	String sqlCommand = "INSERT IGNORE INTO "+TABLE_NAME+" ("+COL_GAME_ID+","+COL_JURISDICTION_ID+") VALUES (?, ?)";
	
	try {
	    jdbcTemplate.batchUpdate(sqlCommand, new BatchPreparedStatementSetter() {
		@Override
		public void setValues(PreparedStatement ps, int i) throws SQLException {
		    int j=1;
		    ps.setInt(j++, gameJurisdictionList.get(i).getGame_id());
		    ps.setInt(j++, gameJurisdictionList.get(i).getJurisdiction_id());
		}
		        
		@Override
		public int getBatchSize() {
		    return gameJurisdictionList.size();
		}
	    });
	}
	catch (Exception e) {
	    logger.error("insertBatch", e);
	    return false;
	}
	return true;
    }

    @Override
    public boolean insert(Integer gameId, String jurisdictionName) {
	if (gameId==null || jurisdictionName==null) {
	    return false;
	}
	String sqlCommand = "INSERT INTO "+TABLE_NAME+" ("+COL_GAME_ID+", "+COL_JURISDICTION_ID+") VALUES (?, "
		+ "(SELECT ID FROM jurisdiction WHERE NAME=?) )";
	try {
	    int rows = jdbcTemplate.update(sqlCommand, new Object[] {gameId, jurisdictionName});
	    return rows >0;
	}catch (Exception e) {
	    logger.error("insert", e);
	}
	return false;
    }

    
}
