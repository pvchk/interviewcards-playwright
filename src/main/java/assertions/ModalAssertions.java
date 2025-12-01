package assertions;

import components.ModalComponent;

import static org.assertj.core.api.Assertions.assertThat;

public class ModalAssertions {

    private final ModalComponent modal;

    public ModalAssertions(ModalComponent modal) {
        this.modal = modal;
    }

    public ModalAssertions beVisible() {
        assertThat(modal.isVisible())
                .as("Modal should be visible")
                .isTrue();
        return this;
    }

    public ModalAssertions haveTitle(String expectedTitle) {
        String actual = modal.getTitle().textContent();
        assertThat(actual)
                .as("Modal title should match expected")
                .isEqualTo(expectedTitle);
        return this;
    }
}
