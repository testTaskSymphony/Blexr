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
	
	myCrawler.startNewSearch();
    }
    
}
