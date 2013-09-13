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

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * A tree structure for Enzyme Commission numbers.
 * 
 * @author dmyersturnbull
 * @see {@link ECNode}
 */
public class ECTree implements Iterable<ECNode> {

	private ECNode root;

	public ECTree() {
		root = new ECNode(null, null);
	}

	public void add(ECNode node) {
		ECNode parent = findByEcNumber(node.getEcNumber().getParentNumber());
		if (parent == null) parent = root;
		parent.addChild(node);
		node.setParent(parent);
	}

	public Iterator<ECNode> breadthFirst() {
		return root.breadthFirst();
	}

	public Iterator<ECNode> depthFirst() {
		return root.depthFirst();
	}

	public NavigableSet<ECNode> findByDescriptionSubstring(String descriptionSubstring) {
		NavigableSet<ECNode> matches = new TreeSet<ECNode>();
		for (ECNode node : root) {
			if (node.getEcNumber() == null) {
				if (descriptionSubstring == null) matches.add(node);
			} else if (node.getDescription().contains(descriptionSubstring)) {
				matches.add(node);
			}
		}
		return matches;
	}

	public ECNode findByEcNumber(ECNumber ecNumber) {
		for (ECNode node : root) {
			if (node.getEcNumber() == null) {
				if (ecNumber == null) return node;
			} else if (node.getEcNumber().equals(ecNumber)) {
				return node;
			}
		}
		return null;
	}

	public ECNode findByEcNumber(String ecNumber) {
		return findByEcNumber(new ECNumber(ecNumber));
	}

	public NavigableSet<ECNode> findByExactDescription(String description) {
		NavigableSet<ECNode> matches = new TreeSet<ECNode>();
		for (ECNode node : root) {
			if (node.getEcNumber() == null) {
				if (description == null) matches.add(node);
			} else if (node.getDescription().equals(description)) {
				matches.add(node);
			}
		}
		return matches;
	}

	public NavigableSet<ECNode> findNodesOfDepth(int depth) {
		NavigableSet<ECNode> matches = new TreeSet<ECNode>();
		Iterator<ECNode> iter = root.breadthFirst();
		while (iter.hasNext()) {
			ECNode node = iter.next();
			if (node.getDepth() == depth) matches.add(node);
			if (node.getDepth() > depth) break; // take advantage of BFS
		}
		return matches;
	}

	public ECNode getRoot() {
		return root;
	}

	@Override
	public Iterator<ECNode> iterator() {
		return listOrder();
	}

	public Iterator<ECNode> listOrder() {
		return root.listOrder();
	}

	public void print() {
		for (ECNode node : root) {
			if (!node.isRoot()) {
				for (int i = 1; i < node.getDepth(); i++) System.out.print("\t");
			}
			System.out.println(node);
		}
	}

	public void printBreadthFirst() {
		Iterator<ECNode> iter = root.breadthFirst();
		while (iter.hasNext()) {
			ECNode node = iter.next();
			if (!node.isRoot()) {
				for (int i = 1; i < node.getDepth(); i++)
					System.out.print("\t");
				System.out.println(node);
			}
		}
	}

	public void setRoot(ECNode root) {
		this.root = root;
	}

}
