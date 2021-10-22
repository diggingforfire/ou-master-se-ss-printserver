package printserver.action;

import printserver.PrintServer;

public class StatusAction extends PrivilegedPrintServerAction {

    public StatusAction(PrintServer printServer) {
          super(printServer);
    }

    @Override
    String getOperationName() {
        return "status";
    }

    @Override
    void operation() {
        getPrintServer().status();
    }

}