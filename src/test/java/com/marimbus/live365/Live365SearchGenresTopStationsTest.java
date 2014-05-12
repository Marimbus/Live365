package com.marimbus.live365;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(Parameterized.class)

public class Live365SearchGenresTopStationsTest {

	private static WebDriver driver;
	private final String MAIN_PAGE_TITLE = "Live365 Internet Radio Network - Listen to Free Music, Online Radio";
	private final String EXPECTED_PAGE_TITLE_TEMPLATE = "Listen to Free %s Music Online - Live365 Internet Radio";
	private static String baseUrl = "http://www.live365.com/new/index.live/";
	private String currentGenre;
	private static StringBuffer verificationErrors = new StringBuffer();

	private static List<String> getListOfGenres(){			
		List<String> strListOfGenreTopStations = new ArrayList<String>();
		//                initialize chrome instance		                 
		System.setProperty("webdriver.chrome.driver", "C:/selenium/chromedriver.exe");
		driver = new ChromeDriver();
		driver.get(baseUrl);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		//						navigate to genres tech cloud page		
		driver.findElement(By.xpath(".//*[@class='headerlinks']//li[1]/a")).click();			
		driver.switchTo().frame("contentFrame");
		driver.switchTo().frame(driver.findElement(By.cssSelector(".tabFrame")));		   

		//                  create genre station titles list extracted from tech cloud
		List<WebElement> genreElementsList = driver.findElements(By.xpath(".//*[@id='tagCloud']/a"));

		//         			 convert it to string list, then  output to a console
		for (WebElement elem : genreElementsList) {
			String ell= elem.getText();
			strListOfGenreTopStations.add(ell);
			System.out.println("Extracted genre - "+ ell.toUpperCase());
		}			
		System.out.println("=======================================");
		System.out.println();
		return strListOfGenreTopStations;		
	}	
	@Parameters
	public static Collection<Object[]> ListOfTopStations(){
		List<Object[]> genresList = new ArrayList<Object[]>();	

		//         	convert strings list to list of object arrays  for data-driven test
		for (String elem : Live365SearchGenresTopStationsTest.getListOfGenres()) {
			Object[] singleTest = {elem};
			genresList.add(singleTest);				
		}
		return genresList;
	}
	public Live365SearchGenresTopStationsTest(String el){
		currentGenre = el;		
	}	
	@Test
	public void Test() throws Exception {
		System.out.println("Search and verify genre - "+ currentGenre.toUpperCase());
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl);

		//                   assert page title
		assertEquals(MAIN_PAGE_TITLE, driver.getTitle());

		//					navigate to search station field
		WebElement searchField = driver.findElement(By.name("query"));
		searchField.clear();
		searchField.sendKeys(currentGenre);		
		driver.findElement(By.xpath(".//*[@class='searchForm']/input")).click();

		//					assert search result page title
		String s = String.format(EXPECTED_PAGE_TITLE_TEMPLATE, currentGenre).toLowerCase();
		assertEquals(s, driver.getTitle().toLowerCase());

		//                  assert crumbhead title
		driver.switchTo().frame("contentFrame");
		WebElement searchResult = driver.findElement(By.id("crumbhead"));
		String expectedResult = String.format("top %s stations", currentGenre).toLowerCase();
		String actualResult = searchResult.getText().toLowerCase();
		assertEquals(expectedResult, actualResult);
	}
	@AfterClass
	public static void tearDown() throws Exception {
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}
}