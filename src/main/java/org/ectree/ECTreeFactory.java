/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * 
 * @author dmyersturnbull
 */
package org.ectree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A collection of factory methods for {@link ECTree ECTrees}. Contains methods
 * for creating ECTrees from text files provided by the ENZYME nomenclature
 * database at the <a href="http://www.isb-sib.ch/">Swiss Institute of
 * Bioinformatics (SIB)</a>. An example of the file can be found at <a
 * href="ftp://ftp.expasy.org/databases/enzyme/enzclass.txt">expasy</a>.
 * 
 * @author dmyersturnbull
 */
public class ECTreeFactory {

	private static final File FILE_2013_07_24 = new File("src/main/resources/enzclass_2013-07-24.txt");

	private static String REGEX = "^(\\d+)(?:\\.\\s*)?(\\d+)?(?:\\.\\s*)?(\\d+)?(?:[\\s\\.-]*)(?<desc>[A-Za-z]+.*)$";

	public static ECTree fromRemoteSibFile(URL url) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
			return fromSibFile(br);
		}
	}

	/**
	 * Creates a new {@link ECTree} from a text file like those provided by the
	 * ENZYME nomenclature database at the <a
	 * href="http://www.isb-sib.ch/">Swiss Institute of Bioinformatics
	 * (SIB)</a>. An example of the format can be found at <a
	 * href="ftp://ftp.expasy.org/databases/enzyme/enzclass.txt">expasy</a>.
	 */
	public static ECTree fromSibFile(BufferedReader br) throws IOException {
		Pattern pattern = Pattern.compile(REGEX);
		ECTree tree = new ECTree();
		String line = "";
		Comparator<ECNode> comp = new Comparator<ECNode>() {
			@Override
			public int compare(ECNode o1, ECNode o2) {
				if (o1.getDepth() < o2.getDepth()) return -1;
				if (o1.getDepth() > o2.getDepth()) return 1;
				return o1.compareTo(o2);
			}
		};
		SortedSet<ECNode> sorted = new TreeSet<ECNode>(comp);
		while ((line = br.readLine()) != null) {
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				List<Integer> codeList = new ArrayList<Integer>(4);
				// group indices start at 1, and we don't want the last one
				for (int i = 1; i < matcher.groupCount(); i++) {
					if (matcher.group(i) == null) break;
					int code = Integer.parseInt(matcher.group(i));
					codeList.add(code);
				}
				int[] codes = new int[codeList.size()];
				for (int i = 0; i < codeList.size(); i++)
					codes[i] = codeList.get(i);
				String description = matcher.group("desc");
				ECNumber number = new ECNumber(codes);
				ECNode node = new ECNode(number, description);
				sorted.add(node);
			}
		}
		for (ECNode node : sorted) {
			tree.addFast(node);
		}
		return tree;
	}

	/**
	 * Creates a new {@link ECTree} from a text file like those provided by the
	 * ENZYME nomenclature database at the <a
	 * href="http://www.isb-sib.ch/">Swiss Institute of Bioinformatics
	 * (SIB)</a>. An example of the format can be found at <a
	 * href="ftp://ftp.expasy.org/databases/enzyme/enzclass.txt">expasy</a>.
	 */
	public static ECTree fromSibFile(File file) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			return fromSibFile(br);
		}
	}

	/**
	 * Creates a new {@link ECTree} from the text file in
	 * {@code src/main/resources/enzclass_2013-07-24.txt}.
	 */
	public static ECTree fromSibFile_2013_07_24() throws IOException {
		return fromSibFile(FILE_2013_07_24);
	}

	/**
	 * <p>
	 * Creates a new {@link ECTree} from a recent version. Note that the
	 * particular tree this method returns is <strong>subject to
	 * change</strong> as the author(s) of the ECTree project try to keep it
	 * up-to-date. If this is not desirable, please see
	 * {@link #fromSibFile_2013_07_24()}.
	 * </p>
	 * <p>
	 * <strong>Good uses</strong> of this method:
	 * <ul>
	 * <li>A general-use end-user program where in a non-critical convenience
	 * feature</li>
	 * <li>Any case where the functionality is non-critical and changes do not
	 * introduce problems</li>
	 * </ul>
	 * <strong>Bad uses</strong> of this method:
	 * <ul>
	 * <li>Any type of global research or analysis where this is being used more
	 * than once, and where this project can be updated (for example, not
	 * packaged in a Jar that is never updated)</li>
	 * <li>Any other case where a change in this method's behavior will cause an
	 * erroneous result</li>
	 * </ul>
	 * </p>
	 */
	public static ECTree recentVersion() throws IOException {
		return fromSibFile(FILE_2013_07_24);
	}

}
