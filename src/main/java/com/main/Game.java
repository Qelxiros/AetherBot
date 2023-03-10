package com.main;

import org.openqa.selenium.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Game {
    private static final int MIN = 50;
    private static final int MAX = 90;

    private final HashMap<String, LinkedList<String>> wordsBySyllable;
    private final HashSet<String> used;
    private final WebDriver driver;

    public Game(WebDriver driver) throws IOException, ClassNotFoundException {
        System.out.println("creating new game");
        this.driver = driver;
        this.wordsBySyllable = (HashMap<String, LinkedList<String>>) Main.deserialize("serialized/wordsBySyllable.ser");
        this.used = new HashSet<>();
    }

    public String getWord(String syllable) throws IOException {
        String word = wordsBySyllable.get(syllable).poll();
        System.err.println("word selected");
        System.err.println(word);
        if (word == null) {
            BufferedWriter out = new BufferedWriter(new FileWriter("incomplete_syllables.txt"));
            out.write(syllable);
            out.close();
            return "";
        }
        if (!used.add(word)) {
            System.err.println("recursion");
            return getWord(syllable);
        }
        return word;
    }

    public void typeWord(String word, String syllable, WebElement answerBox, WebElement spinner, long start) throws InterruptedException {
        System.err.printf("Syllable: %s, Word: %s%n", syllable, word);

        answerBox.click();

        long elapsed = System.currentTimeMillis() - start;
        int sleep = (int) ((Math.random() * (50 - 30) + 30) / 100 * 1000);
        if (sleep > elapsed) {
            TimeUnit.MILLISECONDS.sleep(sleep - elapsed);
        }

        for (char character : word.toCharArray()) {
            int fail = (int) (Math.random() * (100 - 1) + 1);
            if (fail == 2) {
                int loops = (int) (Math.random() * (3 - 1) + 1);

                for (int i = 0; i < loops; i++) {
                    answerBox.sendKeys(Character.toString((char) ('A' + Math.random() * ('Z' - 'A' + 1))));
                    TimeUnit.MILLISECONDS.sleep((int) ((Math.random() * (5 - 2) + 2) / 60 * 1000));
                }

                TimeUnit.MILLISECONDS.sleep(100);

                for (int i = 0; i < loops; i++) {
                    answerBox.sendKeys(Keys.BACK_SPACE);
                }

                TimeUnit.MILLISECONDS.sleep(30);

                answerBox.sendKeys(Character.toString(character));

            } else {
                answerBox.sendKeys(Character.toString(character));
                TimeUnit.MILLISECONDS.sleep((int) (Math.random() * (MAX - MIN) + MIN));
            }
        }
        answerBox.sendKeys(Keys.RETURN);
        TimeUnit.MILLISECONDS.sleep(100);
    }

    public void startGame() throws InterruptedException, IOException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        System.out.println(js.executeScript("return self.name"));
        if (driver.findElements(By.tagName("iframe")).size() > 0) {
            driver.switchTo().frame(0);
            System.out.println("yes1");
            System.out.println(js.executeScript("return self.name"));
        }
        TimeUnit.MILLISECONDS.sleep((int) (Math.random() * (1010 - 990) + 990));
        if (driver.findElements(By.tagName("iframe")).size() > 0) {
            driver.switchTo().frame(0);
            System.out.println("yes2");
            System.out.println(js.executeScript("return self.name"));
        }
        js.executeScript("document.getElementsByClassName(\"styled\")[0].click()");
//        WebElement joinButton = new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> d.findElement(By.cssSelector("button.styled")));
//        joinButton.click();

        while (true) {
            try {
                WebElement button = driver.findElement(By.cssSelector("button.styled"));
                if (button.isDisplayed()) {
                    TimeUnit.MILLISECONDS.sleep(100);
                    button.click();
                }
            } catch (Exception ignored) {
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                //new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> d.findElement(By.tagName("iframe")).findElement(By.cssSelector("input.styled")).isDisplayed());
                try {
                    driver.switchTo().frame(0);
                    System.out.println("switched frame successfully");
                } catch (Exception e) {
                    System.out.println(e);
                    System.out.println(Arrays.toString(e.getStackTrace()));
                }
                WebElement answerBox = null;
                try {
                    answerBox = driver.findElement(By.cssSelector("input.styled"));
                    System.out.println("found answer box successfully");
                } catch (Exception e) {
                    System.err.println("answer box");
                    System.out.println(e);
                    System.out.println(Arrays.toString(e.getStackTrace()));
                }

                WebElement spinner = driver.findElement(By.cssSelector("div.syllable"));

                if (!spinner.isDisplayed()) {
                    continue;
                }
                System.out.println("spinner displayed successfully");

                TimeUnit.MILLISECONDS.sleep(200);
                long start = System.currentTimeMillis();

                String syllable = spinner.getText();
                System.out.println("syllable found successfully");

                String matchingWord = getWord(syllable);
                System.out.println(matchingWord);

                try {
                    typeWord(matchingWord, syllable, answerBox, spinner, start);
                } catch (Exception e) {
                    System.err.println("typing");
                    System.out.println(e);
                    System.out.println(Arrays.toString(e.getStackTrace()));
                }
            } catch (Exception e) {
                System.out.println(e);
                System.out.println(Arrays.toString(e.getStackTrace()));
                try {
                    driver.switchTo().frame(0);
                } catch (Exception ignored) {
                    System.err.println("iframes");
                }
            }
        }
    }
}
