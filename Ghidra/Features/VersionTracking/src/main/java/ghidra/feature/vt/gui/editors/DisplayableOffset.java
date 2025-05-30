/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.feature.vt.gui.editors;

import ghidra.program.model.address.Address;

import java.math.BigInteger;

import docking.widgets.table.DisplayStringProvider;

public interface DisplayableOffset extends DisplayStringProvider, Comparable<DisplayableOffset> {

	public static String NO_OFFSET = "No Offset";

	@Override
	public String getDisplayString();

	public Address getAddress();

	public long getOffset();

	public BigInteger getOffsetAsBigInteger();
}
