package tests;

import com.interviewcards.BaseTest;
import config.Config;
import io.qameta.allure.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pages.LoginPage;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Authentication")
@Feature("Login")
public class LoginTest extends BaseTest {

    @Story("User should be able to login with valid credentials")
    @Description("Test verifies that user can successfully login using email or username")
    @Severity(SeverityLevel.CRITICAL)
    @MethodSource("validLoginData")
    @ParameterizedTest(name = "Login with identifier: {0}")
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