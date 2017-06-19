package com.ac.common.fabric.hfc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.hyperledger.fabric.sdk.Enrollment;
import org.springframework.stereotype.Service;

import com.ac.common.fabric.utils.KeyStoreUtils;
import com.google.common.collect.Maps;

@Service
public class HFCKeyStore {

	private String file;

	private final Map<String, HFCUser> members = Maps.newHashMap();

	static {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	
	@PostConstruct
	private void init() throws Exception {
		File keyStoreFile = new File(System.getProperty("java.io.tmpdir") + "/HFCkeyStore.properties");
		file = keyStoreFile.getAbsolutePath();
	}

	/**
	 * Get the value associated with name.
	 *
	 * @param name
	 * @return value associated with the name
	 */
	public String getValue(String name) {
		Properties properties = loadProperties();
		return properties.getProperty(name);
	}

	/**
	 * Set the value associated with name.
	 *
	 * @param name
	 *            The name of the parameter
	 * @param value
	 *            Value for the parameter
	 */
	public void setValue(String name, String value) {
		Properties properties = loadProperties();
		try (OutputStream output = new FileOutputStream(file)) {
			properties.setProperty(name, value);
			properties.store(output, "");
			output.close();

		} catch (IOException e) {

		}
	}

	/**
	 * Get the user with a given name
	 *
	 * @return user
	 */
	public HFCUser getMember(String name, String org) {

		// Try to get the SampleUser state from the cache
		HFCUser user = members.get(HFCUser.toKeyValStoreName(name, org));
		if (null == user) {
			user = new HFCUser(name, null, org, this);
		}

		return user;
	}

	/**
	 * Get the user with a given name
	 *
	 * @return user
	 */
	public HFCUser getMember(String name, String org, String MSPID, File privateKeyFile, File certificateFile)
			throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {

		try {
			// Try to get the SampleUser state from the cache
			HFCUser sampleUser = members.get(HFCUser.toKeyValStoreName(name, org));
			if (null != sampleUser) {
				return sampleUser;
			}

			// Create the SampleUser and try to restore it's state from the key
			// value store (if found).
			sampleUser = new HFCUser(name, null, org, this);
			sampleUser.setMPSID(MSPID);

			String certificate = new String(IOUtils.toByteArray(new FileInputStream(certificateFile)), "UTF-8");
			PrivateKey privateKey = KeyStoreUtils
					.getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(privateKeyFile)));

			sampleUser.setEnrollment(new KeyStoreEnrollement(privateKey, certificate));
			sampleUser.saveState();

			return sampleUser;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw e;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			throw e;
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			throw e;
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw e;
		}

	}

	private Properties loadProperties() {
		Properties properties = new Properties();
		try (InputStream input = new FileInputStream(file)) {
			properties.load(input);
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		}

		return properties;
	}

	private static class KeyStoreEnrollement implements Enrollment, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2360947839724131303L;

		private PrivateKey privateKey;
		private String certificate;

		public KeyStoreEnrollement(PrivateKey privateKey, String certificate) {
			this.certificate = certificate;
			this.privateKey = privateKey;
		}

		@Override
		public PrivateKey getKey() {
			return privateKey;
		}

		@Override
		public String getCert() {
			return certificate;
		}

	}

}
