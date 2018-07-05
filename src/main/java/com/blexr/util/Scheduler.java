package com.blexr.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);
    
    @Autowired
    Crawler myCrawler;
    
    @Scheduled(cron = "${scheduler.cron}")
    public void newGamesSearchTask() {
	logger.info("***newGamesSearchTask started***");
	
	if (myCrawler.checkIfInitialStart()) {
	    // no data in database, the crawler starts for the first time
	    myCrawler.startSearchForAllGames();
	}
	else {
	    // the crawler will search only for new added games that are not already in the database
	    myCrawler.startNewSearch();
	}
    }
    
}
