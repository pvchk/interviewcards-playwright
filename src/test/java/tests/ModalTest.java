package tests;

import com.interviewcards.BaseTest;
import components.ModalComponent;
import org.junit.jupiter.api.*;
import pages.MainPage;
import assertions.ModalAssertions;

public class ModalTest extends BaseTest {

    @Test
    void openModalTest() {
        page.navigate("http://localhost:3000");

        MainPage main = new MainPage(page);
        ModalComponent modal = new ModalComponent(page);
        ModalAssertions modalAssert = new ModalAssertions(modal);

        main.clickAddCard();
        modalAssert.beVisible();
        modalAssert.haveTitle("Create New Flash Card");
    }
}