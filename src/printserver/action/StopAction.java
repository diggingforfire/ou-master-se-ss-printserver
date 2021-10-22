package printserver.action;

import printserver.PrintServer;

public class StopAction extends PrivilegedPrintServerAction {

    public StopAction(PrintServer printServer) {
        super(printServer);
    }

    @Override
    String getOperationName() {
        return "stop";
    }

    @Override
    void operation() {
        getPrintServer().stop();
    }

}