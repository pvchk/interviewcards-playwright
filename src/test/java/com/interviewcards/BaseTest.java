package com.interviewcards;

import com.microsoft.playwright.*;
import components.ModalComponent;
import org.junit.jupiter.api.*;
import pages.LoginPage;
import pages.MainPage;

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
    void beforeEach() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void afterEach() {
        context.close();
    }

    @AfterAll
    static void afterAll() {
        browser.close();
        playwright.close();
    }
}