package printserver.module;

import printserver.data.DataManager;
import printserver.principal.PrintServerPrincipal;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PrintServerLoginModule implements LoginModule {

    // state
    private Subject subject;
    private CallbackHandler callbackHandler;

    // options
    private boolean debug = false;
    private boolean hardcoded = true;
    private boolean rolebased = false;

    // auth status
    private boolean succeeded = false;
    private boolean commitSucceeded = false;

    private String username;
    private char[] password;
    private String[] roles;

    private List<String> usernames = Arrays.asList("erica", "dirk", "cecile", "bart", "alice");

    private List<PrintServerPrincipal> printServerPrincipals = new ArrayList<>();

    public void initialize(Subject subject,
                           CallbackHandler callbackHandler,
                           Map<java.lang.String, ?> sharedState,
                           Map<java.lang.String, ?> options) {

        this.subject = subject;
        this.callbackHandler = callbackHandler;

        debug = "true".equalsIgnoreCase((String)options.get("debug"));
        hardcoded = "true".equalsIgnoreCase((String)options.get("hardcoded"));
        rolebased = "true".equalsIgnoreCase((String)options.get("rolebased"));

        if (hardcoded && rolebased) {
            throw new IllegalArgumentException("hardcoded and rolebased can't both be true");
        }

        if (debug) {
            System.out.println("hardcoded: " + hardcoded);
            System.out.println("rolebased: " + rolebased);
        }
    }

    public boolean login() throws LoginException {
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("user name: ");
        callbacks[1] = new PasswordCallback("password: ", false);

        try {
            callbackHandler.handle(callbacks);
            username = ((NameCallback)callbacks[0]).getName();
            char[] tmpPassword = ((PasswordCallback)callbacks[1]).getPassword();
            if (tmpPassword == null) {
                tmpPassword = new char[0];
            }

            password = new char[tmpPassword.length];
            System.arraycopy(tmpPassword, 0, password, 0, tmpPassword.length);
            ((PasswordCallback)callbacks[1]).clearPassword();

        } catch (java.io.IOException ioe) {
            throw new LoginException(ioe.toString());
        } catch (UnsupportedCallbackException uce) {
            throw new LoginException("Error: " + uce.getCallback().toString() +
                    " not available to garner authentication information " +
                    "from the user");
        }

        if (debug) {
            System.out.println("\t\t[PrintServerLoginModule] " + "user entered user name: " + username);
            System.out.print("\t\t[PrintServerLoginModule] " + "user entered password: ");
            for (int i = 0; i < password.length; i++)
                System.out.print(password[i]);

            System.out.println();
        }

        boolean usernameCorrect = false;

        if (hardcoded) {
            usernameCorrect = this.usernames.contains(username);
        } else {
            try {
                usernameCorrect = DataManager.validateUsername(username);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (usernameCorrect && isPasswordCorrect(username, password)) {

            if (debug) {
                System.out.println("authentication succeeded");
            }

            succeeded = true;

            if (rolebased) {
                try {
                    this.roles = DataManager.getUserRoles(username);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return true;
        } else {


            if (debug) {
                System.out.println("authentication failed");
            }

            succeeded = false;
            username = null;

            if (roles != null) {
                for (int i = 0; i < roles.length; i++) {
                    roles[i] = "";
                }
                roles = null;
            }

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

    private boolean isPasswordCorrect(String username, char[] password) {
        if (hardcoded) {
            return password.length == 12 &&
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
                    password[11] == 'd';
        } else {
            try {
                return DataManager.validatePassword(username, password);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean commit() {
        if (succeeded == false) {
            return false;
        } else {
            if (rolebased) {
                for (String role : this.roles) {
                    PrintServerPrincipal printServerPrincipal = new PrintServerPrincipal(role);

                    if (debug) {
                        System.out.println("Adding principal for role " + role);
                    }

                    if (!subject.getPrincipals().contains(printServerPrincipal)) {
                        subject.getPrincipals().add(printServerPrincipal);
                    }

                    if (!printServerPrincipals.contains(printServerPrincipal)) {
                        printServerPrincipals.add(printServerPrincipal);
                    }

                }

            } else {
                PrintServerPrincipal printServerPrincipal = new PrintServerPrincipal(username);
                if (!subject.getPrincipals().contains(printServerPrincipal)) {
                    subject.getPrincipals().add(printServerPrincipal);
                }

                if (!printServerPrincipals.contains(printServerPrincipal)) {
                    printServerPrincipals.add(printServerPrincipal);
                }

            }

            cleanUpState();
            commitSucceeded = true;
            return true;
        }
    }

    public boolean abort() {
        if (succeeded == false) {
            return false;
        } else if (succeeded == true && commitSucceeded == false) {
            // login succeeded but overall authentication failed
            succeeded = false;

            cleanUpState();

            printServerPrincipals.clear();
        } else {
            // overall authentication succeeded and commit succeeded,
            // but someone else's commit failed
            logout();
        }
        return true;
    }

    public boolean logout() {

        for (PrintServerPrincipal principal : printServerPrincipals) {
            subject.getPrincipals().remove(principal);
        }

        succeeded = false;
        succeeded = commitSucceeded;

        cleanUpState();

        return true;
    }

    private void cleanUpState() {
        username = null;

        if (roles != null) {
            for (int i = 0; i < roles.length; i++) {
                roles[i] = "";
            }
            roles = null;
        }

        if (password != null) {
            for (int i = 0; i < password.length; i++)
                password[i] = ' ';
            password = null;
        }
    }
}