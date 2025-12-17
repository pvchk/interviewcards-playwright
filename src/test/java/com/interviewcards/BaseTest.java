package com.interviewcards;

import com.microsoft.playwright.*;
import components.ModalComponent;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import pages.LoginPage;
import pages.MainPage;

import java.io.ByteArrayInputStream;

/**
 * Base test class for Playwright tests.
 * Handles browser setup and teardown with better error handling.
 */

public class BaseTest {

    protected static Playwright playwright;
    protected static Browser browser;

    protected BrowserContext context;
    protected Page page;
    protected MainPage mainPage;
    protected LoginPage loginPage;
    protected ModalComponent modalComponent;

    /**
     * Captures screenshot on test failure at the correct moment:
     * After the test method finishes, but BEFORE @AfterEach closes the context.
     */
    @RegisterExtension
    final AfterTestExecutionCallback screenshotOnFailure = new AfterTestExecutionCallback() {
        @Override
        public void afterTestExecution(ExtensionContext extensionContext) {
            if (extensionContext.getExecutionException().isEmpty()) {
                return;
            }
            if (page == null) {
                return;
            }
            try {
                byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
                Allure.addAttachment(
                        "Screenshot: " + extensionContext.getDisplayName(),
                        "image/png",
                        new ByteArrayInputStream(screenshot),
                        ".png"
                );
            } catch (Exception e) {
                // If the page was closed by the test, just skip screenshot.
                System.err.println("Failed to capture screenshot: " + e.getMessage());
            }
        }
    };

    @BeforeAll
    static void beforeAll() {
        playwright = Playwright.create();

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
        );
    }

    @BeforeEach
    void setupContext() {
        context = browser.newContext();

        context.setDefaultTimeout(5_000);
        context.setDefaultNavigationTimeout(5_000);
        page = context.newPage();
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        context.close();
    }

    @AfterAll
    static void afterAll() {
        browser.close();
        playwright.close();
    }
}