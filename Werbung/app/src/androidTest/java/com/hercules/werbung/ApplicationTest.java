package com.hercules.werbung;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.pakhee.common.CryptLib;

import org.junit.Test;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }



    @Test
    public void testCryptLib() {
        try {
            CryptLib _crypt = new CryptLib();
            String output = "";
            String plainText = "This is the text to be encrypted.";
            String key = CryptLib.SHA256("my secret key", 32); //32 bytes = 256 bit
            System.out.println("key=" + key);
            String iv = CryptLib.generateRandomIV(16); //16 bytes = 128 bit
            output = _crypt.encrypt(plainText, key, iv); //encrypt
            System.out.println("encrypted text=" + output);
            output = _crypt.decrypt("dRvigq0mnA8vz+iR4or4jihYwvbW455DnFYsbz9+Iq710GE6Ut+ziFxQaipdtgzyIdVgCzJ9U17ZiUnWvEapfA==", "19D6DFD0BF612440214585203A5B5EB0", "6bYl7nm00btCvn8="); //decrypt
            System.out.println("decrypted text=" + output);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}