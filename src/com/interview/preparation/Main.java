package com.interview.preparation;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        // write your code here
        String url = "https://jsonmock.hackerrank.com/api/inventory?barcode=74001755";
        Map<String, Integer> data = makeRequest(url);
        System.out.println(" price : "+data.get("price") +" Discount "+data.get("discount")*100/data.get("price"));
        int discounted = data.get("price") - ((data.get("discount")*100)/data.get("price"));
        System.out.println(discounted);

    }

    private static Map<String, Integer> makeRequest(String url) {
        Map<String, Integer> data = new HashMap<>();

        HttpsURLConnection connection = null;
        OutputStreamWriter wr = null;
        BufferedReader rd = null;
        StringBuilder sb = null;
        String line = null;
        URL serverAddress = null;

        try {
            serverAddress = new URL(url);
            //set up out communication stuff

            // Set up the initial connection
            connection = (HttpsURLConnection) serverAddress.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setReadTimeout(1000);
            connection.connect();
            /*
             * get the output stream writer and write the output to the server
             * not needed in this example
             * wr = new OutputStreamWriter( connection.getOutputStream());
             * wr.write();
             * wr.flush();
             * read the result from the server
             * */

            rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            sb = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String data_ = sb.toString();
            data_ = data_.substring(data_.indexOf("data"), data_.length());
            String sRegex = "(?<=\\{)([^\\}]+)(?=\\})";


            Matcher m = Pattern.compile(sRegex).matcher(data_);
            int price_ = 0;
            int discount_ = 0;


            while (m.find()) {
                String am = m.group();
                String price = formatter(am, "price\":", ",\"discount\"");
                String discount = formatter(am, "discount\":", ",\"available\"");
                System.out.println(price);

                price_ = Integer.parseInt(price );
                discount_ = Integer.parseInt(discount);

            }
            data.put("price", price_);
            data.put("discount", discount_);
            //System.out.println("Price: "+price_+" discount "+discount_);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //close connection set all object to null
            assert connection != null;
            connection.disconnect();
            rd = null;
            sb = null;
            wr = null;
            connection = null;
        }


        return data;

    }

    private static String formatter(String data, String f, String l) {
        boolean b = data.indexOf(l) < data.indexOf(f) + 1;

        return data.substring(data.indexOf(f) + 1, b ? data.length() : data.indexOf(l)).split(":")[1];
    }
}

