package printserver.action;

import printserver.PrintServerPermission;

import java.security.AccessControlException;
import java.security.Permission;
import java.security.PrivilegedAction;

public abstract class PrivilegedPrintServerAction implements PrivilegedAction<Object> {

    private static PrintServer printServer = new PrintServer();

    protected PrintServer getPrintServer() {
        return printServer;
    }

    abstract String getOperationName();

    abstract void operation();

    @Override
    public Object run() {
        SecurityManager securityManager = System.getSecurityManager();

        if (securityManager == null) {
            throw new IllegalStateException("No security manager available");
        }

        Permission permission = new PrintServerPermission(getOperationName());

        System.out.print("Permission to run operation " + getOperationName() + ":\t\t");

        try {
            securityManager.checkPermission(permission);
            System.out.print("TRUE");
        } catch (AccessControlException ace) {
            System.out.print("FALSE");
        }

        System.out.println();

        operation();

        return null;
    }

}