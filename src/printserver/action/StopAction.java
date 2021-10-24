package printserver.action;

public class StopAction extends PrivilegedPrintServerAction {

    @Override
    String getOperationName() {
        return "stop";
    }

    @Override
    void operation() {
        getPrintServer().stop();
    }

}