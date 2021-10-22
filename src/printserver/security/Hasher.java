package printserver.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class Hasher {
    public static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    public static final int DEFAULT_ITERATION_COUNT = 100000;
    public static final int SALT_BYTES = 16;
    public static final int KEY_SIZE_BYTES = 64;

    /**
     * Creates a hash of the provided password
     * @param password The password to hash
     * @param salt The salt used in the hash function
     * @param iterationCount The number of iterations used in the hash function
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static byte[] createHash(char[] password, byte[] salt, int iterationCount) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password, salt, iterationCount, KEY_SIZE_BYTES * 8);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return hash;
    }

    /**
     * Creates a random salt based on a cryptographically strong random number generator
     * @return The created salt
     */
    public static byte[] createRandomSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);
        return salt;
    }
}
