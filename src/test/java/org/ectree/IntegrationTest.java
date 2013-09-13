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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.NavigableSet;

import org.junit.Test;

public class IntegrationTest {

	@Test
	public void testListOrder() throws IOException {
		ECTree tree = ECTreeFactory.fromSibFile();
		int length = 0;
		ECNumber previous = null;
		for (ECNode node : tree) {
			if (node.isRoot()) continue;
			if (node.getDepth() == length + 1) {
			} else if (node.getDepth() == length) {
				for (int i = 1; i < node.getDepth(); i++) {
					assertTrue(node.getEcNumber() + " does not follow " + previous, node.getEcNumber().getCodeAtDepth(i) == previous.getCodeAtDepth(i));
				}
				assertTrue(node.getEcNumber().getCodeAtDepth(node.getDepth()) > previous.getCodeAtDepth(node.getDepth()));
			} else if (node.getDepth() < length) {
			} else {
				fail("A length-" + node.getDepth() + " cannot follow a length-" + length + " (" + node.getEcNumber() + " followed " + previous + ")");
			}
			previous = node.getEcNumber();
			length = previous.getDepth();
			assertTrue(node.getDepth() >= length);
		}
	}

	@Test
	public void testBreadthFirst() throws IOException {
		ECTree tree = ECTreeFactory.fromSibFile();
		int length = 0;
		ECNumber previous = null;
		Iterator<ECNode> iter = tree.breadthFirst();
		while (iter.hasNext()) {
			ECNode node = iter.next();
			if (node.isRoot()) continue;
			if (node.getDepth() == length + 1) {
			} else if (node.getDepth() == length) {
			} else {
				fail("A length-" + node.getDepth() + " cannot follow a length-" + length + " (" + node.getEcNumber() + " followed " + previous + ")");
			}
			previous = node.getEcNumber();
			length = previous.getDepth();
			assertTrue(node.getDepth() >= length);
		}
	}
	
	@Test
	public void testSubstringSearch() throws IOException {
		ECTree tree = ECTreeFactory.fromSibFile();
		NavigableSet<ECNode> nodes = tree.findByDescriptionSubstring("sulfur");
		assertEquals("Wrong size", 21, nodes.size());
		for (ECNode node : nodes) {
			if (node.getEcNumber().toString().equals("1.3.7")) return;
		}
		fail("Didn't find 1.3.7");
	}
	
	@Test
	public void testExactSearch() throws IOException {
		ECTree tree = ECTreeFactory.fromSibFile();
		NavigableSet<ECNode> nodes = tree.findByExactDescription("Forming carbon-sulfur bonds.");
		assertEquals("Wrong size", 1, nodes.size());
		for (ECNode node : nodes) {
			if (node.getEcNumber().toString().equals("6.2")) return;
		}
		fail("Didn't find 6.2");
	}
}
