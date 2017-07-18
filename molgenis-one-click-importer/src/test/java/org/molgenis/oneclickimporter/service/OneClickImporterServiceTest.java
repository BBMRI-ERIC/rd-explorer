package org.molgenis.oneclickimporter.service;

import com.google.common.io.Resources;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.molgenis.oneclickimporter.model.Column;
import org.molgenis.oneclickimporter.model.DataCollection;
import org.molgenis.oneclickimporter.service.Impl.ExcelServiceImpl;
import org.molgenis.oneclickimporter.service.Impl.OneClickImporterServiceImpl;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.molgenis.data.meta.AttributeType.*;
import static org.testng.Assert.assertEquals;

public class OneClickImporterServiceTest
{
	private OneClickImporterService oneClickImporterService = new OneClickImporterServiceImpl();

	@Test
	public void testBuildDataSheetWithSimpleValidFile() throws IOException, InvalidFormatException, URISyntaxException
	{
		Sheet sheet = loadTestFile("/simple-valid.xlsx");
		DataCollection actual = oneClickImporterService.buildDataCollection("simple-valid", sheet);

		Column c1 = Column.create("name", 0, newArrayList("Mark", "Connor", "Fleur", "Dennis"));
		Column c2 = Column.create("superpower", 1,
				newArrayList("arrow functions", "Oldschool syntax", "Lambda Magician", "Root access"));

		DataCollection expected = DataCollection.create("simple-valid", newArrayList(c1, c2));

		assertEquals(actual, expected);
	}

	@Test
	public void testBuildDataSheetWithValidFormulaFile() throws IOException, InvalidFormatException, URISyntaxException
	{
		Sheet sheet = loadTestFile("/valid-with-formula.xlsx");
		DataCollection actual = oneClickImporterService.buildDataCollection("valid-with-formula", sheet);

		Column c1 = Column.create("name", 0, newArrayList("Mark", "Mariska"));
		Column c2 = Column.create("age", 1, newArrayList(26.0, 22.0));

		DataCollection expected = DataCollection.create("valid-with-formula", newArrayList(c1, c2));

		assertEquals(actual, expected);
	}

	@Test
	public void testBuildDataSheetBuildsColumnsOfEqualLength()
			throws IOException, InvalidFormatException, URISyntaxException
	{
		Sheet sheet = loadTestFile("/valid-with-blank-values.xlsx");
		DataCollection actual = oneClickImporterService.buildDataCollection("valid-with-blank-values", sheet);

		Column c1 = Column.create("name", 0, newArrayList("Mark", "Bart", "Tommy", "Sido", "Connor", null));
		Column c2 = Column.create("favorite food", 1,
				newArrayList("Fries", null, "Vegan food", "Pizza", null, "Spinache"));

		DataCollection expected = DataCollection.create("valid-with-blank-values", newArrayList(c1, c2));
		assertEquals(actual, expected);

		assertEquals(6, c1.getDataValues().size());
		assertEquals(6, c2.getDataValues().size());
	}

	@Test
	public void testBuildDataSheetWithComplexFile() throws IOException, InvalidFormatException, URISyntaxException
	{
		Sheet sheet = loadTestFile("/complex-valid.xlsx");
		DataCollection actual = oneClickImporterService.buildDataCollection("complex-valid", sheet);

		Column c1 = Column.create("first name", 0,
				newArrayList("Mark", "Fleur", "Dennis", "Bart", "Sido", "Mariska", "Tommy", "Connor", "Piet", "Jan"));
		Column c2 = Column.create("last name", 1,
				newArrayList("de Haan", "Kelpin", "Hendriksen", "Charbon", "Haakma", "Slofstra", "de Boer",
						"Stroomberg", "Klaassen", null));
		Column c3 = Column.create("full name", 2,
				newArrayList("Mark de Haan", "Fleur Kelpin", "Dennis Hendriksen", "Bart Charbon", "Sido Haakma",
						"Mariska Slofstra", "Tommy de Boer", "Connor Stroomberg", "Piet Klaassen", null));
		Column c4 = Column.create("UMCG employee", 3,
				newArrayList(true, true, true, true, true, true, true, true, false, false));
		Column c5 = Column.create("Age", 4, newArrayList(26.0, null, null, null, null, 22.0, 27.0, null, 53.0, 32.0));

		DataCollection expected = DataCollection.create("complex-valid", newArrayList(c1, c2, c3, c4, c5));
		assertEquals(actual, expected);
	}

	@Test
	public void guessBasicTypes()
	{
		List<Object> columnValues = Arrays.asList(1, 2, 3);
		assertEquals(oneClickImporterService.guessAttributeType(columnValues), INT);

		columnValues = Arrays.asList("a", "b", "c");
		assertEquals(oneClickImporterService.guessAttributeType(columnValues), STRING);

		columnValues = Arrays.asList(true, false, true);
		assertEquals(oneClickImporterService.guessAttributeType(columnValues), BOOL);

		columnValues = Arrays.asList(1.1, 1.2, 1.3);
		assertEquals(oneClickImporterService.guessAttributeType(columnValues), DECIMAL);

		columnValues = Arrays.asList(1L, 2L, 3L);
		assertEquals(oneClickImporterService.guessAttributeType(columnValues), LONG);

		columnValues = Arrays.asList(1L, "abc", 3L);
		assertEquals(oneClickImporterService.guessAttributeType(columnValues), STRING);
	}

	@Test
	public void guessEnrichedTypes()
	{
		List<Object> columnValues = Arrays.asList(
				"This is a very long string. This is a very long string. This is a very long string. This is a very long string. This is a very long string. This is a very long string. This is a very long string. This is a very long string. This is a very long string. This is a very long string. This is a very long string. This is a very long string. ",
				"This is a short string", "String...");
		assertEquals(oneClickImporterService.guessAttributeType(columnValues), TEXT);
	}

	private Sheet loadTestFile(String fileName) throws IOException, InvalidFormatException, URISyntaxException
	{
		URL resourceUrl = Resources.getResource(OneClickImporterServiceTest.class, fileName);
		File file = new File(new URI(resourceUrl.toString()).getPath());

		ExcelService excelService = new ExcelServiceImpl();
		return excelService.buildExcelSheetFromFile(file);
	}
}
