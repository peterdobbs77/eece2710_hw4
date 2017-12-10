/*TA-BOT:MAILTO joseph.schmitt@mu.edu peter.dobbs@mu.edu*/
/**
 * HexDump :: HW 4
 * COSC 2200 / EECE 2710
 * Joseph Schmitt
 * Peter Dobbs
 * USAGE: HexDump FILE_NAME
 */

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

public class HexDump
{	
	// Human-readable and standard ASCII character range
	private final static int ASCII_START = 32;
	private final static int ASCII_END = 126;
	// ASCII character to use in place of non-standard or non-readable values
	private final static int GENERIC_ASCII = 46;
	
	public static void main(String[] args)
	{
		if(args.length == 0) {
      			System.err.println("Usage: HexDump FileName");
      			return;
   		}

		try {
			String fileName = args[0];

			byte[] bytes = getBytes(fileName);
			String hex = bytesToHex(bytes);
			String ascii = bytesToASCII(bytes);

			prettyPrint(hex, ascii);

		} catch (UnsupportedEncodingException ex) {
			System.err.println("Could not encode ASCII: " + ex.getMessage());
		} catch (IOException ex) {
			System.err.println("Could not read file: " + ex.getMessage());
		} catch (Exception ex) {
			System.err.println("Unknown exception: " + ex.getMessage());
		}
	}

	/**
	 * Takes a hexdump and outputs in a readable format
	 * @param hex String that holds the hex values for the dump
	 * @param ascii String that holds the ASCII values for the dump
	 */
	private static void prettyPrint(String hex, String ascii)
	{
		int byteCount = 0;
		int charPerLine = 16;

		// Cut the file data into multiple lines of charPerLine length
		for(int i=0; i<ascii.length(); i+=charPerLine) {
			// The number of ASCII characters to render on the line
			int lineLength = (i < ascii.length()-charPerLine)
					? charPerLine
					: ascii.length() - i;

			String hexLine = hex.substring(i*2, i*2+lineLength*2);
			String asciiLine = ascii.substring(i, i+lineLength);
			System.out.println(constructPrettyLine(byteCount, hexLine, asciiLine));

			byteCount += lineLength;
		}

		System.out.println(formatByteCount(byteCount));
	}

	/**
	 * Construct a line of the pretty print
	 * @param count The line being outputted
	 * @param hex The hex values to be printed
	 * @param ascii The corresponding ASCII values to the hex
	 * @return
	 */
	private static String constructPrettyLine(int count, String hex, String ascii)
	{
		String offset = formatByteCount(count);

		// Format the hex into blocks of two, and split every 8 hex
		String prettyHex = hex.replaceAll("..", "$0 ");
		prettyHex = prettyHex.replaceAll("........................", "$0 ");

		return String.format("%1$s  %2$-50s|%3$s|", offset,prettyHex, ascii);
	}

	/**
	* Format a byte count into an 8 digit hex
	* @param count The number of bytes
	*/
	private static String formatByteCount(int count)
	{
 		String hexCount = Integer.toHexString(count);
		return String.format("%8s", hexCount).toUpperCase().replace(' ', '0');
	}

	/**
	 * Loads a filename into a byte array
	 * @param fileName The file to load
	 * @return The bytes of the file
	 * @throws IOException
	 */
	private static byte[] getBytes(String fileName) throws IOException
	{
		File file = new File(fileName);
		return Files.readAllBytes(file.toPath());
	}

	/**
	 * Converts a byte array to a hex string
	 * @param bytes The bytes to convert
	 * @return A string of uppercase hex digits
	 */
	private static String bytesToHex(byte[] bytes)
	{
		return javax.xml.bind.DatatypeConverter.printHexBinary(bytes);
	}

	/**
	 * Convert a byte array to a filtered ASCII string
	 * @param bytes The bytes to convert
	 * @return A String of corresponding ASCII values
	 * @throws UnsupportedEncodingException
	 */
	private static String bytesToASCII(byte[] bytes)
		throws UnsupportedEncodingException
	{
		// Filter out non-standard, non-readable bytes
		for(int i = 0; i < bytes.length; i++)
			if(bytes[i] < ASCII_START || bytes[i] > ASCII_END)
				bytes[i] = GENERIC_ASCII;

		return new String(bytes, "ASCII");
	}
}