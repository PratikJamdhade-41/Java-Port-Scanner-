import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ScannerTool {

    // 🔹 Common ports (fallback if detection fails)
    static Map<Integer, String> commonServices = new HashMap<>();

    static {
        commonServices.put(21, "FTP");
        commonServices.put(22, "SSH");
        commonServices.put(23, "TELNET");
        commonServices.put(25, "SMTP");
        commonServices.put(53, "DNS");
        commonServices.put(80, "HTTP");
        commonServices.put(110, "POP3");
        commonServices.put(143, "IMAP");
        commonServices.put(443, "HTTPS");
        commonServices.put(3306, "MySQL");
        commonServices.put(8080, "HTTP-ALT");
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            showHelp();
            return;
        }

        String host = null;
        int startPort = 1, endPort = 100;
        boolean fast = false;
        String outputFile = null;

        // 🔹 Parse CLI arguments
        for (int i = 0; i < args.length; i++) {

            switch (args[i]) {

                case "--help":
                    showHelp();
                    return;

                case "-h":
                    host = args[++i];
                    break;

                case "-p":
                    String[] ports = args[++i].split("-");
                    startPort = Integer.parseInt(ports[0]);
                    endPort = Integer.parseInt(ports[1]);
                    break;

                case "--fast":
                    fast = true;
                    break;

                case "--output":
                    outputFile = args[++i];
                    break;
            }
        }

        if (host == null) {
            System.out.println("❌ Host required. Use -h <host>");
            return;
        }

        List<String[]> results = Collections.synchronizedList(new ArrayList<>());

        long startTime = System.currentTimeMillis();

        System.out.println("\nScanning " + host + " ports " + startPort + "-" + endPort + "...\n");

        // 🔥 FAST MODE (MULTITHREADING)
        if (fast) {
            ExecutorService executor = Executors.newFixedThreadPool(20);
            final String finalHost = host;

            for (int port = startPort; port <= endPort; port++) {
                int p = port;
                executor.submit(() -> scan(finalHost, p, results));
            }

            executor.shutdown();
            try {
                executor.awaitTermination(2, TimeUnit.MINUTES);
            } catch (Exception e) {}
        } else {
            for (int port = startPort; port <= endPort; port++) {
                scan(host, port, results);
            }
        }

        // 🔹 Sort results by port
        results.sort(Comparator.comparingInt(a -> Integer.parseInt(a[0])));

        // 📊 Print table
        printTable(results);

        // 📁 Save file
        if (outputFile != null) {
            saveToFile(results, outputFile);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\n⏱ Scan completed in " + (endTime - startTime) + " ms");
    }

    // 🔍 PORT SCAN + SERVICE DETECTION
    public static void scan(String host, int port, List<String[]> results) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 200);
            socket.setSoTimeout(200);

            String service = detectService(socket, host, port);

            results.add(new String[]{
                    String.valueOf(port),
                    "OPEN",
                    service
            });

            socket.close();

        } catch (Exception e) {
            // closed port (ignore)
        }
    }

    // 🧠 SMART SERVICE DETECTION
    public static String detectService(Socket socket, String host, int port) {

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            OutputStream out = socket.getOutputStream();

            // 🔹 Try HTTP probe
            out.write(("GET / HTTP/1.1\r\nHost: " + host + "\r\n\r\n").getBytes());
            out.flush();

            String response = reader.readLine();

            if (response != null && response.contains("HTTP")) {
                return "HTTP";
            }

            // 🔹 Banner-based detection
            if (response != null) {
                if (response.contains("SSH")) return "SSH";
                if (response.contains("FTP")) return "FTP";
                if (response.contains("SMTP")) return "SMTP";
            }

        } catch (Exception e) {}

        // 🔹 Fallback to common ports
        return commonServices.getOrDefault(port, "Unknown");
    }

    // 📊 TABLE OUTPUT
    public static void printTable(List<String[]> data) {

        System.out.println("PORT     STATUS     SERVICE");
        System.out.println("--------------------------------");

        for (String[] row : data) {
            System.out.printf("%-8s %-10s %-10s\n",
                    row[0], row[1], row[2]);
        }
    }

    // 📁 SAVE RESULTS
    public static void saveToFile(List<String[]> data, String fileName) {

        try (PrintWriter writer = new PrintWriter(fileName)) {

            writer.println("PORT     STATUS     SERVICE");
            writer.println("--------------------------------");

            for (String[] row : data) {
                writer.printf("%-8s %-10s %-10s\n",
                        row[0], row[1], row[2]);
            }

            System.out.println("\n📁 Results saved to " + fileName);

        } catch (Exception e) {
            System.out.println("❌ Error saving file.");
        }
    }

    // 📘 HELP MENU
    public static void showHelp() {

        System.out.println("=== Java Network Scanner ===\n");

        System.out.println("Usage:");
        System.out.println(" java Tool -h <host> [options]\n");

        System.out.println("Options:");
        System.out.println(" -h <host>        → Target host");
        System.out.println(" -p <start-end>   → Port range (default 1-100)");
        System.out.println(" --fast           → Fast scan (multithreading)");
        System.out.println(" --output <file>  → Save results to file");
        System.out.println(" --help           → Show help\n");

        System.out.println("Example:");
        System.out.println(" java Tool -h scanme.nmap.org -p 1-1000 --fast --output result.txt");
    }
}