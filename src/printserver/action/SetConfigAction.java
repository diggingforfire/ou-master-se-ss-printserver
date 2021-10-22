package printserver.action;

import printserver.PrintServer;

public class SetConfigAction extends PrivilegedPrintServerAction {

    private String parameter;
    private String value;

    public SetConfigAction(PrintServer printServer, String parameter, String value) {
        super(printServer);
        this.parameter = parameter;
        this.value = value;
    }

    @Override
    String getOperationName() {
        return "setconfig";
    }

    @Override
    void operation() {
        getPrintServer().setConfig(parameter, value);
    }

}