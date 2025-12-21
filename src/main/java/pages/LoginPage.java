package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import config.Config;
import pages.enums.LoginSubmitType;

public class LoginPage {

    private final Page page;
    public static final String LOGIN_URL = "/login";
    private static final String PASSWORD_INPUT = "#password";
    private static final String USERNAME_INPUT = "#username";
    private static final String SUBMIT_BUTTON = "button[type='submit']";
    private static final String INVALID_USERNAME_DIV = "//div[@id='username-error-message']";
    private static final String INVALID_PASSWORD_DIV = "//div[@id='password-error-message']";
    private static final String INVALID_USERNAME_PASSWORD_DIV = "//div[@id='login-error-message']";
    public static final String INVALID_USERNAME_OR_PASSWORD_HINT = "Invalid username or password. Please try again.";
    public static final String EMPTY_USERNAME_HINT = "Username or email is required";
    public static final String EMPTY_PASSWORD_HINT = "Password is required";

    public LoginPage(Page page) {
        this.page = page;
    }

    private void open() {
        page.navigate(Config.BASE_URL + LOGIN_URL);
    }

    public MainPage login(String login, String password, LoginSubmitType submitType) {
        open();

        page.fill(USERNAME_INPUT, login);
        page.fill(PASSWORD_INPUT, password);

        submit(submitType);

        waitForSuccessfulLogin();

        return new MainPage(page);
    }


    private void submit(LoginSubmitType submitType) {
        switch (submitType) {
            case CLICK ->
                    page.locator(SUBMIT_BUTTON).click();
            case ENTER ->
                    page.locator(PASSWORD_INPUT).press("Enter");
        }
    }

    private void waitForSuccessfulLogin() {
        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("+ New Card")
        ).waitFor();
    }

    public LoginPage loginWithInvalidEmailFormat(String invalidEmailFormat, String password) {
        open();

        page.fill(USERNAME_INPUT, invalidEmailFormat);
        page.fill(PASSWORD_INPUT, password);

        page.locator(SUBMIT_BUTTON).click();
        return this;
    }

    public LoginPage loginWithEmptyEmail(String password) {
        open();
        page.fill(PASSWORD_INPUT, password);
        page.locator(SUBMIT_BUTTON).click();

        return this;
    }

    public LoginPage loginWithEmptyPassword(String login) {
        open();
        page.fill(USERNAME_INPUT, login);
        page.locator(SUBMIT_BUTTON).click();

        return this;
    }

    public LoginPage loginWithEmptyLoginAndPassword() {
        open();
        page.locator(SUBMIT_BUTTON).click();

        return this;
    }

    public String getInvalidUsernameHint() {
        return page.locator(INVALID_USERNAME_DIV).textContent();
    }

    public String getInvalidPasswordHint() {
        return page.locator(INVALID_PASSWORD_DIV).textContent();
    }
    public String getInvalidEmailOrPasswordHint() {
        return page.locator(INVALID_USERNAME_PASSWORD_DIV).textContent();
    }
}
