import java.util.Random;

public class Substitute implements SymCipher
{
    // encoding byte array - each byte value of the byte array 
    // (converted from passed in String) is substituted with 
    // corresponding byte in this array
    private byte[] key; 
    // decoding byte array - inverse of the above substitution
    private byte[] decode;

    // parameter-less constructor
    public Substitute()
    {
        key = new byte[256]; // 256 byte values
        // initialize key array with all 256 byte values in original order
        for (int i = 0; i < 256; i++)
            key[i] = (byte) i;
        
        // Fisher-Yates shuffle - decrementally swap array values randomly 
        // and inclusively, i.e. start from current index i = last index and 
        // swap values with a random index j, i INCLUSIVE, then decrement
        // current index i and repeat process until i == 1 (index 0 has no
        // value to swap with).
        int j = 0; // index of substitute (replace) value
        byte temp = 0;
        decode = new byte[256]; 
        Random random = new Random();
        for (int i = key.length-1; i > 0; i--)
        {
            // swap with random index j
            j = random.nextInt(i+1);
            temp = key[i];
            key[i] = key[j]; // set substitute value for index i
            key[j] = temp;

            // create inverse decoding array while forming encoding array
            decode[key[i] & 0xFF] = (byte) i; // byte value key[i] is signed 
                                              // (2's complement) and can be
                                              // negative, so it is bit-wise
                                              // forced to be positive, since
                                              // array is indexed by positive 
                                              // int (auto promoted from byte)
        }
    }

    // Encoding array is passed in. This constructor creates decoding array.
    public Substitute(byte[] bytes)
    {
        this.key = bytes;
        
        decode = new byte[256];
        for (int i = key.length-1; i > 0; i--)
        {
            decode[key[i] & 0xFF] = (byte) i; // bit-wise forced to be positive
        }
    }

    // Return an array of bytes that represent the key for the cipher
    public byte [] getKey() 
    {
        return this.key;
    }	
	
	// Encode the string using the key and return the result as an array of
	// bytes.
    public byte[] encode(String S)
    {
        byte[] bytes = S.getBytes(); // JDK convert String S to byte array

        for (int i = 0; i < bytes.length; i++)
        {
            bytes[i] = key[bytes[i] & 0xFF]; // convert byte to unsigned int index
        }

        return bytes;
    }
	
	// Decrypt the array of bytes and generate and return the corresponding String.
    public String decode(byte[] bytes)
    {
        for (int i = 0; i < bytes.length; i++)
        {
            bytes[i] = decode[bytes[i] & 0xFF];
        }

        return new String(bytes);
    }
}