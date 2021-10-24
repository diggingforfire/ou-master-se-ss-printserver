package printserver;

import printserver.action.*;
import printserver.data.DataManager;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.security.GeneralSecurityException;
import java.security.PrivilegedAction;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        if (Arrays.asList(args).contains("usedatabase")) {
            System.out.println("Please enter database password: ");
            char[] databaseKey = new char[0];
            try {
                databaseKey = readPassword(System.in);
            } catch (IOException e) {
                System.out.println("Error reading database password");
                e.printStackTrace();
                System.exit(-1);
            }

            try {
                DataManager.init(databaseKey);
                DataManager.createDataIfNotExists();
            } catch (SQLException sqle) {
                System.out.println("Error initializing database. Possible invalid key!");
                System.exit(-1);
            } catch (GeneralSecurityException ge) {
                System.out.println("Error creating database.");
                ge.printStackTrace();
                System.exit(-1);
            }
        }

        LoginContext loginContext = null;
        LoginCallbackHandler loginCallbackHandler = new LoginCallbackHandler();

        try {
            loginContext = new LoginContext("PrintServer", loginCallbackHandler);
        } catch (Exception e) {
            System.err.println("Cannot create LoginContext. "  + e.getMessage());
            System.exit(-1);
        }

        loginCallbackHandler.setPassword(new char[]{'t', 'e', 's', 't', 'P', 'a', 's', 's', 'w', 'o', 'r', 'd'});

        // could be pulled from database instead
        List<String> usernames = Arrays.asList("erica", "dirk", "cecile", "bart", "alice");

        for (String username : usernames) {

            loginCallbackHandler.setUsername(username);

            try {
                loginContext.login();

                Subject subject = loginContext.getSubject();

                List<PrivilegedAction<Object>> actions = getPrivilegedActions();

                System.out.println("Username: " + username);
                System.out.println(subject.getPrincipals().toArray()[0]);
                for (PrivilegedAction<Object> action : actions) {
                    System.out.print("\t");
                    Subject.doAsPrivileged(subject, action, null);
                }

                loginContext.logout();

            } catch (LoginException e) {
                System.out.println("Could not login for user " + username + ". " + e);
            } finally {

            }
        }
    }

    private static List<PrivilegedAction<Object>> getPrivilegedActions() {
        List<PrivilegedAction<Object>> actions = new ArrayList<>();

        actions.add(new PrintAction("dummy.txt"));
        actions.add(new QueueAction());
        actions.add(new ReadConfigAction("dummy"));
        actions.add(new ResetAction());
        actions.add(new SetConfigAction("dummy", "dummy"));
        actions.add(new StartAction());
        actions.add(new StatusAction());
        actions.add(new StopAction());
        actions.add(new TopQueueAction(1));

        return actions;
    }

    private static char[] readPassword(InputStream in) throws IOException {

        char[] lineBuffer;
        char[] buf;

        buf = lineBuffer = new char[128];

        int room = buf.length;
        int offset = 0;
        int c;

        loop:
        while (true) {
            switch (c = in.read()) {
                case -1:
                case '\n':
                    break loop;

                case '\r':
                    int c2 = in.read();
                    if ((c2 != '\n') && (c2 != -1)) {
                        if (!(in instanceof PushbackInputStream)) {
                            in = new PushbackInputStream(in);
                        }
                        ((PushbackInputStream)in).unread(c2);
                    } else
                        break loop;

                default:
                    if (--room < 0) {
                        buf = new char[offset + 128];
                        room = buf.length - offset - 1;
                        System.arraycopy(lineBuffer, 0, buf, 0, offset);
                        Arrays.fill(lineBuffer, ' ');
                        lineBuffer = buf;
                    }
                    buf[offset++] = (char) c;
                    break;
            }
        }

        if (offset == 0) {
            return null;
        }

        char[] ret = new char[offset];
        System.arraycopy(buf, 0, ret, 0, offset);
        Arrays.fill(buf, ' ');

        return ret;
    }
}