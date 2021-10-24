package printserver.action;

public class ReadConfigAction extends PrivilegedPrintServerAction {

    private String parameter;

    public ReadConfigAction(String parameter) {
        this.parameter = parameter;
    }

    @Override
    String getOperationName() {
        return "readconfig";
    }

    @Override
    void operation() {
        getPrintServer().readConfig(parameter);
    }

}