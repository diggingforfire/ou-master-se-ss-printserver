package printserver.action;

public class StartAction extends PrivilegedPrintServerAction {

    @Override
    String getOperationName() {
        return "start";
    }

    @Override
    void operation() {
        getPrintServer().start();
    }

}