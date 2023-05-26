package net.htlgkr.wintersteigerj.client;

import java.io.IOException;
import java.net.MalformedURLException;

public class Main {

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.startLoop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}