package printserver.principal;

import java.security.Principal;
import java.util.Objects;

public class PrintServerPrincipal implements Principal, java.io.Serializable {
    /**
     * @serial
     */
    private String name;

    public PrintServerPrincipal(String name) {
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
        return "PrintServerPrincipal: " + getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrintServerPrincipal that = (PrintServerPrincipal) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
