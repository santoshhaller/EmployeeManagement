package com.casestudy;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

/**
 * Write a simple program which will read the file and report:
	- which managers earn less than they should, and by how much
	- which managers earn more than they should, and by how much
	- which employees have a reporting line which is too long, and by how much
 *
 */
public class App {
	public static void main(String[] args) throws CsvValidationException, IOException, NumberFormatException {
		System.out.println("Employee Records !!\n");

		CSVReader reader = new CSVReader(
				new FileReader("C:\\Users\\Santosh\\Desktop\\workspaceDev\\EmployeRecord\\emprecord.csv"));
		
		//skipping Headers in csv file
		reader.skip(1);
		
		List<Employe> empList = generateEmployeObjects(reader);	
		displayManagersSalaryLessOrMoreThanAverage(empList);
		displayLongReportingLink(empList);	
		
	}	

	//Generating Employe objects
	private static List<Employe> generateEmployeObjects(CSVReader reader) throws CsvValidationException, NumberFormatException, IOException {
		List<Employe> empList = new ArrayList<>();
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {

			Employe employe = new Employe();
			employe.setId(Integer.valueOf(nextLine[0]));
			employe.setFirstName(nextLine[1]);
			employe.setLastName(nextLine[2]);
			employe.setSalary(Long.valueOf(nextLine[3]));
			Optional<String> mngIdOptinal = Optional.ofNullable(nextLine[4]);
			if (mngIdOptinal.isPresent() && !mngIdOptinal.get().equals("")) {
				employe.setManagerId(Integer.valueOf(mngIdOptinal.get()));
			}
			empList.add(employe);
		}
		return empList;
			
	}
   
	//Print managers Name and id who erans less or more than the average of subordinates
	static void displayManagersSalaryLessOrMoreThanAverage(List<Employe> empList)
	{
		Set<Integer> managerIds = empList.stream().filter(i -> i.getManagerId() != null).map(e -> e.getManagerId())
				.collect(Collectors.toSet());
		System.out.println("Managers :" + managerIds);
		Map<Integer, Employe> managersInfoMap = empList.stream().filter(e -> managerIds.contains(e.getId()))
				.collect(Collectors.toMap(e -> e.getId(), e -> e));

		for (Integer id : managerIds) {
			Double avg = empList.stream().filter(e -> e.getManagerId() == id).mapToLong(s -> s.getSalary()).average()
					.getAsDouble();

			if (managersInfoMap.get(id).getSalary() < avg + (avg * 0.2)) {
				Double lessSal = (avg + (avg * 0.2)) - managersInfoMap.get(id).getSalary();
				System.out.println(managersInfoMap.get(id).getFirstName() + " " + managersInfoMap.get(id).getLastName()
						+ "(Emp Id: " + id + ")" + " earns less than they should by " + lessSal);
			}

			if (managersInfoMap.get(id).getSalary() > avg + (avg * 0.5)) {
				Double moreSal = managersInfoMap.get(id).getSalary() - (avg + (avg * 0.5));
				System.out.println(managersInfoMap.get(id).getFirstName() + " " + managersInfoMap.get(id).getLastName()
						+ "(Emp Id: " + id + ")" + " earns more than they should by " + moreSal);
			}

		}
	}
	
	// Method displays long reporting chain between EMployee and CEO when more than 4 managers are in between
	private static void displayLongReportingLink(List<Employe> empList) {
		Map<Integer, Employe> emplToMngerMap = empList.stream().map(e -> e)
				.collect(Collectors.toMap(e -> e.getId(), e -> e));
	
		List<List<Integer>> reportingList = new ArrayList<>();
		for (Employe emp : empList) {
			List<Integer> links = new ArrayList<>();
			Integer id = emp.getManagerId();
			int i = 1;
			links.add(emp.getId());
			while (id != null) {
				links.add(id);
				id = emplToMngerMap.get(id).getManagerId();
				i++;
			}
			reportingList.add(links);

		}
		for (List<Integer> link : reportingList) {
			//this list contains ids and includes Employee ids and CEO ids for Chaining. Comparing with digit 7 .
			if (link.size() > 7) {
				System.out.println("Longest report link : " + link + "long by" + (link.size() - 7));
			} else {
				System.out.println("No reporting line too longer than 4");
			}
		}
		
	}
}
