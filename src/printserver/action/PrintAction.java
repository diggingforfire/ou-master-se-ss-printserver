package printserver.action;

public class PrintAction extends PrivilegedPrintServerAction {

    private String filename;

    public PrintAction(String filename) {
        this.filename = filename;
    }

    @Override
    String getOperationName() {
        return "print";
    }

    @Override
    void operation() {
        getPrintServer().print(filename);
    }

}