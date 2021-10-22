package printserver.action;

import printserver.PrintServer;

public class QueueAction extends PrivilegedPrintServerAction {

    public QueueAction(PrintServer printServer) {
        super(printServer);
    }

    @Override
    String getOperationName() {
        return "queue";
    }

    @Override
    void operation() {
        getPrintServer().queue();
    }

}