package com.marimbus.live365;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.firefox.FirefoxDriver;



import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(Parameterized.class)
public class Live365SearchStationTest {
	private static WebDriver driver;
	private static WebDriver dataDriver;
	private static final String MAIN_PAGE_TITLE = "Live365 Internet Radio Network - Listen to Free Music, Online Radio";
	private static final String EXPECTED_PAGE_TITLE_TEMPLATE = "Listen to Free %s Music Online - Live365 Internet Radio";
	private static String baseUrl;
	private String SearchKeyWord;
	private static StringBuffer verificationErrors = new StringBuffer();


	@Parameters
	public static Collection<Object[]> ListOfTopStations(){
		List<Object[]> listOfTopStations = new ArrayList<Object[]>();
//				initiate a Chrome instance to extract radio station titles
		System.setProperty("webdriver.chrome.driver", "C:/selenium/chromedriver.exe");
		dataDriver = new ChromeDriver();
		dataDriver.get("http://www.live365.com/new/index.live/");
		dataDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
//						navigate to genre page		
		dataDriver.findElement(By.xpath(".//*[@class='headerlinks']//li[1]/a")).click();			
		dataDriver.switchTo().frame("contentFrame");
		dataDriver.switchTo().frame(dataDriver.findElement(By.cssSelector(".tabFrame")));		   
//                  create station titles list
		List<WebElement> genreElementsList = dataDriver.findElements(By.xpath(".//*[@id='tagCloud']/a"));
//         			 populate  object Arraylist with data objects for data-driven test
		for (WebElement elem : genreElementsList) {
			Object[] singleTest = {elem};
			listOfTopStations.add(singleTest);				
		}
		return listOfTopStations;
	}
	public Live365SearchStationTest(WebElement el){
		SearchKeyWord = el.getText();		
	}	

	@BeforeClass
	public static void setUp() throws Exception {	
//	            	initiate a Chrome instance to run tests
		driver = new ChromeDriver();
		baseUrl = "http://www.live365.com/new/index.live/";
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void Test() throws Exception {
		System.out.println("Radio station to verify - "+ SearchKeyWord);
		driver.get(baseUrl);

//                   assert page title
		assertEquals(MAIN_PAGE_TITLE, driver.getTitle());

//					navigate to search station field
		WebElement searchField = driver.findElement(By.name("query"));
		searchField.clear();
		searchField.sendKeys(SearchKeyWord);		
		driver.findElement(By.xpath(".//*[@class='searchForm']/input")).click();

//					assert search result page title
		String s = String.format(EXPECTED_PAGE_TITLE_TEMPLATE, SearchKeyWord).toLowerCase();
		assertEquals(s, driver.getTitle().toLowerCase());

		//                  assert crumbhead title
		driver.switchTo().frame("contentFrame");
		WebElement searchResult = driver.findElement(By.id("crumbhead"));
		String expectedResult = String.format("top %s stations", SearchKeyWord).toLowerCase();
		String actualResult = searchResult.getText().toLowerCase();
		assertEquals(expectedResult, actualResult);
	}
	@AfterClass
	public static void tearDown() throws Exception {
		driver.quit();
		dataDriver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}
}