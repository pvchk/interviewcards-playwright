package tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import pages.MainPage;

public class ModalTest {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;
    MainPage mainPage;

    @BeforeAll
    static void setUpClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @BeforeEach
    void setUp() {
        context = browser.newContext();
        page = context.newPage();
        page.navigate("http://localhost:3000"); // поменяй на свой URL
        mainPage = new MainPage(page);
    }

    @Test
    void modalOpensAfterButtonClick() {
        mainPage.openModal();

        mainPage.getModal()
                .should()
                .beVisible();
    }

    @AfterEach
    void tearDown() {
        context.close();
    }


    @AfterAll
    static void tearDownClass() {
        playwright.close();
    }
}