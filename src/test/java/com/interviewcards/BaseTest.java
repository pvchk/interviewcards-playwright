package com.interviewcards;

import com.microsoft.playwright.*;
import components.ModalComponent;
import org.junit.jupiter.api.*;
import pages.LoginPage;
import pages.MainPage;

import java.nio.file.Path;

/**
 * Base test class for Playwright tests.
 * Handles browser setup and teardown with better error handling.
 */

public abstract class BaseTest {

    protected static Playwright playwright;
    protected static Browser browser;

    protected BrowserContext context;
    protected Page page;
    protected MainPage mainPage;
    protected LoginPage loginPage;
    protected ModalComponent modalComponent;

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
        context.tracing().start(
                new Tracing.StartOptions()
                        .setScreenshots(true)
                        .setSnapshots(true)
                        .setSources(true)
        );
        page = context.newPage();
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        context.tracing().stop(
                new Tracing.StopOptions()
                        .setPath(
                                Path.of("target/traces/" +
                                        testInfo.getDisplayName() + ".zip")
                        )
        );
        context.close();
    }

    @AfterAll
    static void afterAll() {
        browser.close();
        playwright.close();
    }
}