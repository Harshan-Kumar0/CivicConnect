
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class usercreation {
    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("Melvarix@12"));
    }
}