package printserver.module;

import printserver.principal.PrintServerUserPrincipal;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.util.Map;

public class PrintServerLoginModule implements LoginModule {

    // initial state
    private Subject subject;
    private CallbackHandler callbackHandler;

    // configurable option
    private boolean debug = false;

    // the authentication status
    private boolean succeeded = false;
    private boolean commitSucceeded = false;

    // username and password
    private String username;
    private char[] password;

    // principal
    private PrintServerUserPrincipal printServerUserPrincipal;

    public void initialize(Subject subject,
                           CallbackHandler callbackHandler,
                           Map<java.lang.String, ?> sharedState,
                           Map<java.lang.String, ?> options) {

        this.subject = subject;
        this.callbackHandler = callbackHandler;

        // initialize any configured options
        debug = "true".equalsIgnoreCase((String)options.get("debug"));
    }

    public boolean login() throws LoginException {

        // prompt for a user name and password
        if (callbackHandler == null)
            throw new LoginException("Error: no CallbackHandler available " +
                    "to garner authentication information from the user");

        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("user name: ");
        callbacks[1] = new PasswordCallback("password: ", false);

        try {
            callbackHandler.handle(callbacks);
            username = ((NameCallback)callbacks[0]).getName();
            char[] tmpPassword = ((PasswordCallback)callbacks[1]).getPassword();
            if (tmpPassword == null) {
                // treat a NULL password as an empty password
                tmpPassword = new char[0];
            }
            password = new char[tmpPassword.length];
            System.arraycopy(tmpPassword, 0,
                    password, 0, tmpPassword.length);
            ((PasswordCallback)callbacks[1]).clearPassword();

        } catch (java.io.IOException ioe) {
            throw new LoginException(ioe.toString());
        } catch (UnsupportedCallbackException uce) {
            throw new LoginException("Error: " + uce.getCallback().toString() +
                    " not available to garner authentication information " +
                    "from the user");
        }

        // print debugging information
        if (debug) {
            System.out.println("\t\t[PrintServerLoginModule] " +
                    "user entered user name: " +
                    username);
            System.out.print("\t\t[PrintServerLoginModule] " +
                    "user entered password: ");
            for (int i = 0; i < password.length; i++)
                System.out.print(password[i]);
            System.out.println();
        }

        // verify the username/password
        boolean usernameCorrect = false;
        if (username.equals("dirk") || (username.equals("erica")))
            usernameCorrect = true;
        if (usernameCorrect &&
                password.length == 12 &&
                password[0] == 't' &&
                password[1] == 'e' &&
                password[2] == 's' &&
                password[3] == 't' &&
                password[4] == 'P' &&
                password[5] == 'a' &&
                password[6] == 's' &&
                password[7] == 's' &&
                password[8] == 'w' &&
                password[9] == 'o' &&
                password[10] == 'r' &&
                password[11] == 'd') {

            // authentication succeeded!!!
            if (debug)
                System.out.println("\t\t[PrintServerLoginModule] " +
                        "authentication succeeded");
            succeeded = true;
            return true;
        } else {

            // authentication failed -- clean out state
            if (debug)
                System.out.println("\t\t[PrintServerLoginModule] " +
                        "authentication failed");
            succeeded = false;
            username = null;
            for (int i = 0; i < password.length; i++)
                password[i] = ' ';
            password = null;
            if (!usernameCorrect) {
                throw new FailedLoginException("User Name Incorrect");
            } else {
                throw new FailedLoginException("Password Incorrect");
            }
        }
    }

    public boolean commit() throws LoginException {
        if (succeeded == false) {
            return false;
        } else {
            // add a Principal (authenticated identity)
            // to the Subject

            // assume the user we authenticated is the SamplePrincipal
            printServerUserPrincipal = new PrintServerUserPrincipal(username);
            if (!subject.getPrincipals().contains(printServerUserPrincipal))
                subject.getPrincipals().add(printServerUserPrincipal);

            if (debug) {
                System.out.println("\t\t[PrintServerLoginModule] " +
                        "added PrintServerUserPrincipal to Subject");
            }

            // in any case, clean out state
            username = null;
            for (int i = 0; i < password.length; i++)
                password[i] = ' ';
            password = null;

            commitSucceeded = true;
            return true;
        }
    }

    public boolean abort() throws LoginException {
        if (succeeded == false) {
            return false;
        } else if (succeeded == true && commitSucceeded == false) {
            // login succeeded but overall authentication failed
            succeeded = false;
            username = null;
            if (password != null) {
                for (int i = 0; i < password.length; i++)
                    password[i] = ' ';
                password = null;
            }
            printServerUserPrincipal = null;
        } else {
            // overall authentication succeeded and commit succeeded,
            // but someone else's commit failed
            logout();
        }
        return true;
    }

    public boolean logout() throws LoginException {

        subject.getPrincipals().remove(printServerUserPrincipal);
        succeeded = false;
        succeeded = commitSucceeded;
        username = null;
        if (password != null) {
            for (int i = 0; i < password.length; i++)
                password[i] = ' ';
            password = null;
        }
        printServerUserPrincipal = null;
        return true;
    }
}