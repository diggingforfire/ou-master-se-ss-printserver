package printserver.action;

import printserver.PrintServer;

public class ResetAction extends PrivilegedPrintServerAction {

    public ResetAction(PrintServer printServer) {
        super(printServer);
    }

    @Override
    String getOperationName() {
        return "reset";
    }

    @Override
    void operation() {
        getPrintServer().reset();
    }

}