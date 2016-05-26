package main.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import main.grammar.GrammarTable;

public class Utils {
	public static void addWithoutEmpty(Set<String> dst, Set<String> src) {
		for (String data : src) {
			if (data != GrammarTable.EMPTY && data != null) {
				dst.add(data);
			}
		}
	}
	
	public static void printStringToFile(String filename,String... contents) throws IOException{
		File outputFile = new File(filename);
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		writer.newLine();
		for (String ele : contents) {
				writer.write(ele);
				writer.newLine();
				writer.write("***********************\n");
				writer.write("***********************\n");
		}
		writer.flush();
		writer.close();
	}
}
