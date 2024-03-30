import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class GettingWeatherData {
	public String timestampToTime(long timestamp) {
        Date date = new Date(timestamp * 1000); // Convert to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    public String getVisibilityDescription(long visibility) {
        if (visibility < 100) {
            return "Zero";
        } else if (visibility < 1600) {
            return "Very Poor";
        } else if (visibility < 5000) {
            return "Poor";
        } else if (visibility < 8000) {
            return "Moderate";
        } else if (visibility < 10000) {
            return "Good";
        } else {
            return "Excellent";
        }
    }

    public String windDegToDir(long deg) {
        String[] compassDirections = {"NW", "N", "NE", "E", "SE", "S", "SW", "W"};
        return compassDirections[Math.round((float) deg / 45) % 8];
  
    }
    
    public JSONObject fetchWeatherData(String city, String key, String dKey) {
		try {
		String id = Encryption.decrypt(key ,dKey);
		String urlStr = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + id + "&units=metric";
		URL url = new URL(urlStr);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        //Getting the response code
        int responsecode = conn.getResponseCode();

        if (responsecode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responsecode);
        } else {
            String inline = "";
            Scanner scanner = new Scanner(url.openStream());

            //Write all the JSON data into a string using a scanner
            while (scanner.hasNext()) {
                inline += scanner.nextLine();
            }

            //Close the scanner
            scanner.close();

            //Using the JSON simple library parse the string into a json object
            JSONParser parse = new JSONParser();
            JSONObject weatherData = (JSONObject) parse.parse(inline);
            
            return weatherData;
        }
        
        } catch (Exception a) {
        	System.out.print(a);
        	JSONObject jsonObject = new JSONObject();
            return jsonObject;
        }
	}
}
