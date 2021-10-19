package printserver.action;

import printserver.PrintServer;
import printserver.PrintServerPermission;

import java.security.Permission;
import java.security.PrivilegedAction;

public class QueueAction implements PrivilegedAction {
    private PrintServer printServer;

    public QueueAction(PrintServer printServer) {
        this.printServer = printServer;
    }

    @Override
    public Object run() {
        Permission p = new PrintServerPermission("queue");
        SecurityManager s = System.getSecurityManager();
        if (s != null) {
            s.checkPermission(p);
        }

        printServer.queue();
        return null;
    }
}
