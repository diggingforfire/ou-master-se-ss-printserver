package printserver.action;

public class StatusAction extends PrivilegedPrintServerAction {

    @Override
    String getOperationName() {
        return "status";
    }

    @Override
    void operation() {
        getPrintServer().status();
    }

}