package printserver.action;

import printserver.PrintServer;
import printserver.PrintServerPermission;

import java.security.Permission;
import java.security.PrivilegedAction;

public class ReadConfigAction implements PrivilegedAction {
    public ReadConfigAction(PrintServer printServer, String parameter) {
        this.printServer = printServer;
        this.parameter = parameter;
    }

    private PrintServer printServer;
    private String parameter;
    @Override
    public Object run() {
        Permission p = new PrintServerPermission("readconfig");
        SecurityManager s = System.getSecurityManager();
        if (s != null) {
            s.checkPermission(p);
        }

        printServer.readConfig(parameter);
        return null;
    }
}
