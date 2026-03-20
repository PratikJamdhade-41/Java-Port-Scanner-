# 🔍 Java Network Scanner (Mini Nmap)

A lightweight command-line network scanner built in Java that performs port scanning and basic service detection. This project is inspired by tools like Nmap and is designed for learning networking and cybersecurity fundamentals.

---

## 🚀 Features

* 🔍 Scan open ports on a target host
* ⚡ Fast scanning using multithreading (`--fast`)
* 🧠 Basic service detection (HTTP, SSH, FTP, SMTP)
* 📊 Clean table output in terminal
* 📁 Save results to file (`--output`)
* 🖥️ Command-line interface with flags

---

## 🛠️ Technologies Used

* Java (Core)
* Socket Programming
* Multithreading (ExecutorService)

---

## 📦 Installation & Setup

### 1. Clone the repository

```bash
git clone https://github.com/your-username/java-network-scanner.git
cd java-network-scanner
```

### 2. Compile the code

```bash
javac ScannerTool.java
```

### 3. Run the program

```bash
java ScannerTool -h <host> [options]
```
---

## 📘 Usage

```bash
java ScannerTool -h <host> [options]
```

### 🔹 Options

| Flag              | Description                       |
| ----------------- | --------------------------------- |
| `-h <host>`       | Target host (IP or domain)        |
| `-p <start-end>`  | Port range (default: 1-100)       |
| `--fast`          | Enable fast scan (multithreading) |
| `--output <file>` | Save results to file              |
| `--help`          | Show help menu                    |

---

## 🧪 Examples

### Basic Scan

```bash
java ScannerTool -h 127.0.0.1 -p 1-100
```

### Fast Scan

```bash
java ScannerTool -h scanme.nmap.org -p 1-1000 --fast
```

### Save Output

```bash
java ScannerTool -h scanme.nmap.org -p 1-1000 --fast --output result.txt
```

---

## 📊 Sample Output

```
PORT     STATUS     SERVICE
--------------------------------
22       OPEN       SSH
80       OPEN       HTTP
443      OPEN       HTTPS
```

---

## 🧠 How It Works

1. The tool attempts a TCP connection to each port.
2. If the connection is successful, the port is marked as OPEN.
3. It performs basic service detection by:

   * Sending an HTTP request
   * Reading banner responses
4. Results are displayed in a table format and optionally saved to a file.

---

## ⚠️ Limitations

* Uses TCP connect scan (not stealthy)
* Basic service detection (no deep fingerprinting)
* OS detection is not implemented
* Slower compared to professional tools like Nmap

---

## 📚 Learning Outcomes

This project helps understand:

* TCP/IP networking basics
* Socket programming in Java
* Multithreading concepts
* Basic cybersecurity scanning techniques

---

## ⚖️ Disclaimer

This tool is for **educational purposes only**.

Do NOT use it on networks or systems without proper authorization. Unauthorized scanning may be illegal.

---

## 👤 Author

Pratik Jamdhade (Cybersecurity Enthusiast)

---

## ⭐ Future Improvements

* Advanced service fingerprinting
* OS detection
* GUI version (Java Swing)
* Export results in JSON/CSV
* Improved error handling

---
