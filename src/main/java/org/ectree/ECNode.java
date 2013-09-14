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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.TreeSet;

/**
 * A node of an {@link ECTree}. Contains a {@link #getEcNumber() Enzyme
 * Commission number}, a {@link #getDescription() text description}, a
 * {@link #getParent() parent}, and any number of {@link #getChildren()
 * children}. This node is defined at a particular {@link #getDepth() depth} on
 * the tree, which is the number of edges to the root.
 * 
 * The ECNode class implements the {@link Iterable} interface. Iteration is
 * performed {@link #inOrder() in-order} over nodes in the subtree rooted at
 * this ECNode.
 * 
 * @author dmyersturnbull
 */
public class ECNode implements Iterable<ECNode>, Comparable<ECNode> {

	private NavigableSet<ECNode> children;
	private final String description;

	private final ECNumber ecNumber;
	private ECNode parent;

	public ECNode(ECNumber ecNumber, String description) {
		this.ecNumber = ecNumber;
		this.description = description;
		children = new TreeSet<>();
	}

	protected boolean addChild(ECNode e) {
		return children.add(e);
	}

	/**
	 * Returns a breadth-first iterator over ECNodes in the subtree rooted at
	 * this node.
	 */
	public Iterator<ECNode> breadthFirst() {
		List<ECNode> visited = new ArrayList<>();
		Queue<ECNode> queue = new ArrayDeque<>();
		queue.add(this);
		while (!queue.isEmpty()) {
			ECNode current = queue.poll();
			visited.add(current);
			for (ECNode child : current.getChildren()) {
				queue.add(child);
			}
		}
		return visited.iterator();
	}

	/**
	 * Returns true if {@code second} comes before this node in an
	 * {@link #inOrder() in-order} traversal.
	 */
	@Override
	public int compareTo(ECNode second) {
		if (this.equals(second)) return 0;
		int min = Math.min(this.getDepth(), second.getDepth());
		for (int i = 1; i <= min; i++) {
			if (this.getEcNumber().getCodeAtDepth(i) < second.getEcNumber().getCodeAtDepth(i)) {
				return -1;
			} else if (this.getEcNumber().getCodeAtDepth(i) > second.getEcNumber().getCodeAtDepth(i)) {
				return 1;
			}
		}
		return -1;
	}

	/**
	 * Returns a depth-first iterator over ECNodes in the subtree rooted at this
	 * node.
	 */
	public Iterator<ECNode> depthFirst() {
		LinkedHashSet<ECNode> visited = new LinkedHashSet<>();
		for (ECNode child : children) {
			Iterator<ECNode> i = child.depthFirst();
			while (i.hasNext())
				visited.add(i.next());
		}
		visited.add(this);
		return visited.iterator();
	}

	/**
	 * Returns true if {@code obj} is an ECNode that has this Node's
	 * {@link #getEcNumber() EC number}. Note that this may return true for
	 * Nodes from different trees even if the {@link #getDescription()
	 * descriptions}, {@link #getParent() parents}, or {@link #getChildren()
	 * children} differ.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ECNode other = (ECNode) obj;
		if (ecNumber == null) {
			if (other.ecNumber != null) return false;
		} else if (!ecNumber.equals(other.ecNumber)) return false;
		return true;
	}

	/**
	 * Searches the subtree rooted at this ECNode for every ECNode whose
	 * description contains {@code descriptionSubstring} as a substring,
	 * ignoring case.
	 * 
	 * @param description
	 *            A case-insensitive query string
	 * @return A sorted set of ECNodes matching the query
	 */
	public NavigableSet<ECNode> findByDescriptionSubstring(String descriptionSubstring) {
		NavigableSet<ECNode> matches = new TreeSet<ECNode>();
		for (ECNode node : this) {
			if (node.getEcNumber() == null) {
				if (descriptionSubstring == null) matches.add(node);
			} else if (node.getDescription().contains(descriptionSubstring)) {
				matches.add(node);
			}
		}
		return matches;
	}

	/**
	 * Searches the subtree rooted at this ECNode for the unique ECNode with EC
	 * number {@code ecNumber}.
	 */
	public ECNode findByEcNumber(ECNumber ecNumber) {
		for (ECNode node : this) {
			if (node.getEcNumber() == null) {
				if (ecNumber == null) return node;
			} else if (node.getEcNumber().equals(ecNumber)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Searches the subtree rooted at this ECNode for the unique ECNode with EC
	 * number {@code ecNumber}.
	 */
	public ECNode findByEcNumber(String ecNumber) {
		return findByEcNumber(new ECNumber(ecNumber));
	}

	/**
	 * Searches the subtree rooted at this ECNode for every ECNode whose
	 * description contains {@code descriptionSubstring}, ignoring case.
	 * 
	 * @param description
	 *            A case-insensitive query string
	 * @return A sorted set of ECNodes matching the query
	 */
	public NavigableSet<ECNode> findByExactDescription(String description) {
		NavigableSet<ECNode> matches = new TreeSet<ECNode>();
		for (ECNode node : this) {
			if (node.getEcNumber() == null) {
				if (description == null) matches.add(node);
			} else if (node.getDescription().equalsIgnoreCase(description)) {
				matches.add(node);
			}
		}
		return matches;
	}

	public NavigableSet<ECNode> getChildren() {
		return children;
	}

	/**
	 * Returns the depth of this ECNode. Depth is defined such that only the
	 * root has depth 0, and its children (top-level EC codes) have depth 1.
	 */
	public int getDepth() {
		if (isRoot()) return 0;
		return ecNumber.getDepth();
	}

	/**
	 * Returns the text description of this node. For example:
	 * 
	 * <pre>
	 * With a quinone or similar compound as acceptor.
	 * </pre>
	 */
	public String getDescription() {
		return description;
	}

	public ECNumber getEcNumber() {
		return ecNumber;
	}

	public ECNode getParent() {
		return parent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (ecNumber == null ? 0 : ecNumber.hashCode());
		return result;
	}

	/**
	 * Returns an in-order iterator over ECNodes in the subtree rooted at this
	 * node. For example:
	 * 
	 * <pre>
	 * 1, 1.1, 1.1.1, 1.1.2, 1.2, 1.2.1, 1.2.2, 1.2, 1.2.1, 2, 2.1, 2.1.1, ...
	 * </pre>
	 */
	public Iterator<ECNode> inOrder() {
		List<ECNode> visited = new ArrayList<>();
		visited.add(this);
		for (ECNode child : children) {
			Iterator<ECNode> i = child.inOrder();
			while (i.hasNext())
				visited.add(i.next());
		}
		return visited.iterator();
	}

	public boolean isRoot() {
		return ecNumber == null;
	}

	/**
	 * Iterates {@link #inOrder() in-order}.
	 */
	@Override
	public Iterator<ECNode> iterator() {
		return inOrder();
	}

	/**
	 * Prints an {@link #inOrder() in-order} representation of the subtree
	 * rooted at this node.
	 */
	public void print() {
		for (ECNode node : this) {
			if (!node.isRoot()) {
				for (int i = 1; i < node.getDepth(); i++)
					System.out.print("\t");
				System.out.println(node);
			}
		}
	}

	protected void setChildren(NavigableSet<ECNode> children) {
		this.children = children;
	}

	protected void setParent(ECNode parent) {
		this.parent = parent;
	}

	/**
	 * Prints a string formatted as:
	 * 
	 * <pre>
	 * ecNumber: description
	 * </pre>
	 */
	@Override
	public String toString() {
		return ecNumber + ": " + description;
	}

}
