package ca.utoronto.utm.mcs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Utils {
    public static String convert(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }

    }
    public static double coordinatesToKM(double long1, double lat1, double long2, double lat2){
        double  d = 12742* Math.asin(Math.sqrt((Math.sin((lat2 - lat1)/2)*Math.sin((lat2 - lat1)/2) )+ Math.cos(lat2)*Math.cos(lat1)*Math.sin((long2 - long1)/2)*Math.sin((long2 - long1)/2)));
        return d;
    }
}
