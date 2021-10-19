package printserver.action;

import printserver.PrintServer;
import printserver.PrintServerPermission;

import java.security.Permission;
import java.security.PrivilegedAction;

public class ResetAction implements PrivilegedAction {
    private PrintServer printServer;

    public ResetAction(PrintServer printServer) {
        this.printServer = printServer;
    }

    @Override
    public Object run() {
        Permission p = new PrintServerPermission("reset");
        SecurityManager s = System.getSecurityManager();
        if (s != null) {
            s.checkPermission(p);
        }

        printServer.reset();
        return null;
    }
}
