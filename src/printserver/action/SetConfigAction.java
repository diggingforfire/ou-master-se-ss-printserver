package printserver.action;

public class SetConfigAction extends PrivilegedPrintServerAction {

    private String parameter;
    private String value;

    public SetConfigAction(String parameter, String value) {
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