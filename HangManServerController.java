
/* 
 * HangManServerController.java 
 * 
 * 
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * This program is Server Controller part of Multiplayer HangMan game. It
 * listens for client connections
 * 
 * @author Vinay Vasant More
 *
 */

public class HangManServerController {
	int j = 0;
	static int i = 0;
	static int missCount = 0;
	static String randomWord;
	static Random rword = new Random();
	static InetAddress[] clientIP = new InetAddress[4];
	static int[] clientPort = new int[4];

	/**
	 * The main program.
	 *
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) throws Exception {
		int client = 0;
		String path = "";
		FileInputStream fs;
		path = args[0];
		String word = "";
		DatagramPacket receivePacket;
		byte[] rdata = new byte[1024];

		// Stream to read file
		fs = new FileInputStream(path);
		// storing all words from file to array list
		while (word != null) {
			word = new DataInputStream(fs).readLine();
			if (word != null) {
				HangManModel.WordList.add(word);
			}
		}
		// closing file
		fs.close();

		// Server Socket
		DatagramSocket serverSocket = new DatagramSocket(1024);
		System.out.println("Server is running.");

		while (i != 4) {
			receivePacket = new DatagramPacket(rdata, rdata.length);
			serverSocket.receive(receivePacket);
			String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());

			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			if (sentence.equals("connect me")) {
				clientIP[i] = IPAddress;
				clientPort[i] = port;
				i = i + 1;
			}
		}

		for (i = 0; i <= 3; i++) {
			DatagramSocket serverSocket1 = new DatagramSocket(1025 + i);
			new Client(serverSocket1, (1025 + i), clientIP[i], clientPort[i], client++).start();
		}
	}

	/**
	 * This class defines client thread.
	 * 
	 * @author Vinay Vasant More
	 *
	 */
	private static class Client extends Thread {
		private DatagramSocket socket;
		private int playerno;
		boolean play = true;
		byte[] rdata = new byte[1024];
		DatagramPacket sendPacket;
		DatagramPacket receivePacket;
		String sentence;
		InetAddress IPAddress;
		int port;
		int socketNumber;

		// constructor setting socket and client number
		public Client(DatagramSocket socket, int socketNumber, InetAddress IPAddress, int port, int clientNumber) {
			this.socket = socket;
			this.playerno = clientNumber + 1;
			this.IPAddress = IPAddress;
			this.port = port;
			this.socketNumber = socketNumber;
			System.out.println("New connection with client# " + clientNumber + " from IP Address: " + IPAddress
					+ " Port: " + port);
		}

		public void run() {
			try {
				// assign dedicated port for listening to client
				sentence = socketNumber + "";
				sendPacket = new DatagramPacket(sentence.getBytes(), sentence.getBytes().length, IPAddress, port);
				socket.send(sendPacket);

				// assign client a player number
				HangManModel.players[playerno - 1] = "player" + playerno;
				sentence = "Hello, you are " + HangManModel.players[playerno - 1];
				sendPacket = new DatagramPacket(sentence.getBytes(), sentence.getBytes().length, IPAddress, port);
				socket.send(sendPacket);
				sentence = (playerno - 1) + "";
				sendPacket = new DatagramPacket(sentence.getBytes(), sentence.getBytes().length, IPAddress, port);
				socket.send(sendPacket);

				// receive acknowledgement
				receivePacket = new DatagramPacket(rdata, rdata.length);
				socket.receive(receivePacket);
				System.out.println(new String(receivePacket.getData(), 0, receivePacket.getLength()));

				// send welcome message to player and inform rules of the game
				sentence = ("Welcome player" + playerno
						+ " to the Hangman game. You have 8 chances to guess the correct word."
						+ "\nYou'll get 10 points for correct guess and you'll lose 5 if you can't.");
				sendPacket = new DatagramPacket(sentence.getBytes(), sentence.getBytes().length, IPAddress, port);
				socket.send(sendPacket);

				while (play) {
					// selecting random word
					randomWord = HangManModel.WordList.get(rword.nextInt(HangManModel.WordList.size()));
					sendPacket = new DatagramPacket(randomWord.getBytes(), randomWord.getBytes().length, IPAddress,
							port);
					socket.send(sendPacket);

					receivePacket = new DatagramPacket(rdata, rdata.length);
					socket.receive(receivePacket);
					HangManModel.score[playerno - 1] = Integer
							.parseInt(new String(receivePacket.getData(), 0, receivePacket.getLength()));

					// Printing score board calls
					System.out
							.println("\n\n*************************** S C O R E B O A R D ***************************");
					for (int k = 0; k <= 3; k++) {
						System.out.printf("%-20s%s%n", "Player" + (k + 1), HangManModel.score[k]);
						sentence = HangManModel.score[k] + "";
						sendPacket = new DatagramPacket(sentence.getBytes(), sentence.getBytes().length, IPAddress,
								port);
						socket.send(sendPacket);
					}
					System.out.println("***************************************************************************");

					// Checks whether player wants to continue or not
					receivePacket = new DatagramPacket(rdata, rdata.length);
					socket.receive(receivePacket);
					sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
					if (sentence.equals("true"))
						play = true;
					else
						play = false;
				}

			} catch (IOException e1) {
				e1.printStackTrace();
			}
			socket.close();
			System.out.println("Connection with player" + playerno + " closed");
		}
	}
}
