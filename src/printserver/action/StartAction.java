package printserver.action;

import printserver.PrintServer;

public class StartAction extends PrivilegedPrintServerAction {

    public StartAction(PrintServer printServer) {
        super(printServer);
    }

    @Override
    String getOperationName() {
        return "start";
    }

    @Override
    void operation() {
        getPrintServer().start();
    }

}