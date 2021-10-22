package printserver;

import printserver.action.*;
import printserver.data.DataManager;

import javax.security.auth.Subject;
import javax.security.auth.login.*;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, SQLException {

        char[] key = new char[]{'a', 'b', 'c'};

        DataManager.init(key);
        DataManager.createDataIfNotExists();

        LoginContext loginContext = null;
        LoginCallbackHandler loginCallbackHandler = new LoginCallbackHandler();

        try {
            loginContext = new LoginContext("PrintServer", loginCallbackHandler);
        } catch (Exception e) {
            System.err.println("Cannot create LoginContext. "  + e.getMessage());
            System.exit(-1);
        }

        loginCallbackHandler.setPassword(new char[]{'t', 'e', 's', 't', 'P', 'a', 's', 's', 'w', 'o', 'r', 'd'});


        List<String> usernames = Arrays.asList("erica", "dirk", "cecile", "bart", "alice");


        for (String username : usernames) {

            loginCallbackHandler.setUsername(username);

            try {
                loginContext.login();

                Subject subject = loginContext.getSubject();

                PrintServer printServer = new PrintServer();

                List<PrivilegedAction<Object>> actions = getPrivilegedActions(printServer);

                System.out.println("Username: " + username);
                System.out.println(subject.getPrincipals().toArray()[0]);
                for (PrivilegedAction<Object> action : actions) {
                    System.out.print("\t");
                    Subject.doAsPrivileged(subject, action, null);
                }

                loginContext.logout();

            } catch (LoginException e) {
                System.out.println("Could not login for user " + username);
            }
        }
    }

    private static List<PrivilegedAction<Object>> getPrivilegedActions(PrintServer printServer) {
        List<PrivilegedAction<Object>> actions = new ArrayList<>();

        actions.add(new PrintAction(printServer, "dummy.txt"));
        actions.add(new QueueAction(printServer));
        actions.add(new ReadConfigAction(printServer, "dummy"));
        actions.add(new ResetAction(printServer));
        actions.add(new SetConfigAction(printServer, "dummy", "dummy"));
        actions.add(new StartAction(printServer));
        actions.add(new StatusAction(printServer));
        actions.add(new StopAction(printServer));
        actions.add(new TopQueueAction(printServer, 1));

        return actions;
    }
}
