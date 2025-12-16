package tests;

import com.interviewcards.BaseTest;
import components.ModalComponent;
import config.Config;
import org.junit.jupiter.api.*;
import pages.LoginPage;
import pages.MainPage;
import assertions.ModalAssertions;

public class ModalTest extends BaseTest {

    @Test
    void openModalTest() {
        loginPage = new LoginPage(page);
        loginPage.login(Config.EMAIL, Config.PASSWORD);

        mainPage = new MainPage(page);
        mainPage.open();

        modalComponent = mainPage.clickAddCard();

        ModalComponent modal = new ModalComponent(page);
        ModalAssertions modalAssert = new ModalAssertions(modal);

        modalAssert.beVisible();
        modalAssert.haveTitle(modalComponent.getModalTitle());
    }
}