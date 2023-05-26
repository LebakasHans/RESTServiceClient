package net.htlgkr.wintersteigerj.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import com.google.gson.*;

public class Client {
    public static final String MENU = """
            +--------------------------------------------------+
            | 0. Exit                                          |
            +-----------------------+--------------------------+
            | Service Menu          |   Employee Menu          |
            +-----------------------+--------------------------+
            | 1. Get all Services   |   6. Get all Employees   |
            | 2. Create new Service |   7. Create new Employee |
            | 3. Delete Service     |                          |
            | 4. Get Service        |                          |
            | 5. Update Service     |                          |
            +-----------------------+--------------------------+""";

    public static final int PORT = 8080;
    public static final String BASE_PATH = "http://localhost:" + PORT + "/serviceBackend/";
    private Scanner in = new Scanner(System.in);

    public void startLoop() throws IOException {
        int choice;
        do {
            System.out.println(MENU);
            System.out.print("Enter your choice: ");
            choice = in.nextInt();
            in.nextLine(); //consume \n
            if (choice != 0){
                String response = handleChoice(choice);
                System.out.println("Response: \n" + response);
            }
        }while (choice != 0);
    }

    private String handleChoice(int choice) throws IOException {
        URL url = new URL(BASE_PATH + getEndpoint(choice));
        HttpURLConnection connection;
        StringBuilder response = new StringBuilder();
        int serviceId;
        String jsonPayload;

        switch (choice){
            case 1, 6:
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                break;
            case 2:
                jsonPayload = readServiceData();

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Content-Length", Integer.toString(jsonPayload.length())); //not necessary
                connection.setDoOutput(true);


                try (final DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                    outputStream.writeBytes(jsonPayload);
                    outputStream.flush();
                }

                break;
            case 3:
                System.out.println("Enter Service ID: ");
                serviceId = in.nextInt();
                url = new URL(BASE_PATH + getEndpoint(choice) + "/" + serviceId);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");

                break;
            case 4:
                System.out.println("Enter Service ID: ");
                serviceId = in.nextInt();
                url = new URL(BASE_PATH + getEndpoint(choice) + "/" + serviceId);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                break;
            case 5:
                System.out.println("Enter Service ID: ");
                serviceId = in.nextInt();
                in.nextLine(); //consume \n

                url = new URL(BASE_PATH + getEndpoint(choice) + "/" + serviceId);

                jsonPayload = readServiceData();
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Content-Length", Integer.toString(jsonPayload.length())); //not necessary
                connection.setDoOutput(true);


                try (final DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                    outputStream.writeBytes(jsonPayload);
                    outputStream.flush();
                }
                break;
            case 7:
                jsonPayload = readEmployeeData();

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Content-Length", Integer.toString(jsonPayload.length())); //not necessary
                connection.setDoOutput(true);


                try (final DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                    outputStream.writeBytes(jsonPayload);
                    outputStream.flush();
                }

                break;
            default:
                return "Invalid choice";
        }

        int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            return "Error: " + responseCode;
        }

        try(final BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null){
                response.append(line);
            }
        }

        connection.disconnect();
        String prettyJson = prettyPrint(response.toString());

        return prettyJson;
    }

    private String readEmployeeData() {
        JsonObject employee = new JsonObject();

        System.out.println("Enter Employee Name: ");
        employee.addProperty("name", in.nextLine());
        System.out.println("Enter Longitude: ");
        employee.addProperty("longitude", in.nextInt());
        in.nextLine(); //consume \n
        System.out.println("Enter Latitude: ");
        employee.addProperty("latitude", in.nextLine());

        return employee.toString();
    }

    private static String prettyPrint(String response) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Object jsonObject = gson.fromJson(response, Object.class);
        String prettyJson = gson.toJson(jsonObject);
        return prettyJson;
    }


    private String readServiceData() {
        JsonObject service = new JsonObject();

        System.out.println("Enter Service Name: ");
        service.addProperty("name", in.nextLine());
        System.out.println("Enter Employee ID: ");
        service.addProperty("employeeId", in.nextInt());
        in.nextLine(); //consume \n
        System.out.println("Enter Date: ");
        service.addProperty("date", in.nextLine());
        System.out.println("Enter Address: ");
        service.addProperty("address", in.nextLine());

        return service.toString();
    }

    private String getEndpoint(int choice) {
        switch (choice) {
            case 1,2,3,4,5:
                return "services";
            case 6,7:
                return "employees";
            default:
                return null;
        }
    }
}
