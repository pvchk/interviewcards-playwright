package tests;

import com.interviewcards.BaseTest;
import config.Config;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pages.LoginPage;
import io.qameta.allure.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest extends BaseTest {

    @Epic("Login")
    @Feature("Successful login")
    @Story("LGN-001 / LGN-002")
    @ParameterizedTest(name = "Login with identifier: {0}")
    @MethodSource("validLoginData")
    void loginWithValidCredentials(String login) {
        loginPage = new LoginPage(page);
        mainPage = loginPage.login(login, Config.PASSWORD);

        assertTrue(
                mainPage.isLogoutButtonDisplayed(),
                "Logout button should be visible after successful login"
        );
    }

    static Stream<String> validLoginData() {
        return Stream.of(
                Config.EMAIL,
                Config.USERNAME
        );
    }
}