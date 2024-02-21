package GenericGet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import Const.Constant;

public class GenericGet {

	public static Integer getGeneric(String suffix) {
		Integer rvalue = null;
		URL url = null;
		try {
			url = new URL(Constant.PI_HOME+Constant.PORT+Constant.PATH_PREFIX+suffix);
			System.out.println(url.toString());
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
			System.out.print(rvalue);

			return rvalue;

		} catch (IOException e) {

			e.printStackTrace();

		}
		System.out.print(rvalue);
		return rvalue;

	}

}
