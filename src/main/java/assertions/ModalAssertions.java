package assertions;

import com.microsoft.playwright.Locator;
import components.ModalComponent;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ModalAssertions {

    private final ModalComponent modal;

    public ModalAssertions(ModalComponent modal) {
        this.modal = modal;
    }

    public ModalAssertions beVisible() {
        // Проверяем, что корневой элемент модалки видим
        assertThat(modal.getRoot()).isVisible();
        return this;
    }

    public ModalAssertions haveTitle(String expectedTitle) {
        Locator title = modal.getTitle();
        assertThat(title).hasText(expectedTitle);
        return this;
    }
}