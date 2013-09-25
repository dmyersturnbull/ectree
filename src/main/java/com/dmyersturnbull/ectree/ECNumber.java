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

import java.util.Arrays;

public class ECNumber {

	private final int[] codes;

	/**
	 * @param codes
	 *            EC number components from left to right, so that
	 *            {@code codes[0]} is the top-level EC code
	 */
	public ECNumber(int[] codes) {
		this.codes = codes;
	}

	/**
	 * @param code
	 *            A string formatted as "x.x.x.x"; for example "1" or "3.4.5"
	 */
	public ECNumber(String code) {
		String[] parts = code.split("\\.");
		codes = new int[parts.length];
		for (int i = 0; i < parts.length; i++) {
			try {
				codes[i] = Integer.parseInt(parts[i]);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Bad EC number " + code, e);
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ECNumber other = (ECNumber) obj;
		if (!Arrays.equals(codes, other.codes)) return false;
		return true;
	}

	/**
	 * Returns the EC number component corresponding to depth {@code depth}.
	 */
	public int getCodeAtDepth(int depth) {
		return codes[depth - 1];
	}

	/**
	 * Returns the depth of this number. Depth is defined such that only the
	 * root has depth 0, and its children (top-level EC codes) have depth 1.
	 */
	public int getDepth() {
		return codes.length;
	}

	/**
	 * Returns the EC number this ECNumber's parent is expected to have.
	 */
	public ECNumber getParentNumber() {
		int[] p = new int[codes.length - 1];
		for (int i = 0; i < codes.length - 1; i++)
			p[i] = codes[i];
		return new ECNumber(p);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(codes);
		return result;
	}

	/**
	 * Returns a natural representation of this EC number, such as "1" or
	 * "3.4.5".
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < codes.length; i++) {
			sb.append(codes[i]);
			if (i < codes.length - 1) sb.append(".");
		}
		return sb.toString();
	}

}
