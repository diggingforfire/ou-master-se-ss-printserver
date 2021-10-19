package printserver.principal;

import java.security.Principal;
import java.util.Objects;

public class PrintServerUserPrincipal implements Principal, java.io.Serializable {
    /**
     * @serial
     */
    private String name;

    public PrintServerUserPrincipal(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "PrintServerUserPrincipal: " + getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrintServerUserPrincipal that = (PrintServerUserPrincipal) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
