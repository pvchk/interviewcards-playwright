package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import components.ModalComponent;

public class MainPage {

    private final Page page;

    // Кнопка "+ New Card"
    private final Locator addCardButton;

    // Модальное окно карточки
    private final ModalComponent cardModal;

    public MainPage(Page page) {
        this.page = page;
        this.addCardButton = page.locator("#addCardBtn");
        this.cardModal = new ModalComponent(page); // передаем страницу в компонент
    }

    // Клик по кнопке "+ New Card"
    public void clickAddCard() {
        addCardButton.click();
    }

    public ModalComponent getCardModal() {
        return cardModal;
    }
}