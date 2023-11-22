package com.example.lab9;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class Lab9Application implements CommandLineRunner {

	final String SITE_URL = "https://atlas.herzen.spb.ru/";

	public static void main(String[] args) {
		SpringApplication.run(Lab9Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		final var department = args[0];
		System.out.println("Department: " + department);
		Document doc = Jsoup.connect(SITE_URL + "/faculty.php").get();
		if (department.contains("кафедра")) {
			final var departmentUri = doc
				.select("a.alist:contains(" + department + ")")
				.attr("href");
			System.out.println("Department URL: " + SITE_URL + departmentUri);

			String csvString = "";
			Document fac_doc = Jsoup.connect(SITE_URL + departmentUri).get();
			Element table = fac_doc.selectFirst("table.table_good");
			Elements rows = table.select("tr");
			for (Element row : rows) {
				Elements tds = row.select("td");
				for (Element td : tds) {
					csvString += td.text() + ",";
				}
				csvString = csvString.replaceAll(",$", "\n");
			}
			System.out.println(csvString);

		} else {
			var fac_num = doc.select("a.alist:contains(" + department + ")").attr("onclick").replaceAll("[\\D]", "");
			// System.out.println(fac_num);
			final Element fac_content = doc.getElementById("fac_" + fac_num);
			Elements links = fac_content.getElementsByTag("a");
			links.remove(links.size()-1);
			String csvString = "";
			Integer id = 0;
			for (var link : links) {
				String linkHref = link.attr("href");
				// String linkText = link.text();
				// System.out.println(link.attr("href"));
				// System.out.println(link.text());

				Document fac_doc = Jsoup.connect(SITE_URL + linkHref).get();
				Element table = fac_doc.selectFirst("table.table_good");
				Elements rows = table.select("tr");
				if (id == 0) {
					Elements tds = rows.get(0).select("td");
					for (Element td : tds) {
						csvString += td.text() + ",";
					}
					csvString = csvString.replaceAll(",$", "\n");
					id += 1;
				}
				rows.remove(0);
				for (Element row : rows) {
					Elements tds = row.select("td");
					tds.remove(0);
					csvString += id + ",";
					for (Element td : tds) {
						csvString += td.text() + ",";
					}
					csvString = csvString.replaceAll(",$", "\n");
					id += 1;
				}
			}
			System.out.println(csvString);
			}
	}
}
