package printserver.action;

import printserver.PrintServer;
import printserver.PrintServerPermission;

import java.security.Permission;
import java.security.PrivilegedAction;

public class StatusAction implements PrivilegedAction {
    private PrintServer printServer;

    public StatusAction(PrintServer printServer) {
        this.printServer = printServer;
    }

    @Override
    public Object run() {
        Permission p = new PrintServerPermission("status");
        SecurityManager s = System.getSecurityManager();
        if (s != null) {
            s.checkPermission(p);
        }

        printServer.status();
        return null;
    }
}
