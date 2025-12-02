package tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import pages.MainPage;
import assertions.ModalAssertions;

public class ModalTest {

    private Playwright playwright;
    private Browser browser;
    private Page page;
    private MainPage mainPage;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.navigate("http://localhost:3000/"); // Твоя страница
        mainPage = new MainPage(page);
    }

    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }

    @Test
    void shouldOpenCardModal() {
        // Кликаем "+ New Card"
        mainPage.clickAddCard();

        // Проверяем, что модалка открылась
        new ModalAssertions(mainPage.getCardModal())
                .beVisible()
                .haveTitle("Create New Flash Card");

        // заполнить форму
        mainPage.getCardModal().fillQuestion("What is Java?");
        mainPage.getCardModal().fillAnswer("A programming language");
        mainPage.getCardModal().clickSave();
    }
}