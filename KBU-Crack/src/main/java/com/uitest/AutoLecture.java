package com.uitest;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.interactions.Actions;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class AutoLecture {
    public static void main(String[] args) {
        WebDriver driver = null;
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        try {
            System.out.println("🌐 : 크롬(Chrome) 브라우저 실행을 시도합니다...");
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--mute-audio");
            driver = new ChromeDriver(chromeOptions);
        } catch (Exception e1) {
            System.out.println("⚠️ 크롬을 찾을 수 없습니다. 2: 엣지(Edge) 브라우저를 시도합니다...");
            try {
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--mute-audio");
                driver = new org.openqa.selenium.edge.EdgeDriver(edgeOptions);
            } catch (Exception e2) {
                System.out.println("⚠️ 엣지도 찾을 수 없습니다. 3: 사파리(Safari) 브라우저를 시도합니다...");
                try {
                    driver = new org.openqa.selenium.safari.SafariDriver();
                } catch (Exception e3) {
                    System.err.println("❌ 실행 가능한 브라우저가 없습니다. 브라우저를 설치해주세요!");
                    return;
                }
            }
        }

        try {
            driver.get("https://eclass.kbu.ac.kr/");

            System.out.println("======================================");
            System.out.println("브라우저가 열렸습니다.");
            System.out.println("직접 로그인하시고, 수강을 원하는 '주차'를 클릭해 강의 목록을 띄워주세요.");
            System.out.println("목록이 보이면, 이 콘솔 창에서 엔터(Enter)를 치세요!");
            System.out.println("======================================");

            scanner.nextLine();
            System.out.println("✅ 자동화를 시작합니다.");

            int currentIndex = 0;

            while (true) {
                // 페이지 내의 모든 미수강 강의 탐색
                List<WebElement> allUnwatchedList = driver.findElements(By.xpath("//li[contains(@class, 'vod')]//img[contains(@src, 'completion-auto-n')]/ancestor::li[contains(@class, 'vod')]"));

                // 화면에 실제로 '보이는' 강의만 새로운 리스트에 담기
                List<WebElement> visibleList = new ArrayList<>();
                for (WebElement el : allUnwatchedList) {
                    if (el.isDisplayed()) {
                        visibleList.add(el);
                    }
                }

                if (currentIndex >= visibleList.size()) {
                    System.out.println("🎉 선택한 주차에 남은 동영상 강의를 모두 수강했습니다!");
                    break;
                }

                WebElement targetLecture = visibleList.get(currentIndex);
                String lectureName = targetLecture.findElement(By.className("instancename")).getText();
                System.out.println("▶️ 재생 시작: " + lectureName);

                String mainWindow = driver.getWindowHandle();
                WebElement linkElement = targetLecture.findElement(By.tagName("a"));

                // 스크롤이나 가림막 때문에 클릭이 씹히는 것을 방지하는 강제 클릭 로직
                try {
                    linkElement.click();
                } catch (Exception e) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", linkElement);
                }

                for (String windowHandle : driver.getWindowHandles()) {
                    if (!mainWindow.contentEquals(windowHandle)) {
                        driver.switchTo().window(windowHandle);
                        break;
                    }
                }

                System.out.println("⏳ 화면 렌더링 및 팝업 대기 중... (8초)");
                Thread.sleep(8000);

                JavascriptExecutor js = (JavascriptExecutor) driver;
                Actions actions = new Actions(driver);

                try {
                    try { driver.switchTo().alert().accept(); Thread.sleep(1000); } catch (Exception ignore) {}

                    js.executeScript(
                            "var btns = document.querySelectorAll('button, a');" +
                                    "for(var i=0; i<btns.length; i++) {" +
                                    "    if(btns[i].innerText.includes('확인') || btns[i].innerText.includes('닫기')) {" +
                                    "        btns[i].click();" +
                                    "    }" +
                                    "}"
                    );
                    Thread.sleep(1000);

                    js.executeScript("document.querySelectorAll('.iziModal-overlay, .modal-backdrop, #block-ui').forEach(el => el.remove());");
                } catch (Exception e) {
                    System.out.println("팝업 제거 로직 통과 (팝업이 없거나 이미 닫힘)");
                }

                try {
                    WebElement videoElement = driver.findElement(By.id("my-video"));
                    actions.moveToElement(videoElement).perform();
                    Thread.sleep(1000);

                    try {
                        WebElement volumePanel = driver.findElement(By.cssSelector(".vjs-volume-panel"));
                        actions.moveToElement(volumePanel).click().perform();
                        System.out.println("🔇 가상 마우스로 음소거 버튼 클릭 성공!");
                    } catch (Exception e) {
                        System.out.println("⚠️ 볼륨 버튼 클릭 실패");
                    }

                    Thread.sleep(1000);

                    try {
                        WebElement bigPlayBtn = driver.findElement(By.cssSelector(".vjs-big-play-button"));
                        if (bigPlayBtn.isDisplayed()) {
                            actions.moveToElement(bigPlayBtn).click().perform();
                            System.out.println("▶️ 가상 마우스로 재생 버튼 클릭 성공!");
                        }
                    } catch (Exception ignore) {}

                } catch (Exception e) {
                    System.out.println("⚠️ 마우스 제어 에러 발생. 아래 로그를 확인해주세요.");
                    e.printStackTrace();
                }

                System.out.println("⏳ 영상이 끝날 때까지 백그라운드에서 대기합니다...");

                while (true) {
                    Thread.sleep(5000);
                    try {
                        Boolean isEnded = (Boolean) js.executeScript(
                                "var v = document.querySelector('video'); " +
                                        "if (!v) return false; " +
                                        "return v.ended || (v.duration > 0 && v.currentTime >= v.duration - 1);"
                        );

                        if (isEnded != null && isEnded) {
                            System.out.println("✅ 영상 재생이 종료되었습니다.");
                            break;
                        }
                    } catch (Exception e) {
                        break;
                    }
                }

                int delay = 10000 + random.nextInt(4000);
                System.out.println("🤫 우회를 위해 " + (delay / 1000) + "초 대기 중...");
                Thread.sleep(delay);

                driver.close();
                driver.switchTo().window(mainWindow);
                currentIndex++;
            }

        } catch (Exception e) {
            System.err.println("전체 에러 발생: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
            scanner.close();
        }
    }
}