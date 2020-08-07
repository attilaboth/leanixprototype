package com.telekom.timon.leanix.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class IOUtil {

	public static String getFileContentAsString(String pathtoFile) {
		StringBuffer contentAsStrBuffer = new StringBuffer();
		try (Stream<String> fileAsStream = Files.lines(Paths.get(pathtoFile))) {

			fileAsStream.forEach(aLine -> {

				contentAsStrBuffer.append(aLine);

			});

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return contentAsStrBuffer.toString();
	}

	public static List<String> getFileContentAsList(String pathtoFile) {
		List<String> linesInArray = new ArrayList<>();

		Path path = Paths.get(pathtoFile);
		//File file = path.toFile();

		try (Stream<String> fileAsStreaam = Files.lines(path)) {

			fileAsStreaam.forEach(aLine -> {

				linesInArray.add(aLine.trim());

			});

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return linesInArray;
	}

	public static boolean deleteProcessedFile(Path filePath) {
		boolean isDeleted = false;

		try {
			isDeleted = Files.deleteIfExists(filePath);

			System.out.println(filePath.getFileName() + (isDeleted ? " was deleted." : " was NOT deleted!!!"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isDeleted;
	}

	public static void dumpContentToFile(StringBuilder contetToBeSaved, String filePathWithName) {
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(filePathWithName)))) {

			bufferedWriter.write(contetToBeSaved.toString());

			bufferedWriter.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File unzipGzipFile(File aGzippedFile) {

		byte[] buffer = new byte[1024];
		String unzippedFile = aGzippedFile.getPath() + ".csv";

		try (GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(aGzippedFile));
			 FileOutputStream zpiCSVFileOutStream = new FileOutputStream(unzippedFile)) {

			int len;
			while ((len = gzipInputStream.read(buffer)) > 0) {
				zpiCSVFileOutStream.write(buffer, 0, len);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new File(unzippedFile);
	}

	public static void dumpContentToFile(List<String> dataToBeDumped, String filePathWithName) {
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(filePathWithName)))) {

			for (String aLine : dataToBeDumped) {

				bufferedWriter.write(aLine);
				bufferedWriter.write("\n");
			}

			bufferedWriter.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println(filePathWithName + " was created.");
		}
	}
/*
	public String getFileContentFromResourceStream(final String fileOnResources) {
		String fileContentAsString = "";
		try (InputStream resourceAsStream = this.getClass().getResourceAsStream(fileOnResources.trim())) {
			fileContentAsString = inputStreamToString(resourceAsStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileContentAsString;
	}

	private static String inputStreamToString(InputStream inputStream) {
		return new Scanner(inputStream,
				"UTF-8").useDelimiter("\\A").next();
	}
*/

	public static String getFileContentFromResourceStreamBufferedReader(final String fileOnResources) {
		StringBuffer contentAsStrBuffer = new StringBuffer();

		try (InputStream resourceAsStream = IOUtil.class.getResourceAsStream(fileOnResources.trim());
			 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream))) {

			String strCurrentLine;

			while ((strCurrentLine = bufferedReader.readLine()) != null) {
				contentAsStrBuffer.append(strCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		//System.out.println(fileOnResources + " : \n" +contentAsStrBuffer.toString());
		return contentAsStrBuffer.toString();
	}

	public static String getTimeStamp() {
		Timestamp ts = new Timestamp(new Date().getTime());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

		return formatter.format(ts);
	}

}
