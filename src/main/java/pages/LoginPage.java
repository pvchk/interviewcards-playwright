package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import config.Config;
import pages.enums.LoginSubmitType;

import static pages.locators.LoginPageLocators.*;

public class LoginPage {

    private final Page page;
    public static final String LOGIN_URL = "/login";

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
}