package tests;

import components.ModalComponent;
import org.junit.jupiter.api.*;
import pages.MainPage;
import assertions.ModalAssertions;

public class ModalTest extends LoginTest {

    @Test
    void openModalTest() {
        MainPage main = new MainPage(page);
        main.open(); // перейти на страницу карточек

        main.clickAddCard();

        ModalComponent modal = new ModalComponent(page);
        ModalAssertions modalAssert = new ModalAssertions(modal);

        modalAssert.beVisible();
        modalAssert.haveTitle("Create New Flash Card");
    }
}