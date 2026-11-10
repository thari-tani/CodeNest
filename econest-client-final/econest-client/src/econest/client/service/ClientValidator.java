package econest.client.service;

import econest.client.exception.ValidationException;
import econest.client.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// all the input checks live here so the service stays clean
public class ClientValidator {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    // Sri Lankan numbers: 0771234567, +94771234567, 0112345678
    private static final Pattern PHONE = Pattern.compile("^(?:\\+94|0)\\d{9}$");

    // collects every mistake first and throws once, so the user sees them all together
    public void validate(Client c) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (isBlank(c.getName()) || c.getName().trim().length() < 2)
            errors.add("Name must be at least 2 characters.");
        if (isBlank(c.getEmail()) || !EMAIL.matcher(c.getEmail().trim()).matches())
            errors.add("Email format is invalid.");
        if (isBlank(c.getPhone()) || !PHONE.matcher(c.getPhone().trim().replace(" ", "")).matches())
            errors.add("Phone must be a valid Sri Lankan number (e.g. 0771234567 or +94771234567).");
        if (isBlank(c.getAddress()) || c.getAddress().trim().length() < 5)
            errors.add("Address must be at least 5 characters.");

        if (c instanceof HouseholdClient) {
            int size = ((HouseholdClient) c).getHouseholdSize();
            if (size < 1 || size > 30) errors.add("Household size must be between 1 and 30.");
        } else if (c instanceof OfficeClient) {
            OfficeClient o = (OfficeClient) c;
            if (isBlank(o.getCompanyName())) errors.add("Company name is required for office clients.");
            if (isBlank(o.getContactPerson())) errors.add("Contact person is required for office clients.");
            if (isBlank(o.getBusinessRegNo())) errors.add("Business registration number is required.");
        }

        if (!errors.isEmpty()) throw new ValidationException(String.join("\n - ", errors));
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}
