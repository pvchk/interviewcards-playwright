package tests;

import com.interviewcards.BaseTest;
import config.Config;
import org.junit.jupiter.api.BeforeEach;
import pages.LoginPage;

public abstract class LoginTest extends BaseTest {

    @BeforeEach
    void login() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.open();
        loginPage.login(Config.LOGIN, Config.PASSWORD);
    }
}
