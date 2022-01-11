package pl.kskowronski.views.login;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay {
    public LoginView() {
        setAction("login");

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setDescription("Portal pracowniczy user admin");
        i18n.getHeader().setTitle("Rekeep Polska");
        i18n.getForm().setSubmit("Zaloguj");
        i18n.getForm().setUsername("Login");
        i18n.getForm().setPassword("Hasło");
        i18n.getForm().setForgotPassword("");
        i18n.getForm().setTitle("");
        i18n.setAdditionalInformation("v.2022");
        setI18n(i18n);

        LoginI18n.ErrorMessage i18nErrorMessage = i18n.getErrorMessage();
        i18nErrorMessage.setTitle("Niepoprawna nazwa użytkownika lub hasło");
        i18nErrorMessage.setMessage("Sprawdź, czy podałeś poprawną nazwę użytkownika i hasło, i spróbuj ponownie.");
        i18n.setErrorMessage(i18nErrorMessage);


        setForgotPasswordButtonVisible(false);
        setOpened(true);

    }

}
