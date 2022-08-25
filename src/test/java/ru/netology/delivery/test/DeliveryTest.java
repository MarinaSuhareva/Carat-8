package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$;


class DeliveryTest {

    @BeforeEach
    void setUp() {
        SelenideLogger.addListener("allure", new AllureSelenide());
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
    }
    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        $x("//input[@placeholder='Город']").setValue(validUser.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $x("//input[@placeholder='Дата встречи']").setValue(firstMeetingDate);
        $("[data-test-id = 'name'] input").setValue(validUser.getName());
        $("[data-test-id = 'phone'] input").setValue(validUser.getPhone());
        $("[data-test-id = 'agreement']").click();
        $x("//span[@class='button__text']").click();
        $(withText("Успешно!")).should(visible, Duration.ofSeconds(15));
        $("[class='notification__content']").shouldHave(Condition.exactText("Встреча успешно запланирована на " + firstMeetingDate));
        $x("//input[@placeholder='Дата встречи']").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $x("//span[@class='button__text']").click();
        $x("//button[contains(@class,'button')]").click();
        $("[data-test-id='date'] input").setValue(secondMeetingDate);
        $x("//span[@class='button__text']").click();
        $x("//*[contains(text(),'У вас уже запланирована встреча на другую дату. Перепланировать?')]").shouldBe(Condition.visible);
        $x("//button[@type='button']/span[.='Перепланировать']").click();
        $("[class='notification__content']").shouldHave(Condition.exactText("Встреча успешно запланирована на " + secondMeetingDate));
    }
}