package printserver.action;

import printserver.PrintServer;

public class PrintAction extends PrivilegedPrintServerAction {

    private String filename;

    public PrintAction(PrintServer printServer, String filename) {
        super(printServer);
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