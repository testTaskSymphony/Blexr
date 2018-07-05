package com.blexr.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.sql.rowset.serial.SerialBlob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.blexr.dao.impl.GameBrandDaoImpl;
import com.blexr.dao.impl.GameDaoImpl;
import com.blexr.dao.impl.GameJurisdictionDaoImpl;
import com.blexr.dao.impl.GamePlatformDaoImpl;
import com.blexr.dao.impl.GameReelDaoImpl;
import com.blexr.dao.impl.GameTypeDaoImpl;
import com.blexr.dao.impl.ImageDaoImpl;
import com.blexr.dao.impl.JurisdictionDaoImpl;
import com.blexr.dao.impl.UtilsDaoImpl;
import com.blexr.entity.Game;
import com.blexr.entity.GameBrand;
import com.blexr.entity.GamePlatform;
import com.blexr.entity.GameReel;
import com.blexr.entity.GameType;
import com.blexr.entity.Image;
import com.blexr.entity.Jurisdiction;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlItalic;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJobManager;

@Component
public class Crawler {
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);
    
    // game elements
    private String GAME_TITLE = "game-title";
    private String GAME_DATA = "game-data";
    private String GAME_IMAGE = "game-image";
    
//    private String TAG_FILTER_DIV = "ContentPlaceHolder1_main_0_divSelectByWrapper";
    private String TAG_FILTER1 = "ContentPlaceHolder1_main_0_ddlSelectBy";
    private String TAG_FILTER_NEWEST_GAMES = "ContentPlaceHolder1_main_0_ddlNewestGames";
    private String TAG_FILTER3 = "ContentPlaceHolder1_main_0_ddlThemePlatform";
    private String TAG_FILTER_GAME_TYPES = "ContentPlaceHolder1_main_0_ddlGameCategory2";
    
    private String TAG_PAGE_SIZE = "ContentPlaceHolder1_main_0_PageSizeDropDownList";

    private String TAG_PAGE_SIZE_LABEL = "ContentPlaceHolder1_main_0_lblPageSize";
    private String TAG_TOTAL_SIZE_LABEL = "ContentPlaceHolder1_main_0_lblTotalCount";

    // next button id '>'
    private String TAG_NEXT_PAGE_BUTTON = "ContentPlaceHolder1_main_0_ulDataPagerTop";
    
    // jurisdiction elements
//    private String TAG_JURISDICTION_BUTTON = "ContentPlaceHolder1_main_0_spanUpdateJurisdictional";
    private String TAG_JURISDICTION_TABLE = "ctl00_ContentPlaceHolder1_main_0_ListViewJurisdictionsTypeModel";
    private String SAVE_JURISDICTION_BTN_ID = "ContentPlaceHolder1_main_0_JurisdictionSaveLinkButton";
    private String SELECT_ALL_ID = "SelectAllLink";

    // reels elements
    private String REELS_BUTTON_ID = "ContentPlaceHolder1_main_0_anNumberOfReelsTotal";
    private String REELS_RADIO_BUTTON_LIST_ID = "ContentPlaceHolder1_main_0_NumberOfReelsRadioButtonList";

    // brand elements
    private String BRAND_BUTTON_ID = "ContentPlaceHolder1_main_0_anBrandTotal";
    private String BRAND_CHECK_BOX_LIST_ID = "ContentPlaceHolder1_main_0_BrandCheckBoxList";
    
//    private final String SELECT_GAME_ALL = "All Games";
//    private final String SELECT_GAME_NEW = "Newest Games";
    private final String SELECT_GAME_TYPE = "Game Type";
    
    @Autowired
    GameDaoImpl gameDaoImpl;
    
    @Autowired
    ImageDaoImpl imageDaoImpl;
    
    @Autowired
    JurisdictionDaoImpl jurisdictionDaoImpl;
    
    @Autowired
    GameJurisdictionDaoImpl gameJurisdictionDaoImpl;
    
    @Autowired
    GameBrandDaoImpl gameBrandDaoImpl;
    
    @Autowired
    GamePlatformDaoImpl gamePlatformDaoImpl;
    
    @Autowired
    GameReelDaoImpl gameReelDaoImpl;
    
    @Autowired
    GameTypeDaoImpl gameTypeDaoImpl;
    
    @Autowired
    UtilsDaoImpl utilsDaoImpl;
    
    @Value("${crawler.url}")
    private String crawlerUrl;
    
    @Autowired
    EMail eMail;
    
    /*
     * Search only for the new games
     */
    public void startNewSearch() {

	crawl(true);
	
    }
    
    /*
     * Inital search for all games
     */
    public void startSearchForAllGames() {

	crawl(false);
    }
    

    /*
     * Crawl the page for all the available games and get the according game tyoe, brand, jurisdiction and reel
     * 
     * IN: newSearch:
     * 	- true : will search only for new games ('Newest Games' option)
     * 	- false: will search for all availabke games ('All Games' option)
     */
    public void crawl(boolean newSearch) {
	logger.info("crawlerUrl" + crawlerUrl);
	WebClient webClient = null;
	try {
	    
	    // set the browser settings 
	    webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
	    webClient.getOptions().setJavaScriptEnabled(true);
	    webClient.getOptions().setCssEnabled(true);
	    webClient.setAjaxController(new NicelyResynchronizingAjaxController());
	    webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
	    webClient.getOptions().setThrowExceptionOnScriptError(true);
	    
	    // get the page and wait for it to completely load
	    HtmlPage currentPage = webClient.getPage(crawlerUrl);
	    waitForPageToLoad(currentPage);

	    if (newSearch) {
		// prepare the page as a new games search
		// select the 'Newest Games' option of the first filter
		currentPage = selectFilterNewestGames(currentPage);

		// select the lowest 'One month' option of the second filter, so that we don't get older games that are already in db
		currentPage = selectOneMonthPeriod(currentPage);

		getNewGames(currentPage);
	    }
	    else {
		// prepare the page as a all games search (initial crawl)
		// select the 'All Games' option of the first filter
		currentPage = selectFilterAllGames(currentPage);
	    }
	    
	    // refresh the page
//	    currentPage = (HtmlPage) currentPage.refresh();
//	    waitForPageToLoad(currentPage);
	    
	    // get games platform and add them in the db
	    getPlatforms(currentPage);

	    // get games Jurisdictions and add them in the db
	    getJurisdictions(currentPage);

	    // get games reel and add them in the db
	    getReels(currentPage);

	    // get games Brand and add them in the db
	    getBrands(currentPage);
	    
	    // get games types and add them in the db
	    getGameTypes(currentPage);
	    
	}
	catch (Exception e) {
	    logger.error("",e);
	}
	finally {
	    if (webClient!=null) {
		webClient.close();
	    }
	}
	
    }
    
    /* 
     * return the first child node with given name
     */
    private Node getChildNodeByName(Node parentNode, String nodeName) {
	if (parentNode==null || nodeName==null) {
	    return null;
	}
	
	NodeList childNodes = parentNode.getChildNodes();
	Node result = null;
	if (childNodes!=null && childNodes.getLength()>0) {
	    for (int i=0; i<childNodes.getLength(); i++) {
		Node n = childNodes.item(i);
		if (nodeName.equals(n.getNodeName())) {
		    // found it!!
		    result = n;
		    break;
		}
	    }
	}
	
	return result;
    }
    
    /*
     * Convert the htmlImage to Blob
     */
    private Blob htmlImageToBlob(HtmlImage htmlImage) {
	if (htmlImage==null) {
	    return null;
	}
	try {
	    ImageReader imageReader = htmlImage.getImageReader();
	    BufferedImage bufferedImage = imageReader.read(0);
	    String formatName = imageReader.getFormatName();

	    ByteArrayOutputStream byteaOutput = new ByteArrayOutputStream();
	    ImageIO.write(bufferedImage, formatName, byteaOutput);
	    
	    Blob blob = new SerialBlob(byteaOutput.toByteArray());
	    byteaOutput.flush();
	    return blob;
	}
	catch (Exception e) {
	    logger.error("", e);
	    return null;
	}
	finally {
	}
    }
    
    /*
     * get all games from the grid with the selected criteria in 'currentPage'
     * 
     * 1. get the games that are currently in the result grid
     * 2. Check if the next button is enabled and if it is click on it
     * 3. wait for the page to reload and repeat the process for the new results
     */
    private List<Game> getGames(HtmlPage currentPage) {
	HtmlPage nextPage = null;
	List<Game> allGames = new ArrayList<Game>();
	do {
	    // just to be sure that the page is fully loaded with data
	    waitForPageToLoad(currentPage);

	    @SuppressWarnings({ "rawtypes", "unchecked" })
	    List<HtmlListItem> elements = (List) currentPage.getByXPath("//li[@class='three columns']");
	    
//	    // check if the images are loaded
//	    if (!checkIfImagesLoaded(currentPage)) {
//		// if not refresh the page
//		try {
//		    currentPage = (HtmlPage)currentPage.refresh();
//		    waitForPageToLoad(currentPage);
//		}
//		catch (Exception e) {
//		    logger.error("page refresh", e);
//		}
//	    }

	    List<Game> loadedGames = new ArrayList<Game>();

	    if ((elements != null) && (elements.size() > 0)) {
		for (HtmlListItem e : elements) {
		    Game game = new Game();
		    
		    HtmlAnchor a = (HtmlAnchor)e.getElementsByTagName("a").get(0);
		    String gameUrl = a.getAttribute("href");
		    if ( (gameUrl==null) || (gameUrl.trim().length()==0)) {
			continue;
		    }
		    logger.info("gameUrl="+gameUrl);
		    game.setUrl(gameUrl);

		    DomNodeList<HtmlElement> divs = e.getElementsByTagName("div");

		    try {
			for (int i = 0; i < divs.getLength(); i++) {
			    Node div = divs.item(i);

			    NamedNodeMap attributes = div.getAttributes();

			    if (attributes.item(0).getTextContent().equals(GAME_TITLE)) {
				// get the content of the tag and place it in the Game object as Game name
				game.setName(div.getTextContent());
			    } else if (attributes.item(0).getTextContent().equals(GAME_DATA)) {
				// get the content of the tag and place it in the Game object as game details
				game.setDetails(div.getTextContent());
			    } else if (attributes.item(0).getTextContent().equals(GAME_IMAGE)) {
				// get img child node and set it as game image
				HtmlImage imageNode = (HtmlImage) getChildNodeByName(div, "img");

				Image image = new Image();
				if (imageNode!=null) {
				    image.setFile(htmlImageToBlob(imageNode));
				    if (image.getFile()!=null) {
					int blobLength = (int) image.getFile().length();
					byte[] bytes = image.getFile().getBytes(1, blobLength);
					image.setMd5(MD5.generateChecksum(bytes));
					
					// save the image in database as a Blob
					// in order not to duplicate same image for different games, we calculate a md5 hash for each of the images and compare them
					// MD5 column in the database is Unique so that we don't duplicate the same image. If there is a dunplicate MD5, the ID of the original images is returned on insert
					Integer imageId = imageDaoImpl.insert(image);
					image.setId(imageId);
				    }
				}
				
				game.setImage(image);
			    }
			}
		    } catch (Exception ex) {
			logger.error("", ex);
		    }

		    loadedGames.add(game);
		}

		allGames.addAll(loadedGames);
	    } else {
		break;
	    }
	    nextPage = checkForNextPage(currentPage);
	} while (nextPage != null);

	return allGames;
    }
    
    /*
     * select the maximum result size for the result grid. Dropdown next to the "Display" label
     */
    private HtmlPage setMaximumLoadSize(HtmlPage page) {
	if (page==null) {
	    return null;
	}
	// find the html element
	DomElement pageSizeElement = page.getElementById(TAG_PAGE_SIZE);
	if (pageSizeElement==null) {
	    return page;
	}
	
	try {
	    HtmlSelect select = (HtmlSelect)pageSizeElement;
	    int optionSize = select.getOptionSize();
	    select.setSelectedIndex(optionSize-1);
	    return selectClick(select);
	}
	catch (Exception e) {
	    logger.error("setMaximumLoadSize", e);
	    return page;
	}
	
    }
    
    /*
     * returns the size of the loaded results label in the result grid
     * for debug purposes only
     */
    private Integer getResultSize(HtmlPage page) {
	Integer result = 0;
	if (page == null)
	    return result;
	DomElement pageSizeLabel = page.getElementById(TAG_PAGE_SIZE_LABEL);
	if (pageSizeLabel != null) {
	    String label = pageSizeLabel.getTextContent();
	    label = label.split("-")[1].trim();
	    if (label != null && label.length() > 0) {
		try {
		    result = Integer.parseInt(label);
		} catch (NumberFormatException e) {
		    logger.error("getResultSize", e);
		    result = 0;
		}
	    }
	}
	return result;
    }
    
    /*
     * returns the size of the total label in the result grid
     * for debug purposes only
     */
    private Integer getTotalSize(HtmlPage page) {
	Integer result = 0;
	if (page == null)
	    return result;
	DomElement totalSizeLabel = page.getElementById(TAG_TOTAL_SIZE_LABEL);
	if (totalSizeLabel != null) {
	    String label = totalSizeLabel.getTextContent();
	    label = label.split(" ")[0].trim();
	    if (label != null && label.length() > 0) {
		try {
		    result = Integer.parseInt(label);
		} catch (NumberFormatException e) {
		    logger.error("getTotalSize", e);
		    result = 0;
		}
	    }
	}
	return result;
    }
    
    
    /*
     * crawl the games and get their Platforms
     * 
     * 1. Select the maximum size of result grid
     * 2. Click on the +Reels option above the result grid
     * 3. go through all the checkboxes that appear. Get the resulting games in the grid and add the reel
     * 4. save the reels in db
     */
    private void getPlatforms(HtmlPage page) {
	
	List<Game> allGames = new ArrayList<Game>();
	
	// load all games from database
	List<Game> gamesInDb = gameDaoImpl.getAll();
	
	try {
	    // set the maximum size of results per page (100)
	    page = setMaximumLoadSize(page);

	    try {
		// get the platform dropdown element ('Refine Search with Platform')
		DomElement filterPlatform = page.getElementById(TAG_FILTER3);
		if (filterPlatform != null) {
		    HtmlSelect selectPlatform = (HtmlSelect) filterPlatform;
		    List<HtmlOption> optionsPlatform = selectPlatform.getOptions();
		    if (optionsPlatform != null && optionsPlatform.size() > 0) {
			// skip index 0 'All Platforms'
			for (int j = 1; j < optionsPlatform.size(); j++) {

			    // select the next option of the platform filter ( 'Refine Search with Platform')
			    selectPlatform = (HtmlSelect) page.getElementById(TAG_FILTER3);
			    selectPlatform.setSelectedIndex(j);
			    page = selectClick(selectPlatform);

			    // check if the images are loaded
			    if (!checkIfImagesLoaded(page)) {
				// if not refresh the page
				try {
				    page = (HtmlPage)page.refresh();
				    waitForPageToLoad(page);
				}
				catch (Exception e) {
				    logger.error("page refresh", e);
				}
			    }

			    // get all games with selected filters
			    allGames = getGames(page);

			    // add the platform name to the game object
			    String platformName = selectPlatform.getOptions().get(j).getTextContent();
			    platformName = platformName.substring(0, platformName.indexOf('(')).trim();
			    for (Game g : allGames) {
				g.getPlatformList().add(platformName);
			    }
			    
			    // go through the games and set the platform for each of them
			    // if there is a new game that is not in the database - insert it
			    for (Game g : allGames) {
				if (g.getId() == null) {
				    boolean found = false;
				    for (Game gDb : gamesInDb) {
					if (g.equals(gDb)) {
					    g.setId(gDb.getId());
					    found = true;
					    break;
					}
				    }
				    if (!found) {
					// new game.. insert game in database
					Integer gameId = gameDaoImpl.insert(g);
					g.setId(gameId);
				    }
				}
				GamePlatform gamePlatform = new GamePlatform();
				gamePlatform.setGameId(g.getId());
				for (String platform : g.getPlatformList()) {
				    gamePlatform.setPlatformName(platform);
				    gamePlatformDaoImpl.insert(gamePlatform);
				}
			    }
			}
		    }
		} else {
		    logger.info("filterPLatform = null");
		}
	    } catch (Exception e) {
		logger.error("getPlatforms", e);
	    }

	}
	catch (Exception e) {
	    logger.error("getFilter",e);
	}

    }
    
    /*
     * get the new games
     * 1. set the maximum size of results per page (100)
     * 2. get the games from result grid and save them in database
     */
    private void getNewGames(HtmlPage page) {
	
	List<Game> allGames = new ArrayList<Game>();
	List<Game> newGames = new ArrayList<Game>();
	
	List<Game> gamesInDb = gameDaoImpl.getAll();
	try {
	    // set the maximum size of results per page (100)
	    page = setMaximumLoadSize(page);
	    
	    // check if the images are loaded
	    if (!checkIfImagesLoaded(page)) {
		// if not refresh the page
		try {
		    page = (HtmlPage) page.refresh();
		    waitForPageToLoad(page);
		} catch (Exception e) {
		    logger.error("page refresh", e);
		}
	    }

	    // get all games with selected filters
	    allGames = getGames(page);
	    
	    if (allGames!=null && allGames.size()>0) {
		for (Game game : allGames) {
		    boolean found = false;
		    for (Game gDb : gamesInDb) {
			if (gDb.equals(game)) {
			    found = true;
			    break;
			}
		    }
		    
		    if (!found) {
			// new game that is not in the database
			newGames.add(game);
		    }
		}
	    }
	    
	    if((newGames!=null) && newGames.size()>0) {
		// there are new games that are not in the database already
		
		// insert the new games in database
		gameDaoImpl.insertBatch(newGames);

		// send mail
		String messageBody = "";
		for (Game g : newGames) {
		    if (messageBody.length()>0) {
			messageBody += ", ";
		    }
		    messageBody += g.getName();
		}
		messageBody = "New games added: " + messageBody;
		
		// send the mail with default subject from the properties file
		eMail.sendMessage(null, messageBody);
	    }
	}
	catch (Exception e) {
	    logger.error("getFilter",e);
	}

    }
    
    /*
     * Simulate click on a select element with already selected option
     */
    private HtmlPage selectClick(HtmlSelect select) {
	HtmlPage page = null;

	if (select==null) {
	    return null;
	}
	
	try {
	    logger.info("SELECTED "+select.getOption(select.getSelectedIndex()).getTextContent());
	    page = select.click();
	    waitForPageToLoad(page);
	    page.getWebClient().waitForBackgroundJavaScript(2000);
	}
	catch (Exception e) {
	    logger.error("selectClick", e);
	}
	return page;
    }
    
    /*
     * Check if there is next button and it is enabled.
     * If the button is enabled click it and go back to load the next data in grid
     */
    private HtmlPage checkForNextPage (HtmlPage page) {
	HtmlUnorderedList nextPageButton = (HtmlUnorderedList) page.getElementById(TAG_NEXT_PAGE_BUTTON);// HtmlUnorderedList
													 // HtmlListItem<li>
													 // HtmlAnchor
													 // <a>
	try {
	    if (nextPageButton != null) {
		DomNodeList<HtmlElement> list = nextPageButton.getElementsByTagName("li");
		if (list != null && list.size() > 0) {
		    for (int i = 0; i < list.size(); i++) {
			HtmlListItem listElement = (HtmlListItem) list.get(i);
			DomNodeList<HtmlElement> listAnchors = listElement.getElementsByTagName("a");
			if ( (listAnchors != null) && (listAnchors.size()>0) ) {
			    HtmlAnchor a = (HtmlAnchor) listAnchors.get(0);
			    if (!a.getTextContent().equals("›")) {
				// this is not the next button ">"
				continue;
			    }

			    // this is the next button
			    if (a.hasAttribute("class")) {
				String classAttribute = a.getAttribute("class");
				if (classAttribute != null && classAttribute.equalsIgnoreCase("aspNetDisabled")) {
				    return null;
				}

			    } else if (a.hasAttribute("href")) {
				String hrefAttribute = a.getAttribute("href");
				if (hrefAttribute != null) {
				    page = (HtmlPage)page.executeJavaScript(hrefAttribute).getNewPage();
				    waitForPageToLoad(page);
				    return page;
				}
			    }

			}
		    }
		}
	    } else {
		return null;
	    }

	} catch (Exception e) {

	    logger.error("checkForNextPage", e);
	}
	return null;
	
    }
    
    /*
     * Make sure that the whole page is loaded and all java scripts are executed
     */
    private void waitForPageToLoad(HtmlPage page) {
	logger.info("waitForPageToLoad");
	try {
	    JavaScriptJobManager manager = page.getEnclosingWindow().getJobManager();
	    while (manager.getJobCount() > 0) {
		Thread.sleep(1000);
	    }

	} catch (Exception e) {
	    logger.error("waitForPageToLoad", e);
	}
    }
    
    /*
     * crawl the games for the Reels filter
     * 
     * 1. Select the maximum size of result grid
     * 2. Click on the +Reels option above the result grid
     * 3. go through all the checkboxes that appear. Get the resulting games in the grid and add the reel
     * 4. save the reels in db
     */
    private void getReels(HtmlPage page) {
	// get the games from database
	List<Game> gamesInDb = gameDaoImpl.getAll();

	try {
	    // set the maximum size of result grid in order to get the games with as less possible clicking on the 'next page' button
	    page = setMaximumLoadSize(page);
	    
	    // get the '+Reels' button and click it. Wait for the page to reload
	    HtmlAnchor showReelsFilterButton = (HtmlAnchor) page.getElementById(REELS_BUTTON_ID);
	    page = showReelsFilterButton.click();
	    waitForPageToLoad(page);
	    
	    // get the radio button list of the availabkle reels
	    HtmlUnorderedList reelList = (HtmlUnorderedList) page.getElementById(REELS_RADIO_BUTTON_LIST_ID);
	    int reelListSize = reelList.getElementsByTagName("li").size();

	    // skip index 0 (All)
	    for (int i=1; i<reelListSize; i++) {
		// get element from unfiltered page
		reelList = (HtmlUnorderedList) page.getElementById(REELS_RADIO_BUTTON_LIST_ID);
		HtmlListItem li = (HtmlListItem) reelList.getElementsByTagName("li").get(i);
		HtmlLabel label = (HtmlLabel) li.getElementsByTagName("label").get(0);
		logger.info("label:" + label.getTextContent());
		
		page = label.click();
		waitForPageToLoad(page);
		
		// debug info only {
		int totalContentSize = getTotalSize(page);
		int  loadContentSize = getResultSize(page);
		logger.info("LOADED GAMES=" + loadContentSize);
		logger.info("TOTAL GAMES=" + totalContentSize);
		// debug info only }
		
		// check if the images are loaded
		if (!checkIfImagesLoaded(page)) {
		    // if not refresh the page
		    try {
			page = (HtmlPage) page.refresh();
			waitForPageToLoad(page);
		    } catch (Exception e) {
			logger.error("page refresh", e);
		    }
		}

		// get the loaded games for a single selected reel
		List<Game> allGames = getGames(page);

		// add reel to game
		String reel = label.getTextContent();
		reel = reel.substring(0, reel.indexOf('('));
		reel = reel.trim();
		logger.info("***" + reel + "***");
		if (allGames != null) {
		    for (Game game : allGames) {
			if (game.getId()==null) {
			    boolean found = false;
			    for (Game gDb : gamesInDb) {
				if (game.equals(gDb)) {
				    game.setId(gDb.getId());
				    found = true;
				    break;
				}
			    }
			    if (!found) {
				Integer gameId = gameDaoImpl.insert(game);
				game.setId(gameId);
			    }
			}
			
			// add the game reel in db
			GameReel gameReel = new GameReel();
			gameReel.setGameId(game.getId());
			gameReel.setReel(reel);
			gameReelDaoImpl.insert(gameReel);
			
		    }
		} else {
		    logger.info("games list is null");
		}
		
		// check if there are more radio buttons left 
		if (i+1<reelListSize) {
		    // click again on all to enable the rest radio buttons
		    HtmlListItem liAll = (HtmlListItem) reelList.getElementsByTagName("li").get(i);
		    HtmlLabel labelAll = (HtmlLabel) liAll.getElementsByTagName("label").get(0);

		    page = labelAll.click();
		    waitForPageToLoad(page);
		}
	    }
	}
	catch (Exception e) {
	    logger.error("getReels", e);
	}
    }
    
    /*
     * crawl the games for the Brands filter
     * 
     * 1. Select the 'All Games' option of the first filter
     * 2. Click on the +Brand option above the result grid
     * 3. go through all the checkboxes that appear. Get the resulting games in the grid and add the addecuate brand
     * 4. save the brands in db
     */
    private void getBrands(HtmlPage page) {
	
	// get the games from database
	List<Game> gamesInDb = gameDaoImpl.getAll();

	try {
	    // select the 'All Games' option in the filter
	    page = selectFilterAllGames(page);
	    
	    // set the maximum size of result grid in order to get the games with as less possible clicking on the 'next page' button
	    page = setMaximumLoadSize(page);
	    
	    // find the "+Brand" button to expand the checkbox list options
	    HtmlAnchor showBrandsFilterButton = (HtmlAnchor) page.getElementById(BRAND_BUTTON_ID);
	    page = showBrandsFilterButton.click();
	    waitForPageToLoad(page);
	    
	    HtmlUnorderedList brandList = (HtmlUnorderedList) page.getElementById(BRAND_CHECK_BOX_LIST_ID);
	    int brandListSize = brandList.getElementsByTagName("li").size();

	    // go through all checkbox options
	    for (int i=0; i<brandListSize; i++) {

		// get element from unfiltered page
		brandList = (HtmlUnorderedList) page.getElementById(BRAND_CHECK_BOX_LIST_ID);
		HtmlListItem li = (HtmlListItem) brandList.getElementsByTagName("li").get(i);
		HtmlLabel label = (HtmlLabel) li.getElementsByTagName("label").get(0);
		String brand = label.getTextContent().substring(0, label.getTextContent().indexOf('(')).trim();
		logger.info("brand:" + brand);
		// select the brand
		page = label.click();
		waitForPageToLoad(page);
		
		// debug info only {
		int totalContentSize = getTotalSize(page);
		int loadContentSize = getResultSize(page);
		logger.info("LOADED GAMES=" + loadContentSize);
		logger.info("TOTAL GAMES=" + totalContentSize);
		// debug info only }
		
		// check if the images are loaded
		if (!checkIfImagesLoaded(page)) {
		    // if not refresh the page
		    try {
			page = (HtmlPage) page.refresh();
			waitForPageToLoad(page);
		    } catch (Exception e) {
			logger.error("page refresh", e);
		    }
		}

		// get the loaded games for a single selected brand
		List<Game> games = getGames(page);
		
		// add brand to game
		logger.info("***" + brand + "***");
		if (games != null) {
		    for (Game game : games) {
			if (game.getId()==null) {
			    boolean found = false;
			    for (Game gDb : gamesInDb) {
				if (game.equals(gDb)) {
				    game.setId(gDb.getId());
				    found = true;
				    break;
				}
			    }
			    if (!found) {
				Integer gameId = gameDaoImpl.insert(game);
				game.setId(gameId);
			    }
			}
			
			// add the game brand in db
			GameBrand gameBrand = new GameBrand();
			gameBrand.setGameId(game.getId());
			gameBrand.setBrandName(brand);
			gameBrandDaoImpl.insert(gameBrand);
			
		    }
		} else {
		    logger.info("games list is null");
		}

		// we need to deselect the selected checkbox to enable the next one
		// check if there are more checkboxes left 
		if (i+1<brandListSize) {
		    // click again on all to enable the rest radio buttons
		    li = (HtmlListItem) brandList.getElementsByTagName("li").get(i);
		    label = (HtmlLabel) li.getElementsByTagName("label").get(0);

		    page = label.click();
		    waitForPageToLoad(page);
		}

	    }
	}
	catch (Exception e) {
	    logger.error("getBrands", e);
	}
    }

    /*
     * crawl the games with the game type filter
     * 
     * 1. Select the 'Game Type' option of the first filter
     * 2. go through the options of the second filter ' Search Games by Game Type' and save the game type for each of the games in the result grid
     * 3. save the game type in db
     */
    private void getGameTypes(HtmlPage page) {
	List<Game> gamesInDb = gameDaoImpl.getAll();
	
	try {
	    // select the 'Game Types' option in the filter
	    page = selectGameTypes(page);
	    
	    // find the "Search Games by Game Type" dropdown list
	    HtmlSelect selectGameTypes = (HtmlSelect) page.getElementById(TAG_FILTER_GAME_TYPES);
	    for (int i=1; i<selectGameTypes.getOptionSize(); i++) {
		
		String gameTypeString = selectGameTypes.getOption(i).getTextContent();
		logger.info("option:"+gameTypeString );
		
		// click the option to refresh the page
		selectGameTypes = (HtmlSelect) page.getElementById(TAG_FILTER_GAME_TYPES);
		selectGameTypes.setSelectedIndex(i);
		waitForPageToLoad(page);
		
		// check if the images are loaded
		if (!checkIfImagesLoaded(page)) {
		    // if not refresh the page
		    try {
			page = (HtmlPage) page.refresh();
			waitForPageToLoad(page);
		    } catch (Exception e) {
			logger.error("page refresh", e);
		    }
		}

		// get the games for the selected game type
		List<Game> loadedGames = getGames(page);
		
		// add game type to Game object
		logger.info("***" + gameTypeString + "***");
		if (loadedGames != null) {
		    for (Game game : loadedGames) {
			if (game.getId()==null) {
			    boolean found = false;
			    for (Game gDb : gamesInDb) {
				if (game.equals(gDb)) {
				    game.setId(gDb.getId());
				    found = true;
				    break;
				}
			    }
			    if (!found) {
				Integer gameId = gameDaoImpl.insert(game);
				game.setId(gameId);
			    }
			}
			
			GameType gameType = new GameType();
			gameType.setGameId(game.getId());
			gameType.setGameType(gameTypeString);
			gameTypeDaoImpl.insert(gameType);
		    }
		} else {
		    logger.info("games list is null");
		}

	    }
	}
	catch (Exception e) {
	    logger.error("", e);
	}
	    
    }
    
    private boolean checkIfJurisdictionsAreLoaded (HtmlPage page) {
	try {
	    DomElement jurisdictionTable = page.getElementById(TAG_JURISDICTION_TABLE);
	    if (jurisdictionTable != null) {
		DomNodeList<HtmlElement> tbody = jurisdictionTable.getElementsByTagName("tbody");
		if (tbody != null && tbody.size() > 0) {
		    DomNodeList<HtmlElement> rows = tbody.get(0).getElementsByTagName("tr");
		    HtmlAnchor a = (HtmlAnchor) rows.get(0).getElementsByTagName("a").get(0);

		    if ((a != null) && (a.getTextContent() != null) && (a.getTextContent().trim().length() != 0)) {
			return true;
		    }
		}
	    }
	} catch (Exception e) {
	    logger.error("", e);
	}
	return false;
    }
    
    /*
     * Get all the jurisdictions of the jurisdiction modal
     */
    private List<Jurisdiction> getAllJurisdictionElements(HtmlPage page) {
	List<Jurisdiction> list = new ArrayList<Jurisdiction>();
	DomElement jurisdictionTable = page.getElementById(TAG_JURISDICTION_TABLE);
	if (jurisdictionTable!=null) {
	    DomNodeList<HtmlElement> tbody = jurisdictionTable.getElementsByTagName("tbody");
	    if (tbody!=null && tbody.size()>0) {
		DomNodeList<HtmlElement> rows = tbody.get(0).getElementsByTagName("tr");
		for (HtmlElement row : rows) {
		    Jurisdiction j = new Jurisdiction();
		    HtmlAnchor a = (HtmlAnchor) row.getElementsByTagName("a").get(0);
		    HtmlListItem li = (HtmlListItem) row.getElementsByTagName("li").get(0);
		    j.setName(a.getTextContent().trim());
		    j.setElementId(li.getAttribute("id"));
		    list.add(j);
		}
	    }
	}
	return list;
    }
    
    /*
     * 1. find jurisdiction button and click it
     * 2. wait for page to load
     * 3. deselect all jurisdiction
     * 4. select one jurisdiction and go through the games and update the game objects
     * 5. deselect the jurisdiction and repeat the steps with the next jurisdiction
     * 
     */
    private void getJurisdictions(HtmlPage page) {
	
	// check if the page is loaded including the jurisdictions
	if (!checkIfJurisdictionsAreLoaded(page)) {
	    try {
		page = (HtmlPage)page.refresh();
		waitForPageToLoad(page);
	    }
	    catch (Exception e) {
		logger.error("", e);
	    }
	}
	
	List<Jurisdiction> jurisdictionList = getAllJurisdictionElements(page);
	jurisdictionDaoImpl.insertBatch(jurisdictionList);
	
	// get all games that are already stored in db
	// presumably all available games are in db at this point, now we just need to update the jurisdictions
	List<Game> gamesInDb = gameDaoImpl.getAll();
	
	try {
	    // set the maximum size of result grid in order to get the games with as less possible clicking on the 'next page' button
	    page = setMaximumLoadSize(page);
	    
	    // debug info only {
	    int totalContentSize = getTotalSize(page);
	    int loadContentSize = getResultSize(page);
	    logger.info("LOADED GAMES=" + loadContentSize);
	    logger.info("TOTAL GAMES=" + totalContentSize);
	    // debug info only }
	    
	    for (Jurisdiction j : jurisdictionList) {
		// open jurisdiction modal
		HtmlAnchor openJurisdictionButtton = (HtmlAnchor) page.getByXPath("//a[@class='switch active selected_jurisdictions_button']").get(0);
		page = openJurisdictionButtton.click();
		    
		// if select all is checked we need to uncheck it
		page = deselectJurisdictions(page);

		// find the element to select a single jurisdiction
		HtmlListItem li = (HtmlListItem) page.getElementById(j.getElementId());
		HtmlAnchor a = (HtmlAnchor) li.getElementsByTagName("a").get(0);

		// select jurisdiction
		page = a.click();

		// find the Save button inside the jurisdiction modal
		HtmlAnchor saveButton = (HtmlAnchor) page.getElementById(SAVE_JURISDICTION_BTN_ID);
		// execute the javascript to save the selected jurisdiction
		page = executeHrefAttribute(page, saveButton);
		waitForPageToLoad(page);
		
		// check if the images are loaded
		if (!checkIfImagesLoaded(page)) {
		    // if not refresh the page
		    try {
			page = (HtmlPage) page.refresh();
			waitForPageToLoad(page);
		    } catch (Exception e) {
			logger.error("page refresh", e);
		    }
		}

		// get the loaded games
		List<Game> games = getGames(page);
		
		// add jurisdiction to game
		logger.info("***" + j.getName() + "***");
		if (games != null) {
		    for (Game game : games) {
			boolean found = false;
			for (int i=0; i<gamesInDb.size(); i++) {
			    if (gamesInDb.get(i).equals(game)) {
				found = true;
				gamesInDb.get(i).getJurisdictionList().add(j.getName());
				
				gameJurisdictionDaoImpl.insert(gamesInDb.get(i).getId(), j.getName());
				continue;
			    }

			}
			if (!found) {
			    int gameId = gameDaoImpl.insert(game);
			    gameJurisdictionDaoImpl.insert(gameId, j.getName());
			}
		    }
		} else {
		    logger.info("games list is null");
		}
		
		// deselect jurisdiction before going to the next one
		li = (HtmlListItem) page.getElementById(j.getElementId());
		a = (HtmlAnchor) li.getElementsByTagName("a").get(0);
		page = a.click();
		
		// reload the Save button inside the jurisdiction modal
		saveButton = (HtmlAnchor) page.getElementById(SAVE_JURISDICTION_BTN_ID);
		// execute the javascript to save the selected jurisdiction
		page = executeHrefAttribute(page, saveButton);
		waitForPageToLoad(page);
	    }

	}
	catch (Exception e) {
	    logger.error("getJurisdictions",e);
	}

    }
    
    /*
     *  check if 'select all' is checked or not. 
     *  if the class of the element is 'fa fa-check-square-o' than it is checked
     */
    private HtmlPage deselectJurisdictions (HtmlPage page) {
	try {
	    HtmlAnchor selectAllButton = (HtmlAnchor) page.getElementById(SELECT_ALL_ID);
	    HtmlItalic checkBoxElement = (HtmlItalic) selectAllButton.getElementsByTagName("i").get(0);
	    // check the class of the element. If the value of the class parameter is "fa fa-check-square-o" than the chekcbox is selected 
	    String checkBoxValue = checkBoxElement.getAttribute("class");
	    if ("fa fa-check-square-o".equals(checkBoxValue)) {
		// selected all checked
		// we need to deselect it
		page = selectAllButton.click();
		waitForPageToLoad(page);
	    }
	}
	catch (Exception e) {
	    logger.error("deselectJurisdictions", e);
	}
	return page;
    }

    /*
     * Select the 'All Games' option from the first filter "How do you want to search for games?"
     */
    private HtmlPage selectFilterAllGames(HtmlPage page) {
	try {
	    DomElement filterOne = page.getElementById(TAG_FILTER1);
	    HtmlSelect select = (HtmlSelect) filterOne;

	    select.setSelectedIndex(1);
	    page = selectClick(select);
	} catch (Exception e) {
	    logger.error("selectFilterAllGames", e);
	}

	return page;
    }
    
    /*
     * Select the 'Newest Games' option from the first filter "How do you want to search for games?"
     */
    private HtmlPage selectFilterNewestGames(HtmlPage page) {
	try {
	    DomElement filterOne = page.getElementById(TAG_FILTER1);
	    HtmlSelect select = (HtmlSelect) filterOne;

	    select.setSelectedIndex(2);
	    page = selectClick(select);
	} catch (Exception e) {
	    logger.error("selectFilterAllGames", e);
	}
	return page;
    }
    
    /*
     * Select the 'Game Type' option from the first filter "How do you want to search for games?"
     */
    private HtmlPage selectGameTypes(HtmlPage page) {
	try {
	    DomElement filterOne = page.getElementById(TAG_FILTER1);
	    HtmlSelect select = (HtmlSelect) filterOne;

	    for(HtmlOption o: select.getOptions() ) {
		if (SELECT_GAME_TYPE.equalsIgnoreCase(o.getTextContent().trim()) ) {
		    select.setSelectedAttribute(o, true);
		    break;
		}
	    }
	    page = selectClick(select);
	} catch (Exception e) {
	    logger.error("selectFilterAllGames", e);
	}
	return page;
    }
    
    /*
     * Select the first (lowest) option of the 'Search Games by Newest Games' filter
     */
    private HtmlPage selectOneMonthPeriod(HtmlPage page) {
	try {
	    HtmlSelect select = (HtmlSelect)page.getElementById(TAG_FILTER_NEWEST_GAMES);

	    select.setSelectedIndex(0);
	    page = selectClick(select);
	} catch (Exception e) {
	    logger.error("selectFilterAllGames", e);
	}
	return page;
    }
    
    // execute javascript from href attribute
    private HtmlPage executeHrefAttribute(HtmlPage page, HtmlElement element) {
	try {
	    if (element.hasAttribute("href")) {
		String hrefAttribute = element.getAttribute("href");
		logger.info("href=" + hrefAttribute);
		if (hrefAttribute != null) {
		    page = (HtmlPage) page.executeJavaScript(hrefAttribute).getNewPage();
		    waitForPageToLoad(page);
		    return page;
		}
	    }
	}
	catch (Exception e) {
	    logger.error("executeHrefAttribute", e);
	}
	return page;
	
    }
    
    /*
     * check if the images are loaded
     * if the game images are missing return shouldRefresh = true;
     */
    private boolean checkIfImagesLoaded(HtmlPage page) {
	boolean imagesLoaded = true;
	
	try {
	    @SuppressWarnings({ "rawtypes", "unchecked" })
	    List<HtmlListItem> divImages = (List) page.getByXPath("//div[@class='game-image']");
	    if (divImages != null && divImages.size() > 0) {
		HtmlElement item = (HtmlElement) divImages.get(0);
		DomNodeList<?> list = item.getElementsByTagName("img");
		if (list == null || list.size() == 0) {
		    // image missing should refresh page
		    imagesLoaded = false;
		}
	    } else {
		// should refresh page
		imagesLoaded = false;
	    }
	} catch(Exception e) {
	    logger.info("checkIfImagesLoaded", e);
	    return false;
	}
	return imagesLoaded;
    }
    
    public boolean checkIfInitialStart() {
	return utilsDaoImpl.isInitialAppStart();
    }
    
}
