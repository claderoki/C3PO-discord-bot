import com.c3po.helper.EncryptionHelper;
import com.c3po.helper.environment.Configuration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EncryptionTest {
    public SecretKey getTestKey() {
        byte[] KEY = {118, 106, 107, 122, 76, 99, 69, 83, 101, 103, 82, 101, 116, 75, 101, 127};
        return new SecretKeySpec(KEY, "AES");
    }

    public static Set<String> plainValues() {
        return Set.of(
            "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from \"de Finibus Bonorum et Malorum\" by Cicero are also reproduced in their exact original form, accompanied by English versions from the 1914 translation by H. Rackham.",
            "The second law of thermodynamics",
            "Jona",
            "15"
        );
    }

    @ParameterizedTest
    @MethodSource("plainValues")
    public void testValues(String plainValue) {
        Configuration.initiate(Configuration.builder()
            .encryptionKey(getTestKey())
            .build());

        String encryptedValue = EncryptionHelper.encrypt(plainValue);
        String decryptedValue = EncryptionHelper.decrypt(encryptedValue);
        assertEquals(plainValue, decryptedValue);
    }
}
