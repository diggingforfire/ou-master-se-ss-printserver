package printserver.action;

import printserver.PrintServer;
import printserver.PrintServerPermission;

import java.security.Permission;
import java.security.PrivilegedAction;

public class PrintAction implements PrivilegedAction {
    private PrintServer printServer;
    private String filename;

    public PrintAction(PrintServer printServer, String filename) {
        this.printServer = printServer;
        this.filename = filename;
    }

    @Override
    public Object run() {
        Permission p = new PrintServerPermission("print");
        SecurityManager s = System.getSecurityManager();

        if (s != null) {
            s.checkPermission(p);
        }

        printServer.print(filename);
        return null;
    }
}
