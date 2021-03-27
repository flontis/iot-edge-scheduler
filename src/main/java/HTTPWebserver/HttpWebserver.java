package HTTPWebserver;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HttpWebserver {

    public static void main(String[] args) throws IOException {
        CloseableHttpClient getClient = HttpClients.createDefault();
        HttpGet get = new HttpGet("http://localhost:20000/");
        StringEntity entity = new StringEntity("",ContentType.APPLICATION_JSON);
//    entity.setContentEncoding("application/json");
        CloseableHttpResponse response = getClient.execute(get);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String responseLine;
        while ((responseLine = bufferedReader.readLine()) != null) {
            result.append(responseLine);
        }
        //added test commentary
        System.out.println("Response: " + result.toString());


    }

}
