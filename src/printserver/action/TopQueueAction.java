package printserver.action;

import printserver.PrintServer;

public class TopQueueAction extends PrivilegedPrintServerAction {
    private int job;

    public TopQueueAction(PrintServer printServer, int job) {
        super(printServer);
        this.job = job;
    }

    @Override
    String getOperationName() {
        return "topqueue";
    }

    @Override
    void operation() {
        getPrintServer().topQueue(job);
    }

}