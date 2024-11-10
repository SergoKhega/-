import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherService {

    private static final String API_KEY = "11bfe9c8-3a36-4c3c-a2a7-01847c547cd0";
    private static final String WEATHER_URL = "https://api.weather.yandex.ru/v2/forecast";

    public static void main(String[] args) {
        double lat = 55.75;
        double lon = 37.62;
        int limit = 7;

        try {
            String jsonResponse = getWeatherData(lat, lon);
            System.out.println("Ответ на запрос:\n" + jsonResponse);

            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject fact = jsonObject.getJSONObject("fact");
            int currentTemp = fact.getInt("temp");
            System.out.println("Текущая температура: " + currentTemp + "°C");

            double averageTemp = calculateAverageTemperature(jsonObject, limit);
            System.out.println("Средняя температура за последние " + limit + " дней: " + averageTemp + "°C");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getWeatherData(double lat, double lon) throws IOException {
        String urlString = WEATHER_URL + "?lat=" + lat + "&lon=" + lon;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("X-Yandex-Weather-Key", API_KEY);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    private static double calculateAverageTemperature(JSONObject jsonObject, int limit) {
        JSONArray forecasts = jsonObject.getJSONArray("forecasts");
        double totalTemp = 0;
        int count = Math.min(limit, forecasts.length());

        for (int i = 0; i < count; i++) {
            JSONObject forecast = forecasts.getJSONObject(i);
            JSONObject parts = forecast.getJSONObject("parts");
            totalTemp += parts.getJSONObject("day").getInt("temp_avg");
        }

        return totalTemp / count;
    }
}