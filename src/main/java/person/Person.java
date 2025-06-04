package person;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class Person {
    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthDate;
    private HashMap<String, Integer> demeritRecords;
    private boolean isSuspended;

    public Person(String personID, String firstName, String lastName, String address, String birthdate) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthDate = birthdate;
        this.demeritRecords = new HashMap<>();
        this.isSuspended = false;
    }

    public boolean addPerson() {

        if (!isValidPersonID(personID)) {
            System.out.println("fail id");
            return false;
        }

        if (!isValidAddress(address)) {
            System.out.println("fail address");
            return false;
        }

        if (!isValidDate(birthDate)) {
            System.out.println("fail birthdate");
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("people.txt", true))) {
            writer.write(this.toFileString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean updatePersonalDetails(String personID, String firstName, String lastName, String address, String birthDate) {
        List<String> linesList = new ArrayList<>();
        int personLineIndex = -1;

        // Attempt to locate user while reading file and record index if found
        try (BufferedReader reader = new BufferedReader(new FileReader("people.txt"))) {
            String line;
            int curLineIndex = 0;
            while ((line = reader.readLine()) != null) {
                linesList.add(line);
                if (line.startsWith(personID + "|") && personLineIndex < 0) {
                    personLineIndex = curLineIndex;
                }
                curLineIndex++;
            }
        } catch (IOException e) {
            System.err.println("File read error");
            return false;
        }

        if (personLineIndex >= 0) {
            // Validate the user is over 18 years old
            if (this.getAge(new SimpleDateFormat("dd-MM-yyyy").format(new Date())) < 18) {
                return false;
            }
            // Validate only the date of birth is being changed 
            if ((!birthDate.isEmpty() && this.birthDate != birthDate) && (!firstName.isEmpty() || !lastName.isEmpty() || !address.isEmpty())) {
                return false;
            }
            // Validate ID
            if (!Character.isDigit(this.personID.charAt(0)) || (this.personID.charAt(0) - '0') % 2 == 0) {
                return false;
            }

            // Update properties in class
            if (!personID.isEmpty() && isValidPersonID(personID)) this.personID = personID;
            if (!firstName.isEmpty()) this.firstName = firstName;
            if (!lastName.isEmpty()) this.lastName = lastName;
            if (!address.isEmpty() && isValidAddress(address)) this.address = address;
            if (!birthDate.isEmpty() && isValidDate(birthDate)) this.birthDate = birthDate;

            // Update file with new entry for person (updated)
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("people.txt"))) {
                for (int i = 0; i < linesList.size(); i++) {
                    if (i != personLineIndex) writer.write(linesList.get(i));
                    else {
                        writer.write(this.toFileString());
                    }
                    writer.newLine();
                }
            } catch (IOException e) {
                System.err.println("File write error");
                return false;
            }
        } else {
            System.err.println("User entry not found");
            return false;
        }
        return true;
    }

    public String addDemeritPoints(String date, int points) {
        // Check for point range and validity of date
        boolean isSuccess = points >= 1 && points <= 6 && this.isValidDate(date);
        if (!isSuccess) {
            return "Failed";
        }
        // Assuming that birthDate is validated by this point
        long age = this.getAge(date);

        int suspensionThreshold = age <= 21 ? 6 : 12;
        int totalDemeritPoints = demeritRecords.values().stream().mapToInt(Integer::intValue).sum();
        if (totalDemeritPoints + points > suspensionThreshold) {
            this.isSuspended = true;
        }

        // Create bufferedWriter to update people.txt file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("people.txt", true))) {
            writer.write(String.format("%s %s %d", this.personID, date, points));
            writer.newLine();
            writer.write(toFileString());
            writer.newLine();
            demeritRecords.put(date, points);
        } catch (IOException e) {
            return "Failed";
        }

        return "Success";
    }

    private long getAge(String currentDate) {
        // Use dateTimeFormatter to convert string dates into localDates, so I can find out the days in between
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate startDate = LocalDate.parse(this.birthDate, formatter);
        LocalDate endDate = LocalDate.parse(currentDate, formatter);
        return Period.between(startDate, endDate).getYears();
    }

    private boolean isValidPersonID(String id) {
        return id.matches("^[2-9]{2}(?=(?:.*[^a-zA-Z0-9]){2,}).{6}[A-Z]{2}$") && id.length() == 10;
    }

    private boolean isValidAddress(String address) {
        String[] parts = address.split("\\|");
        return parts.length == 5 && parts[3].equals("Victoria");
    }

    private boolean isValidDate(String date) {
        try {
            new SimpleDateFormat("dd-MM-yyyy").parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private String toFileString() {
        return personID + "|" + firstName + "|" + lastName + "|" + address + "|" + birthDate + "|suspended=" + isSuspended;
    }


}
