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
package ghidra.app.util.viewer.field;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import docking.widgets.fieldpanel.field.*;
import docking.widgets.fieldpanel.support.FieldLocation;
import generic.theme.GThemeDefaults.Colors;
import ghidra.app.util.ListingHighlightProvider;
import ghidra.app.util.viewer.format.FieldFormatModel;
import ghidra.app.util.viewer.proxy.ProxyObj;
import ghidra.framework.options.Options;
import ghidra.framework.options.ToolOptions;
import ghidra.program.model.listing.CodeUnit;
import ghidra.program.model.listing.Data;
import ghidra.program.util.ProgramLocation;
import ghidra.program.util.SpaceFieldLocation;
import ghidra.util.exception.NoValueException;

/**
  *  Generates empty line Fields.
  */
public class SpaceFieldFactory extends FieldFactory {
	public static final String FIELD_NAME = "Space";

	/**
	 * Constructor
	 */
	public SpaceFieldFactory() {
		super(FIELD_NAME);
	}

	/**
	 * Constructor
	 * @param model the model that the field belongs to.
	 * @param hlProvider the HightLightStringProvider.
	 * @param displayOptions the Options for display properties.
	 * @param fieldOptions the Options for field specific properties.
	 */
	private SpaceFieldFactory(FieldFormatModel model, ListingHighlightProvider hlProvider,
			Options displayOptions, Options fieldOptions) {

		super(FIELD_NAME, model, hlProvider, displayOptions, fieldOptions);
	}

	/**
	 * @see ghidra.app.util.viewer.field.FieldFactory#getField(ProxyObj, int)
	 */
	@Override
	public ListingField getField(ProxyObj<?> proxy, int varWidth) {
		Object obj = proxy.getObject();

		if (!enabled || !(obj instanceof CodeUnit)) {
			return null;
		}

		CodeUnit cu = (CodeUnit) obj;
		Integer n = getSpaces(cu);
		if (n != null) {
			if (n == 0) {
				cu.removeProperty(CodeUnit.SPACE_PROPERTY);
				return null;
			}
			else if (n < 0) {
				n = -n;
			}

			List<FieldElement> elements = new ArrayList<>(n);
			AttributedString as = new AttributedString("", Colors.FOREGROUND, getMetrics());
			for (int i = 0; i < n; i++) {
				elements.add(new TextFieldElement(as, 0, 0));
			}

			return ListingTextField.createMultilineTextField(this, proxy, elements,
				startX + varWidth, width, hlProvider);

		}
		return null;
	}

	private Integer getSpaces(CodeUnit cu) {
		if (cu.hasProperty(CodeUnit.SPACE_PROPERTY)) {
			try {
				return cu.getIntProperty(CodeUnit.SPACE_PROPERTY);
			}
			catch (NoValueException e) {
				// can't happen
			}
		}
		return null;
	}

	@Override
	public ProgramLocation getProgramLocation(int row, int col, ListingField bf) {
		Object obj = bf.getProxy().getObject();
		if (!(obj instanceof CodeUnit)) {
			return null;
		}
		CodeUnit cu = (CodeUnit) obj;

		int[] cpath = null;
		if (obj instanceof Data) {
			cpath = ((Data) obj).getComponentPath();
		}

		return new SpaceFieldLocation(cu.getProgram(), cu.getMinAddress(), null, cpath, row);
	}

	/**
	 * @see ghidra.app.util.viewer.field.FieldFactory#getFieldLocation(ghidra.app.util.viewer.field.ListingField, BigInteger, int, ghidra.program.util.ProgramLocation)
	 */
	@Override
	public FieldLocation getFieldLocation(ListingField bf, BigInteger index, int fieldNum,
			ProgramLocation programLoc) {

		if (programLoc instanceof SpaceFieldLocation) {

			SpaceFieldLocation loc = (SpaceFieldLocation) programLoc;
			return new FieldLocation(index, fieldNum, loc.getRow(), 0);
		}
		return null;
	}

	/**
	 * @see ghidra.app.util.viewer.field.FieldFactory#acceptsType(int, java.lang.Class)
	 */
	@Override
	public boolean acceptsType(int category, Class<?> proxyObjectClass) {
		if (!CodeUnit.class.isAssignableFrom(proxyObjectClass)) {
			return false;
		}
		return (category == FieldFormatModel.INSTRUCTION_OR_DATA);
	}

	/**
	 * @see ghidra.app.util.viewer.field.FieldFactory#newInstance(ghidra.app.util.viewer.format.FieldFormatModel, ghidra.app.util.ListingHighlightProvider, ghidra.framework.options.ToolOptions, ghidra.framework.options.ToolOptions)
	 */
	@Override
	public FieldFactory newInstance(FieldFormatModel formatModel, ListingHighlightProvider provider,
			ToolOptions displayOptions, ToolOptions fieldOptions) {
		return new SpaceFieldFactory(formatModel, provider, displayOptions, fieldOptions);
	}
}
