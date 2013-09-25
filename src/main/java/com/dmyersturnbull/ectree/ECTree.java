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
package com.dmyersturnbull.ectree;

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

	/**
	 * Creates a new ECTree. The root does not need to be added separately.
	 */
	public ECTree() {
		root = new ECNode(null, null);
	}

	/**
	 * Add {@code node}. Nodes must be added in order in the sense that the
	 * parent of {@code node} must have already been added.
	 */
	public void add(ECNode node) {
		if (findByEcNumber(node.getEcNumber()) != null) {
			throw new IllegalArgumentException("Parent " + node.getEcNumber() + " already exists");
		}
		addFast(node);
		if (node.getParent() == null && node.getParent().getDepth() != 0) {
			throw new IllegalArgumentException("Parent " + node.getParent().getEcNumber() + " does not exist yet");
		}
	}

	/**
	 * Add {@code node} without safeguards. Nodes must be added in order in the
	 * sense that the parent of {@code node} must have already been added. Just
	 * faster than {@link #add(ECNode)}.
	 */
	protected void addFast(ECNode node) {
		ECNumber parentNumber = node.getEcNumber().getParentNumber();
		ECNode parent = findByEcNumber(parentNumber);
		if (parent == null) parent = root;
		parent.addChild(node);
		node.setParent(parent);
	}

	/**
	 * Returns a breadth-first iterator over ECNodes in this tree.
	 */
	public Iterator<ECNode> breadthFirst() {
		return root.breadthFirst();
	}

	/**
	 * Returns a depth-first iterator over ECNodes in this tree.
	 */
	public Iterator<ECNode> depthFirst() {
		return root.depthFirst();
	}

	/**
	 * Searches this tree for every ECNode whose description contains
	 * {@code descriptionSubstring} as a substring, ignoring case.
	 * 
	 * @param description
	 *            A case-insensitive query string
	 * @return A sorted set of ECNodes matching the query
	 */
	public NavigableSet<ECNode> findByDescriptionSubstring(String descriptionSubstring) {
		return root.findByDescriptionSubstring(descriptionSubstring);
	}

	/**
	 * Searches this tree for the unique ECNode with EC number {@code ecNumber}.
	 */
	public ECNode findByEcNumber(ECNumber ecNumber) {
		return root.findByEcNumber(ecNumber);
	}

	/**
	 * Searches this tree for the unique ECNode with EC number {@code ecNumber}.
	 */
	public ECNode findByEcNumber(String ecNumber) {
		return root.findByEcNumber(ecNumber);
	}

	/**
	 * Searches this tree for every ECNode whose description contains
	 * {@code descriptionSubstring}, ignoring case.
	 * 
	 * @param description
	 *            A case-insensitive query string
	 * @return A sorted set of ECNodes matching the query
	 */
	public NavigableSet<ECNode> findByExactDescription(String description) {
		return root.findByExactDescription(description);
	}

	public NavigableSet<ECNode> findNodesOfDepth(int depth) {
		NavigableSet<ECNode> matches = new TreeSet<ECNode>();
		Iterator<ECNode> iter = this.breadthFirst();
		while (iter.hasNext()) {
			ECNode node = iter.next();
			if (node.getDepth() == depth) matches.add(node);
			if (node.getDepth() > depth) break; // take advantage of BFS
		}
		return matches;
	}

	protected ECNode getRoot() {
		return root;
	}

	/**
	 * Returns an in-order iterator over ECNodes in this tree. For example:
	 * 
	 * <pre>
	 * 1, 1.1, 1.1.1, 1.1.2, 1.2, 1.2.1, 1.2.2, 1.2, 1.2.1, 2, 2.1, 2.1.1, ...
	 * </pre>
	 */
	public Iterator<ECNode> inOrder() {
		return root.inOrder();
	}

	/**
	 * Iterates {@link #inOrder() in-order}.
	 */
	@Override
	public Iterator<ECNode> iterator() {
		return inOrder();
	}

	/**
	 * Prints an {@link #inOrder() in-order} representation of this tree.
	 */
	public void print() {
		root.print();
	}

}
