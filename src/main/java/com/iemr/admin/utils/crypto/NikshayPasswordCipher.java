package com.iemr.admin.utils.crypto;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Encrypts the Ni-kshay PHI password of a camp device before it is stored in
 * phir_device_configuration.password.
 *
 * Format (MMU-API's Ni-kshay job must decrypt the same way, with the same key):
 * AES/GCM/NoPadding, 12-byte random IV, 128-bit tag; stored value is
 * Base64( IV || ciphertext+tag ).
 *
 * The key is Base64 of 16, 24 or 32 random bytes (e.g. `openssl rand -base64 32`),
 * read from the NIKSHAY_DEVICE_AES_KEY environment variable through the
 * nikshay.device.aes-key property. It is never logged. When it is missing or
 * invalid the app still starts, but saving a device password fails with a
 * clear error.
 */
@Component
public class NikshayPasswordCipher {

	private final Logger logger = LoggerFactory.getLogger(NikshayPasswordCipher.class);

	private static final String TRANSFORMATION = "AES/GCM/NoPadding";
	private static final int IV_LENGTH = 12;
	private static final int TAG_LENGTH_BITS = 128;

	private final SecureRandom secureRandom = new SecureRandom();

	@Value("${nikshay.device.aes-key:}")
	private String base64Key;

	private SecretKey key;

	@PostConstruct
	void init() {
		if (base64Key == null || base64Key.isBlank()) {
			logger.warn("nikshay.device.aes-key (NIKSHAY_DEVICE_AES_KEY) is not set; Ni-kshay device passwords cannot be saved");
			return;
		}
		// A bad key only disables device password saving; it must not stop the
		// rest of Admin-API from starting
		byte[] raw;
		try {
			raw = Base64.getDecoder().decode(base64Key.trim());
		} catch (IllegalArgumentException e) {
			logger.error("nikshay.device.aes-key is not valid Base64; Ni-kshay device passwords cannot be saved");
			return;
		}
		if (raw.length != 16 && raw.length != 24 && raw.length != 32) {
			logger.error("nikshay.device.aes-key must decode to 16, 24 or 32 bytes; Ni-kshay device passwords cannot be saved");
			return;
		}
		key = new SecretKeySpec(raw, "AES");
	}

	public String encrypt(String plainText) {
		SecretKey k = requireKey();
		try {
			byte[] iv = new byte[IV_LENGTH];
			secureRandom.nextBytes(iv);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, k, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
			byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder()
					.encodeToString(ByteBuffer.allocate(iv.length + cipherText.length).put(iv).put(cipherText).array());
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Could not encrypt device password", e);
		}
	}

	// Not used by Admin-API itself; kept next to encrypt() so the format is
	// defined in one place (MMU-API mirrors this).
	public String decrypt(String stored) {
		SecretKey k = requireKey();
		try {
			byte[] all = Base64.getDecoder().decode(stored);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, k, new GCMParameterSpec(TAG_LENGTH_BITS, all, 0, IV_LENGTH));
			return new String(cipher.doFinal(all, IV_LENGTH, all.length - IV_LENGTH), StandardCharsets.UTF_8);
		} catch (GeneralSecurityException | IllegalArgumentException e) {
			throw new IllegalStateException("Could not decrypt device password");
		}
	}

	private SecretKey requireKey() {
		if (key == null) {
			throw new IllegalStateException(
					"Device password encryption key is not configured (NIKSHAY_DEVICE_AES_KEY). Contact the administrator.");
		}
		return key;
	}
}
