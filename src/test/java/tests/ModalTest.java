package tests;

import com.interviewcards.BaseTest;
import components.ModalComponent;
import config.Config;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import pages.LoginPage;
import pages.MainPage;
import assertions.ModalAssertions;
import pages.enums.LoginSubmitType;

@Epic("UI Components")
@Feature("Modal")
public class ModalTest extends BaseTest {

    @Epic("Cards")
    @Feature("Modal")
    @Story("Open add card modal")
    @Description("Test verifies that the add card modal opens correctly with proper title")
    @Severity(SeverityLevel.NORMAL)
    @Test
    void openModalTest() {

        // login
        mainPage = new LoginPage(page)
                .login(Config.EMAIL, Config.PASSWORD, LoginSubmitType.CLICK);

        // open modal
        modalComponent = mainPage.clickAddCard();

        // assertions
        ModalAssertions modalAssert = new ModalAssertions(modalComponent);

        modalAssert.beVisible();
        modalAssert.haveTitle(modalComponent.getModalTitle());
    }
}