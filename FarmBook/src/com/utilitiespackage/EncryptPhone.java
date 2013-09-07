package com.utilitiespackage;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.*;
import javax.crypto.*;

public class EncryptPhone {
	public static void main(String[] args) throws Exception{


		KeyPairGenerator kgen = KeyPairGenerator.getInstance("RSA");
		kgen.initialize(1024,new SecureRandom());
		KeyPair pair = kgen.genKeyPair();

		RSAPublicKey pkey= (RSAPublicKey) pair.getPublic();

		System.out.println("Public Key :");
		System.out.println("Public Exponent e = " + pkey.getPublicExponent().toString());
		System.out.println("Modulus n = " + pkey.getModulus().toString());

		RSAPrivateKey prkey= (RSAPrivateKey) pair.getPrivate();

		System.out.println("\nPrivate Key :");
		System.out.println("Private Exponent d = " + prkey.getPrivateExponent().toString());
		System.out.println("Modulus n = " + pkey.getModulus().toString());

		//Encryption
		Cipher cipher = Cipher.getInstance("RSA");

		cipher.init(Cipher.ENCRYPT_MODE, pair.getPublic());
		System.out.println("\nOriginal Number : '015555245554'");

		byte[] encrypted = cipher.doFinal("015555245554".getBytes());

		BigInteger bi = new BigInteger(encrypted);
		System.out.println("Encrypted Message : " + bi.toString()); 

		cipher.init(Cipher.DECRYPT_MODE, pair.getPrivate());
		byte[] decrypted = cipher.doFinal(encrypted);

		System.out.println("Decrypted Message : '" + new String(decrypted) + "'");
	}

}
