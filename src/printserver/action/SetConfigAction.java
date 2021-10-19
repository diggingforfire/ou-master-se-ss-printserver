package printserver.action;

import printserver.PrintServer;
import printserver.PrintServerPermission;

import java.security.Permission;
import java.security.PrivilegedAction;

public class SetConfigAction implements PrivilegedAction {
    private PrintServer printServer;
    private String parameter;
    private String value;

    public SetConfigAction(PrintServer printServer, String parameter, String value) {
        this.printServer = printServer;
        this.parameter = parameter;
        this.value = value;
    }

    @Override
    public Object run() {
        Permission p = new PrintServerPermission("setconfig");
        SecurityManager s = System.getSecurityManager();
        if (s != null) {
            s.checkPermission(p);
        }

        printServer.setConfig(parameter, value);
        return null;
    }
}
