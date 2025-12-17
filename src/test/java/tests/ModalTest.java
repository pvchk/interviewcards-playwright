package tests;

import com.interviewcards.BaseTest;
import components.ModalComponent;
import config.Config;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import pages.LoginPage;
import pages.MainPage;
import assertions.ModalAssertions;

@Epic("UI Components")
@Feature("Modal")
public class ModalTest extends BaseTest {

    @Test
    @Story("User should be able to open add card modal")
    @Description("Test verifies that the add card modal opens correctly with proper title")
    @Severity(SeverityLevel.NORMAL)
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