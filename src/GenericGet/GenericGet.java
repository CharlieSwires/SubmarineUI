package GenericGet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Const.Constant;



public class GenericGet {

	private static final ExecutorService executor = Executors.newCachedThreadPool(); // Thread pool for async tasks
	private static final int MAX_RETRIES = 5;
    private static final int TIMEOUT_SECONDS = 5; // Timeout for each request
    private static final Logger log = LoggerFactory.getLogger(GenericGet.class);

    public void getGenericAsync(String suffix, Consumer<Integer> onSuccess, Consumer<String> onError) {
        executor.submit(() -> {
            int retries = MAX_RETRIES;
            while (retries > 0) {
                Future<Integer> future = executor.submit(() -> {
                    URL url = new URL(Constant.PI_HOME + Constant.PORT + Constant.PATH_PREFIX + suffix);
                    log.info(url.toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-Type", "application/json");

                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("HTTP error code: " + conn.getResponseCode());
                    }

                    try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        StringBuilder inString = new StringBuilder();
                        String output;
                        while ((output = br.readLine()) != null) {
                            inString.append(output);
                        }
                        conn.disconnect();
                        return Integer.parseInt(inString.toString());
                    }
                });

                try {
                    Integer result = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS); // Enforce timeout
                    onSuccess.accept(result);
                    return; // Exit if successful
                } catch (TimeoutException e) {
                    future.cancel(true); // Cancel the task
                    log.error("Request timed out after " + TIMEOUT_SECONDS + " seconds", e);
                    retries--;
                } catch (Exception e) {
                    log.error("Error in HTTP request. Retries left: " + retries, e);
                    retries--;
                }

                if (retries == 0) {
                    onError.accept("Failed after " + MAX_RETRIES + " retries or timeout.");
                }

                try {
                    Thread.sleep(1000); // Delay between retries
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    onError.accept("Thread interrupted: " + e.getMessage());
                    return;
                }
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }

}
