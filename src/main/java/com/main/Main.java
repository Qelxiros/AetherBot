package com.main;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Main {
    public static void main(String[] args) throws Exception {
//        readAndSerializeFile("./");
//        System.exit(69);
        WebDriver driver = WebDriverManager.chromedriver().create();
        driver.manage().window().maximize();

        driver.get("https://jklm.fun");
        while (driver.getCurrentUrl().length() < 20 || !driver.getCurrentUrl().startsWith("https://jklm.fun")) {
            TimeUnit.SECONDS.sleep(12);
        }
        Game game = new Game(driver);
        game.startGame();
        System.out.println("ready");

//  The below code is supposed to automatically create and enter a room, but discord auth is very robust and doesn't like automation

//            driver.get("https://jklm.fun");
//            driver.executeScript("$hide(\".page:not([hidden])\");\n" + "showAuthPage();");
//            WebElement discordButton = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(driver.findElement(By.cssSelector("a.discord"))));
//            discordButton.click();
//            discordLogIn(driver, args[0], args[1]);
//            new WebDriverWait(driver, Duration.ofSeconds(60)).until(d -> d.findElements(By.tagName("button")).size() > 0);
//            TimeUnit.MILLISECONDS.sleep(100);
//            driver.executeScript("document.getElementsByTagName(\"button\")[1].click()");
//
//            new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> d.getCurrentUrl().startsWith("https://jklm.fun"));
//            TimeUnit.SECONDS.sleep(1);

//            driver.get("https://discord.com/channels/970415453929353269/970752674385526784");
//
//            WebElement textBox = new WebDriverWait(driver, Duration.ofSeconds(60)).until(ExpectedConditions.elementToBeClickable(By.cssSelector("div[role=textbox]")));
//            textBox.sendKeys(".b private\uE007");
//
//            WebElement temp = driver.findElement(By.cssSelector("ol[role=list]"));
//            List<WebElement> messages = temp.findElements(By.tagName("li"));
//            int count = messages.size();
//
//            new WebDriverWait(driver, Duration.ofSeconds(20)).until(d -> d.findElement(By.cssSelector("ol[role=list]")).findElements(By.tagName("li")).size() > count);
//
//            messages = temp.findElements(By.tagName("li"));
//            Collections.reverse(messages);
//            WebElement message = messages.get(0);
//            String link = message.findElement(By.tagName("a")).getAttribute("href");
//
//            driver.get(link);
        while (true) {
            game.startGame();
        }
    }

//    public static HashMap<String, ArrayList<LinkedList<String>>> deepCopyOfThisBullshit(HashMap<String, ArrayList<LinkedList<String>>> other) {
//        HashMap<String, ArrayList<LinkedList<String>>> copy = new HashMap<>();
//        for (String s : other.keySet()) {
//            ArrayList<LinkedList<String>> thisIsHere = new ArrayList<>();
//            for (LinkedList<String> l : other.get(s)) {
//                LinkedList<String> temp = new LinkedList<>();
//                for (String ls : l) {
//                    temp.offer(ls);
//                }
//                thisIsHere.add(temp);
//            }
//            copy.put(s, thisIsHere);
//        }
//        return copy;
//    }

    public static void twitchLogIn(ChromeDriver driver, String email, String pwd) {
        WebElement emailBox = new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> d.findElement(By.cssSelector("input#login-username")));
        WebElement pwdBox = driver.findElement(By.cssSelector("input#password-input"));
        emailBox.sendKeys(email);
        pwdBox.sendKeys(pwd);
        WebElement submitButton = driver.findElement(By.cssSelector("button[state=default]"));
        submitButton.click();
    }

    // broken when using headless browser (discord programmers are unfortunately competent)
    public static void discordLogIn(ChromeDriver driver, String email, String pwd) {
        WebElement emailBox = new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> d.findElement(By.cssSelector("input")));
        WebElement pwdBox = driver.findElements(By.cssSelector("input")).get(1);
        emailBox.sendKeys(email);
        pwdBox.sendKeys(pwd);
        WebElement submitButton = driver.findElement(By.cssSelector("button[type=submit]"));
        submitButton.click();
    }

    public static Object deserialize(String filename) throws IOException, ClassNotFoundException {
        ObjectInputStream input = new ObjectInputStream(Files.newInputStream(Paths.get(filename)));
        Object object = input.readObject();
        input.close();
        return object;
    }

    // only used when the word list needs to be updated
    public static void readAndSerializeFile(String dir) throws IOException {
        Charset charset = Charset.defaultCharset();
        List<String> stringList = Files.readAllLines(new File(dir + "/wordlist.txt").toPath(), charset);
        System.out.println("File read.");

        Collections.shuffle(stringList);
        HashMap<String, LinkedList<String>> wordsBySyllable = new HashMap<>();
        ArrayList<String> prompts = new ArrayList<>();
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        System.out.println("Data structures initialized.");
        for (char i : alphabet) {
            for (char j : alphabet) {
                prompts.add(String.valueOf(i) + j);
                for (char k : alphabet) {
                    prompts.add(String.valueOf(i) + j + k);
                }
            }
        }
        System.out.println("Syllables generated.");

        for (String p : prompts) {
            wordsBySyllable.put(p, new LinkedList<>());
        }

        for (String s : stringList) {
            for (String p : prompts) {
                if (s.contains(p)) {
                    wordsBySyllable.get(p).offer(s);
                }
            }
        }
        System.out.println("wordsBySyllable populated.");

        ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(Paths.get("serialized/wordsBySyllable.ser")));
        out.writeObject(wordsBySyllable);
        out.close();
    }
}