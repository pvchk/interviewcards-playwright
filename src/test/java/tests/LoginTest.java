package tests;

import com.interviewcards.BaseTest;
import config.Config;
import org.junit.jupiter.api.Test;
import pages.LoginPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest extends BaseTest {

    @Test
    void loginWithValidEmail() {
        loginPage = new LoginPage(page);
        mainPage = loginPage.login(Config.EMAIL, Config.PASSWORD);
        assertTrue(mainPage.isLogoutButtonDisplayed());
    }

    @Test
    void loginWithValidUsername() {
        loginPage = new LoginPage(page);
        mainPage = loginPage.login(Config.USERNAME, Config.PASSWORD);
        assertTrue(mainPage.isLogoutButtonDisplayed());
    }

}
