package printserver.action;

import printserver.PrintServer;

public class ReadConfigAction extends PrivilegedPrintServerAction {

    private String parameter;

    public ReadConfigAction(PrintServer printServer, String parameter) {
        super(printServer);
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