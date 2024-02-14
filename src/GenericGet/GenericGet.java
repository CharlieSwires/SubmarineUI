package GenericGet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GenericGet {

	public static Integer getGeneric(URL url) {
		Integer rvalue = null;

		try {

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

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
			return rvalue;

		} catch (IOException e) {

			e.printStackTrace();

		}
		return rvalue;

	}

}
