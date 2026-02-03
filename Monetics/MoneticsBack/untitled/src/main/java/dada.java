import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarBCrypt {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("admin123    -> " + encoder.encode("admin123"));
        System.out.println("manager123  -> " + encoder.encode("manager123"));
        System.out.println("user123     -> " + encoder.encode("user123"));
    }
}
