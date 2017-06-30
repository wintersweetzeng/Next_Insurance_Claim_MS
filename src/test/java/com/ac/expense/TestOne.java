package com.ac.expense;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestOne {

	@Test
	public void test() {

		ExpenseDetail expense = new ExpenseDetail();
		expense.setClaimed(false);
		expense.setExpenseTime("20001010010203");
		expense.setUid("3702821982");

		List<MedicineDetail> medicines = new ArrayList<>();

		MedicineDetail detail1 = new MedicineDetail();
		detail1.setId("1000");
		detail1.setName("med1000");
		detail1.setNumber(10);
		detail1.setPrice(10);
		MedicineDetail detail2 = new MedicineDetail();
		detail2.setId("2000");
		detail2.setName("med2000");
		detail2.setNumber(10);
		detail2.setPrice(20);
		MedicineDetail detail3 = new MedicineDetail();
		detail3.setId("3000");
		detail3.setName("med3000");
		detail3.setNumber(10);
		detail3.setPrice(30);

		medicines.add(detail1);
		medicines.add(detail2);
		medicines.add(detail3);

		expense.setMedicines(medicines);

		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(expense);
			System.out.println(json);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
