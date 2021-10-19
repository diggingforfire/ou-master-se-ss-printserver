package printserver.action;

import printserver.PrintServer;
import printserver.PrintServerPermission;

import java.security.Permission;
import java.security.PrivilegedAction;

public class TopQueueAction implements PrivilegedAction {
    private PrintServer printServer;
    private int job;

    public TopQueueAction(PrintServer printServer, int job) {
        this.printServer = printServer;
        this.job = job;
    }

    @Override
    public Object run() {
        Permission p = new PrintServerPermission("topqueue");
        SecurityManager s = System.getSecurityManager();
        if (s != null) {
            s.checkPermission(p);
        }

        printServer.status();
        return null;
    }
}
