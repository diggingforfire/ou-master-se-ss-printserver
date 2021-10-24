package printserver.action;

public class TopQueueAction extends PrivilegedPrintServerAction {
    private int job;

    public TopQueueAction(int job) {
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