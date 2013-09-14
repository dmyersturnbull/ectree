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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class IntegrationTest {

	private ECTree tree;

	@Before
	public void setUp() throws IOException {
		tree = ECTreeFactory.fromSibFile_2013_07_24();
	}

	@Test
	public void testBreadthFirst() throws IOException {
		int length = 0;
		ECNumber previous = null;
		Iterator<ECNode> iter = tree.breadthFirst();
		while (iter.hasNext()) {
			ECNode node = iter.next();
			if (node.isRoot()) continue;
			if (node.getDepth() == length + 1) {
			} else if (node.getDepth() == length) {
			} else {
				fail("A length-" + node.getDepth() + " cannot follow a length-" + length + " (" + node.getEcNumber()
						+ " followed " + previous + ")");
			}
			previous = node.getEcNumber();
			length = previous.getDepth();
			assertTrue(node.getDepth() >= length);
		}
	}

	@Test
	public void testExactSearch() throws IOException {
		NavigableSet<ECNode> nodes = tree.findByExactDescription("Forming carbon-sulfur bonds.");
		assertEquals("Wrong size", 1, nodes.size());
		for (ECNode node : nodes) {
			if (node.getEcNumber().toString().equals("6.2")) return;
		}
		fail("Didn't find 6.2");
	}

	@Test
	public void testInOrder() throws IOException {
		tree.print();
		int length = 0;
		ECNumber previous = null;
		for (ECNode node : tree) {
			if (node.isRoot()) continue;
			if (node.getDepth() == length + 1) {
			} else if (node.getDepth() == length) {
				for (int i = 1; i < node.getDepth(); i++) {
					assertTrue(node.getEcNumber() + " does not follow " + previous, node.getEcNumber()
							.getCodeAtDepth(i) == previous.getCodeAtDepth(i));
				}
				assertTrue(node.getEcNumber().getCodeAtDepth(node.getDepth()) > previous
						.getCodeAtDepth(node.getDepth()));
			} else if (node.getDepth() < length) {
			} else {
				fail("A length-" + node.getDepth() + " cannot follow a length-" + length + " (" + node.getEcNumber()
						+ " followed " + previous + ")");
			}
			previous = node.getEcNumber();
			length = previous.getDepth();
			assertTrue(node.getDepth() >= length);
		}
	}

	@Test
	public void testInOrderIdentical() throws IOException {
		StringBuilder expected = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader("src/test/resources/expected_in_order.txt"))) {
			String line = "";
			while ((line = br.readLine()) != null)
				expected.append(line);
		}
		StringBuilder actual = new StringBuilder();
		for (ECNode node : tree) {
			if (!node.isRoot()) {
				for (int i = 1; i < node.getDepth(); i++)
					actual.append("\t");
				actual.append(node);
			}
		}
		assertEquals("Wrong in-order output", expected.toString(), actual.toString());
	}

	@Test
	public void testOfDepth() {
		NavigableSet<ECNode> roots = tree.findNodesOfDepth(0);
		assertEquals("Wrong number", 1, roots.size());
		NavigableSet<ECNode> topLevel = tree.findNodesOfDepth(1);
		assertEquals("Wrong number", 6, topLevel.size());
		for (ECNode node : topLevel)
			assertEquals("Wrong depth", node.getDepth(), 1);
		NavigableSet<ECNode> midLevel = tree.findNodesOfDepth(2);
		assertEquals("Wrong number", 67, midLevel.size());
		for (ECNode node : midLevel)
			assertEquals("Wrong depth", node.getDepth(), 2);
		NavigableSet<ECNode> lowerLevel = tree.findNodesOfDepth(3);
		assertEquals("Wrong number", 260, lowerLevel.size());
		for (ECNode node : lowerLevel)
			assertEquals("Wrong depth", node.getDepth(), 3);
	}

	@Test
	public void testSubstringSearch() throws IOException {
		NavigableSet<ECNode> nodes = tree.findByDescriptionSubstring("sulfur");
		String[] ecArray = new String[] { "1.2.7", "1.3.7", "1.4.7", "1.5.7", "1.7.7", "1.8.7", "1.8", "1.12.7",
				"1.14.15", "1.17.7", "1.18", "2.8", "3.10.1", "3.10", "3.12.1", "3.12", "3.13.1", "3.13", "4.4.1",
				"4.4", "6.2" };
		assertEquals("Wrong size", ecArray.length, nodes.size());
		Set<String> ecs = new HashSet<String>();
		for (String ec : ecArray)
			ecs.add(ec);
		for (ECNode node : nodes) {
			assertTrue("Extra node " + node.getEcNumber(), ecs.contains(node.getEcNumber().toString()));
		}
	}
}
