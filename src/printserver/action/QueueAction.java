package printserver.action;

public class QueueAction extends PrivilegedPrintServerAction {

    @Override
    String getOperationName() {
        return "queue";
    }

    @Override
    void operation() {
        getPrintServer().queue();
    }

}