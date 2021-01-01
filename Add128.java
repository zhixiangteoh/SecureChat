import java.security.SecureRandom;

public class Add128 implements SymCipher
{
    private byte[] key;

    // parameter-less constructor
    public Add128()
    {
        // JDK cryptographically strong RNG
        SecureRandom random = new SecureRandom();
        // Create 128-byte array of random byte values
        key = new byte[128];
        random.nextBytes(key);
    }

    public Add128(byte[] bytes)
    {
        this.key = bytes;
    }

    // Return an array of bytes that represent the key for the cipher
    public byte[] getKey() 
    {
        return this.key;
    }	
	
	// Encode the string using the key and return the result as an array of
	// bytes. 
    public byte[] encode(String S)
    {
        byte[] bytes = S.getBytes(); // JDK convert String S to byte array

        // For each byte in byte-array form of String S, add the byte value in 
        // corresponding index of encoding array (byte[] key). Because encoding
        // array has length 128, while byte[] bytes has variable length, 'wrap'
        // the indices of byte[] bytes around byte[] key as required.
        for (int i = 0, j = 0; i < bytes.length; i++, j++)
        {
            bytes[i] += key[j];

            if (j == key.length-1)
                j = 0;
        }

        return bytes;
    }
	
	// Decrypt the array of bytes and generate and return the corresponding String.
    public String decode(byte[] bytes)
    {
        // Inverse of encoding array - instead of adding the byte values we subtract
        // accordingly. Similar to the above we 'wrap' the variable-length byte[] bytes
        // around the encoding array byte[] keys as many times as required.
        for (int i = 0, j = 0; i < bytes.length; i++, j++)
        {
            bytes[i] -= key[j];

            if (j == key.length-1)
                j = 0;
        }

        // return the decoded (after subtracting encoding values accordingly) byte array
        // converted to String
        return new String(bytes);
    }
}