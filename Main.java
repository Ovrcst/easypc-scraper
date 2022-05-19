package com.company;
import java.util.List;
import java.util.Scanner;
import java.io.*;
import java.io.IOException;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.*;

public class Main {

    void createHTML(){
        try{
            File htmlFile = new File("price.html");
            File cssFile = new File("style.css");

            if(htmlFile.createNewFile()){
                System.out.println(htmlFile.getName() + " Created");
            }
            if(cssFile.createNewFile()){
                System.out.println(cssFile.getName() + " Created");
            }
        }catch (IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try{
            FileWriter cssWrite = new FileWriter("style.css");
            cssWrite.write("""
                    *{
                        margin:0;
                        padding: 0;
                        box-sizing: border-box;
                        scroll-behavior: smooth;
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    }

                    .container{
                        display: flex;
                        width: 100vw;
                        height: auto;
                        background-color: lightgray;
                        align-items: center;
                        justify-content: center;
                    }

                    .mainTable{
                        padding: 10px;
                        background-color: #52525270; border-radius: 20px;
                        width: auto;
                        height: 100%;
                        text-align: center;
                        border-spacing: 10px 10px;
                    }

                    button{
                        cursor: pointer;
                        border-radius: 20px;
                    }

                    td{
                        width: 30vw;
                        padding-right: 20px;
                        padding-bottom: 20px;
                    }

                    .imageClass{
                        width: 50px;
                        height: 50px;
                    }

                    img{
                        border-radius: 20px;
                        width: 5rem;
                        height: 5rem;
                    }""");
            cssWrite.close();
        }catch(IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    void writeHTMLHeader(){
        try{
            FileWriter htmlHEAD = new FileWriter("price.html");
            htmlHEAD.write("""
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <meta http-equiv="X-UA-Compatible" content="IE=edge">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Document</title>
                        <link rel="stylesheet" href="style.css">
                    </head>
                    <body>
                        <div class="container">
                            <table class="mainTable">
                                <thead>
                                    <tr>
                                        <th>Image</th>
                                        <th>Product Name</th>
                                        <th>Price</th>
                                    </tr>
                                </thead>
                                <tbody>""");
            htmlHEAD.close();
        }catch(IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    void writeHTMLFoot(){
        try{
            FileWriter htmlFOOT = new FileWriter("price.html",true);
            htmlFOOT.write("""
                                </tbody>
                            </table>
                        </div>
                    </body>
                    </html>""");
            htmlFOOT.close();
        }catch (IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /*void scraper(String url) throws IOException {
        Document document = Jsoup.connect(url).userAgent("Mozilla/49.0").timeout(15*1000).get();
        Elements allImg = document.getElementsByClass("snize-thumbnail-wrapper");
        Elements allProduct = document.getElementsByClass("snize-overhidden");
        Elements allPrice = document.getElementsByClass("snize-price-list");

        String imgString = "";
        String productString = "";
        String priceString = "";

        for(Element e : allImg){
            imgString = allImg.select("div.snize-thumbnail-wrapper > span.snize-thumbnail > img.snize-item-image").text();
            System.out.println(imgString);
        }
        for(Element e : allProduct.select("spam.snize-overhidden > span.snize-title")){
            productString = e.text();
            System.out.println(e.text());
        }
        for(Element e : allPrice.select("div.snize-price-list > span.snize-price.money")){
            priceString = e.text();
            System.out.println(priceString);
        }

        htmlAppender(imgString,priceString,productString,url);
    }*/

    void siteHandler(String url){
        int count = 1;
        WebDriver driver;
        System.setProperty("webdriver.gecko.driver",".\\Driver\\geckodriver.exe");
        System.setProperty("webdriver.chrome.driver",".\\Driver\\chromedriver.exe");
        System.setProperty("webdriver.edge.driver",".\\Driver\\msedgedriver.exe");
        try{
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless", "--disable-gpu", "--window-size=1400,800","--ignore-certificate-errors");
            driver = new ChromeDriver(options);
        }catch(IllegalStateException | SessionNotCreatedException e){
            FirefoxBinary firefoxBinary = new FirefoxBinary();
            FirefoxOptions options = new FirefoxOptions();
            options.setBinary(firefoxBinary);
            options.setHeadless(true);
            driver = new FirefoxDriver(options);
        }
        do{
            String iteratedUrl = url + "&tab=products&page=" + count;
            driver.findElements(By.className("snize-no-products-found-text"));
            seleniumScraper(iteratedUrl, driver);
            count++;
        }while(driver.findElements(By.cssSelector("span[class='snize-thumbnail']")).size() !=0);
        System.out.println("Closing");
        driver.quit();
        System.out.println("Closed");
    }

    void seleniumScraper(String url, WebDriver driver){
        driver.get(url);
        List<WebElement> allImg = driver.findElements(By.cssSelector("span[class='snize-thumbnail']"));
        List<WebElement> allProduct = driver.findElements(By.cssSelector("span[class='snize-overhidden']"));
        List<WebElement> allPrice = driver.findElements(By.cssSelector("div[class='snize-price-list']"));
        List<WebElement> allStock = driver.findElements((By.className("snize-product")));
        System.out.printf("%d %d %d %d",allImg.size(),allProduct.size(),allPrice.size(),allStock.size());
        for(int i = 0; i < allImg.size();i++){
            WebElement imgs = allImg.get(i).findElement(By.cssSelector("img[class='snize-item-image ']"));
            String imgSrc = imgs.getAttribute("src");
            WebElement products = allProduct.get(i).findElement(By.cssSelector("span[class='snize-title']"));
            String productSrc = products.getText();
            WebElement prices = allPrice.get(i).findElement(By.className("money"));
            String priceSrc = prices.getText();
            String stocksSrc;
            try{
                WebElement stocks = allStock.get(i).findElement(By.className("snize-in-stock"));
                stocksSrc = stocks.getText();
                htmlAppender(imgSrc,priceSrc,productSrc,stocksSrc,url);
            }catch (NoSuchElementException e){
                WebElement stocks = allStock.get(i).findElement(By.className("snize-out-of-stock"));
                stocksSrc = stocks.getText();
                htmlAppender(imgSrc,priceSrc,productSrc,stocksSrc,url);
            }


        }

    }

    void htmlAppender(String imgSrc, String priceSrc, String titleSrc, String stocksSrc, String url){
        try{
            FileWriter htmlAPPEND = new FileWriter("price.html",true);
            htmlAPPEND.write("<tr>\n" +
                    "                    <td class=\"imageClass\">\n" +
                    "                        <a href="+url+">\n" +
                    "                        <button>\n" +
                    "                            <img src="+imgSrc+" alt=\"GPU\">\n" +
                    "                        </button>\n" +
                    "                        </a>\n" +
                    "                    </td>\n" +
                    "\n" +
                    "                    <td class=\"nameClass\">"+titleSrc+"</td>\n" +
                    "\n" +
                    "                    <td class=\"priceClass\">"+priceSrc+ " "+ stocksSrc + "</td>\n" +
                    "                </tr>");
            htmlAPPEND.close();
        }catch (IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        // write your code here
        Main main = new Main();
        System.out.print("Input Product Name To Search: ");
        Scanner sc = new Scanner(System.in);
        String search = sc.nextLine();
        search = search.replace(" ","+");
        String url = "https://easypc.com.ph/pages/search-results-page?q=";
        url += search;
        main.createHTML();
        main.writeHTMLHeader();
        main.siteHandler(url);
        //main.seleniumScraper(url);
        main.writeHTMLFoot();
        System.out.println("END");

    }
}
