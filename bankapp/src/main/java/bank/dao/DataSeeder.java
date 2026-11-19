package bank.dao;

import bank.model.*;

public class DataSeeder {

    public static void seedIfEmpty(CustomerDAO dao) {
        if (!dao.findAll().isEmpty()) {
            return;
        }
        String[][] people = {
            {"Neo", "Kgosi"}, {"Lesedi", "Moeng"}, {"Kabo", "Dube"}, {"Thato", "Sebego"},
            {"Mpho", "Tau"}, {"Naledi", "Modise"}, {"Tumelo", "Seretse"}, {"Boitumelo", "Phiri"}
        };
        for (int i = 0; i < people.length; i++) {
            String num = String.format("%03d", i + 1);
            boolean employed = (i != 4);   // the fifth person is unemployed
            IndividualCustomer c = new IndividualCustomer("C" + num, "Plot " + (10 + i) + ", Gaborone",
                    "pass" + (i + 1), people[i][0], people[i][1],
                    String.format("%09d", 100000001 + i), 22 + i * 3, employed);

            c.openSavingsAccount("S" + num, "Main Mall").deposit(500 + i * 250);
            if (i % 2 == 0) {
                c.openInvestmentAccount("I" + num, "Main Mall", 500 + i * 100).deposit(1000);
            }
            if (employed && i % 3 == 0) {
                c.openChequeAccount("CH" + num, "Main Mall", "ABC Ltd", "Plot 1, Gaborone").deposit(2000);
            }
            dao.insert(c);
        }

        String[][] companies = {{"Kalahari Traders", "BW00001234"}, {"Okavango Supplies", "BW00005678"}};
        for (int j = 0; j < companies.length; j++) {
            String num = String.format("%03d", people.length + j + 1);
            CompanyCustomer co = new CompanyCustomer("C" + num, "Industrial Area, Gaborone",
                    "pass" + (people.length + j + 1), companies[j][0], companies[j][1]);
            co.openSavingsAccount("S" + num, "Main Mall").deposit(10000);
            co.openInvestmentAccount("I" + num, "Main Mall", 5000);
            dao.insert(co);
        }
    }
}
