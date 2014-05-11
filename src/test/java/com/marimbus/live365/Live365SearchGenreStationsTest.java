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
import org.openqa.selenium.firefox.FirefoxDriver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(Parameterized.class)
public class Live365SearchGenreStationsTest {
	private static WebDriver driver = new FirefoxDriver();
	private final String MAIN_PAGE_TITLE = "Live365 Internet Radio Network - Listen to Free Music, Online Radio";
	private final String EXPECTED_PAGE_TITLE_TEMPLATE = "Listen to Free %s Music Online - Live365 Internet Radio";
	private static String baseUrl = "http://www.live365.com/new/index.live/";
	private String currentGenre;
	private static StringBuffer verificationErrors = new StringBuffer();

	private static List<String> getListOfGenres(){			
		List<String> strListOfGenreTopStations = new ArrayList<String>();
		driver.get(baseUrl);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		//						navigate to genre page		
		driver.findElement(By.xpath(".//*[@class='headerlinks']//li[1]/a")).click();			
		driver.switchTo().frame("contentFrame");
		driver.switchTo().frame(driver.findElement(By.cssSelector(".tabFrame")));		   
		//                  create station titles list
		List<WebElement> genreElementsList = driver.findElements(By.xpath(".//*[@id='tagCloud']/a"));
		//         			 populate  object Arraylist with data objects for data-driven test	
		for (WebElement elem : genreElementsList) {
			String ell= elem.getText();
			strListOfGenreTopStations.add(ell);
			System.out.println("Extracted genre - "+ ell.toUpperCase());
		}			
		System.out.println("=======================================");
		return strListOfGenreTopStations;		
	}	
	@Parameters
	public static Collection<Object[]> ListOfTopStations(){
		List<Object[]> data = new ArrayList<Object[]>();	
		//         			 populate  object Arraylist with data objects for data-driven test
		for (String elem : Live365SearchGenreStationsTest.getListOfGenres()) {
			Object[] singleTest = {elem};
			data.add(singleTest);				
		}
		return data;
	}
	public Live365SearchGenreStationsTest(String el){
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