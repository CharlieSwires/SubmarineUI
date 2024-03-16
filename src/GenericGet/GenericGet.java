package GenericGet;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import Const.Constant;

public class GenericGet {

	private static int retryCount = 5;
	public static Integer getGeneric(String suffix) {
		Integer rvalue = null;
		URL url = null;
		try {
			url = new URL(Constant.PI_HOME+Constant.PORT+Constant.PATH_PREFIX+suffix);
			System.out.print(url.toString());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			throw new RuntimeException(e1);
		}
		while (retryCount > 0 ) {
			try {

				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("Content-Type", "application/json");

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));

				String output;
				StringBuffer inString = new StringBuffer();

				while ((output = br.readLine()) != null) {
					inString.append(output);
				}
				//JSON from String to Object
				rvalue = Integer.parseInt(inString.toString());

				conn.disconnect();
				System.out.println(":"+rvalue);
				retryCount = 5;
				return rvalue;

			} catch (Exception  e2) {
				retryCount--;
			}
		} //end retry while
		throw new RuntimeException("Retried and failed 5 times!!");

	}
	public static Image getImage(String suffix) {
		URL url = null;
		try {
			url = new URL(Constant.PI_HOME + Constant.PORT + Constant.PATH_PREFIX + suffix);
			System.out.println("Requesting URL: " + url.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null; // Return null if URL is malformed
		}

		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "image/jpeg");
			conn.setRequestProperty("Content-Type", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed: HTTP error code : " + conn.getResponseCode());
			}

			// Use ImageIO to read the image directly from the InputStream
			Image image = ImageIO.read(conn.getInputStream());

			conn.disconnect(); // Disconnect after processing
			return image; // Return the fetched image

		} catch (IOException e) {
			e.printStackTrace(); // Log exceptions
		} finally {
			if (conn != null) {
				conn.disconnect(); // Ensure disconnection in case of an exception
			}
		}
		return null; // Return null if the process fails
	}

}
