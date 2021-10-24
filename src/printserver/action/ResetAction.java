package printserver.action;

public class ResetAction extends PrivilegedPrintServerAction {

    @Override
    String getOperationName() {
        return "reset";
    }

    @Override
    void operation() {
        getPrintServer().reset();
    }

}