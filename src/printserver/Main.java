package printserver;

import printserver.action.PrintAction;
import printserver.data.DataManager;
import printserver.security.Hasher;

import javax.security.auth.Subject;
import javax.security.auth.login.*;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Iterator;

public class Main {

    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, SQLException {

        char[] key = new char[]{'a', 'b', 'c'};

        DataManager dataManager = new DataManager(new Hasher(), key);
        dataManager.createDataIfNotExists();

        dataManager.validateCredentials("dirk", "testpassword".toCharArray());

        LoginContext lc = null;

        try {
            lc = new LoginContext("PrintServer", new LoginCallbackHandler());
        } catch (LoginException le) {
            System.err.println("Cannot create LoginContext. "  + le.getMessage());
            System.exit(-1);
        } catch (SecurityException se) {
            System.err.println("Cannot create LoginContext. "  + se.getMessage());
            System.exit(-1);
        }

        // the user has 3 attempts to authenticate successfully
        int i;
        for (i = 0; i < 3; i++) {
            try {

                // attempt authentication
                lc.login();

                // if we return with no exception,
                // authentication succeeded
                break;

            } catch (LoginException le) {

                System.err.println("Authentication failed:");
                System.err.println("  " + le.getMessage());
                try {
                    Thread.currentThread().sleep(3000);
                } catch (Exception e) {
                    // ignore
                }

            }
        }

        // did they fail three times?
        if (i == 3) {
            System.out.println("Sorry");
            System.exit(-1);
        }

        System.out.println("Authentication succeeded!");

        Subject mySubject = lc.getSubject();

        Iterator<Principal> principalIterator = mySubject.getPrincipals().iterator();
        System.out.println("Authenticated user has the following Principals:");
        while (principalIterator.hasNext()) {
            Principal p = principalIterator.next();
            System.out.println("\t" + p.toString());
        }
        System.out.println("User has " +
                mySubject.getPublicCredentials().size() +
                " Public Credential(s)");

        PrintServer printServer = new PrintServer();

        // now try to execute the SampleAction as the authenticated Subject
        PrivilegedAction<Object> action = new PrintAction(printServer, "dummy.txt");
        Subject.doAsPrivileged(mySubject, action, null);

        System.exit(0);
    }
}
